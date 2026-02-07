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
    private Table contentTable; // New field for the content table
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
        
        // Add content table
        contentTable = new Table();
        table.add(contentTable);
        
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

        // Calculate resources to check for changes
        boolean changed = calculateRequiredResources();
        
        // Also check if core resources changed (less critical but good for numbers)
        // For performance, maybe only update text if numbers change, but rebuilding table is easier.
        // We will rebuild if plans changed OR every 30 frames to update core resources numbers?
        // Or just rebuild if plans changed.
        
        // Let's implement a hash/changed check
        if (changed) {
            getCoreResources(); // Only needed when we rebuild
            rebuildTable();
        } else {
             // Just update numbers if table exists? 
             // For now, let's stick to rebuild only on plan changes.
             // But core resources (available amount) change often.
             // If we don't rebuild, the available numbers won't update.
             
             // Issue: If we rebuild every frame, drag breaks.
             // Solution: Update existing labels instead of clearing table.
             
             // BUT, implementing update-existing logic is complex with dynamic grid.
             // Alternative: only rebuild if something actually changed.
             // Core resources change often.
             
             // FIX FOR DRAG:
             // The issue with drag and rebuild is likely that rebuild changes the layout/size/pointers.
             // If we use keep the table structure and just update text, it should be fine.
             
             // Let's try to update CoreResources every 60 frames if no plan change?
             // Or update core resources always, but only rebuild table if plans change.
             
             // Actually, if we just want to fix drag, we can make the rebuild check if we are currently dragging?
             // No, that's hacky.
             
             // Real solution:
             // 1. Calculate reqs.
             // 2. Check if structure needs change (keys of maps).
             // 3. If keys same, update text of existing labels.
             // 4. If keys diff, rebuild.
             
             updateTableContent();
        }
    }

    private boolean shouldShow() {
        // Only show when:
        // 1. Game is paused
        // 2. In multiplayer
        // 3. Any player has plans (checked in calculateRequiredResources)
        return state.isPaused() && net.active();
    }

    private boolean calculateRequiredResources() {
        // We need to return true if values changed.
        // But for structure we need to check if keys changed.
        // Let's just recalculate always and use the updateTableContent to diff.
        
        playerRequirements.clear();
        totalRequirements.clear();
        
        boolean hasAnyPlan = false;

        // Iterate through all players
        for (Player p : Groups.player) {
            if (p.unit() != null && p.unit().plans != null && !p.unit().plans.isEmpty()) {
                ObjectMap<Item, Integer> pReq = new ObjectMap<>();
                boolean pHasPlans = false;

                for (BuildPlan plan : p.unit().plans) {
                    if (!plan.breaking && plan.block != null) {
                        ItemStack[] requirements = plan.block.requirements;
                        if (requirements != null) {
                            for (ItemStack stack : requirements) {
                                pHasPlans = true;
                                int pCurrent = pReq.get(stack.item, 0);
                                pReq.put(stack.item, pCurrent + stack.amount);

                                int tCurrent = totalRequirements.get(stack.item, 0);
                                totalRequirements.put(stack.item, tCurrent + stack.amount);
                            }
                        }
                    }
                }
                
                if (pHasPlans) {
                    playerRequirements.put(p, pReq);
                    hasAnyPlan = true;
                }
            }
        }
        return hasAnyPlan;
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

    private void updateTableContent() {
        getCoreResources();
        
        // Simply clearing and rebuilding every frame Kills drag because the element under mouse is destroyed and recreated.
        // Input system loses focus.
        
        // Strategy: 
        // 1. If structure matches exactly (same players, same items), update labels.
        // 2. Else, rebuild.
        
        // To implement this simply without complex diffing code:
        // We can just rebuild every X frames, OR checking if we are dragging?
        // No.
        
        // Let's Try: Snapshot the last state.
        // Or easier:
        // Identify if the layout is stable.
        
        // Actually, the easiest fix for "Drag doesn't work" when content updates is to put the drag listener on a Container that does NOT get cleared.
        // The `table` variable IS the container.
        // But `table.clear()` removes children.
        // Does it remove listeners? No.
        // BUT if the drag behavior depends on hitting a child (unlikely for Table, unless strict hit test), 
        // or if layout rebuild moves the table?
        
        // PROPER FIX:
        // Create a parent 'container' table that has the drag listener.
        // Create a child 'content' table inside the container.
        // Only clear the 'content' table.
        // The container stays stable.
        
        // BUT, we already added listener to `table`.
        // `table.clear()` preserves listeners.
        
        // Maybe the problem is `clampToScreen` calling `setPosition` which interferes with `moveBy` during the same frame sequence?
        // Or `pack()` resetting size?
        
        // Let's do the Container approach. It is robust.
        rebuildTable(); 
    }
    
    private void rebuildTable() {
        contentTable.clear(); // Only clear content, not the main table with listener
        
        // Drag hint top
        contentTable.add("(Drag to move)").color(Color.gray).padBottom(4f).get().setFontScale(0.75f);
        contentTable.row();

        if (playerRequirements.isEmpty()) {
            contentTable.add("No active plans").color(Color.lightGray).pad(10f);
        } else {
            // 1. Per Player Sections
            for (ObjectMap.Entry<Player, ObjectMap<Item, Integer>> entry : playerRequirements) {
                Player p = entry.key;
                ObjectMap<Item, Integer> reqs = entry.value;

                // Player Name Header
                contentTable.add(p.name).color(p.color).left().padTop(6f).row();
                contentTable.image().color(p.color).fillX().height(2f).padBottom(4f).row();

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
                contentTable.add(grid).left().row();
            }

            // Separator
            contentTable.image().color(Color.gray).fillX().height(2f).pad(6f).row();

            // 2. Footer: Total / Available
            contentTable.add("Total Consumption / Available").color(Color.goldenrod).padBottom(4f).row();
            
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
            contentTable.add(footerGrid).left().row();
        }
        
        // Pack content table, then pack main table
        contentTable.pack();
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
