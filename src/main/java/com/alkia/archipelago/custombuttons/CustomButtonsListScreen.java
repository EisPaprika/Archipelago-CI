package com.alkia.archipelago.custombuttons;

import com.alkia.archipelago.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.List;

public class CustomButtonsListScreen extends Screen {
    private final Screen parent;
    private CustomButtonListWidget list;

    public CustomButtonsListScreen(Screen parent) {
        super(Component.literal("Custom Buttons"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int topY = 28;

        this.addRenderableWidget(Button.builder(Component.literal("Add"), button -> {
            this.minecraft.setScreen(new CustomButtonEditScreen(this, null));
        }).bounds(centerX - 100, topY, 98, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> {
            ModConfig.saveConfig();
            this.minecraft.setScreen(parent);
        }).bounds(centerX + 2, topY, 98, 20).build());

        this.list = new CustomButtonListWidget(this.minecraft, this.width, this.height - 64, 60, 24);
        this.addRenderableWidget(this.list);

        for (int i = 0; i < ModConfig.customButtons.size(); i++) {
            this.list.addCustomEntry(ModConfig.customButtons.get(i), i);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 14, 0xFFFFFF);
    }

    private class CustomButtonListWidget extends ContainerObjectSelectionList<CustomButtonListWidget.Entry> {
        public CustomButtonListWidget(Minecraft client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, itemHeight);
        }

        public void addCustomEntry(CustomCommandButton button, int index) {
            this.addEntry(new Entry(button, index));
        }

        private class Entry extends ContainerObjectSelectionList.Entry<Entry> {
            private final Button editButton;
            private final Button toggleButton;
            private final Button deleteButton;
            public Entry(CustomCommandButton button, int index) {
                String label = (button.enabled ? "" : "[OFF] ") + (button.name == null ? "Custom" : button.name);
                this.editButton = Button.builder(Component.literal(label), btn -> {
                    minecraft.setScreen(new CustomButtonEditScreen(CustomButtonsListScreen.this, index));
                }).bounds(0, 0, 100, 20).build();

                this.toggleButton = Button.builder(Component.literal(button.enabled ? "Enabled" : "Disabled"), btn -> {
                    button.enabled = !button.enabled;
                    ModConfig.saveConfig();
                    CustomButtonsListScreen.this.rebuildWidgets();
                }).bounds(0, 0, 78, 20).build();

                this.deleteButton = Button.builder(Component.literal("X"), btn -> {
                    ModConfig.customButtons.remove(index);
                    ModConfig.buttonOrder.remove("custom_" + index);
                    ModConfig.saveConfig();
                    CustomButtonsListScreen.this.rebuildWidgets();
                }).bounds(0, 0, 20, 20).build();
            }

            @Override
            public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float partialTick) {
                editButton.setX(x + entryWidth / 2 - 100);
                editButton.setY(y);
                toggleButton.setX(x + entryWidth / 2);
                toggleButton.setY(y);
                deleteButton.setX(x + entryWidth / 2 + 80);
                deleteButton.setY(y);

                editButton.render(graphics, mouseX, mouseY, partialTick);
                toggleButton.render(graphics, mouseX, mouseY, partialTick);
                deleteButton.render(graphics, mouseX, mouseY, partialTick);
            }

            @Override
            public List<? extends net.minecraft.client.gui.components.events.GuiEventListener> children() {
                return List.of(editButton, toggleButton, deleteButton);
            }

            @Override
            public List<? extends net.minecraft.client.gui.narration.NarratableEntry> narratables() {
                return List.of(editButton, toggleButton, deleteButton);
            }
        }
    }
}