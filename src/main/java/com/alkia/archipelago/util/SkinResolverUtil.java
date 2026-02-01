package com.alkia.archipelago.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SkinResolverUtil {
    private static final Gson GSON = new Gson();
    private static final Map<String, List<SkinVariation>> SKIN_CACHE = new HashMap<>();
    private static boolean loaded = false;

    public static void loadSkins() {
        SKIN_CACHE.clear();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        
        // Looks inside of assets/cobblemon/bedrock/pokemon/resolvers/. (From the CI Resource pack)
        Map<ResourceLocation, Resource> resolvers = resourceManager.listResources("bedrock/pokemon/resolvers", 
            rl -> rl.getPath().endsWith(".json") && rl.getNamespace().equals("cobblemon"));

        for (Map.Entry<ResourceLocation, Resource> entry : resolvers.entrySet()) {
            try (InputStreamReader reader = new InputStreamReader(entry.getValue().open(), StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                if (json.has("species") && json.has("variations")) {
                    String species = json.get("species").getAsString();
                    JsonArray variations = json.getAsJsonArray("variations");
                    
                    List<SkinVariation> skinVariations = SKIN_CACHE.computeIfAbsent(species, k -> new ArrayList<>());
                    
                    for (JsonElement varElement : variations) {
                        JsonObject varJson = varElement.getAsJsonObject();
                        if (varJson.has("aspects")) {
                            List<String> aspects = new ArrayList<>();
                            varJson.getAsJsonArray("aspects").forEach(a -> aspects.add(a.getAsString()));

                            // Only skins start with "skin-" Stops megas, gmax n stuff
                            String skinToken = aspects.stream()
                                    .filter(a -> a.startsWith("skin-"))
                                    .findFirst()
                                    .orElse("");

                            if (skinToken.isEmpty()) {
                                continue; // Skip variations that are not skins (e.g., mega, gmax, etc.)
                            }

                            boolean isDuplicate = skinVariations.stream()
                                    .anyMatch(v -> v.aspects.contains(skinToken));
                            if (isDuplicate) continue;
                            String skinName = skinToken.substring(5);
                            skinVariations.add(new SkinVariation(skinName, aspects));
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        loaded = true;
    }

    public static List<SkinVariation> getSkinsForSpecies(String species) {
        if (!loaded) loadSkins();
        if (!species.contains(":")) {
            species = "cobblemon:" + species;
        }
        return SKIN_CACHE.getOrDefault(species, Collections.emptyList());
    }

    public static void invalidate() {
        loaded = false;
    }

    public static class SkinVariation {
        public final String name;
        public final List<String> aspects;

        public SkinVariation(String name, List<String> aspects) {
            this.name = name;
            this.aspects = aspects;
        }

        @Override
        public String toString() {
            return name + " (" + String.join(", ", aspects) + ")";
        }
    }
}
