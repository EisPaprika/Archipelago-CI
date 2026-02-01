package com.alkia.archipelago.mixin;

import net.minecraft.util.profiling.ActiveProfiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActiveProfiler.class)
public abstract class ProfilerSuppressionMixin {
    @Unique
    private static final ThreadLocal<Integer> archipelago$suppressedPushes = ThreadLocal.withInitial(() -> 0);

    @Inject(method = "push(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private void onPush(String name, CallbackInfo ci) {
        boolean isAsyncTicker = Thread.currentThread().getName().startsWith("AsyncParticleTicker");
        if ("async_particles".equals(name) || isAsyncTicker) {
            archipelago$suppressedPushes.set(archipelago$suppressedPushes.get() + 1);
            ci.cancel();
        }
    }

    @Inject(method = "push(Ljava/util/function/Supplier;)V", at = @At("HEAD"), cancellable = true)
    private void onPushSupplier(java.util.function.Supplier<String> nameSupplier, CallbackInfo ci) {
        boolean isAsyncTicker = Thread.currentThread().getName().startsWith("AsyncParticleTicker");
        if (isAsyncTicker) {
            archipelago$suppressedPushes.set(archipelago$suppressedPushes.get() + 1);
            ci.cancel();
            return;
        }

        try {
            String name = nameSupplier.get();
            if ("async_particles".equals(name)) {
                archipelago$suppressedPushes.set(archipelago$suppressedPushes.get() + 1);
                ci.cancel();
            }
        } catch (Exception ignored) {}
    }

    @Inject(method = "pop()V", at = @At("HEAD"), cancellable = true)
    private void onPop(CallbackInfo ci) {
        int count = archipelago$suppressedPushes.get();
        if (count > 0) {
            archipelago$suppressedPushes.set(count - 1);
            ci.cancel();
        } else if (Thread.currentThread().getName().startsWith("AsyncParticleTicker")) {
            ci.cancel();
        }
    }

    @Inject(method = "popPush(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private void onPopPush(String name, CallbackInfo ci) {
        boolean isAsyncTicker = Thread.currentThread().getName().startsWith("AsyncParticleTicker");
        if ("async_particles".equals(name) || isAsyncTicker) {
            
            int count = archipelago$suppressedPushes.get();
            if (count > 0) {
                ci.cancel();
            } else {
                if (isAsyncTicker) {
                    ci.cancel();
                }
            }
        }
    }
}