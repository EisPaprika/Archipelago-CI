package com.alkia.archipelago.custombuttons;

import com.alkia.archipelago.config.ModConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CustomButtonEditScreen extends Screen {
    private final Screen parent;
    private final Integer editIndex;

    private EditBox nameField;
    private EditBox commandField;
    private EditBox iconItemIdField;

    public CustomButtonEditScreen(Screen parent, Integer editIndex) {
        super(Component.literal(editIndex == null ? "Add Custom Button" : "Edit Custom Button"));
        this.parent = parent;
        this.editIndex = editIndex;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 40;

        CustomCommandButton initial = (editIndex == null)
                ? new CustomCommandButton(true, "Custom", "", "minecraft:stone")
                : ModConfig.customButtons.get(editIndex);

        this.nameField = new EditBox(this.font, centerX - 100, y, 200, 20, Component.literal("Name"));
        this.nameField.setValue(initial.name == null ? "" : initial.name);
        this.addRenderableWidget(this.nameField);

        y += 38;
        this.commandField = new EditBox(this.font, centerX - 100, y, 200, 20, Component.literal("Command"));
        this.commandField.setValue(initial.command == null ? "" : initial.command);
        this.commandField.setMaxLength(2048);
        this.addRenderableWidget(this.commandField);

        y += 38;
        this.iconItemIdField = new EditBox(this.font, centerX - 100, y, 200, 20, Component.literal("Icon Item ID"));
        this.iconItemIdField.setValue(initial.iconItemId == null ? "minecraft:stone" : initial.iconItemId);
        this.iconItemIdField.setMaxLength(2048);
        this.addRenderableWidget(this.iconItemIdField);

        y += 36;
        this.addRenderableWidget(Button.builder(Component.literal("Save"), btn -> {
            CustomCommandButton out = new CustomCommandButton();
            out.name = this.nameField.getValue();
            out.command = CustomButtonUtil.normalizeCommand(this.commandField.getValue());
            out.iconItemId = this.iconItemIdField.getValue().trim();

            if (editIndex == null) {
                ModConfig.customButtons.add(out);
                String newId = "custom_" + (ModConfig.customButtons.size() - 1);
                if (!ModConfig.buttonOrder.contains(newId)) {
                    ModConfig.buttonOrder.add(newId);
                }
            } else {
                out.enabled = ModConfig.customButtons.get(editIndex).enabled;
                ModConfig.customButtons.set(editIndex, out);
            }

            ModConfig.saveConfig();
            if (this.minecraft != null) this.minecraft.setScreen(parent);
        }).bounds(centerX - 100, y, 98, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), btn -> {
            if (this.minecraft != null) this.minecraft.setScreen(parent);
        }).bounds(centerX + 2, y, 98, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawString(this.font, "Name:", this.width / 2 - 100, 28, 0xFFFFFF);
        graphics.drawString(this.font, "Command:", this.width / 2 - 100, 66, 0xFFFFFF);
        graphics.drawString(this.font, "Icon Item ID:", this.width / 2 - 100, 104, 0xFFFFFF);

        CustomButtonUtil.drawIcon(graphics, iconItemIdField.getValue(), this.width / 2 - 120, 118);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(parent);
    }
}
