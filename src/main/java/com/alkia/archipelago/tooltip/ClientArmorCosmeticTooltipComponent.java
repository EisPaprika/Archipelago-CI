package com.alkia.archipelago.tooltip;

import com.alkia.archipelago.config.ModConfig;
import com.alkia.archipelago.util.KeyUtil;
import com.alkia.archipelago.util.PolymerItemUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class ClientArmorCosmeticTooltipComponent implements ClientTooltipComponent {
    private static final String CI_PREFIX = "ci:";
    private static final String ARMOR_PATH_PREFIX = "armors/";
    private static final String COSMETIC_PATH_PREFIX = "item/cosmetics/";
    private static final ResourceLocation QUARTZ_MATERIAL = ResourceLocation.fromNamespaceAndPath("minecraft", "quartz");
    private static final UUID PREVIEW_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final ArmorCosmeticTooltipComponent data;
    private final RemotePlayer dummyStand;

    private final Map<EquipmentSlot, ItemStack> singleItemStacks = new EnumMap<>(EquipmentSlot.class);
    private final Map<EquipmentSlot, ItemStack> fullSetStacks = new EnumMap<>(EquipmentSlot.class);
    private boolean cachedStacksInitialized = false;
    private Boolean lastShowFullSet = null;

    private final Map<String, String> lastEquippedDebugKey = new HashMap<>();

    public ClientArmorCosmeticTooltipComponent(ArmorCosmeticTooltipComponent data) {
        this.data = data;
        GameProfile profile = Minecraft.getInstance().player != null
                ? Minecraft.getInstance().player.getGameProfile()
                : new GameProfile(PREVIEW_UUID, "preview");
        this.dummyStand = new RemotePlayer(Minecraft.getInstance().level, profile);
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public int getWidth(Font font) {
        return 80;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        // Init cache (Was generating every frame last time. Bad idea.)
        if (!cachedStacksInitialized) {
            initializeCachedStacks();
            cachedStacksInitialized = true;
        }
        // Key press handling for showing full set, bound to mega key, not sure if I should make it its own individual
        // keybind at some point..
        boolean showFullSet = KeyUtil.isModifierKeyDown(ModConfig.megaKey);
        if (lastShowFullSet == null || lastShowFullSet != showFullSet) {
            applyStacksToDummy(showFullSet);
            lastShowFullSet = showFullSet;
        }
        // Transforms for the dummy
        float scale = 30.0f;
        int renderX = x + 40;
        int renderY = y + 70;
        float spin = (float) (((System.currentTimeMillis() % 8000) / 8000.0) * 360.0);
        float yaw = (spin + 180.0f) % 360.0f;
        dummyStand.tickCount = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.tickCount : 0;
        dummyStand.setYRot(yaw);
        dummyStand.setYBodyRot(yaw);
        dummyStand.setYHeadRot(yaw);
        dummyStand.setXRot(0f);
        dummyStand.yRotO = yaw;
        dummyStand.yBodyRotO = yaw;
        dummyStand.yHeadRotO = yaw;
        dummyStand.xRotO = 0f;
        dummyStand.setInvisible(false);
        Vector3f translation = new Vector3f(0.0f, 0.0f, 0.0f);
        Quaternionf bodyRotation = new Quaternionf().rotationZ((float) Math.PI);
        Quaternionf cameraRotation = new Quaternionf();
        // Finally, renders the dummy
        InventoryScreen.renderEntityInInventory(
                guiGraphics, (float) renderX, (float) renderY, scale,
                translation, bodyRotation, cameraRotation, dummyStand
        );
    }

    private void initializeCachedStacks() {
        // Single item handling (For when you're not previewing the full set
        String slotType = data.slot();
        EquipmentSlot slot = getSlot(slotType);
        singleItemStacks.put(slot, createCosmeticStack(data.polymerId(), slotType, true));

        // Full set (For when you're previewing the full set)
        if (data.setName().isPresent()) {
            String prefix = data.setName().get();
            // Debugs are basically needed because some of these ids make NO SENSE
            if (ModConfig.enableSkinDebug) {
                System.out.println("[ARCHIPELAGO] Equip full set for " + prefix + " polymerId =" + data.polymerId() + " slot=" + data.slot());
            }
            // Path checking. Still ugly but looks better now
            boolean looksLikePath = prefix.contains("/") || prefix.startsWith(ARMOR_PATH_PREFIX) || prefix.contains(COSMETIC_PATH_PREFIX);
            String sep = (prefix.endsWith("_") || prefix.endsWith(":") || prefix.endsWith("/")) ? "" : "_";
            if (!looksLikePath && !prefix.contains(":")) prefix = CI_PREFIX + prefix;
            // Create cosmetics
            fullSetStacks.put(EquipmentSlot.HEAD, createCosmeticStack(prefix + sep + "helmet", "head", false));
            fullSetStacks.put(EquipmentSlot.CHEST, createCosmeticStack(prefix + sep + "chestplate", "chest", false));
            fullSetStacks.put(EquipmentSlot.LEGS, createCosmeticStack(prefix + sep + "leggings", "legs", false));
            fullSetStacks.put(EquipmentSlot.FEET, createCosmeticStack(prefix + sep + "boots", "feet", false));
        }
    }
    // Adds the cosmetics to the dummy
    private void applyStacksToDummy(boolean fullSet) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            dummyStand.setItemSlot(slot, ItemStack.EMPTY);
        }

        if (fullSet && !fullSetStacks.isEmpty()) {
            fullSetStacks.forEach(this::equipWithDebug);
        } else {
            singleItemStacks.forEach(this::equipWithDebug);
        }
    }


    //Equips item, logs item/polymer debug info if enabled. Had to use this a lot because ids are weird and  will make
    //bug reports easier anyways
    private void equipWithDebug(EquipmentSlot slot, ItemStack stack) {
        dummyStand.setItemSlot(slot, stack);
        // Stops if debug is disabled
        if (!ModConfig.enableSkinDebug) return;
        // CustomModelData grabbing
        int cmd = 0;
        CustomModelData cmdComponent = null;
        try {
            cmdComponent = stack.get(DataComponents.CUSTOM_MODEL_DATA);
            if (cmdComponent != null) cmd = cmdComponent.value();
        } catch (Throwable ignored) {}
        // PolymerId Grabbing
        String polymerId = "(none)";
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag nbt = customData.copyTag();
            if (nbt.contains("$polymer:stack", Tag.TAG_COMPOUND)) {
                polymerId = nbt.getCompound("$polymer:stack").getString("id");
            } else if (nbt.contains("polymer:id", Tag.TAG_STRING)) {
                polymerId = nbt.getString("polymer:id");
            }
        }
        // Print stuffs
        String info = stack.getItem() + " cmd=" + cmd;
        String debugKey = slot.toString() + ":" + polymerId;
        String last = lastEquippedDebugKey.put(debugKey, info);
        if (last == null || !last.equals(info)) {
            System.out.println("[ARCHIPELAGO] Equip " + slot + " [" + polymerId + "]: " + info);
            System.out.println("[ARCHIPELAGO] " + slot + " CUSTOM_MODEL_DATA component=" + cmdComponent);

            if (customData != null) {
                CompoundTag nbt = customData.copyTag();
                System.out.println("[ARCHIPELAGO] " + slot + " CUSTOM_DATA=" + nbt);
            } else {
                System.out.println("[ARCHIPELAGO] " + slot + " CUSTOM_DATA=(none)");
            }
        }
    }
    // Grabs equipment slots
    private EquipmentSlot getSlot(String slotName) {
        return switch (slotName.toLowerCase()) {
            case "head" -> EquipmentSlot.HEAD;
            case "chest" -> EquipmentSlot.CHEST;
            case "legs" -> EquipmentSlot.LEGS;
            case "feet" -> EquipmentSlot.FEET;
            default -> EquipmentSlot.MAINHAND;
        };
    }
    // Creating the cosmetic stack (tetris)  (Not actually tetris)
    private ItemStack createCosmeticStack(String polymerIdRaw, String slotRaw, boolean allowFallback) {
        String slot = slotRaw == null ? "" : slotRaw.toLowerCase();
        EquipmentSlot desiredSlot = getSlot(slot);
        PolymerItemUtil.Entry match = null;
        String matchedKey = null;
        
        for (String candidateId : buildCandidatePolymerIds(polymerIdRaw, slot)) {
            PolymerItemUtil.Entry e = PolymerItemUtil.findForSlot(candidateId, desiredSlot).orElse(null);
            if (e != null) {
                match = e;
                matchedKey = candidateId;
                break;
            }
        }
        // Actually setting up the stack now
        ItemStack stack;
        if (match != null) {
            stack = new ItemStack(match.baseItem());
            try {
                stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(match.customModelData()));
            } catch (Throwable ignored) {}
            applyCosmeticTrimIfPossible(stack, matchedKey != null ? matchedKey : polymerIdRaw, desiredSlot);
        // Fallback. Needed a default so put paper, idk. Maybe a better way of doing this....?
        } else if (allowFallback) {
            stack = switch (desiredSlot) {
                case HEAD -> new ItemStack(Items.POPPED_CHORUS_FRUIT);
                case CHEST -> new ItemStack(Items.CHAINMAIL_CHESTPLATE);
                case LEGS -> new ItemStack(Items.CHAINMAIL_LEGGINGS);
                case FEET -> new ItemStack(Items.CHAINMAIL_BOOTS);
                default -> new ItemStack(Items.PAPER);
            };
        } else {
            return ItemStack.EMPTY;
        }

        CompoundTag polymerStack = new CompoundTag();
        polymerStack.putString("id", polymerIdRaw);

        CompoundTag customDataNbt = new CompoundTag();
        customDataNbt.put("$polymer:stack", polymerStack);
        customDataNbt.putString("polymer:id", polymerIdRaw);
        // Setting NBT
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customDataNbt));
        // More debugs
        if (ModConfig.enableSkinDebug && match != null) {
            System.out.println("[ARCHIPELAGO] Polymer matched slot=" + desiredSlot
                    + " rawId=" + polymerIdRaw
                    + " matchedKey=" + matchedKey
                    + " base=" + match.baseItem()
                    + " cmd=" + match.customModelData());
        }
        return stack;
    }

    // So CI Cosmetics are applied via armor trim. This is a cool idea, but jesus christ...
    // Is this entirely done because of the "Only need cobblemon to join rule?" Jesus...
    // CITResewn fixes this entire issue :(
    // I mean, we have it for elytras anyways, so wtf.
    private void applyCosmeticTrimIfPossible(ItemStack stack, String idUsedForLookup, EquipmentSlot slot) {
        if (slot != EquipmentSlot.CHEST && slot != EquipmentSlot.LEGS && slot != EquipmentSlot.FEET && slot != EquipmentSlot.HEAD) return;
        // funni minecraft null check again
        var mc = Minecraft.getInstance();
        if (mc.level == null) return;
        // Get pattern from id, thankfully this usually works.
        String patternName = extractTrimPatternName(idUsedForLookup);
        if (patternName == null || patternName.isBlank()) return;
        // Applies cosmetic trim to item stack (Chestplates, leggings, boots, etc.)
        try {
            var access = mc.level.registryAccess();
            var matReg = access.registryOrThrow(Registries.TRIM_MATERIAL);
            var patReg = access.registryOrThrow(Registries.TRIM_PATTERN);

            Holder<TrimMaterial> material = matReg.getHolderOrThrow(
                    ResourceKey.create(Registries.TRIM_MATERIAL, QUARTZ_MATERIAL)
            );

            ResourceLocation patternIdMc = ResourceLocation.withDefaultNamespace(patternName);
            Holder<TrimPattern> pattern = patReg.getHolder(ResourceKey.create(Registries.TRIM_PATTERN, patternIdMc))
                    .or(() -> patReg.getHolder(ResourceKey.create(Registries.TRIM_PATTERN, ResourceLocation.fromNamespaceAndPath("ci", patternName))))
                    .orElse(null);
            // If trim doesn't exist, scream at those with debug enabled that it shouldn't happen
            if (pattern == null) {
                if (ModConfig.enableSkinDebug) {
                    System.out.println("[ARCHIPELAGO] Trim pattern not found in registry: " + patternName);
                }
                return;
            }
            stack.set(DataComponents.TRIM, new ArmorTrim(material, pattern));
            // Debug to show trim thats been applied and its pattern
            if (ModConfig.enableSkinDebug) {
                System.out.println("[ARCHIPELAGO] Applied TRIM material=" + QUARTZ_MATERIAL + " pattern=" + patternName + " for " + idUsedForLookup);
            }
        } catch (RuntimeException ignored) {}
    }
    // Grabs pattern name from cosmetic ids
    private String extractTrimPatternName(String idUsedForLookup) {
        if (idUsedForLookup == null) return null;

        String s = idUsedForLookup;
        // Removema colon
        int colon = s.indexOf(':');
        if (colon >= 0) s = s.substring(colon + 1);
        // slashma slash
        int slash = s.lastIndexOf('/');
        String last = (slash >= 0) ? s.substring(slash + 1) : s;

        return stripArmorSuffix(last);
    }
    // Builds list of candidate polymer ids from NBT
    private List<String> buildCandidatePolymerIds(String polymerIdRaw, String slot) {
        List<String> out = new ArrayList<>(12);
        if (polymerIdRaw == null || polymerIdRaw.isBlank()) return out;

        out.add(polymerIdRaw);
        if (polymerIdRaw.contains("item/cosmetics/")) return out;
        // For Ids that start with ci: (most of em)
        if (polymerIdRaw.startsWith(CI_PREFIX)) {
            String name = polymerIdRaw.substring(CI_PREFIX.length());
            for (String folder : cosmeticFoldersForSlot(slot)) {
                out.add(CI_PREFIX + COSMETIC_PATH_PREFIX + folder + "/" + name);
            }
            // reformatting
            String base = stripArmorSuffix(name);
            String baseNoUnderscore = base.replace("_", "");
            String piece = armorPieceForSlot(slot);
            if (!piece.isBlank()) {
                out.add(CI_PREFIX + base + "_" + piece);
                out.add(CI_PREFIX + baseNoUnderscore + "_" + piece);
                out.add(ARMOR_PATH_PREFIX + base + "_" + piece);
                out.add(ARMOR_PATH_PREFIX + baseNoUnderscore + "_" + piece);
            }
        }

        return out;
    }
    // Convert slots to lowercase (Mainly just a check just in case)
    private String armorPieceForSlot(String slot) {
        return switch (slot == null ? "" : slot.toLowerCase()) {
            case "head" -> "helmet";
            case "chest" -> "chestplate";
            case "legs" -> "leggings";
            case "feet" -> "boots";
            default -> "";
        };
    }
    // removes suffixes like _helmet etc. from the ID, so it can be read
    private String stripArmorSuffix(String name) {
        String lower = name.toLowerCase();
        if (lower.endsWith("_helmet")) return name.substring(0, name.length() - 7);
        if (lower.endsWith("_chestplate")) return name.substring(0, name.length() - 11);
        if (lower.endsWith("_leggings")) return name.substring(0, name.length() - 9);
        if (lower.endsWith("_boots")) return name.substring(0, name.length() - 6);
        return name;
    }
    // Switchma slots
    // (for cases where it's using different variations  for its Id for some dumb reason)
    private List<String> cosmeticFoldersForSlot(String slot) {
        return switch (slot == null ? "" : slot.toLowerCase()) {
            case "head" -> List.of("head", "helmet", "hats", "hat");
            case "chest" -> List.of("chest", "body", "torso");
            case "legs" -> List.of("legs", "pants");
            case "feet" -> List.of("feet", "boots", "shoes");
            default -> List.of("head", "chest", "body", "torso", "legs", "feet");
        };
    }
}
