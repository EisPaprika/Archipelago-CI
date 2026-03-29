package com.alkia.archipelago.mixin;

import com.alkia.archipelago.config.ModConfig;
import com.alkia.archipelago.tooltip.ArmorCosmeticTooltipComponent;
import com.alkia.archipelago.tooltip.PokemonSkinTooltipComponent;
import com.alkia.archipelago.util.KeyUtil;
import com.alkia.archipelago.util.PokemonUtil;
import com.alkia.archipelago.util.SkinTokenUtil;
import com.alkia.archipelago.util.PolymerItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void addShiftPrompt(Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        boolean isPokemonSkin = SkinTokenUtil.getSkinTokenTag(stack).isPresent();
        boolean isArmorCosmetic = false;

        String polymerId = PolymerItemUtil.getPolymerId(stack);
        if (polymerId != null && !getArmorSlot(polymerId).isEmpty()) {
            isArmorCosmetic = true;
        }

        if (isPokemonSkin || isArmorCosmetic) {
            List<Component> tooltip = cir.getReturnValue();
            String previewKeyName = ModConfig.previewKey.getLocalizedName().getString();

            String pressOrHold = ModConfig.tapPreview ? "Press " : "Hold ";
            tooltip.add(Component.literal(pressOrHold + previewKeyName + " for Preview").withStyle(ChatFormatting.AQUA));

            if (KeyUtil.isPreviewActive()) {
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
                    String zoomKeyName = ModConfig.zoomKey.getLocalizedName().getString();
                    String megaKeyName = ModConfig.megaKey.getLocalizedName().getString();
                    tooltip.add(Component.literal("Hold " + megaKeyName + " for Full Set").withStyle(ChatFormatting.AQUA));
                    tooltip.add(Component.literal("Hold " + zoomKeyName + " to Zoom").withStyle(ChatFormatting.AQUA));
                }
            }
        }

        SkinTokenUtil.getSkinTokenTag(stack).ifPresent(tag -> {
            List<Component> tooltip = cir.getReturnValue();
            if (ModConfig.enabletokensintooltips) {
                for (String skinId : PokemonUtil.getNormalAspectsFromToken(tag)) {
                    tooltip.add(Component.literal("Skin ID: " + skinId).withStyle(ChatFormatting.GOLD));
                }
            }
        });
    }

    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void injectPokemonSkinTooltip(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        if (!KeyUtil.isPreviewActive()) return;

        SkinTokenUtil.getSkinTokenTag((ItemStack) (Object) this)
                .flatMap(PokemonUtil::createPokemonSkinComponent)
                .ifPresent(component -> cir.setReturnValue(Optional.of(component)));
    }


    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void injectArmorCosmeticTooltip(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        if (!KeyUtil.isPreviewActive()) return;

        ItemStack stack = (ItemStack) (Object) this;
        String polymerId = PolymerItemUtil.getPolymerId(stack);

        if (polymerId != null) {
            String slot = getArmorSlot(polymerId);

            String baseId = polymerId.toLowerCase();
            if (baseId.contains(":")) baseId = baseId.substring(baseId.indexOf(':') + 1);

            String setNameStr = baseId;
            boolean firstpassed = false;

            if (setNameStr.endsWith("_helmet")) {
                setNameStr = setNameStr.substring(0, setNameStr.length() - 7);
                firstpassed = true;
            } else if (setNameStr.endsWith("_chestplate")) {
                setNameStr = setNameStr.substring(0, setNameStr.length() - 11);
                firstpassed = true;
            } else if (setNameStr.endsWith("_leggings")) {
                setNameStr = setNameStr.substring(0, setNameStr.length() - 9);
                firstpassed = true;
            } else if (setNameStr.endsWith("_boots")) {
                setNameStr = setNameStr.substring(0, setNameStr.length() - 6);
                firstpassed = true;
            }
            if (!firstpassed) {
                // fallback for sets like rainydays because it has _bucket_ inside of its ID for some reason
                int underscore = setNameStr.indexOf('_');
                if (underscore > 0) setNameStr = setNameStr.substring(0, underscore);
            }

            Optional<String> setName = Optional.of(setNameStr);

            if (!slot.isEmpty()) {
                cir.setReturnValue(Optional.of(new ArmorCosmeticTooltipComponent(polymerId, slot, setName)));
            }
        }
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
