package com.alkia.archipelago.util;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;


// Moved stuff into here because it was bothering me in ModConfig.
public class KeyUtil {
    // Helper for checking if keys are pressed. Used for skin token shenanigans.
    private static final Map<Integer, Boolean> KEY_STATES = new HashMap<>();

    private static boolean previewToggled = false;
    private static boolean lastPreviewToggled = false;

    public static boolean isPreviewActive() {
        boolean previewing = isModifierKeyDown(com.alkia.archipelago.config.ModConfig.previewKey);

        if (com.alkia.archipelago.config.ModConfig.tapPreview) {
            if (previewing && !lastPreviewToggled) {
                previewToggled = !previewToggled;
            }
            lastPreviewToggled = previewing;
            return previewToggled;
        } else {
            previewToggled = false;
            lastPreviewToggled = previewing;
            return previewing;
        }
    }

    public static boolean wasModifierKeyPressed(ModifierKeyCode modKey) {
        if (modKey.getKeyCode() == InputConstants.UNKNOWN) return false;

        int keyHash = modKey.hashCode();
        boolean isDown = isModifierKeyDown(modKey);
        boolean previouslyDown = KEY_STATES.getOrDefault(keyHash, false);
        KEY_STATES.put(keyHash, isDown);

        return isDown && !previouslyDown;
    }

    public static boolean isModifierKeyDown(ModifierKeyCode modKey) {
        if (modKey.getKeyCode() == InputConstants.UNKNOWN) return false;

        long window = Minecraft.getInstance().getWindow().getWindow();
        int keyCode = modKey.getKeyCode().getValue();
        boolean keyHeld;
        // Fix for using mouse buttons, didn't work before since I changed to only check for keyboard keys
        if (modKey.getKeyCode().getType() == InputConstants.Type.MOUSE) {
            keyHeld = GLFW.glfwGetMouseButton(window, keyCode) == GLFW.GLFW_PRESS;
        } else {
            keyHeld = InputConstants.isKeyDown(window, keyCode);
        }

        Modifier modifier = modKey.getModifier();
        boolean modifiersMatch = true;
        if (modifier.hasShift()) modifiersMatch &= Screen.hasShiftDown();
        if (modifier.hasControl()) modifiersMatch &= Screen.hasControlDown();
        if (modifier.hasAlt()) modifiersMatch &= Screen.hasAltDown();

        return keyHeld && modifiersMatch;
    }
}
