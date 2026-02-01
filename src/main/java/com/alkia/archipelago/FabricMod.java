package com.alkia.archipelago;


import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alkia.archipelago.config.ModConfig;

import static net.minecraft.commands.Commands.literal;

public class FabricMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("archipelago");

    @Override
    public void onInitialize() {
        ModConfig.loadConfig();
        LOGGER.info("Archipelago initialized! Enjoy Cobblemon Islands!");
    }
}