package com.alkia.archipelago.custombuttons;

import com.alkia.archipelago.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.List;

public class RearrangeButtonsScreen extends Screen {
    private final Screen parent;
    private ButtonListWidget list;
    public RearrangeButtonsScreen(Screen parent) {
        super(Component.literal("Rearrange Buttons"));
        this.parent = parent;

        for (String id : ModConfig.DEFAULT_BUTTON_ORDER) {
            if (!ModConfig.buttonOrder.contains(id)) {
                ModConfig.buttonOrder.add(id);
            }
        }


        for (int i = 0; i < ModConfig.customButtons.size(); i++) {
            String id =  "custom_"  + i;
            if (!ModConfig.buttonOrder.contains(id)) {
                ModConfig.buttonOrder.add(id);
            }
        }
    }

    private boolean isButtonEnabled(String id) {
        if (id.startsWith("custom_")) {
            try {
                int index = Integer.parseInt(id.substring(7));
                return index < ModConfig.customButtons.size() && ModConfig.customButtons.get(index).enabled;
            } catch (Exception e) {return false; }
        }

        return switch (id) {
            case "daycare" -> ModConfig.enableDaycareButton;
            case "shop" -> ModConfig.enableBalanceButton;
            case "gts" -> ModConfig.enableGtsButton;
            case "pc" -> ModConfig.enablePCButton;
            case "pokebuilder" -> ModConfig.enablePokebuilderButton;
            case "realms" -> ModConfig.enableRealmsButton;
            case "wondertrade" -> ModConfig.enableWondertradeButton;
            case "stonecutter" -> ModConfig.enableStonecutterButton;
            case "anvil" -> ModConfig.enableAnvilButton;
            case "balloons" -> ModConfig.enableBalloonsButton;
            case "vehicle" -> ModConfig.enableVehiclesButton;
            case "previewer" -> ModConfig.enablePreviewerButton;
            default -> false;
        };
    }

    private String getButtonName(String id) {
        if (id.startsWith("custom_")) {
            int index = Integer.parseInt(id.substring(7));
            return ModConfig.customButtons.get(index).name;
        }
        return id.substring(0, 1).toUpperCase() + id.substring(1);
    }

    @Override
    protected void init() {
        this.list = new ButtonListWidget(this.minecraft, this.width, this.height - 64, 32, 22);
        this.addRenderableWidget(this.list);

        List<String> order = ModConfig.buttonOrder;
        List<String> activeOrder = order.stream().filter(this::isButtonEnabled).toList();

        for (int i = 0; i < activeOrder.size(); i++) {
            String id = activeOrder.get(i);
            this.list.addButton(id, i, activeOrder);
        }

        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.minecraft.setScreen(parent))
                .bounds(this.width / 2 - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }

    private class ButtonListWidget extends ContainerObjectSelectionList<ButtonListWidget.Entry> {
        public ButtonListWidget(Minecraft client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, itemHeight);
        }

        public void addButton(String id, int displayIndex, List<String> activeOrder) {
            this.addEntry(new Entry(id, displayIndex, activeOrder));
        }

        private class Entry extends ContainerObjectSelectionList.Entry<Entry> {
            private final Button mainButton;
            private final Button upButton;
            private final Button downButton;
            private final List<Button> children = new java.util.ArrayList<>();

            public Entry(String id, int displayIndex, List<String> activeOrder) {
                this.mainButton = Button.builder(Component.literal(getButtonName(id)), b -> {})
                        .bounds(0, 0, 120, 20).build();

                this.upButton = Button.builder(Component.literal("↑"), b -> {
                    int fullIdx = ModConfig.buttonOrder.indexOf(id);
                    String prevId = activeOrder.get(displayIndex - 1);
                    move(fullIdx, ModConfig.buttonOrder.indexOf(prevId) - fullIdx);
                }).bounds(0, 0, 20, 20).build();

                this.downButton = Button.builder(Component.literal("↓"), b -> {
                    int fullIdx = ModConfig.buttonOrder.indexOf(id);
                    String nextId = activeOrder.get(displayIndex + 1);
                    move(fullIdx, ModConfig.buttonOrder.indexOf(nextId) - fullIdx);
                }).bounds(0, 0, 20, 20).build();

                if (displayIndex == 0) upButton.active = false;
                if (displayIndex == activeOrder.size() - 1) downButton.active = false;

                children.add(mainButton);
                children.add(upButton);
                children.add(downButton);
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                mainButton.setX(x + entryWidth / 2 - 80);
                mainButton.setY(y);
                upButton.setX(x + entryWidth / 2 + 45);
                upButton.setY(y);
                downButton.setX(x + entryWidth / 2 + 70);
                downButton.setY(y);

                mainButton.render(guiGraphics, mouseX, mouseY, tickDelta);
                upButton.render(guiGraphics, mouseX, mouseY, tickDelta);
                downButton.render(guiGraphics, mouseX, mouseY, tickDelta);
            }

            @Override
            public List<? extends GuiEventListener> children() { return children; }
            @Override
            public List<? extends NarratableEntry> narratables() { return children; }
        }
    }

    private void move(int index, int delta) {
        String item = ModConfig.buttonOrder.remove(index);
        ModConfig.buttonOrder.add(index + delta, item);
        ModConfig.saveConfig();
        this.rebuildWidgets();
    }
}

