package com.alkia.archipelago.mixin;

import com.alkia.archipelago.util.RenderFlags;
import com.alkia.archipelago.config.ModConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Unique
    private boolean archipelago$shouldSuppress(double x, double y, double z) {
        // Main thread suppression during render
        if (RenderFlags.isRenderingSkinPreview) return true;

        // Async ticker suppression
        if (Thread.currentThread().getName().startsWith("AsyncParticleTicker")) {
            // Previews are usually at 0,0,0
            if (Math.abs(x) < 50.0 && Math.abs(z) < 50.0) {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.player == null || mc.player.distanceToSqr(x, y, z) > 400.0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Inject(method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", at = @At("HEAD"), cancellable = true)
    private void onAddParticle(ParticleOptions particle, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci) {
        if (archipelago$shouldSuppress(x, y, z)) {
            ci.cancel();
        } else if (ModConfig.enableParticleDebug) {
            if (particle.getType().getClass().getName().contains("Snowstorm")) {
                System.out.println("[ARCHIPELAGO-DEBUG] Snowstorm particle added at " + x + ", " + y + ", " + z + " Thread: " + Thread.currentThread().getName());
            } else {
                System.out.println("[ARCHIPELAGO-DEBUG] Particle added while NOT in preview: " + particle.getType() + " at " + x + ", " + y + ", " + z + " Thread: " + Thread.currentThread().getName());
            }
        }
    }

    @Inject(method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZDDDDDD)V", at = @At("HEAD"), cancellable = true)
    private void onAddParticle(ParticleOptions particle, boolean force, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci) {
        if (archipelago$shouldSuppress(x, y, z)) {
            ci.cancel();
        } else if (ModConfig.enableParticleDebug) {
            if (particle.getType().getClass().getName().contains("Snowstorm")) {
                System.out.println("[ARCHIPELAGO-DEBUG] Snowstorm particle (forced) added at " + x + ", " + y + ", " + z + " Thread: " + Thread.currentThread().getName());
            } else {
                System.out.println("[ARCHIPELAGO-DEBUG] Particle added while NOT in preview: " + particle.getType() + " at " + x + ", " + y + ", " + z + " Thread: " + Thread.currentThread().getName());
            }
        }
    }

    @Inject(method = "addAlwaysVisibleParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", at = @At("HEAD"), cancellable = true)
    private void onAddAlwaysVisibleParticle(ParticleOptions particle, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci) {
        if (archipelago$shouldSuppress(x, y, z)) {
            ci.cancel();
        } else if (ModConfig.enableParticleDebug) {
            System.out.println("[ARCHIPELAGO-DEBUG] Always visible particle added while NOT in preview: " + particle.getType() + " at " + x + ", " + y + ", " + z + " Thread: " + Thread.currentThread().getName());
        }
    }

    @Inject(method = "addAlwaysVisibleParticle(Lnet/minecraft/core/particles/ParticleOptions;ZDDDDDD)V", at = @At("HEAD"), cancellable = true)
    private void onAddAlwaysVisibleParticle(ParticleOptions particle, boolean force, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci) {
        if (archipelago$shouldSuppress(x, y, z)) {
            ci.cancel();
        } else if (ModConfig.enableParticleDebug) {
            System.out.println("[ARCHIPELAGO-DEBUG] Always visible particle (forced) added while NOT in preview: " + particle.getType() + " at " + x + ", " + y + ", " + z + " Thread: " + Thread.currentThread().getName());
        }
    }
}
