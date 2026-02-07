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
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.gen.Tex;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;

import static mindustry.Vars.*;

public class ResourcePreviewUI {
    private Table container;
    private Table table;
    private ObjectMap<Item, Integer> coreResources = new ObjectMap<>();
    private boolean visible = false;

    public ResourcePreviewUI() {
        build();
    }

    private void build() {
        container = new Table();
        table = new Table();
        
        container.setBackground(Styles.none); // Initial no border
        container.margin(2f); // Border thickness
        container.touchable = Touchable.enabled;
        
        table.setBackground(Styles.black6);
        table.margin(8f);
        table.touchable = Touchable.enabled;

        // Add inner table to container
        container.add(table);

        // Drag Listener on the container
        container.addListener(new InputListener() {
            float lastX, lastY;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, arc.input.KeyCode button) {
                lastX = event.stageX;
                lastY = event.stageY;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                float dx = event.stageX - lastX;
                float dy = event.stageY - lastY;
                
                float curX = Core.settings.getFloat("multiplayerpause-ui-x", Core.graphics.getWidth() - 4f);
                float curY = Core.settings.getFloat("multiplayerpause-ui-y", Core.graphics.getHeight() - 180f);
                
                Core.settings.put("multiplayerpause-ui-x", curX + dx);
                Core.settings.put("multiplayerpause-ui-y", curY + dy);
                Core.settings.put("multiplayerpause-ui-custom", true);
                
                updatePosition();
                
                lastX = event.stageX;
                lastY = event.stageY;
            }
        });

        // Hover effect state
        final boolean[] hovered = {false};
        container.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, arc.scene.Element fromActor) {
                if(pointer == -1) hovered[0] = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, arc.scene.Element toActor) {
                if(pointer == -1) hovered[0] = false;
            }
        });

        // Hover effect update
        container.update(() -> {
            if (hovered[0]) {
                container.setBackground(Tex.whiteui);
                container.color.set(Color.sky); // Border color
            } else {
                container.setBackground(Styles.none);
                container.color.set(Color.white);
            }
        });

        // Add to HUD
        ui.hudGroup.addChild(container);
    }

    public void update() {
        // Check if should show
        boolean shouldShow = shouldShow();
        
        if (shouldShow != visible) {
            visible = shouldShow;
            container.visible = visible;
        }

        if (!visible) return;

        // Get core resources once
        getCoreResources();

        // Rebuild table content
        boolean hasData = rebuildTable();
        
        // Update visibility
        if (hasData != container.visible) {
            container.visible = hasData;
        }
        
        // Always update position to honor the anchor (especially when size changes)
        if (container.visible) updatePosition();
    }

    private boolean shouldShow() {
        // Only show when game is paused
        if (!state.isPaused()) return false;
        
        // Show if any player has build plans
        for (Player p : Groups.player) {
            if (p.unit() != null && p.unit().plans != null && p.unit().plans.size > 0) return true;
        }
        
        return false;
    }

    private void getCoreResources() {
        coreResources.clear();

        // Use player's team core (primary player)
        if (player == null || player.team() == null) return;

        Building core = player.team().core();
        if (core != null && core.items != null) {
            for (Item item : content.items()) {
                int amount = core.items.get(item);
                if (amount > 0) {
                    coreResources.put(item, amount);
                }
            }
        }
    }

    private boolean rebuildTable() {
        table.clear();
        
        boolean any = false;
        for (Player p : Groups.player) {
            if (p.unit() == null || p.unit().plans == null || p.unit().plans.isEmpty()) continue;

            any = true;
            
            // Player name row
            table.add(p.name).color(p.color).left().row();
            
            Table resTable = new Table();
            ObjectMap<Item, Integer> reqs = new ObjectMap<>();
            
            for (BuildPlan plan : p.unit().plans) {
                if (!plan.breaking && plan.block != null && plan.block.requirements != null) {
                    for (ItemStack stack : plan.block.requirements) {
                        reqs.put(stack.item, reqs.get(stack.item, 0) + stack.amount);
                    }
                }
            }

            if (reqs.size == 0) {
                table.add("  No construction").color(Color.lightGray).left().row();
            } else {
                int i = 0;
                for (ObjectMap.Entry<Item, Integer> entry : reqs) {
                    Item item = entry.key;
                    int required = entry.value;
                    int available = coreResources.get(item, 0);

                    Color textColor = available >= required ? Color.white : Color.red;

                    resTable.image(item.uiIcon).size(24f).padLeft(10f).padRight(4f);
                    resTable.add("-" + required).color(textColor).fontScale(0.8f);
                    
                    if (++i % 3 == 0) resTable.row();
                }
                table.add(resTable).left().padBottom(4f).row();
            }
            
            table.image().color(Color.gray).fillX().height(1f).pad(2f).row();
        }

        if (any) table.pack();
        return any;
    }

    private void updatePosition() {
        // Default values match the initial "below minimap" position
        float defaultX = Core.graphics.getWidth() - 4f;
        float defaultY = Core.graphics.getHeight() - 180f;
        
        float x = Core.settings.getFloat("multiplayerpause-ui-x", defaultX);
        float y = Core.settings.getFloat("multiplayerpause-ui-y", defaultY);
        
        // Anchoring to Top-Right means the table grows DOWN and LEFT from (x, y)
        container.setPosition(x, y, Align.topRight);
    }

    public void dispose() {
        if (container != null) {
            container.remove();
        }
    }
}
