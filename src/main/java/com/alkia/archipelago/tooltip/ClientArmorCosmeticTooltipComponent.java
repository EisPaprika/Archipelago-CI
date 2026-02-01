package com.alkia.archipelago.tooltip;

import com.alkia.archipelago.config.ModConfig;
import com.alkia.archipelago.util.KeyUtil;
import com.alkia.archipelago.util.PolymerItemUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ClientArmorCosmeticTooltipComponent implements ClientTooltipComponent {
    private final ArmorCosmeticTooltipComponent data;
    private final RemotePlayer dummyStand;


    private final Map<String, String> lastEquippedDebugKey = new HashMap<>();

    public ClientArmorCosmeticTooltipComponent(ArmorCosmeticTooltipComponent data) {
        this.data = data;
        GameProfile profile = Minecraft.getInstance().player != null
                ? Minecraft.getInstance().player.getGameProfile()
                : new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000000"), "preview");
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
        boolean showFullSet = KeyUtil.isModifierKeyDown(ModConfig.megaKey);

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


        setupDummyArmor(showFullSet);
        InventoryScreen.renderEntityInInventory(
                guiGraphics,
                (float) renderX,
                (float) renderY,
                scale,
                translation,
                bodyRotation,
                cameraRotation,
                dummyStand
        );
    }

    private void setupDummyArmor(boolean fullSet) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            dummyStand.setItemSlot(slot, ItemStack.EMPTY);
        }

        if (fullSet && data.setName().isPresent()) {
            String prefix = data.setName().get();

            if (ModConfig.enableSkinDebug) {
                System.out.println("[ARCHIPELAGO] Equip full set for " + prefix + " polymerId =" + data.polymerId() + " slot=" + data.slot());
            }

            boolean looksLikePath = prefix.contains("/") || prefix.startsWith("armors/") || prefix.contains("item/cosmetics/");
            String sep = (prefix.endsWith("_") || prefix.endsWith(":") || prefix.endsWith("/")) ? "" : "_";
            if (!looksLikePath  && !prefix.contains(":")) prefix = "ci:" + prefix;

            equipWithDebug(EquipmentSlot.HEAD, createCosmeticStack(prefix + sep + "helmet", "head", false));
            equipWithDebug(EquipmentSlot.CHEST, createCosmeticStack(prefix + sep + "chestplate", "chest", false));
            equipWithDebug(EquipmentSlot.LEGS, createCosmeticStack(prefix + sep + "leggings", "legs", false));
            equipWithDebug(EquipmentSlot.FEET, createCosmeticStack(prefix + sep + "boots", "feet", false));
        } else {
            String slotType = data.slot();
            EquipmentSlot slot = getSlot(slotType);
            equipWithDebug(slot, createCosmeticStack(data.polymerId(), slotType, true));
        }
    }

    private void equipWithDebug(EquipmentSlot slot, ItemStack stack) {
        if (!stack.isEmpty() && slot == EquipmentSlot.HEAD) {
             ensureEquippable(stack);
        }
        dummyStand.setItemSlot(slot, stack);

        if (!ModConfig.enableSkinDebug) return;

        int cmd = 0;
        CustomModelData cmdComponent = null;
        try {
            cmdComponent = stack.get(DataComponents.CUSTOM_MODEL_DATA);
            if (cmdComponent != null) cmd = cmdComponent.value();
        } catch (Throwable ignored) {}

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

    private void ensureEquippable(ItemStack stack) {

        try {
            //I have no idea what the class name for it is but this worked so
            Class<?> equippableClass = Class.forName("net.minecraft.world.item.component.Equippable");

            Object equippableType = null;
            for (Field f : DataComponents.class.getDeclaredFields()) {
                if (f.getName().equalsIgnoreCase("EQUIPPABLE")) {
                    equippableType = f.get(null);
                    break;
                }
            }
            if (equippableType == null) return;


            if (stack.get((net.minecraft.core.component.DataComponentType<?>)equippableType) != null) return;

            Method builderMethod = equippableClass.getDeclaredMethod("builder", EquipmentSlot.class);
            Object builder = builderMethod.invoke(null, EquipmentSlot.HEAD);
            

            try {
                Method dispensableMethod = builder.getClass().getDeclaredMethod("setDispensable", boolean.class);
                dispensableMethod.invoke(builder, true);
            } catch (Throwable ignored) {}
            
            Method buildMethod = builder.getClass().getDeclaredMethod("build");
            Object equippable = buildMethod.invoke(builder);

            for (Method m : stack.getClass().getMethods()) {
                if (m.getName().equals("set") && m.getParameterCount() == 2) {
                    Class<?>[] params = m.getParameterTypes();
                    if (params[0].getSimpleName().equals("DataComponentType")) {
                        m.invoke(stack, equippableType, equippable);
                        break;
                    }
                }
            }
        } catch (Throwable ignored) {}
    }

    private EquipmentSlot getSlot(String slotName) {
        return switch (slotName.toLowerCase()) {
            case "head" -> EquipmentSlot.HEAD;
            case "chest" -> EquipmentSlot.CHEST;
            case "legs" -> EquipmentSlot.LEGS;
            case "feet" -> EquipmentSlot.FEET;
            default -> EquipmentSlot.MAINHAND;
        };
    }

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

        ItemStack stack;
        if (match != null) {
            stack = new ItemStack(match.baseItem());


            try {
                stack.set(DataComponents.CUSTOM_MODEL_DATA,
                        new net.minecraft.world.item.component.CustomModelData(match.customModelData()));
            } catch (Throwable ignored) {}

            applyCosmeticTrimIfPossible(stack, matchedKey != null ? matchedKey : polymerIdRaw, desiredSlot);
        } else if (allowFallback) {
            if (desiredSlot == EquipmentSlot.HEAD) stack = new ItemStack(Items.POPPED_CHORUS_FRUIT);
            else if (desiredSlot == EquipmentSlot.CHEST) stack = new ItemStack(Items.CHAINMAIL_CHESTPLATE);
            else if (desiredSlot == EquipmentSlot.LEGS) stack = new ItemStack(Items.CHAINMAIL_LEGGINGS);
            else if (desiredSlot == EquipmentSlot.FEET) stack = new ItemStack(Items.CHAINMAIL_BOOTS);
            else stack = new ItemStack(Items.PAPER);
        } else {
            return ItemStack.EMPTY;
        }

        CompoundTag polymerStack = new CompoundTag();
        polymerStack.putString("id", polymerIdRaw);

        CompoundTag customDataNbt = new CompoundTag();
        customDataNbt.put("$polymer:stack", polymerStack);
        customDataNbt.putString("polymer:id", polymerIdRaw);

        stack.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(customDataNbt));

        if (ModConfig.enableSkinDebug && match != null) {
            System.out.println("[ARCHIPELAGO] Polymer matched slot=" + desiredSlot
                    + " rawId=" + polymerIdRaw
                    + " matchedKey=" + matchedKey
                    + " base=" + match.baseItem()
                    + " cmd=" + match.customModelData());
        }

        return stack;
    }


    private void applyCosmeticTrimIfPossible(ItemStack stack, String idUsedForLookup, EquipmentSlot slot) {

        if (slot != EquipmentSlot.CHEST && slot != EquipmentSlot.LEGS && slot != EquipmentSlot.FEET && slot != EquipmentSlot.HEAD) return;

        var mc = Minecraft.getInstance();
        if (mc.level == null) return;

        String patternName = extractTrimPatternName(idUsedForLookup, slot);
        if (patternName == null || patternName.isBlank()) return;


        ResourceLocation patternIdMc = ResourceLocation.fromNamespaceAndPath("minecraft", patternName);


        ResourceLocation materialId = ResourceLocation.fromNamespaceAndPath("minecraft", "quartz");

        try {
            var access = mc.level.registryAccess();

            var matReg = access.registryOrThrow(Registries.TRIM_MATERIAL);
            var patReg = access.registryOrThrow(Registries.TRIM_PATTERN);

            Holder<TrimMaterial> material = matReg.getHolderOrThrow(
                    ResourceKey.create(Registries.TRIM_MATERIAL, materialId)
            );

            Holder<TrimPattern> pattern = patReg.getHolder(
                    ResourceKey.create(Registries.TRIM_PATTERN, patternIdMc)
            ).orElse(null);


            if (pattern == null) {
                ResourceLocation patternIdCi = ResourceLocation.fromNamespaceAndPath("ci", patternName);
                pattern = patReg.getHolder(
                        ResourceKey.create(Registries.TRIM_PATTERN, patternIdCi)
                ).orElse(null);
            }

            if (pattern == null) {
                if (ModConfig.enableSkinDebug) {
                    System.out.println("[ARCHIPELAGO] Trim pattern not found in registry: " + patternName);
                }
                return;
            }

            stack.set(DataComponents.TRIM, new ArmorTrim(material, pattern));

            if (ModConfig.enableSkinDebug) {
                System.out.println("[ARCHIPELAGO] Applied TRIM material=" + materialId + " pattern=" + patternName + " for " + idUsedForLookup);
            }
        } catch (Throwable t) {

        }
    }

    private String extractTrimPatternName(String idUsedForLookup, EquipmentSlot slot) {
        if (idUsedForLookup == null) return null;


        String s = idUsedForLookup;


        int colon = s.indexOf(':');
        if (colon >= 0) s = s.substring(colon + 1);


        int slash = s.lastIndexOf('/');
        String last = (slash >= 0) ? s.substring(slash + 1) : s;

        String lower = last.toLowerCase();

        if (lower.endsWith("_helmet")) return last.substring(0, last.length() - "_helmet".length());
        if (lower.endsWith("_chestplate")) return last.substring(0, last.length() - "_chestplate".length());
        if (lower.endsWith("_leggings")) return last.substring(0, last.length() - "_leggings".length());
        if (lower.endsWith("_boots")) return last.substring(0, last.length() - "_boots".length());


        return last;
    }

    private List<String> buildCandidatePolymerIds(String polymerIdRaw, String slot) {
        List<String> out = new ArrayList<>(12);
        if (polymerIdRaw == null || polymerIdRaw.isBlank()) return out;

        out.add(polymerIdRaw);

        if (polymerIdRaw.contains("item/cosmetics/")) return out;


        if (polymerIdRaw.startsWith("ci:")) {
            String name = polymerIdRaw.substring("ci:".length()); // e.g. spore_hatterene_chestplate
            for (String folder : cosmeticFoldersForSlot(slot)) {
                out.add("ci:item/cosmetics/" + folder + "/" + name);
            }

            String base = stripArmorSuffix(name);                 // spore_hatterene
            String baseNoUnderscore = base.replace("_", "");      // sporehatterene

            String piece = armorPieceForSlot(slot);               // chestplate / leggings / boots / helmet
            if (!piece.isBlank()) {
                out.add("ci:" + base + "_" + piece);
                out.add("ci:" + baseNoUnderscore + "_" + piece);
                out.add("armors/" + base + "_" + piece);
                out.add("armors/" + baseNoUnderscore + "_" + piece);
            }
        }

        return out;
    }

    private String armorPieceForSlot(String slot) {
        return switch (slot == null ? "" : slot.toLowerCase()) {
            case "head" -> "helmet";
            case "chest" -> "chestplate";
            case "legs" -> "leggings";
            case "feet" -> "boots";
            default -> "";
        };
    }

    private String stripArmorSuffix(String name) {
        String lower = name.toLowerCase();
        if (lower.endsWith("_helmet")) return name.substring(0, name.length() - "_helmet".length());
        if (lower.endsWith("_chestplate")) return name.substring(0, name.length() - "_chestplate".length());
        if (lower.endsWith("_leggings")) return name.substring(0, name.length() - "_leggings".length());
        if (lower.endsWith("_boots")) return name.substring(0, name.length() - "_boots".length());
        return name;
    }

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
