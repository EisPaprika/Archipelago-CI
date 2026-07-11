package com.alkia.archipelago.config;

import com.alkia.archipelago.custombuttons.CustomButtonsListScreen;
import com.alkia.archipelago.custombuttons.CustomCommandButton;
import com.alkia.archipelago.custombuttons.RearrangeButtonsScreen;
import com.google.gson.*;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.clothconfig2.api.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {


    public static boolean enableDaycareButton = true;
    public static boolean enableGtsButton = true;
    public static boolean enableBalanceButton = true;
    public static boolean enablePCButton = true;
    public static boolean enablePokebuilderButton = true;
    public static boolean enableRealmsButton = true;
    public static boolean enableKitsButton = true;
    public static boolean enableRSWButton = true;
    public static boolean enableMailboxButton = true;
    public static boolean enableTrainercardButton = true;
    public static boolean enableHomeButton = true;
    public static String homeName = "home";
    public static boolean enableHomeButton2 = false;
    public static String homeName2 = "home2";
    public static boolean enableHomeButton3 = false;
    public static String homeName3 = "home3";
    public static boolean enableBalloonsButton = true;
    public static boolean enableVehiclesButton = true;
    public static boolean enableStonecutterButton = true;
    public static boolean enablePreviewerButton = false;
    public static boolean enableWondertradeButton = true;
    public static boolean enableRSWWidget = true;
    public static boolean enableBalloonsWidget = true;
    public static boolean enableVehiclesWidget = true;
    public static boolean enableStonecutterWidget = true;
    public static boolean enableEnderchestButton = true;
    public static boolean enableWorkbenchButton = true;
    public static boolean enableAnvilButton = false;
    public static boolean enableAnvilWidget = false;
    public static boolean enableDailyRewards = true;
    public static boolean enableBattlepass = true;
    public static boolean enableCustomButtonsButton = true;
    public static boolean enableEvolutionScrollling = true;
    public static boolean enableItemFrameSkins = true;
    public static boolean tapPreview = false;
    public static List<String> buttonOrder = new ArrayList<>();
    public static List<CustomCommandButton> customButtons = new ArrayList<>();

    public static final List<String> DEFAULT_BUTTON_ORDER = List.of(
            "daycare", "shop", "gts", "pc", "pokebuilder",
            "realms", "wondertrade", "stonecutter", "anvil",
            "balloons", "vehicle", "previewer"
    );
    public static boolean enableShinyIcon = false;
    public static boolean enablePreviewBackground = true;
    public static boolean enabletokensintooltips = true;
    public static boolean enableSkinDebug = false;
    public static boolean enableParticleDebug = false;
    // Egg & Feed Keys
    public static ModifierKeyCode feedKey = ModifierKeyCode.of(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(96), me.shedaniel.clothconfig2.api.Modifier.none());
    public static ModifierKeyCode egginfo1Key = ModifierKeyCode.unknown();
    public static ModifierKeyCode egginfo2Key = ModifierKeyCode.unknown();
    public static ModifierKeyCode egginfo3Key = ModifierKeyCode.unknown();
    public static ModifierKeyCode egginfo4Key = ModifierKeyCode.unknown();
    public static ModifierKeyCode egginfo5Key = ModifierKeyCode.unknown();
    public static ModifierKeyCode egginfo6Key = ModifierKeyCode.unknown();

    public static ModifierKeyCode previewKey = ModifierKeyCode.of(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(340), me.shedaniel.clothconfig2.api.Modifier.none());
    public static ModifierKeyCode zoomKey = ModifierKeyCode.of(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(342), me.shedaniel.clothconfig2.api.Modifier.none());
    public static ModifierKeyCode shinyKey = ModifierKeyCode.of(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(341), me.shedaniel.clothconfig2.api.Modifier.none());
    public static ModifierKeyCode formKey = ModifierKeyCode.of(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(78), me.shedaniel.clothconfig2.api.Modifier.none());
    public static ModifierKeyCode megaKey = ModifierKeyCode.of(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(77), me.shedaniel.clothconfig2.api.Modifier.none());
//    public static ModifierKeyCode gmaxKey = ModifierKeyCode.of(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(71), me.shedaniel.clothconfig2.api.Modifier.none());
    public static ModifierKeyCode evolutionKey = ModifierKeyCode.of(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(82), me.shedaniel.clothconfig2.api.Modifier.none());

    //KeyCodes for future reference
//8 - Backspace
//9 - Tab
//12 - 5 in the numeric keypad when Num Lock is off
//13 - Enter
//16 - Shift
//17 - Ctrl
//18 - Alt
//19 - Pause/Break
//20 - Caps Lock
//27 - Esc
//32 - Space
//33 - Page Up
//34 - Page Down
//35 - End
//36 - Home
//37 - Left arrow
//38 - Up arrow
//39 - Right arrow
//40 - Down arrow
//44 - Print Screen
//45 - Insert
//46 - Delete
//48 - 0
//49 - 1
//50 - 2
//51 - 3
//52 - 4
//53 - 5
//54 - 6
//55 - 7
//56 - 8
//57 - 9
//65 - A
//66 - B
//67 - C
//68 - D
//69 - E
//70 - F
//71 - G
//72 - H
//73 - I
//74 - J
//75 - K
//76 - L
//77 - M
//78 - N
//79 - O
//80 - P
//81 - Q
//82 - R
//83 - S
//84 - T
//85 - U
//86 - V
//87 - W
//88 - X
//89 - Y
//90 - Z
//91 - left Win
//92 - right Win
//93 - Popup
//96 - 0 in the numeric keypad
//97 - 1 in the numeric keypad
//98 - 2 in the numeric keypad
//99 - 3 in the numeric keypad
//100 - 4 in the numeric keypad
//101 - 5 in the numeric keypad
//102 - 6 in the numeric keypad
//103 - 7 in the numeric keypad
//104 - 8 in the numeric keypad
//105 - 9 in the numeric keypad
//106 - * in the numeric keypad
//107 - + in the numeric keypad
//109 - - in the numeric keypad
//110 - . in the numeric keypad
//111 - / in the numeric keypad
//112 - F1
//113 - F2
//114 - F3
//115 - F4
//116 - F5
//117 - F6
//118 - F7
//119 - F8
//120 - F9
//121 - F10
//122 - F11
//123 - F12
//144 - Num Lock
//145 - Scroll Lock
//160 - left Shift
//161 - right Shift
//162 - left Ctrl
//163 - right Ctrl

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ModifierKeyCode.class, new JsonSerializer<ModifierKeyCode>() {
                @Override
                public JsonElement serialize(ModifierKeyCode src, Type typeOfSrc, JsonSerializationContext context) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("key", src.getKeyCode().getName());
                    Modifier modifier = src.getModifier();
                    obj.addProperty("shift", modifier.hasShift());
                    obj.addProperty("control", modifier.hasControl());
                    obj.addProperty("alt", modifier.hasAlt());
                    return obj;
                }
            })
            .registerTypeAdapter(ModifierKeyCode.class, new JsonDeserializer<ModifierKeyCode>() {
                @Override
                public ModifierKeyCode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    JsonObject obj = json.getAsJsonObject();
                    return ModifierKeyCode.of(
                            InputConstants.getKey(obj.get("key").getAsString()),
                            Modifier.of(
                                    obj.get("shift").getAsBoolean(),
                                    obj.get("control").getAsBoolean(),
                                    obj.get("alt").getAsBoolean()
                            )
                    );
                }
            })
            .setPrettyPrinting()
            .create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("archipelago.json");


    //There's gotta be a more efficient way to do this.........
    private static final class Data {
        boolean enableDaycareButton = true;
        boolean enableGtsButton = true;
        boolean enableBalanceButton = true;
        boolean enablePCButton = true;
        boolean enablePokebuilderButton = true;
        boolean enableRealmsButton = true;
        boolean enableKitsButton = true;
        boolean enableRSWButton = true;
        boolean enableMailboxButton = true;
        boolean enableTrainercardButton = true;
        boolean enableHomeButton = true;
        String homeName = "home";
        boolean enableHomeButton2 = false;
        String homeName2 = "home2";
        boolean enableHomeButton3 = false;
        String homeName3 = "home3";
        boolean enableBalloonsButton = false;
        boolean enableVehiclesButton = false;
        boolean enableStonecutterButton = false;
        boolean enableWondertradeButton = true;
        boolean enableRSWWidget = true;
        boolean enableBalloonsWidget = true;
        boolean enableVehiclesWidget = true;
        boolean enableStonecutterWidget = false;
        boolean enableEnderchestButton = false;
        boolean enableWorkbenchButton = false;
        boolean enableAnvilButton = false;
        boolean enablePreviewerButton = false;
        boolean enableAnvilWidget = false;
        boolean enableDailyRewards = true;
        boolean enableBattlepass = true;
        boolean enableCustomButtonsButton = true;
        boolean enableShinyIcon = false;
        boolean enablePreviewBackground = true;
        boolean enableSkinDebug = false;
        boolean enableParticleDebug = false;
        boolean enableEvolutionScrollling = true;
        boolean enabletokensintooltips = true;
        boolean enableItemFrameSkins = true;
        boolean tapPreview = false;
        ModifierKeyCode previewKey = ModConfig.previewKey;
        ModifierKeyCode zoomKey = ModConfig.zoomKey;
        ModifierKeyCode shinyKey = ModConfig.shinyKey;
        ModifierKeyCode feedKey = ModConfig.feedKey;
        ModifierKeyCode formKey = ModConfig.formKey;
        ModifierKeyCode megaKey = ModConfig.megaKey;
        ModifierKeyCode evolutionKey = ModConfig.evolutionKey;
        ModifierKeyCode egginfo1Key = ModConfig.egginfo1Key;
        ModifierKeyCode egginfo2Key = ModConfig.egginfo2Key;
        ModifierKeyCode egginfo3Key = ModConfig.egginfo3Key;
        ModifierKeyCode egginfo4Key = ModConfig.egginfo4Key;
        ModifierKeyCode egginfo5Key = ModConfig.egginfo5Key;
        ModifierKeyCode egginfo6Key = ModConfig.egginfo6Key;
        List<String> buttonOrder = new ArrayList<>();
        List<CustomCommandButton> customButtons = new ArrayList<>();
    }

    public static Screen createScreen(Screen parent) {
        return new ArchipelagoConfigHubScreen(parent);
    }

    public static void loadConfig() {
        for (String id : DEFAULT_BUTTON_ORDER) {
            if (!buttonOrder.contains(id)) {
                buttonOrder.add(id);
            }
        }
        for (int i = 0; i < customButtons.size(); i++) {
            String id = "custom_" + i;
            if (!buttonOrder.contains(id)) {
                buttonOrder.add(id);
            }
        }
        if (!Files.exists(CONFIG_PATH)) {
            return;
        }
        try {
            String json = Files.readString(CONFIG_PATH);
            Data data = GSON.fromJson(json, Data.class);
            if (data == null) return;
            enableDaycareButton = data.enableDaycareButton;
            enableGtsButton = data.enableGtsButton;
            enableBalanceButton = data.enableBalanceButton;
            enablePCButton = data.enablePCButton;
            enablePokebuilderButton = data.enablePokebuilderButton;
            enableRealmsButton = data.enableRealmsButton;
            enableKitsButton = data.enableKitsButton;
            enableRSWButton = data.enableRSWButton;
            enableMailboxButton = data.enableMailboxButton;
            enableTrainercardButton = data.enableTrainercardButton;
            enableHomeButton = data.enableHomeButton;
            enableHomeButton2 = data.enableHomeButton2;
            enableHomeButton3 = data.enableHomeButton3;
            homeName = (data.homeName == null ? "home" : data.homeName);
            homeName2 = (data.homeName2 == null ? "home2" : data.homeName2);
            homeName3 = (data.homeName3 == null ? "home3" : data.homeName3);
            enableBalloonsButton = data.enableBalloonsButton;
            enableVehiclesButton = data.enableVehiclesButton;
            enableStonecutterButton = data.enableStonecutterButton;
            enablePreviewerButton = data.enablePreviewerButton;
            enableWondertradeButton = data.enableWondertradeButton;
            enableDailyRewards = data.enableDailyRewards;
            enableBattlepass = data.enableBattlepass;
            enableRSWWidget = data.enableRSWWidget;
            enableBalloonsWidget = data.enableBalloonsWidget;
            enableVehiclesWidget = data.enableVehiclesWidget;
            enableStonecutterWidget = data.enableStonecutterWidget;
            enableEnderchestButton = data.enableEnderchestButton;
            enableWorkbenchButton = data.enableWorkbenchButton;
            enableAnvilButton = data.enableAnvilButton;
            enableAnvilWidget = data.enableAnvilWidget;
            enableCustomButtonsButton = data.enableCustomButtonsButton;
            enableEvolutionScrollling = data.enableEvolutionScrollling;
            enabletokensintooltips = data.enabletokensintooltips;
            enableItemFrameSkins = data.enableItemFrameSkins;
            tapPreview = data.tapPreview;
            buttonOrder = (data.buttonOrder == null) ? new ArrayList<>() : new ArrayList<>(data.buttonOrder);
            for (String id : DEFAULT_BUTTON_ORDER) {
                if (!buttonOrder.contains(id)) {
                    buttonOrder.add(id);
                }
            }
            customButtons = (data.customButtons == null) ? new ArrayList<>() : new ArrayList<>(data.customButtons);
            for (int i = 0; i < customButtons.size(); i++) {
                String id = "custom_" + i;
                if (!buttonOrder.contains(id)) {
                    buttonOrder.add(id);
                }
            }
            enableShinyIcon = data.enableShinyIcon;
            enablePreviewBackground = data.enablePreviewBackground;
            enableSkinDebug = data.enableSkinDebug;
            enableParticleDebug = data.enableParticleDebug;
            previewKey = data.previewKey;
            zoomKey = data.zoomKey;
            shinyKey = data.shinyKey;
            feedKey = data.feedKey;
            megaKey = data.megaKey;
            formKey = data.formKey;
            evolutionKey = data.evolutionKey;
            egginfo1Key = data.egginfo1Key;
            egginfo2Key = data.egginfo2Key;
            egginfo3Key = data.egginfo3Key;
            egginfo4Key = data.egginfo4Key;
            egginfo5Key = data.egginfo5Key;
            egginfo6Key = data.egginfo6Key;
        } catch (Exception e) {
            System.err.println("[ARCHIPELAGO] Failed to load Archipelago config!" + CONFIG_PATH);
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Data data = new Data();
            data.enableDaycareButton = enableDaycareButton;
            data.enableGtsButton = enableGtsButton;
            data.enableBalanceButton = enableBalanceButton;
            data.enablePCButton = enablePCButton;
            data.enablePokebuilderButton = enablePokebuilderButton;
            data.enableRealmsButton = enableRealmsButton;
            data.enableKitsButton = enableKitsButton;
            data.enableRSWButton = enableRSWButton;
            data.enableMailboxButton = enableMailboxButton;
            data.enableTrainercardButton = enableTrainercardButton;
            data.enableHomeButton = enableHomeButton;
            data.enableHomeButton2 = enableHomeButton2;
            data.enableHomeButton3 = enableHomeButton3;
            data.homeName = homeName;
            data.homeName2 = homeName2;
            data.homeName3 = homeName3;
            data.enableBalloonsButton = enableBalloonsButton;
            data.enableVehiclesButton = enableVehiclesButton;
            data.enableStonecutterButton = enableStonecutterButton;
            data.enableWondertradeButton = enableWondertradeButton;
            data.enableRSWWidget = enableRSWWidget;
            data.enableBalloonsWidget = enableBalloonsWidget;
            data.enableVehiclesWidget = enableVehiclesWidget;
            data.enableStonecutterWidget = enableStonecutterWidget;
            data.enableEnderchestButton = enableEnderchestButton;
            data.enableWorkbenchButton = enableWorkbenchButton;
            data.enableAnvilButton = enableAnvilButton;
            data.enablePreviewerButton = enablePreviewerButton;
            data.enableAnvilWidget = enableAnvilWidget;
            data.enableDailyRewards = enableDailyRewards;
            data.enableBattlepass = enableBattlepass;
            data.enableCustomButtonsButton = enableCustomButtonsButton;
            data.buttonOrder = (buttonOrder == null) ? new ArrayList<>() : new ArrayList<>(buttonOrder);
            data.customButtons = (customButtons == null) ? new ArrayList<>() : new ArrayList<>(customButtons);
            data.enableShinyIcon = enableShinyIcon;
            data.enablePreviewBackground = enablePreviewBackground;
            data.enableSkinDebug = enableSkinDebug;
            data.enableParticleDebug = enableParticleDebug;
            data.enabletokensintooltips = enabletokensintooltips;
            data.enableEvolutionScrollling = enableEvolutionScrollling;
            data.enableItemFrameSkins = enableItemFrameSkins;
            data.tapPreview = tapPreview;
            data.previewKey = previewKey;
            data.zoomKey = zoomKey;
            data.shinyKey = shinyKey;
            data.feedKey = feedKey;
            data.megaKey = megaKey;
            data.formKey = formKey;
            data.evolutionKey = evolutionKey;
            data.egginfo1Key = egginfo1Key;
            data.egginfo2Key = egginfo2Key;
            data.egginfo3Key = egginfo3Key;
            data.egginfo4Key = egginfo4Key;
            data.egginfo5Key = egginfo5Key;
            data.egginfo6Key = egginfo6Key;
            Files.writeString(CONFIG_PATH, GSON.toJson(data));

        } catch (Exception e) {
            System.err.println("[ARCHIPELAGO] Failed to save Archipelago config!" + CONFIG_PATH);
            e.printStackTrace();
        }
    }

    public static Screen createClothConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.archipelago.config"));

        builder.setSavingRunnable(ModConfig::saveConfig);

        ConfigCategory buttons = builder.getOrCreateCategory(Component.translatable("category.archipelago.buttons"));
        ConfigCategory widgets = builder.getOrCreateCategory(Component.translatable("category.archipelago.widgets"));
        ConfigCategory misc = builder.getOrCreateCategory(Component.translatable("category.archipelago.misc"));
        ConfigCategory home1 = builder.getOrCreateCategory(Component.translatable("category.archipelago.home1"));
        ConfigCategory home2 = builder.getOrCreateCategory(Component.translatable("category.archipelago.home2"));
        ConfigCategory home3 = builder.getOrCreateCategory(Component.translatable("category.archipelago.home3"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_daycare"), enableDaycareButton)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("tooltip.archipelago.enable_daycare"))
                .setSaveConsumer(newValue -> enableDaycareButton = newValue)
                .build());

        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_gts"), enableGtsButton)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableGtsButton = newValue)
                .build());

        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_balance"), enableBalanceButton)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableBalanceButton = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_PC"), enablePCButton)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enablePCButton = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Pokebuilder"), enablePokebuilderButton)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enablePokebuilderButton = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Realms"), enableRealmsButton)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableRealmsButton = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_stonecutter"), enableStonecutterButton)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableStonecutterButton = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_balloons"), enableBalloonsButton)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableBalloonsButton = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_vehicles"), enableVehiclesButton)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableVehiclesButton = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.daily_rewards"), enableDailyRewards)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableDailyRewards = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.battlepass"), enableBattlepass)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableBattlepass = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.previewer"), enablePreviewerButton)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enablePreviewerButton = newValue)
                .build());
        widgets.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Kits"), enableKitsButton)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableKitsButton = newValue)
                .build());
        widgets.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_RSW"), enableRSWWidget)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableRSWWidget = newValue)
                .build());
        widgets.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Mailbox"), enableMailboxButton)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableMailboxButton = newValue)
                .build());
        widgets.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Trainercard"), enableTrainercardButton)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableTrainercardButton = newValue)
                .build());
        widgets.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Balloonswidget"), enableBalloonsWidget)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableBalloonsWidget = newValue)
                .build());
        widgets.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Vehicleswidget"), enableVehiclesWidget)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableVehiclesWidget = newValue)
                .build());
        widgets.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Stonecutterwidget"), enableStonecutterWidget)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableStonecutterWidget = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Wondertradewidget"), enableWondertradeButton)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableWondertradeButton = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Workbench"), enableWorkbenchButton)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableWorkbenchButton = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Enderchest"), enableEnderchestButton)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableEnderchestButton = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Anvil"), enableAnvilButton)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableAnvilButton = newValue)
                .build());
        buttons.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_custom_buttons_button"), enableCustomButtonsButton)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableCustomButtonsButton = newValue)
                .build());
        widgets.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Anvil"), enableAnvilWidget)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableAnvilWidget = newValue)
                .build());
        home1.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Home"), enableHomeButton)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableHomeButton = newValue)
                .build());
        home1.addEntry(entryBuilder.startStrField(
                        Component.translatable("option.archipelago.home_name"),
                        homeName
                )
                .setDefaultValue("home")
                .setSaveConsumer(newValue -> homeName = (newValue == null ? "" : newValue))
                .build());
        home2.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Home2"), enableHomeButton2)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableHomeButton2 = newValue)
                .build());
        home2.addEntry(entryBuilder.startStrField(
                        Component.translatable("option.archipelago.home_name2"),
                        homeName2
                )
                .setDefaultValue("home2")
                .setSaveConsumer(newValue -> homeName2 = (newValue == null ? "" : newValue))
                .build());
        home3.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_Home3"), enableHomeButton3)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableHomeButton3 = newValue)
                .build());
        home3.addEntry(entryBuilder.startStrField(
                        Component.translatable("option.archipelago.home_name3"),
                        homeName3
                )
                .setDefaultValue("home3")
                .setSaveConsumer(newValue -> homeName3 = (newValue == null ? "" : newValue))
                .build());

        misc.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_shiny_icon"), enableShinyIcon)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableShinyIcon = newValue)
                .build());
        misc.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_preview_background"), enablePreviewBackground)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enablePreviewBackground = newValue)
                .build());
        misc.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_skin_debug"), enabletokensintooltips)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enabletokensintooltips = newValue)
                .build());
        misc.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_item_frame_skins"), enableItemFrameSkins)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableItemFrameSkins = newValue)
                .setTooltip(Component.translatable("tooltip.archipelago.enable_item_frame_skins"))
                .build());
        misc.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.debug_mode"), enableSkinDebug)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableSkinDebug = newValue)
                .build());
        misc.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.particle_debug"), enableParticleDebug)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> enableParticleDebug = newValue)
                .build());
        misc.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.enable_evolution_scrolling"), enableEvolutionScrollling)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> enableEvolutionScrollling = newValue)
                .build());
        misc.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.evolution"), evolutionKey)
                .setModifierSaveConsumer(newValue -> evolutionKey = newValue)
                .build());

        //Keybinds stuffs
        ConfigCategory keybinds = builder.getOrCreateCategory(Component.translatable("category.archipelago.keybinds"));

        keybinds.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.archipelago.tap_preview"), tapPreview)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> tapPreview = newValue)
                .build());
        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.preview"), previewKey)
                .setDefaultValue(previewKey)
                .setModifierSaveConsumer(newValue -> previewKey = newValue)
                .build());

        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.zoom"), zoomKey)
                .setDefaultValue(zoomKey)
                .setModifierSaveConsumer(newValue -> zoomKey = newValue)
                .build());

        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.shiny"), shinyKey)
                .setDefaultValue(shinyKey)
                .setModifierSaveConsumer(newValue -> shinyKey = newValue)
                .build());

        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.mega"), megaKey)
                .setDefaultValue(megaKey)
                .setModifierSaveConsumer(newValue -> megaKey = newValue)
                .build());
