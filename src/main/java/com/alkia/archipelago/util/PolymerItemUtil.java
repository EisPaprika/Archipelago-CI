package com.alkia.archipelago.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class PolymerItemUtil {

    private static final ResourceLocation POLYMER_ITEMS_JSON =
            ResourceLocation.fromNamespaceAndPath("polymer", "items.json");

    private static volatile Map<String, List<Entry>> byPolymerId; // polymerId -> all possible (baseItem, cmd)
    private static volatile Map<String, List<String>> normalizedToActual;
    private static volatile boolean loadAttempted;

    private PolymerItemUtil() {}

    public static Optional<Entry> findForSlot(String polymerId, EquipmentSlot desiredSlot) {
        ensureLoaded();
        Map<String, List<Entry>> idx = byPolymerId;
        if (idx == null) return Optional.empty();

        List<Entry> entries = idx.get(polymerId);
        if (entries == null || entries.isEmpty()) {
            // Try normalized fallback
            return findByNormalized(polymerId, desiredSlot);
        }

        return pickBestEntry(entries, desiredSlot);
    }

    private static Optional<Entry> pickBestEntry(List<Entry> entries, EquipmentSlot desiredSlot) {
        // Prefer armor items that match the requested slot
        for (Entry e : entries) {
            if (e.baseItem() instanceof ArmorItem armorItem) {
                if (armorItem.getEquipmentSlot() == desiredSlot) {
                    return Optional.of(e);
                }
            }
        }

        // Fallback: if none match by slot, return the first entry
        return Optional.of(entries.get(0));
    }

    public static Optional<Entry> findByNormalized(String polymerId, EquipmentSlot desiredSlot) {
        ensureLoaded();
        Map<String, List<String>> normIdx = normalizedToActual;
        Map<String, List<Entry>> idx = byPolymerId;
        if (normIdx == null || idx == null) return Optional.empty();

        String targetNorm = normalize(polymerId);
        List<String> actualIds = normIdx.get(targetNorm);
        if (actualIds == null) return Optional.empty();

        for (String actualId : actualIds) {
            List<Entry> entries = idx.get(actualId);
            if (entries == null) continue;
            
            // If we find one that matches the slot, great
            for (Entry e : entries) {
                if (e.baseItem() instanceof ArmorItem armorItem) {
                    if (armorItem.getEquipmentSlot() == desiredSlot) {
                        return Optional.of(e);
                    }
                }
            }
        }

        // If no slot match found in any normalized candidate, just take the first entry of the first candidate
        List<Entry> firstEntries = idx.get(actualIds.get(0));
        return (firstEntries != null && !firstEntries.isEmpty()) ? Optional.of(firstEntries.get(0)) : Optional.empty();
    }

    private static String normalize(String s) {
        if (s == null) return "";
        int lastCol = s.lastIndexOf(':');
        int lastSla = s.lastIndexOf('/');
        int start = Math.max(lastCol, lastSla) + 1;
        String local = s.substring(start).toLowerCase();
        return local.replaceAll("[^a-z0-9]", "");
    }

    public static Map<String, List<Entry>> getEntriesByPrefix(String prefix) {
        ensureLoaded();
        Map<String, List<Entry>> idx = byPolymerId;
        if (idx == null) return Collections.emptyMap();

        Map<String, List<Entry>> matches = new HashMap<>();
        if (idx instanceof TreeMap<String, List<Entry>> tree) {
            SortedMap<String, List<Entry>> tail = tree.tailMap(prefix);
            for (Map.Entry<String, List<Entry>> e : tail.entrySet()) {
                if (e.getKey().startsWith(prefix)) {
                    matches.put(e.getKey(), e.getValue());
                } else {
                    break;
                }
            }
        } else {
            for (Map.Entry<String, List<Entry>> e : idx.entrySet()) {
                if (e.getKey().startsWith(prefix)) {
                    matches.put(e.getKey(), e.getValue());
                }
            }
        }

        // If no matches found with direct prefix, try normalized prefix search
        if (matches.isEmpty()) {
            String normPrefix = normalize(prefix);
            if (!normPrefix.isEmpty()) {
                Map<String, List<String>> normIdx = normalizedToActual;
                if (normIdx != null) {
                    for (Map.Entry<String, List<String>> e : normIdx.entrySet()) {
                        if (e.getKey().startsWith(normPrefix)) {
                            for (String actualId : e.getValue()) {
                                matches.put(actualId, idx.get(actualId));
                            }
                        }
                    }
                }
            }
        }
        return matches;
    }

    public static void clearCache() {
        byPolymerId = null;
        normalizedToActual = null;
        loadAttempted = false;
    }

    private static void ensureLoaded() {
        if (byPolymerId != null || loadAttempted) return;

        synchronized (PolymerItemUtil.class) {
            if (byPolymerId != null || loadAttempted) return;
            loadAttempted = true;

            Minecraft mc = Minecraft.getInstance();
            if (mc == null) return;

            ResourceManager rm = mc.getResourceManager();
            if (rm == null) return;

            Optional<Resource> resOpt = rm.getResource(POLYMER_ITEMS_JSON);
            if (resOpt.isEmpty()) {
                System.out.println("[ARCHIPELAGO] Could not find polymer:items.json");
                return;
            }

            Resource res = resOpt.get();

            try (InputStream in = res.open();
                 InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {

                System.out.println("[ARCHIPELAGO] Loading polymer:items.json...");
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                TreeMap<String, List<Entry>> map = new TreeMap<>();
                Map<String, List<String>> normMap = new HashMap<>(65536);

                int count = 0;
                for (String baseItemKey : root.keySet()) {
                    JsonElement innerEl = root.get(baseItemKey);
                    if (innerEl == null || !innerEl.isJsonObject()) continue;

                    Item baseItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(baseItemKey));
                    JsonObject inner = innerEl.getAsJsonObject();

                    for (Map.Entry<String, JsonElement> e : inner.entrySet()) {
                        String pid = e.getKey();
                        JsonElement cmdEl = e.getValue();
                        if (cmdEl == null || !cmdEl.isJsonPrimitive()) continue;

                        int cmd = cmdEl.getAsInt();
                        map.computeIfAbsent(pid, k -> new ArrayList<>(2)).add(new Entry(baseItem, cmd));
                        
                        String norm = normalize(pid);
                        if (!norm.isEmpty()) {
                            normMap.computeIfAbsent(norm, k -> new ArrayList<>(2)).add(pid);
                        }
                        count++;
                    }
                }

                byPolymerId = map;
                normalizedToActual = normMap;
                System.out.println("[ARCHIPELAGO] Loaded " + count + " polymer item mappings.");
            } catch (Exception e) {
                System.err.println("[ARCHIPELAGO] Failed to load polymer:items.json");
                e.printStackTrace();
            }
        }
    }

    public record Entry(Item baseItem, int customModelData) {}
}
