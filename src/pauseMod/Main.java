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
    private Player lastPauser;

    @Override
    public void init() {
        addSettings();
        resourceUI = new ResourcePreviewUI();
        setupEvents();
        setupPackets();
    }

    void addSettings() {
        ui.settings.addCategory("Multiplayer Pause", Icon.pause, s -> {
            s.checkPref("multiplayerpause-toasts", true);
            s.checkPref("multiplayerpause-allowany", false);
            s.checkPref("multiplayerpause-synconpause", false);
            s.checkPref("multiplayerpause-synconunpause", false); // Is enabling this by default a good idea? I have no clue how much desync this mod is going to cause...
            s.checkPref("multiplayerpause-schedulesync", false);
            


            s.checkPref("multiplayerpause-showotherpreview", true);
            s.checkPref("multiplayerpause-shownames", true);

        });
    }

    void setupEvents() {
        Events.run(Trigger.update, () -> {
            // Only handle pause key in multiplayer for clients. 
            // For host, we use StateChangeEvent to sync state changes naturally.
            if (net.active() && net.client() && Core.input.keyTap(Binding.pause) && !renderer.isCutscene() && !scene.hasDialog() && !scene.hasKeyboard() && !ui.restart.isShown() && state.isGame()) {
                Call.serverPacketReliable("multiplayerpause-request", "");
            }

            // Sync plans if paused
            if (state.isPaused() && net.active() && player != null && player.unit() != null) {
                Queue<BuildPlan> plans = player.unit().plans;
                String currentPlans = packetString(plans);
                if (!currentPlans.equals(lastPlans)) {
                    lastPlans = currentPlans;
                    if (net.client()) {
                        Call.serverPacketReliable("multiplayerpause-syncplans", currentPlans);
                    } else if (net.server()) {
                        // Host directly broadcasts its own plans to all clients
                        Call.clientPacketReliable("multiplayerpause-updateplans", player.id + "|" + currentPlans);
                    }
                }
            }

            // Update resource preview UI
            if (resourceUI != null) {
                resourceUI.update();
            }
        });

        Events.run(Trigger.draw, () -> {
            if (!state.isPaused() || !Core.settings.getBool("multiplayerpause-showotherpreview")) return;

            for (Player p : Groups.player) {
                if (p == player || p.unit() == null || p.unit().plans.isEmpty()) continue;

                for (BuildPlan plan : p.unit().plans) {
                    if (plan.block == null) continue;

                    if (plan.breaking) {
                        // Draw outline with player color
                        Draw.color(p.color);
                        Lines.stroke(1f);
                        Lines.rect(plan.drawx() - plan.block.size * tilesize / 2f, plan.drawy() - plan.block.size * tilesize / 2f, plan.block.size * tilesize, plan.block.size * tilesize);
                        Draw.reset();
                    } else {
                        // Draw the plan with half transparency using the player's color
                        Draw.color(p.color, 0.5f);
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

        // Sync state changes on host to all clients
        Events.on(StateChangeEvent.class, event -> {
            if (net.server()) {
                boolean paused = event.to == GameState.State.paused || event.to == GameState.State.menu;
                boolean previouslyPaused = event.from == GameState.State.paused || event.from == GameState.State.menu;
                
                if (paused != previouslyPaused) {
                    showToast(lastPauser != null ? lastPauser : player, paused);
                    lastPauser = null; // Reset after use
                }
            }
        });
    }

    void setupPackets() {
        netServer.addPacketHandler("multiplayerpause-request", (p, data) -> {
            if (!(p.admin || Core.settings.getBool("multiplayerpause-allowany")) || state.isMenu()) return;

            lastPauser = p;
            state.set(state.isPaused() ? GameState.State.playing : GameState.State.paused);
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

            // Update plan for the player on server side so the Host can see it
            if (p.unit() != null) {
                p.unit().plans.clear();
                for (BuildPlan plan : parsePacket(data)) {
                    p.unit().plans.add(plan);
                }
            }

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
                p.unit().plans.clear();
                for (BuildPlan plan : parsePacket(plansData)) {
                    p.unit().plans.add(plan);
                }
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
