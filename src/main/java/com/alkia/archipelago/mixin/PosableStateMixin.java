package com.alkia.archipelago.mixin;

import com.alkia.archipelago.util.ArchipelagoPosableState;
import com.alkia.archipelago.util.SkinTokenUtil;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.HashSet;
import java.util.Set;

import static net.minecraft.commands.arguments.EntityArgument.getEntity;


// Exists entirely because you can't change pose of ModelWidget by default
@Mixin(value = PosableState.class, remap = false)
public abstract class PosableStateMixin implements ArchipelagoPosableState {
    @Unique
    private String archipelago$forcedPose = null;
    @Shadow public abstract Entity getEntity();

    @Inject(method = "setPoseToFirstSuitable", at = @At("HEAD"), cancellable = true)
    private void onSetPoseToFirstSuitable(PoseType poseType, CallbackInfo ci) {
        if (archipelago$forcedPose != null) {
            ((PosableState) (Object) this).setPose(archipelago$forcedPose);
            ci.cancel();
        }
    }

    // Intercepting aspect updates to add skins based on nicknames for donors
    @Inject(method = "setCurrentAspects", at = @At("TAIL"))
    private void archipelago$applyNicknameAspect(Set<String> aspects, CallbackInfo ci) {
        try {
            Entity entity = getEntity();
            // Make sure it's a pokemon
            if (!(entity instanceof PokemonEntity pokemonEntity)) return;

            // Grab pokemon
            Pokemon pokemon = pokemonEntity.getPokemon();
            if(pokemon == null) return;

            // grab nickname
            MutableComponent nickname = pokemon.getNickname();
            if  (nickname == null) return;

            String name = nickname.getString().toLowerCase().trim();
            if (name.isEmpty() || !SkinTokenUtil.customNicknames.contains(name)) return;

            String aspect = "nickname-" + name;
            if (!aspects.contains(aspect)) {
                Set<String> clientaspects = new HashSet<>(aspects);
                clientaspects.add(aspect);
                ((PosableState) (Object) this).setCurrentAspects(clientaspects);
            }
        } catch (Throwable ignored) {}
    }

    @Override
    @Unique
    public void archipelago$setForcedPose(String pose) {
        this.archipelago$forcedPose = pose;
    }
}
