package com.alkia.archipelago;

import com.alkia.archipelago.config.ModConfig;
import com.alkia.archipelago.util.KeyUtil;
import com.alkia.archipelago.util.PolymerItemUtil;
import com.alkia.archipelago.util.SkinResolverUtil;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


public class FabricModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModConfig.loadConfig();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (KeyUtil.wasModifierKeyPressed(com.alkia.archipelago.config.ModConfig.feedKey)) {
                if (client.player != null) {
                    client.player.connection.sendUnsignedCommand("feed");
                }
            }
            if (KeyUtil.wasModifierKeyPressed(com.alkia.archipelago.config.ModConfig.egginfo1Key)) {
                if (client.player != null) {
                    client.player.connection.sendUnsignedCommand("egginfo 1");
                }
            }
            if (KeyUtil.wasModifierKeyPressed(com.alkia.archipelago.config.ModConfig.egginfo2Key)) {
                if (client.player != null) {
                    client.player.connection.sendUnsignedCommand("egginfo 2");
                }
            }
            if (KeyUtil.wasModifierKeyPressed(com.alkia.archipelago.config.ModConfig.egginfo3Key)) {
                if (client.player != null) {
                    client.player.connection.sendUnsignedCommand("egginfo 3");
                }
            }
            if (KeyUtil.wasModifierKeyPressed(com.alkia.archipelago.config.ModConfig.egginfo4Key)) {
                if (client.player != null) {
                    client.player.connection.sendUnsignedCommand("egginfo 4");
                }
            }
            if (KeyUtil.wasModifierKeyPressed(com.alkia.archipelago.config.ModConfig.egginfo5Key)) {
                if (client.player != null) {
                    client.player.connection.sendUnsignedCommand("egginfo 5");
                }
            }
            if (KeyUtil.wasModifierKeyPressed(com.alkia.archipelago.config.ModConfig.egginfo6Key)) {
                if (client.player != null) {
                    client.player.connection.sendUnsignedCommand("egginfo 6");
                }
            }
        });
        // For whenever resource packs reload
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath("archipelago", "resource_invalidator");
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, ProfilerFiller preperationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return barrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
                    SkinResolverUtil.invalidate();
                    PolymerItemUtil.clearCache();
                }, gameExecutor);
            }
        });
    }
}


