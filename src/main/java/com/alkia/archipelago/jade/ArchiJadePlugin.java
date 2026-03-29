package com.alkia.archipelago.jade;

import com.alkia.archipelago.config.ModConfig;
import com.alkia.archipelago.util.KeyUtil;
import com.alkia.archipelago.util.PokemonUtil;
import com.alkia.archipelago.util.SkinTokenUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

import java.util.Optional;

@WailaPlugin
public class ArchiJadePlugin implements IWailaPlugin {
    @Override
    // Jade Registration
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(SkinTokenJadeProvider.INSTANCE, ItemFrame.class);
        registration.registerEntityComponent(SkinTokenJadeProvider.INSTANCE, GlowItemFrame.class);
        registration.registerEntityComponent(SkinTokenJadeProvider.INSTANCE, ItemEntity.class);

        registration.registerEntityIcon(SkinTokenJadeProvider.INSTANCE, ItemFrame.class);
        registration.registerEntityIcon(SkinTokenJadeProvider.INSTANCE, GlowItemFrame.class);
        registration.registerEntityIcon(SkinTokenJadeProvider.INSTANCE, ItemEntity.class);
    }
}

enum SkinTokenJadeProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        // Get item stack
        ItemStack stack = getDisplayedItem(accessor);
        if (stack.isEmpty()) return;

        Optional<TooltipComponent> pokemonSkin = SkinTokenUtil.getSkinTokenTag(stack).flatMap(PokemonUtil::createPokemonSkinComponent);

        if (pokemonSkin.isPresent()) {
            // Replace title with item name
            tooltip.replace(JadeIds.CORE_OBJECT_NAME, stack.getHoverName());
            // Remove the "Item: " line from ItemFrameProvider
            tooltip.remove(JadeIds.MC_ITEM_FRAME);
            // Get aspects
            if (ModConfig.enabletokensintooltips && pokemonSkin.isPresent()) {
                SkinTokenUtil.getSkinTokenTag(stack).ifPresent(tag -> {
                    for (String skinId : PokemonUtil.getNormalAspectsFromToken(tag)) {
                        tooltip.add(Component.literal("Skin ID: " + skinId).withStyle(ChatFormatting.GOLD));
                    }
                });
            }
            // Basically, just the code for the strings contained within the Jade preview (for zooming n stuff)
            String previewKeyName = ModConfig.previewKey.getLocalizedName().getString();
            String pressOrHold = ModConfig.tapPreview ? "Press " : "Hold ";
            tooltip.add(Component.literal(pressOrHold + previewKeyName + " for Preview").withStyle(ChatFormatting.AQUA));

            if (KeyUtil.isPreviewActive()) {
                if (pokemonSkin.isPresent()) {
                    String zoomKeyName = ModConfig.zoomKey.getLocalizedName().getString();
                    String shinyKeyName = ModConfig.shinyKey.getLocalizedName().getString();
                    String megaKeyName = ModConfig.megaKey.getLocalizedName().getString();
                    String formKeyName = ModConfig.formKey.getLocalizedName().getString();
                    String evolutionKeyName = ModConfig.evolutionKey.getLocalizedName().getString();

                    tooltip.add(Component.literal("Hold " + zoomKeyName + " to Zoom").withStyle(ChatFormatting.AQUA));
                    tooltip.add(Component.literal("Hold " + shinyKeyName + " for Shiny").withStyle(ChatFormatting.AQUA));
                    tooltip.add(Component.literal("Press " + megaKeyName + " to Toggle Mega").withStyle(ChatFormatting.AQUA));
                    tooltip.add(Component.literal("Press " + formKeyName + " for Next Form").withStyle(ChatFormatting.AQUA));
                    if (!ModConfig.enableEvolutionScrollling)
                        tooltip.add(Component.literal("Press " + evolutionKeyName + " for Next Evolution").withStyle(ChatFormatting.AQUA));

                    ITooltip nested = IElementHelper.get().tooltip();
                    nested.add(new TooltipComponentElement(pokemonSkin.get()));
                    tooltip.add(IElementHelper.get().box(nested, BoxStyle.getNestedBox()));
                }
            }
        }
    }
    // I had to basically just take the icon that would normally display and override it to display the skin instead using one of
    // those pokemon itemstacks. It's probably not the best way of doing it. Originally the Tooltip UI used in inventories would just
    // Append to the Jade Interface, but that looked weird, and was moved to the preview button UI.
    @Override
    public IElement getIcon(EntityAccessor accessor, IPluginConfig config, IElement currentIcon) {
        if (!ModConfig.enableItemFrameSkins) return null;
        ItemStack stack = getDisplayedItem(accessor);
        if (stack.isEmpty()) return null;

        if (SkinTokenUtil.getSkinTokenTag(stack).isPresent()) {
            return IElementHelper.get().item(stack);
        }
        return null;
    }

    private ItemStack getDisplayedItem(EntityAccessor accessor) {
        Entity entity = accessor.getEntity();
        ItemStack stack = ItemStack.EMPTY;
        if (entity instanceof ItemFrame itemFrame) {
            stack = itemFrame.getItem();
        } else if (entity instanceof ItemEntity itemEntity) {
            stack = itemEntity.getItem();
        }

        if (stack.isEmpty()) return ItemStack.EMPTY;

        return SkinTokenUtil.getSkinTokenTag(stack)
                .map(PokemonUtil::getPokemonStackFromToken)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("archipelago", "skin_token");
    }
}
