package com.alkia.archipelago;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.world.item.ItemStack;

public interface ItemFrameCache {
    PokemonEntity archipelago$getCachedEntity();
    void archipelago$setCachedEntity(PokemonEntity entity);
    String archipelago$getActivePose();
    void archipelago$setActivePose(String pose);
    // Note block stuffs
    int archipelago$getCachedStateHash();
    void archipelago$setCachedStateHash(int hash);
}
