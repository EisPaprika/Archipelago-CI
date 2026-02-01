package com.alkia.archipelago.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import java.util.List;

public record PokemonSkinTooltipComponent(List <String> pokemonIds, List<String> aspects, List<String> whitelistAspects) implements TooltipComponent {
}