// For future (assuming they're adding gmax at some point........?
//        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.gmax"), gmaxKey)
//                .setDefaultValue(gmaxKey)
//                .setModifierSaveConsumer(newValue -> gmaxKey = newValue)
//                .build());


        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.form"), formKey)
                .setDefaultValue(formKey)
                .setModifierSaveConsumer(newValue -> formKey = newValue)
                .build());
        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.feed"), feedKey)
                .setDefaultValue(feedKey)
                .setModifierSaveConsumer(newValue -> feedKey = newValue)
                .build());

        // Egg Info keys 1-6
        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.egginfo1"), egginfo1Key)
                .setDefaultValue(egginfo1Key)
                .setModifierSaveConsumer(newValue -> egginfo1Key = newValue)
                .build());
        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.egginfo2"), egginfo2Key)
                .setDefaultValue(egginfo2Key)
                .setModifierSaveConsumer(newValue -> egginfo2Key = newValue)
                .build());
        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.egginfo3"), egginfo3Key)
                .setDefaultValue(egginfo3Key)
                .setModifierSaveConsumer(newValue -> egginfo3Key = newValue)
                .build());
        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.egginfo4"), egginfo4Key)
                .setDefaultValue(egginfo4Key)
                .setModifierSaveConsumer(newValue -> egginfo4Key = newValue)
                .build());
        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.egginfo5"), egginfo5Key)
                .setDefaultValue(egginfo5Key)
                .setModifierSaveConsumer(newValue -> egginfo5Key = newValue)
                .build());
        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(Component.translatable("key.archipelago.egginfo6"), egginfo6Key)
                .setDefaultValue(egginfo6Key)
                .setModifierSaveConsumer(newValue -> egginfo6Key = newValue)
                .build());

        return builder.build();
    }

    //Config hub screen. Appears when you open the config in pause > mods > Archipelago or just press the settings button in inv.
    private static final class ArchipelagoConfigHubScreen extends Screen {
        private final Screen parent;

        private ArchipelagoConfigHubScreen(Screen parent) {
            super(Component.translatable("title.archipelago.config"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            int centerX = this.width / 2;
            int y = this.height / 4;

            this.addRenderableWidget(Button.builder(
                            Component.literal("Settings"),
                            button -> {
                                Minecraft client = Minecraft.getInstance();
                                client.setScreen(createClothConfigScreen(this));
                            }
                    )
                    .bounds(centerX - 100, y, 200, 20)
                    .build());

            y += 24;
            this.addRenderableWidget(Button.builder(
                            Component.literal("Custom Buttons"),
                            button -> {
                                Minecraft client = Minecraft.getInstance();
                                client.setScreen(new CustomButtonsListScreen(this));
                            })
                    .bounds(centerX - 100, y, 200, 20)
                    .build());

            y += 24;
            this.addRenderableWidget(Button.builder(
                            Component.literal("Rearrange Buttons"),
                            button -> {
                                Minecraft client = Minecraft.getInstance();
                                client.setScreen(new RearrangeButtonsScreen(this));
                            })
                    .bounds(centerX - 100, y, 200, 20)
                    .build());

            y += 24;
            this.addRenderableWidget(Button.builder(
                            Component.literal("Skin Previewer"),
                            button -> {
                                Minecraft client = Minecraft.getInstance();
                                if (client.level == null) {
                                    button.setMessage(Component.literal("Must be in-game to use!"));
                                    return;
                                }
                                client.setScreen(new PokemonSkinPreviewScreen(this));
                            }
                    )
                    .bounds(centerX - 100, y, 200, 20)
                    .build());

            y += 24;
            this.addRenderableWidget(Button.builder(
                            Component.literal("Skin Display Guide"),
                            button -> {
                                Minecraft client = Minecraft.getInstance();
                                client.setScreen(new SkinDisplayGuideScreen(this));
                            })
                    .bounds(centerX - 100, y, 200, 20)
                    .build());
            y += 24;
            this.addRenderableWidget(Button.builder(
                            Component.literal("Credits"),
                            button -> {
                                Minecraft client = Minecraft.getInstance();
                                client.setScreen(new CreditsScreen(this));
                            })
                    .bounds(centerX - 100, y, 200, 20)
                    .build());

            y += 36;
            this.addRenderableWidget(Button.builder(
                            Component.literal("Done"),
                            button -> {
                                Minecraft client = Minecraft.getInstance();
                                client.setScreen(parent);
                            })
                    .bounds(centerX - 100, y, 200, 20)
                    .build());
        }

        @Override
        public void onClose() {
            if (this.minecraft != null) {
                this.minecraft.setScreen(parent);
            }
        }
    }
}
