package com.alkia.archipelago.mixin;

import com.alkia.archipelago.tooltip.ArmorCosmeticTooltipComponent;
import com.alkia.archipelago.tooltip.ClientArmorCosmeticTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import com.alkia.archipelago.tooltip.ClientPokemonSkinTooltipComponent;
import com.alkia.archipelago.tooltip.PokemonSkinTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipComponentMixin {

    @Inject(method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;", at = @At("HEAD"), cancellable = true)
    private static void onCreate(TooltipComponent component, CallbackInfoReturnable<ClientTooltipComponent> cir) {
        if (component instanceof PokemonSkinTooltipComponent pokemonSkinComponent) {
            cir.setReturnValue(new ClientPokemonSkinTooltipComponent(pokemonSkinComponent));
        } else if (component instanceof ArmorCosmeticTooltipComponent armorComponent) {
            cir.setReturnValue(new ClientArmorCosmeticTooltipComponent(armorComponent));
        }
    }
}
