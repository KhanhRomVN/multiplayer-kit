package pauseMod;

import arc.*;
import arc.struct.Queue;
import arc.util.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.pooling.*;
import mindustry.core.*;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.mod.*;
import mindustry.ui.*;
import mindustry.world.Block;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Main extends Mod {
    private long lastSyncTime;
    private String lastPlans = "";
    private ResourcePreviewUI resourceUI;

    @Override
    public void init() {
        setupPackets();
        
        Events.on(ClientLoadEvent.class, e -> {
            addSettings();
            resourceUI = new ResourcePreviewUI();
            setupEvents();
        });
    }

    // Keep setupEvents separate or merge? 
    // setupEvents uses Events.run(Trigger.update), which is fine to register early, 
    // but resourceUI internal logic needs to run. 
    // Better to move setupEvents call inside ClientLoadEvent too, 
    // OR just keep setupEvents registration here but ensure resourceUI is checked.
    // However, Main.java uses resourceUI in setupEvents triggers.
    // So setupEvents MUST be called after resourceUI is assigned.


    void addSettings() {
        ui.settings.addCategory("Multiplayer Pause", Icon.pause, s -> {
            s.checkPref("multiplayerpause-toasts", true);
            s.checkPref("multiplayerpause-allowany", false);
            s.checkPref("multiplayerpause-synconpause", false);
            s.checkPref("multiplayerpause-synconunpause", false); // Is enabling this by default a good idea? I have no clue how much desync this mod is going to cause...
            s.checkPref("multiplayerpause-schedulesync", false);
            
            s.sliderPref("multiplayerpause-resourceui-position-idx", 1, 0, 3, i -> {
                String[] positions = {"Top Left", "Top Right", "Bottom Left", "Bottom Right"};
                return positions[i];
            });

            s.checkPref("multiplayerpause-showotherpreview", true);
            s.checkPref("multiplayerpause-shownames", true);
        });
    }

    void setupEvents() {
        Events.run(Trigger.update, () -> {
            if (Core.input.keyTap(Binding.pause) && !renderer.isCutscene() && !scene.hasDialog() && !scene.hasKeyboard() && !ui.restart.isShown() && state.isGame() && net.active()) {
                if (net.client()) Call.serverPacketReliable("multiplayerpause-request", ""); // Send pause request
                else showToast(player, !state.isPaused()); // Show toast for host pausing (inverted as the state hasn't been updated yet)
            }

            // Sync plans if paused
            if (state.isPaused() && net.active() && player != null && player.unit() != null) {
                Queue<BuildPlan> plans = player.unit().plans;
                String currentPlans = packetString(plans);
                if (!currentPlans.equals(lastPlans)) {
                    lastPlans = currentPlans;
                    if (net.client()) {
                        Call.serverPacketReliable("multiplayerpause-syncplans", currentPlans);
                    }
                }
            }

            // Update resource preview UI
            if (resourceUI != null) {
                resourceUI.update();
            }
        });

        Events.run(Trigger.draw, () -> {
            if (!state.isPaused() || !net.active() || !Core.settings.getBool("multiplayerpause-showotherpreview")) return;

            for (Player p : Groups.player) {
                if (p == player || p.unit() == null || p.unit().plans.isEmpty()) continue;

                for (BuildPlan plan : p.unit().plans) {
                    if (plan.block == null) continue;

                    if (plan.breaking) {
                        // Draw a red outline for breaking
                        Draw.color(Color.scarlet);
                        Lines.stroke(1f);
                        Lines.rect(plan.drawx() - plan.block.size * tilesize / 2f, plan.drawy() - plan.block.size * tilesize / 2f, plan.block.size * tilesize, plan.block.size * tilesize);
                        Draw.reset();
                    } else {
                        // Draw the plan with half transparency using the block's full region
                        Draw.color(Color.white, 0.5f);
                        Draw.rect(plan.block.fullIcon, plan.drawx(), plan.drawy(), plan.rotation * 90);
                        Draw.reset();
                    }
                }

                // Draw player name above their first plan for identification
                if (Core.settings.getBool("multiplayerpause-shownames") && !p.unit().plans.isEmpty()) {
                    BuildPlan first = p.unit().plans.first();
                    Font font = Fonts.outline;
                    font.setUseIntegerPositions(false);
                    font.getData().setScale(1f / 4f); // Scale down font for world space
                    font.setColor(Color.white);
                    font.draw(p.name, first.drawx(), first.drawy() + first.block.size * tilesize / 2f + 4f, Align.center);
                    font.getData().setScale(1f); // Reset scale
                }
            }
        });
    }

    void setupPackets() {
        netServer.addPacketHandler("multiplayerpause-request", (p, data) -> {
            if (!(p.admin || Core.settings.getBool("multiplayerpause-allowany")) || state.isMenu()) return;

            state.set(state.isPaused() ? GameState.State.playing : GameState.State.paused);
            showToast(p, state.isPaused());
        });
        // State changes are forwarded to clients for more responsive pausing (avoids waiting for next stateSnapshot) which should reduce desync (I hope) and allows for toasts
        netClient.addPacketHandler("multiplayerpause-updatestate", data -> {
            String[] d = data.split(" ");
            if (d.length != 2) return;
            boolean paused = d[1].equals("t");
            state.set(paused ? GameState.State.paused : GameState.State.playing); // Reflect state change on the client ASAP
            showToast(Groups.player.getByID(Strings.parseInt(d[0])), paused);
            if (Core.settings.getBool("multiplayerpause-syncon" + (paused ? "pause" : "unpause"))) {
                long since = Time.millis() - lastSyncTime;
                if (since > 5100) { // Sync now
                    Call.sendChatMessage("/sync");
                    lastSyncTime = Time.millis();
                } else if (Core.settings.getBool("multiplayerpause-schedulesync") && since > 0) { // Schedule a sync as one has taken place recently
                    Timer.schedule(() -> Call.sendChatMessage("/sync"), (5100 - since) / 1000f);
                    lastSyncTime = Time.millis() + 5100 - since;
                }

            }
        });

        // Server receives plans from a client
        netServer.addPacketHandler("multiplayerpause-syncplans", (p, data) -> {
            if (state.isMenu() || p == null) return;
            // Validate data size to prevent spam/crashes
            if (data.length() > 5000) return; 

            // Update plan for the player on server side? Not strictly necessary for preview, but good for consistency.
            // p.unit().plans = parsePacket(data); // Optional: update server logic if needed.

            // Broadcast to other clients
            // Format: "playerID|planData"
            Call.clientPacketReliable("multiplayerpause-updateplans", p.id + "|" + data);
        });

        // Client receives plans from another player
        netClient.addPacketHandler("multiplayerpause-updateplans", data -> {
            int splitIndex = data.indexOf("|");
            if (splitIndex == -1) return;

            String pidStr = data.substring(0, splitIndex);
            String plansData = data.substring(splitIndex + 1);

            int pid = Strings.parseInt(pidStr);
            Player p = Groups.player.getByID(pid);
            if (p != null && p != player && p.unit() != null) {
                p.unit().plans = parsePacket(plansData);
            }
        });
    }

    void showToast(Player p, boolean paused) {
        if (net.server()) Call.clientPacketReliable("multiplayerpause-updatestate", p.id + " " + (paused ? "t" : "f")); // Forward state change to clients
        if (!Core.settings.getBool("multiplayerpause-toasts")) return;

        Menus.infoToast(Strings.format("@ @ the game.", p == null ? "[lightgray]Unknown player[]" : Strings.stripColors(p.name), paused ? "paused" : "unpaused"), 2f);
    }
    
    // Serialization logic
    // Format: x,y,rotation,blockId,breaking;...
    String packetString(Queue<BuildPlan> plans) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (BuildPlan p : plans) {
            if (count++ > 0) sb.append(";");
            sb.append(p.x).append(",")
              .append(p.y).append(",")
              .append(p.rotation).append(",")
              .append(p.block == null ? -1 : p.block.id).append(",")
              .append(p.breaking ? 1 : 0);
              // Limit packet size if needed
              if (sb.length() > 4000) break;
        }
        return sb.toString();
    }

    Queue<BuildPlan> parsePacket(String data) {
        Queue<BuildPlan> out = new Queue<>();
        if (data.isEmpty()) return out;

        String[] parts = data.split(";");
        for (String part : parts) {
            String[] props = part.split(",");
            if (props.length != 5) continue;
            try {
                int x = Integer.parseInt(props[0]);
                int y = Integer.parseInt(props[1]);
                int rotation = Integer.parseInt(props[2]);
                short blockId = Short.parseShort(props[3]);
                boolean breaking = props[4].equals("1");

                Block block = (blockId == -1) ? null : content.block(blockId);
                
                BuildPlan plan;
                if (breaking) {
                     plan = new BuildPlan(x, y, rotation, block);
                     plan.breaking = true;
                } else {
                    if (block == null) continue; // Skip invalid build plans
                    plan = new BuildPlan(x, y, rotation, block);
                }
                out.add(plan);
            } catch (Exception e) {
                // Ignore malformed plans
            }
        }
        return out;
    }
}
