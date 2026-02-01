package com.alkia.archipelago.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.Optional;

public record ArmorCosmeticTooltipComponent(
        String polymerId,
        String slot,
        Optional<String> setName
) implements TooltipComponent {}
