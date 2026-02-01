package com.alkia.archipelago.util;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class RenderFlags {
    public static volatile boolean isRenderingSkinPreview = false;
    public static final Set<Object> previewEntities = Collections.newSetFromMap(new WeakHashMap<>());
}
