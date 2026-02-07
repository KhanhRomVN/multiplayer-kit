package pauseMod;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Align;
import mindustry.content.Items;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;

import static mindustry.Vars.*;

public class ResourcePreviewUI {
    private Table table;
    private ObjectMap<Item, Integer> requiredResources = new ObjectMap<>();
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
    }

    public void update() {
        // Check if should show
        boolean shouldShow = shouldShow();
        
        if (shouldShow != visible) {
            visible = shouldShow;
            table.visible = visible;
        }

        if (!visible) return;

        // Update position based on settings
        updatePosition();

        // Calculate resources
        calculateRequiredResources();
        getCoreResources();

        // Rebuild table content
        rebuildTable();
    }

    private boolean shouldShow() {
        // Only show when:
        // 1. Game is paused
        // 2. Player exists and has a unit
        // 3. Unit has build plans
        // 4. In multiplayer
        return state.isPaused() 
            && player != null 
            && player.unit() != null 
            && player.unit().plans != null
            && player.unit().plans.size > 0
            && net.active();
    }

    private void calculateRequiredResources() {
        requiredResources.clear();

        if (player == null || player.unit() == null) return;

        // Iterate through all build plans
        for (BuildPlan plan : player.unit().plans) {
            if (!plan.breaking && plan.block != null) {
                // Get block requirements
                ItemStack[] requirements = plan.block.requirements;
                if (requirements != null) {
                    for (ItemStack stack : requirements) {
                        int current = requiredResources.get(stack.item, 0);
                        requiredResources.put(stack.item, current + stack.amount);
                    }
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
        
        // Title
        table.add("Build Preview Resources").color(Color.white).row();
        table.image().color(Color.gray).fillX().height(3f).pad(4f).row();

        // Display resources
        if (requiredResources.size == 0) {
            table.add("No resources required").color(Color.lightGray).row();
        } else {
            for (ObjectMap.Entry<Item, Integer> entry : requiredResources) {
                Item item = entry.key;
                int required = entry.value;
                int available = coreResources.get(item, 0);

                // Determine color based on availability
                Color textColor = available >= required ? Color.green : Color.red;

                Table row = new Table();
                
                // Item icon
                row.image(item.uiIcon).size(32f).padRight(8f);
                
                // Item name and amounts
                String text = item.localizedName + ": " + available + "/" + required;
                row.add(text).color(textColor).left();

                table.add(row).left().padBottom(4f).row();
            }
        }

        table.pack();
    }

    private void updatePosition() {
        int index = Core.settings.getInt("multiplayerpause-resourceui-position-idx", 1);
        
        float padding = 10f;
        
        switch (index) {
            case 0: // top-left
                table.setPosition(padding, Core.graphics.getHeight() - padding, Align.topLeft);
                break;
            case 1: // top-right
                table.setPosition(Core.graphics.getWidth() - padding, Core.graphics.getHeight() - padding, Align.topRight);
                break;
            case 2: // bottom-left
                table.setPosition(padding, padding, Align.bottomLeft);
                break;
            case 3: // bottom-right
                table.setPosition(Core.graphics.getWidth() - padding, padding, Align.bottomRight);
                break;
            default:
                table.setPosition(Core.graphics.getWidth() - padding, Core.graphics.getHeight() - padding, Align.topRight);
                break;
        }
    }

    public void dispose() {
        if (table != null) {
            table.remove();
        }
    }
}
