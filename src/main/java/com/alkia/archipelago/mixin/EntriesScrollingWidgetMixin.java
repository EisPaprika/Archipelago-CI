package com.alkia.archipelago.mixin;
import com.alkia.archipelago.config.ModConfig;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexEntry;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants;
import com.cobblemon.mod.common.client.gui.pokedex.widgets.EntriesScrollingWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(value = EntriesScrollingWidget.PokemonScrollSlotRow.class)
public abstract class EntriesScrollingWidgetMixin {
    @Shadow(remap = false)
    @Final
    private List<PokedexEntry> dexDataList;
    @Shadow(remap = false)
    @Final
    private List<PokedexEntryProgress> discoveryLevelList;
    @Shadow(remap = false)
    private int x;

    @Unique
    private static final ResourceLocation ARCHIPELAGO_SHINY_ICON = ResourceLocation.fromNamespaceAndPath("archipelago", "textures/gui/sprites/shiny_icon.png");

    @Inject(
            method = "render",
            at = @At("RETURN")
    )

    private void onRender(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        float scale = PokedexGUIConstants.SCALE;

        for (int i = 0; i < dexDataList.size(); i++) {
            PokedexEntry dexData = dexDataList.get(i);
            PokedexEntryProgress discoveryLevel = discoveryLevelList.get(i);

            if (discoveryLevel == PokedexEntryProgress.CAUGHT) {
                
                // Attempt to only display shiny mark on pokemon that have specifically been caught shiny. Sadly, doesn't work :( Still displays icon if only scanned with dex
                var record = CobblemonClient.INSTANCE.getClientPokedexData().getSpeciesRecord(dexData.getSpeciesId());
                boolean hasCaughtShiny = record != null && record.hasAspect("shiny");

                if (hasCaughtShiny) {
                    if (ModConfig.enableShinyIcon) {
                        int startPosX = this.x + ((PokedexGUIConstants.SCROLL_SLOT_SPACING + PokedexGUIConstants.SCROLL_SLOT_SIZE) * i);
                        int startPosY = y + PokedexGUIConstants.SCROLL_SLOT_SPACING + 1;

                        context.pose().pushPose();
                        context.pose().translate(startPosX + 18, startPosY + 18, 200);
                        context.pose().scale(scale, scale, 1.0f);

                        context.blit(ARCHIPELAGO_SHINY_ICON, 0, 0, 0, 0, 11, 11, 11, 11);

                        context.pose().popPose();
                    }
                }
            }
        }
    }
}
