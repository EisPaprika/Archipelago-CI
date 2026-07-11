package com.alkia.archipelago.mixin;


import com.alkia.archipelago.ItemFrameCache;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ItemFrame.class, net.minecraft.world.entity.decoration.GlowItemFrame.class})
public class ItemFrameMixin implements ItemFrameCache {
    @Unique
    private PokemonEntity cachedEntity;
    @Unique
    private String activePose = "";
    @Unique
    private int cachedStateHash = 0;
    @Unique
    private String activeAttackAnim = null;

    @Override
    public PokemonEntity archipelago$getCachedEntity() {
        return cachedEntity;
    }
    @Override
    public void archipelago$setCachedEntity(PokemonEntity entity) {
        this.cachedEntity = entity;
    }
    @Override
    public String archipelago$getActivePose() {
        return activePose;
    }
    @Override
    public void archipelago$setActivePose(String pose) {
        this.activePose = pose;
    }
    @Override
    public int archipelago$getCachedStateHash() {
        return cachedStateHash;
    }
    @Override
    public void archipelago$setCachedStateHash(int hash) {
        this.cachedStateHash = hash;
    }
    @Override
    public String archipelago$getActiveAttackAnim() {
        return activeAttackAnim;
    }
    @Override
    public void archipelago$setActiveAttackAnim(String anim) {this.activeAttackAnim = anim;}
}
