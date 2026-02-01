package com.alkia.archipelago.custombuttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public class CustomButtonUtil {
    private CustomButtonUtil() {}

    public static String normalizeCommand(String raw) {
        if (raw == null) return "";
        String s = raw.trim();
        if (s.startsWith("/")) s = s.substring(1).trim();
        return s;
    }

    public static void drawIcon(GuiGraphics graphics, String iconId, int x, int y) {
        String trimmed = iconId.trim();
        if (trimmed.isEmpty()) {
            graphics.renderItem(new ItemStack(Items.BARRIER), x, y);
            return;
        }
        // NBT Support
        try {
            Minecraft client = Minecraft.getInstance();
            if (client.level != null) {
                var parser  = new ItemParser(client.level.registryAccess());
                var result =  parser.parse(new com.mojang.brigadier.StringReader(trimmed));
                ItemStack stack = new ItemStack(result.item(), 1);
                stack.applyComponents(result.components());

                if (!stack.isEmpty() && !stack.is(Items.AIR)) {
                    graphics.renderItem(stack, x, y);
                    return;
                }
            }

        } catch (Exception ignored) {

        }

        ResourceLocation id = ResourceLocation.tryParse(trimmed);
        if (id == null) {
            graphics.renderItem(new ItemStack(Items.BARRIER), x, y);
            return;
        }

        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(id));
        if (!stack.isEmpty() && !stack.is(Items.AIR)) {
            graphics.renderItem(stack, x, y);
        } else {
            graphics.blitSprite(id, x, y, 16, 16);
        }
    }

    public static void sendCommandIfPossible(String command) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        String normalized = normalizeCommand(command);
        if (normalized.isEmpty()) return;
        client.player.connection.sendUnsignedCommand(normalized);
    }

    public static Component tooltipOrDefault(String name, String command) {
        String n = (name == null) ? "" : name.trim();
        if (!n.isEmpty()) return Component.literal(n);
        String c = normalizeCommand(command);
        if (!c.isEmpty()) return Component.literal("/" + c);
        return Component.literal("Custom Button");
    }
}