package com.alkia.archipelago.custombuttons;

public class CustomCommandButton {
    public boolean enabled = true;
    public String name = "Custom";
    public String command = "";
    public String iconItemId = "minecraft:stone";

    public CustomCommandButton() {}

    public CustomCommandButton(boolean enabled, String name, String command, String iconItemId) {
        this.enabled = enabled;
        this.name = name;
        this.command = command;
        this.iconItemId = iconItemId;
    }
}
