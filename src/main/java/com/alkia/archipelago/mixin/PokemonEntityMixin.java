package com.alkia.archipelago.mixin;

import com.alkia.archipelago.util.RenderFlags;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonEntity.class)
public abstract class PokemonEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (RenderFlags.previewEntities.contains(this)) {
            ci.cancel();
        }
    }
}
