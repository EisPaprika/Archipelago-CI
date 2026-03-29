package com.alkia.archipelago.jade;

import com.alkia.archipelago.config.ModConfig;
import com.alkia.archipelago.util.KeyUtil;
import com.alkia.archipelago.util.RenderFlags;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.ui.Element;
import snownee.jade.overlay.DisplayHelper;


// Class basically exists to render the preview tooltips inside of Jade UIs with the correct scales/coords
public class TooltipComponentElement extends Element {

    private final ClientTooltipComponent component;

    public TooltipComponentElement(TooltipComponent component) {
        this.component = ClientTooltipComponent.create(component);
    }

    @Override
    public Vec2 getSize() {
        Font font = DisplayHelper.font();
        float w = component.getWidth(font);
        float h = component.getHeight();

        if (h == 0 && w == 0) {
            boolean isZoomed = KeyUtil.isModifierKeyDown(ModConfig.zoomKey);
            h = isZoomed ? 128 : 64;
            w = isZoomed ? 128 : 64;
        }
        
        return new Vec2(w, h);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        Font font = DisplayHelper.font();
        
        float finalX = x;
        float finalY = y;

        // Shifts the coords so its centered within Jade's box.
        if (component.getHeight() == 0 && component.getWidth(font) == 0) {
            boolean isZoomed = KeyUtil.isModifierKeyDown(ModConfig.zoomKey);
            float modelWidth = isZoomed ? 128 : 64;
            float availableWidth = maxX - x;
            if (availableWidth > modelWidth) {
                finalX += (availableWidth - modelWidth) / 2f;
            }
            finalX += isZoomed ? 145 : 85;
            finalY += isZoomed ? 64 : 32;
        }
        
        try {
            RenderFlags.isRenderingJade = true;
            component.renderImage(font, (int) finalX, (int) finalY, guiGraphics);
        } finally {
            RenderFlags.isRenderingJade = false;
        }
    }
}
