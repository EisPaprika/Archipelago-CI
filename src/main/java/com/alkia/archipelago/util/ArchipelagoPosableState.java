package com.alkia.archipelago.util;


// Exists entirely because you can't change pose of ModelWidget by default
public interface ArchipelagoPosableState {
    void archipelago$setForcedPose(String pose);
}
