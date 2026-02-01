package com.alkia.archipelago.mixin;

import com.alkia.archipelago.util.ArchipelagoPosableState;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState;
import com.cobblemon.mod.common.entity.PoseType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


// Exists entirely because you can't change pose of ModelWidget by default
@Mixin(value = PosableState.class, remap = false)
public abstract class PosableStateMixin implements ArchipelagoPosableState {
    @Unique
    private String archipelago$forcedPose = null;

    @Inject(method = "setPoseToFirstSuitable", at = @At("HEAD"), cancellable = true)
    private void onSetPoseToFirstSuitable(PoseType poseType, CallbackInfo ci) {
        if (archipelago$forcedPose != null) {
            ((PosableState) (Object) this).setPose(archipelago$forcedPose);
            ci.cancel();
        }
    }

    @Override
    @Unique
    public void archipelago$setForcedPose(String pose) {
        this.archipelago$forcedPose = pose;
    }
}
