package pauseMod;

import arc.Core;
import arc.graphics.Color;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.event.Touchable;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Align;

import mindustry.content.Items;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;

import static mindustry.Vars.*;

public class ResourcePreviewUI {
    private Table table;
    private ObjectMap<Player, ObjectMap<Item, Integer>> playerRequirements = new ObjectMap<>();
    private ObjectMap<Item, Integer> totalRequirements = new ObjectMap<>();
    private ObjectMap<Item, Integer> coreResources = new ObjectMap<>();
    private boolean visible = false;

    public ResourcePreviewUI() {
        build();
    }

    private void build() {
        table = new Table();
        table.setBackground(Styles.black6);
        table.margin(8f);
        
        // Add to HUD
        ui.hudGroup.addChild(table);
        
        // Load position
        float x = Core.settings.getFloat("multiplayerpause-resourceui-x", 100);
        float y = Core.settings.getFloat("multiplayerpause-resourceui-y", Core.graphics.getHeight() - 100);
        table.setPosition(x, y);
        table.touchable = Touchable.enabled;

        // Add drag listener
        table.addListener(new ElementGestureListener() {
            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                table.moveBy(deltaX, deltaY);
                clampToScreen();
                
                // Save position
                Core.settings.put("multiplayerpause-resourceui-x", table.x);
                Core.settings.put("multiplayerpause-resourceui-y", table.y);
            }
        });
    }

    private void clampToScreen() {
        float x = Math.max(0, Math.min(Core.graphics.getWidth() - table.getWidth(), table.x));
        float y = Math.max(0, Math.min(Core.graphics.getHeight() - table.getHeight(), table.y));
        table.setPosition(x, y);
    }

    public void update() {
        // Check if should show
        boolean shouldShow = shouldShow();
        
        if (shouldShow != visible) {
            visible = shouldShow;
            table.visible = visible;
        }

        if (!visible) return;

        // Calculate resources
        calculateRequiredResources();
        getCoreResources();

        // Rebuild table content
        rebuildTable();
    }

    private boolean shouldShow() {
        // Only show when:
        // 1. Game is paused
        // 2. In multiplayer
        // 3. Any player has plans (checked in calculateRequiredResources)
        return state.isPaused() && net.active();
    }

    private void calculateRequiredResources() {
        playerRequirements.clear();
        totalRequirements.clear();

        // Iterate through all players
        for (Player p : Groups.player) {
            if (p.unit() != null && p.unit().plans != null && !p.unit().plans.isEmpty()) {
                ObjectMap<Item, Integer> pReq = new ObjectMap<>();
                boolean hasPlans = false;

                for (BuildPlan plan : p.unit().plans) {
                    if (!plan.breaking && plan.block != null) {
                        ItemStack[] requirements = plan.block.requirements;
                        if (requirements != null) {
                            for (ItemStack stack : requirements) {
                                hasPlans = true;
                                // Add to player reqs
                                int pCurrent = pReq.get(stack.item, 0);
                                pReq.put(stack.item, pCurrent + stack.amount);

                                // Add to total reqs
                                int tCurrent = totalRequirements.get(stack.item, 0);
                                totalRequirements.put(stack.item, tCurrent + stack.amount);
                            }
                        }
                    }
                }
                
                if (hasPlans) {
                    playerRequirements.put(p, pReq);
                }
            }
        }
    }

    private void getCoreResources() {
        coreResources.clear();

        if (player == null || player.team() == null) return;

        // Get nearest core
        Building core = player.team().core();
        if (core != null && core.items != null) {
            // Get all items from core
            for (Item item : content.items()) {
                int amount = core.items.get(item);
                if (amount > 0) {
                    coreResources.put(item, amount);
                }
            }
        }
    }

    private void rebuildTable() {
        table.clear();
        
        // Drag hint top
        table.add("(Drag to move)").color(Color.gray).padBottom(4f).get().setFontScale(0.75f);
        table.row();

        if (playerRequirements.isEmpty()) {
            table.add("No active plans").color(Color.lightGray).pad(10f);
        } else {
            // 1. Per Player Sections
            for (ObjectMap.Entry<Player, ObjectMap<Item, Integer>> entry : playerRequirements) {
                Player p = entry.key;
                ObjectMap<Item, Integer> reqs = entry.value;

                // Player Name Header
                table.add(p.name).color(p.color).left().padTop(6f).row();
                table.image().color(p.color).fillX().height(2f).padBottom(4f).row();

                // Grid for items (max 3 cols)
                Table grid = new Table();
                int col = 0;
                for (ObjectMap.Entry<Item, Integer> itemEntry : reqs) {
                    Item item = itemEntry.key;
                    int amount = itemEntry.value;

                    // Icon + Amount
                    grid.image(item.uiIcon).size(16f).padRight(4f);
                    grid.add(String.valueOf(amount)).color(Color.white).padRight(12f).left();

                    col++;
                    if (col % 3 == 0) grid.row();
                }
                table.add(grid).left().row();
            }

            // Separator
            table.image().color(Color.gray).fillX().height(2f).pad(6f).row();

            // 2. Footer: Total / Available
            table.add("Total Consumption / Available").color(Color.goldenrod).padBottom(4f).row();
            
            Table footerGrid = new Table();
            int col = 0;
            for (ObjectMap.Entry<Item, Integer> itemEntry : totalRequirements) {
                Item item = itemEntry.key;
                int required = itemEntry.value;
                int available = coreResources.get(item, 0);

                // Format: All Req / All Avail
                // Color red if Req > Avail
                Color color = available >= required ? Color.white : Color.scarlet;
                
                footerGrid.image(item.uiIcon).size(16f).padRight(4f);
                footerGrid.add(required + " / " + available).color(color).padRight(12f).left();

                col++;
                if (col % 3 == 0) footerGrid.row();
            }
            table.add(footerGrid).left().row();
        }

        table.pack();
        clampToScreen();
    }

    // Removed old updatePosition method as it is replaced by drag logic

    public void dispose() {
        if (table != null) {
            table.remove();
        }
    }
}
