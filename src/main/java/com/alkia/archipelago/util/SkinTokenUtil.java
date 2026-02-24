package com.alkia.archipelago.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.*;

public class SkinTokenUtil {
    public static final Set<String> customNicknames = new HashSet<>(List.of("softwaddle"));
    public static Optional<CompoundTag> getSkinTokenTag(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return Optional.empty();

        CompoundTag nbt = customData.copyTag();

        if (nbt.contains("islander:skin_token", Tag.TAG_COMPOUND)) {
            return Optional.of(nbt.getCompound("islander:skin_token"));
        }


        if (nbt.contains("$polymer:stack", Tag.TAG_COMPOUND)) {
            CompoundTag polymer = nbt.getCompound("$polymer:stack");
            if (polymer.contains("components", Tag.TAG_COMPOUND)) {
                CompoundTag components = polymer.getCompound("components");
                if (components.contains("islander:skin_token", Tag.TAG_COMPOUND)) {
                    return Optional.of(components.getCompound("islander:skin_token"));
                }
            }
        }

        return Optional.empty();
    }
}
