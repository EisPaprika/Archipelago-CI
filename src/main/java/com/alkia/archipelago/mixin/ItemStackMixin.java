package com.alkia.archipelago.mixin;

import com.alkia.archipelago.config.ModConfig;
import com.alkia.archipelago.tooltip.ArmorCosmeticTooltipComponent;
import com.alkia.archipelago.util.KeyUtil;
import com.alkia.archipelago.util.PolymerItemUtil;
import net.minecraft.ChatFormatting;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import com.alkia.archipelago.tooltip.PokemonSkinTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void addShiftPrompt(Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
        boolean isPokemonSkin = getSkinTokenTag().isPresent();
        boolean isArmorCosmetic = false;

        ItemStack stack = (ItemStack) (Object) this;
        String polymerId = PolymerItemUtil.getPolymerId(stack);
        if (polymerId != null && !getArmorSlot(polymerId).isEmpty()) {
            isArmorCosmetic = true;
        }

        if (isPokemonSkin || isArmorCosmetic) {
            List<Component> tooltip = cir.getReturnValue();
            String previewKeyName = ModConfig.previewKey.getLocalizedName().getString();
            tooltip.add(Component.literal("Hold " + previewKeyName + " for Preview").withStyle(ChatFormatting.AQUA));

            if (KeyUtil.isModifierKeyDown(ModConfig.previewKey)) {
                if (isPokemonSkin) {
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
                } else {
                    String megaKeyName = ModConfig.megaKey.getLocalizedName().getString();
                    tooltip.add(Component.literal("Hold " + megaKeyName + " for Full Set").withStyle(ChatFormatting.AQUA));
                }
            }
        }

        getSkinTokenTag().ifPresent(tag -> {
            List<Component> tooltip = cir.getReturnValue();
            if (ModConfig.enabletokensintooltips) {
                if (tag.contains("aspects", Tag.TAG_LIST)) {
                    ListTag aspects = tag.getList("aspects", Tag.TAG_STRING);
                    for (int i = 0; i < aspects.size(); i++) {
                        String skinId = aspects.getString(i).replace('=', '-');
                        tooltip.add(Component.literal("Skin ID: " + skinId).withStyle(ChatFormatting.GOLD));
                    }
                }
            }
        });
    }

    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void injectPokemonSkinTooltip(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        if (!KeyUtil.isModifierKeyDown(ModConfig.previewKey)) return;

        getSkinTokenTag()
                .flatMap(this::createPokemonSkinComponent)
                .ifPresent(component -> cir.setReturnValue(Optional.of(component)));
    }

    private Optional<CompoundTag> getSkinTokenTag() {
        ItemStack stack = (ItemStack) (Object) this;
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return Optional.empty();

        CompoundTag nbt = customData.copyTag();
        if (nbt.contains("islander:skin_token", Tag.TAG_COMPOUND)) {
            return Optional.of(nbt.getCompound("islander:skin_token"));
        }

        if (nbt.contains("$polymer:stack", Tag.TAG_COMPOUND)) {
            CompoundTag components = nbt.getCompound("$polymer:stack").getCompound("components");
            if (components.contains("islander:skin_token", Tag.TAG_COMPOUND)) {
                return Optional.of(components.getCompound("islander:skin_token"));
            }
        }

        return Optional.empty();
    }

    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void injectArmorCosmeticTooltip(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        if (!KeyUtil.isModifierKeyDown(ModConfig.previewKey)) return;

        ItemStack stack = (ItemStack) (Object) this;
        String polymerId = PolymerItemUtil.getPolymerId(stack);

        if (polymerId != null) {
            String slot = getArmorSlot(polymerId);

            String baseId = polymerId.toLowerCase();
            if (baseId.contains(":")) baseId = baseId.substring(baseId.indexOf(':') + 1);

            String setNameStr = baseId;
            if (setNameStr.endsWith("_helmet")) setNameStr = setNameStr.substring(0, setNameStr.length() - 7);
            else if (setNameStr.endsWith("_chestplate"))
                setNameStr = setNameStr.substring(0, setNameStr.length() - 11);
            else if (setNameStr.endsWith("_leggings"))
                setNameStr = setNameStr.substring(0, setNameStr.length() - 9);
            else if (setNameStr.endsWith("_boots")) setNameStr = setNameStr.substring(0, setNameStr.length() - 6);

            Optional<String> setName = Optional.of(setNameStr);

            if (!slot.isEmpty()) {
                cir.setReturnValue(Optional.of(new ArmorCosmeticTooltipComponent(polymerId, slot, setName)));
            }
        }
    }

    private Optional<TooltipComponent> createPokemonSkinComponent(CompoundTag skinToken) {
        List<String> pokemonIds = new ArrayList<>();
        //Grabbing Pokemon Species
        if (skinToken.contains("whitelist", Tag.TAG_LIST)) {
            ListTag whitelist = skinToken.getList("whitelist", Tag.TAG_STRING);
            for (int i = 0; i < whitelist.size(); i++) {
                pokemonIds.add(whitelist.getString(i));
            }
        }

        //Grabbing Pokemon Form
        List<String> whitelistAspects = new ArrayList<>();
        if (skinToken.contains("whitelist_aspects", Tag.TAG_LIST)) {
            ListTag whitelistAspectsTag = skinToken.getList("whitelist_aspects", Tag.TAG_STRING);
            for (int i = 0; i < whitelistAspectsTag.size(); i++) {
                whitelistAspects.add(whitelistAspectsTag.getString(i));
            }
        }
        //Grabbing Pokemon Skins
        List<String> aspects = new ArrayList<>();
        if (skinToken.contains("aspects", Tag.TAG_LIST)) {
            ListTag aspectsTag = skinToken.getList("aspects", Tag.TAG_STRING);
            for (int i = 0; i < aspectsTag.size(); i++) {
                aspects.add(aspectsTag.getString(i).replace('=', '-'));
            }
        }

        if (!pokemonIds.isEmpty() && !aspects.isEmpty()) {
            return Optional.of(new PokemonSkinTooltipComponent(pokemonIds, aspects, whitelistAspects));
        }
        return Optional.empty();
    }

    //Getting armor slot, tried grabbing just ids that contain _helmet etc. But the rainy days goomy hat decided it felt quirky
    // and added _cap instead????????????????
    @Unique
    private String getArmorSlot(String polymerId) {
        if (polymerId == null) return "";

        for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            var match = PolymerItemUtil.findForSlot(polymerId, slot);
            if (match.isPresent()) {
                Item baseItem = match.get().baseItem();
                if (baseItem instanceof ArmorItem armor) {
                    if (armor.getEquipmentSlot() == slot) {
                        return slot.getName();
                    }
                    // Currently still flags back cosmetics. Don't like this, yet. Back cosmetics are not supported.
                } else if (baseItem == Items.POPPED_CHORUS_FRUIT) {
                    return "head";
                }
            }
        }


        // Just in case it's not picked up still for some reason, using the old way I did it
        String lowerId = polymerId.toLowerCase();
        if (lowerId.contains("helmet") || lowerId.contains("head") || (lowerId.contains("_hat"))) return "head";
        if (lowerId.contains("chestplate") || lowerId.contains("chest")) return "chest";
        if (lowerId.contains("leggings") || lowerId.contains("legs")) return "legs";
        if (lowerId.contains("boots") || lowerId.contains("feet")) return "feet";
        return "";

    }
}
