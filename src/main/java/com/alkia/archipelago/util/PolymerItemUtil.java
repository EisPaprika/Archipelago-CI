package com.alkia.archipelago.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

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

    public static String getPolymerId(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return null;

        CompoundTag nbt = customData.copyTag();

        // Support for transmog on armors (filament is a neat codename, kudos to whoever came up with that)
        if (nbt.contains("filament:skin_data", Tag.TAG_COMPOUND)) {
            CompoundTag skinData = nbt.getCompound("filament:skin_data");
            if (skinData.contains("id", Tag.TAG_STRING)) {
                return skinData.getString("id");
            }
        }

        // Check inside $polymer:stack/components for cosmetic ids
        if (nbt.contains("$polymer:stack", Tag.TAG_COMPOUND)) {
            CompoundTag polymerStack = nbt.getCompound("$polymer:stack");
            if (polymerStack.contains("components", Tag.TAG_COMPOUND)) {
                CompoundTag components = polymerStack.getCompound("components");
                if (components.contains("filament:skin_data", Tag.TAG_COMPOUND)) {
                    CompoundTag skinData = components.getCompound("filament:skin_data");
                    if (skinData.contains("id", Tag.TAG_STRING)) {
                        return skinData.getString("id");
                    }
                }
            }
        }

        // Check for standard polymer id (Usually only if the cosmetic isn't transmogged
        if (nbt.contains("$polymer:stack", Tag.TAG_COMPOUND)) {
            String pid = nbt.getCompound("$polymer:stack").getString("id");
            if (!pid.isEmpty()) return pid;
        }

        if (nbt.contains("polymer:id", Tag.TAG_STRING)) {
            return nbt.getString("polymer:id");
        }

        return null;
    }

    public static Optional<Entry> findForSlot(String polymerId, EquipmentSlot desiredSlot) {
        ensureLoaded();
        Map<String, List<Entry>> idx = byPolymerId;
        if (idx == null) return Optional.empty();

        List<Entry> entries = idx.get(polymerId);
        if (entries == null || entries.isEmpty()) {
            // Try normalized fallback (Used if entries are null for some weird reason. Shouldn't happen normally........?
            return findByNormalized(polymerId, desiredSlot);
        }
        return pickBestEntry(entries, desiredSlot);
    }

    private static Optional<Entry> pickBestEntry(List<Entry> entries, EquipmentSlot desiredSlot) {
        // Prefer armor items that match the requested slot of the cosmetic (chestplate, leggings, boots)
        for (Entry e : entries) {
            if (e.baseItem() instanceof ArmorItem armorItem) {
                if (armorItem.getEquipmentSlot() == desiredSlot) {
                    return Optional.of(e);
                }
            }
        }

        // Fallback: if none match by slot, return the first entry (For chorus fruit n stuffs)
        return Optional.of(entries.get(0));
    }


    //Finds entry by normalized ID, matching equipment slot. Works, but was annoying to figure out.
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
            // If we find one that matches the slot, great!!! Yippee!!! Woohoo!!!!
            // Otherwise, cry.
            for (Entry e : entries) {
                if (e.baseItem() instanceof ArmorItem armorItem) {
                    if (armorItem.getEquipmentSlot() == desiredSlot) {
                        return Optional.of(e);
                    }
                }
            }
        }

        // After crying, if no slot match found in any normalized candidate, just take the first entry of the first candidate
        List<Entry> firstEntries = idx.get(actualIds.get(0));
        return (firstEntries != null && !firstEntries.isEmpty()) ? Optional.of(firstEntries.get(0)) : Optional.empty();
    }
    // Strips out all the useless stuff from a string, like /, :, etc. (Usually used for IDs, might use some other places
    // in the future...)
    private static String normalize(String s) {
        if (s == null) return "";
        int lastCol = s.lastIndexOf(':');
        int lastSla = s.lastIndexOf('/');
        int start = Math.max(lastCol, lastSla) + 1;
        String local = s.substring(start).toLowerCase();
        return local.replaceAll("[^a-z0-9]", "");
    }
    // Clearma cache (Used on RP Reload)
    public static void clearCache() {
        byPolymerId = null;
        normalizedToActual = null;
        loadAttempted = false;
    }
    // Loads polymer mappings from the items.json inside of the RP.
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
                // This shouldn't ever have to be printed. If it is, the resource pack either ain't
                // downloaded yet, or something is SERIOUSLY wrong. Or they've edited the RP to mess with me.
                System.out.println("[ARCHIPELAGO] Could not find polymer:items.json");
                return;
            }

            Resource res = resOpt.get();
            // Reads the items.json
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
                // Usually prints a pretty funny number. Feel bad for whoever does it if this file ever needs to be refactored.
                System.out.println("[ARCHIPELAGO] Loaded " + count + " polymer item mappings.");
            } catch (Exception e) {
                // Shouldn't ever happen (Hopefully)
                System.err.println("[ARCHIPELAGO] Failed to load polymer:items.json");
                e.printStackTrace();
            }
        }
    }

    public record Entry(Item baseItem, int customModelData) {}
}
