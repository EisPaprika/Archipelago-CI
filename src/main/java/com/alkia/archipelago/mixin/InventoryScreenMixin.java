package com.alkia.archipelago.mixin;

import com.alkia.archipelago.config.ModConfig;
import com.alkia.archipelago.config.PokemonSkinPreviewScreen;
import com.alkia.archipelago.custombuttons.CustomButtonUtil;
import com.alkia.archipelago.custombuttons.CustomButtonsListScreen;
import com.alkia.archipelago.custombuttons.CustomCommandButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//I've only made mods to add blocks, items, and commands before. This is my first time editing the inventory screen. Please go easy on my code...
//Inventory Modification
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {
    @Unique private boolean widthTooSmall;
    @Unique private boolean lastRecipeBookState = false;

    @Shadow public abstract RecipeBookComponent getRecipeBookComponent();

    //I have no idea if every one of these needed a @Unique tag, but Intellij kept bugging me about it. From what I found out, these seem to stop mod conflicts, so cool!
    @Unique private static final ResourceLocation DAYCARE_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "daycare_button");
    @Unique private static final ResourceLocation GTS_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "gts_button");
    @Unique private static final ResourceLocation SHOP_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "balance_button");
    @Unique private static final ResourceLocation PC_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "pc_button");
    @Unique private static final ResourceLocation POKEBUILDER_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "pokebuilder_button");
    @Unique private static final ResourceLocation DAILY_REWARDS_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "daily_rewards_button");
    @Unique private static final ResourceLocation BATTLE_PASS_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "battle_pass_button");
    @Unique private static final ResourceLocation REALMS_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "realms_button");
    @Unique private static final ResourceLocation CRAFTING_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "crafting_button");
    @Unique private static final ResourceLocation CRAFTING_ICON_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "crafting_button_hover");
    @Unique private static final ResourceLocation ENDER_CHEST_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "ender_chest_button");
    @Unique private static final ResourceLocation ENDER_CHEST_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "ender_chest_button_hover");
    @Unique private static final ResourceLocation UTILITY_BACKGROUND_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "utility_bar");
    @Unique private static final ResourceLocation KITS_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "kits_button");
    @Unique private static final ResourceLocation KITS_ICON_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "kits_button_hover");
    @Unique private static final ResourceLocation RSW_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "rsw_button");
    @Unique private static final ResourceLocation RSW_ICON_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "rsw_button_hover");
    @Unique private static final ResourceLocation MAILBOX_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "mailbox_button");
    @Unique private static final ResourceLocation MAILBOX_ICON_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "mailbox_button_hover");
    @Unique private static final ResourceLocation TRAINERCARD_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "trainercard_button");
    @Unique private static final ResourceLocation TRAINERCARD_ICON_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "trainercard_button_hover");
    @Unique private static final ResourceLocation HOME_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "home_button");
    @Unique private static final ResourceLocation HOME_ICON_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "home_button_hover");
    @Unique private static final ResourceLocation OPTIONS_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "options_button");
    @Unique private static final ResourceLocation OPTIONS_ICON_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "options_button_hover");
    @Unique private static final ResourceLocation BALLOONS_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "balloon_widget");
    @Unique private static final ResourceLocation BALLOONS_ICON_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "balloon_widget_hover");
    @Unique private static final ResourceLocation VEHICLE_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "vehicle_widget");
    @Unique private static final ResourceLocation VEHICLE_ICON_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "vehicle_widget_hover");
    @Unique private static final ResourceLocation STONECUTTER_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "stonecutter_widget");
    @Unique private static final ResourceLocation STONECUTTER_ICON_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "stonecutter_widget_hover");
    @Unique private static final ResourceLocation BALLOONS_ICON_ID_BUTTON = ResourceLocation.fromNamespaceAndPath("archipelago", "balloon_button");
    @Unique private static final ResourceLocation VEHICLES_ICON_ID_BUTTON = ResourceLocation.fromNamespaceAndPath("archipelago", "vehicle_button");
    @Unique private static final ResourceLocation WONDERTRADE_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "wondertrade_button");
    @Unique private static final ResourceLocation ANVIL_ICON_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "anvil_widget");
    @Unique private static final ResourceLocation ANVIL_ICON_HOVER_ID = ResourceLocation.fromNamespaceAndPath("archipelago", "anvil_widget_hover");

    public InventoryScreenMixin(InventoryMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addArchipelagoButtons(CallbackInfo ci) {
        //Gets Recipe book state to use later for fixing issues with Recipe book not hiding buttons.
        this.lastRecipeBookState = this.getRecipeBookComponent().isVisible();
        if (this.lastRecipeBookState && !this.widthTooSmall) {
            return;
        }
        //Runs the methods to add buttons to the inventory screen.
        this.addConfigurableButtons();
        this.addBottomWidgets();
    }
    //Method for adding the buttons that appear on the left side of the screen.
    @Unique
    private void addConfigurableButtons() {
        final int baseButtonY = this.topPos + 10;
        final int buttonSpacing = 25;
        final int baseButtonX = this.leftPos - 30;
        final int buttonXSpacing = 25;
        final int maxPerColumn = 6;
        int buttonCount = 0;

// New implementation of Buttons, accounting for buttonOrder to allow rearrangement.
        for (String buttonId : ModConfig.buttonOrder) {
            int buttonX = baseButtonX - ((buttonCount / maxPerColumn) * buttonXSpacing);
            int buttonY = baseButtonY + ((buttonCount % maxPerColumn) * buttonSpacing);
            if (buttonId.startsWith("custom_")) {
                try {
                    int index = Integer.parseInt(buttonId.substring(7));
                    if (index < ModConfig.customButtons.size()) {
                        CustomCommandButton entry = ModConfig.customButtons.get(index);
                        if (entry != null && entry.enabled) {
                            this.addCustomCommandButton(buttonX, buttonY, entry);
                            buttonCount++;
                        }
                    }
                } catch (Exception ignored) {}
                continue;
            }

            boolean added = switch (buttonId) {
                case "daycare" -> tryAddButton(ModConfig.enableDaycareButton, buttonX, buttonY, DAYCARE_ICON_ID, "daycare", "Daycare");
                case "shop" -> tryAddButton(ModConfig.enableBalanceButton, buttonX, buttonY, SHOP_ICON_ID, "shop", "Shop");
                case "gts" -> tryAddButton(ModConfig.enableGtsButton, buttonX, buttonY, GTS_ICON_ID, "gts", "GTS");
                case "pc" -> tryAddButton(ModConfig.enablePCButton, buttonX, buttonY, PC_ICON_ID, "pc", "PC");
                case "pokebuilder" -> tryAddButton(ModConfig.enablePokebuilderButton, buttonX, buttonY, POKEBUILDER_ICON_ID, "pokebuilder", "Pokebuilder");
                case "realms" -> tryAddButton(ModConfig.enableRealmsButton, buttonX, buttonY, REALMS_ICON_ID, "realms", "Realms");
                case "wondertrade" -> tryAddButton(ModConfig.enableWondertradeButton, buttonX, buttonY, WONDERTRADE_ICON_ID, "wondertrade", "Wondertrade");
                case "stonecutter" -> tryAddItemButton(ModConfig.enableStonecutterButton, buttonX, buttonY, "minecraft:stonecutter", "stonecutter", "Stonecutter");
                case "anvil" -> tryAddItemButton(ModConfig.enableAnvilButton, buttonX, buttonY, "minecraft:anvil", "anvil", "Anvil");
                case "previewer" -> tryAddMenuButton(ModConfig.enablePreviewerButton, buttonX, buttonY, "minecraft:gold_nugget[minecraft:custom_model_data=61]",
                        () -> {
                    if (this.minecraft != null) {
                            this.minecraft.setScreen(new PokemonSkinPreviewScreen(this));
                        }
                    }, "Skin Previewer");
                case "balloons" -> tryAddButton(ModConfig.enableBalloonsButton, buttonX, buttonY, BALLOONS_ICON_ID_BUTTON, "balloons", "Balloons");
                case "vehicle" -> tryAddButton(ModConfig.enableVehiclesButton, buttonX, buttonY, VEHICLES_ICON_ID_BUTTON, "vehicle", "Vehicles");
                default -> false;
            };
            if (added) buttonCount++;
        }
        //+ Button for adding Custom Buttons. Funny name.
        if (ModConfig.enableCustomButtonsButton) {
            this.addRenderableWidget(Button.builder(Component.literal("+"), button -> {
                if (this.minecraft != null) this.minecraft.setScreen(new CustomButtonsListScreen(this));
            }).bounds(this.leftPos - 27, this.topPos + 166, 20, 20).tooltip(Tooltip.create(Component.literal("Custom Buttons"))).build());
        }
        //Daily Rewards. This is super hacky, not really happy with how it's placed atm. Is there a way to anchor to a different part of the GUI?
        if (ModConfig.enableDailyRewards)
            this.addArchipelagoButton(this.leftPos + 150, this.topPos - 30, DAILY_REWARDS_ICON_ID, "dailyrewards", "Daily Rewards");
        //Cobblemon Battle pass, Considered changing every button to say "Browse Addons!" so it'd be more Mojang-adjacent, but figured adding a battlepass button was pretty cool instead.
        if (ModConfig.enableBattlepass)
            this.addArchipelagoButton(this.leftPos + 125, this.topPos - 30, BATTLE_PASS_ICON_ID, "bp", "Battle Pass");
    }
    //Widgets on bottom of inventory.
    @Unique
    private void addBottomWidgets() {
        int widgetX = this.leftPos + 163;
        int widgetSpacing = 10;
        //Crafting
        if (ModConfig.enableWorkbenchButton) this.addArchipelagoWidget(this.leftPos + 77, this.topPos + 43, 16, 16, CRAFTING_ICON_ID, CRAFTING_ICON_HOVER_ID, "workbench");
        //Echest
        if (ModConfig.enableEnderchestButton) this.addArchipelagoWidget(this.leftPos + 77, this.topPos + 26, 16, 16, ENDER_CHEST_ID, ENDER_CHEST_HOVER_ID, "ec");
        //Settings
        this.addArchipelagoWidget(widgetX, this.topPos + 166, 12, 7, OPTIONS_ICON_ID, OPTIONS_ICON_HOVER_ID, () -> {
            if (this.minecraft != null) this.minecraft.setScreen(ModConfig.createScreen(this));
        });
        //Widgets
        widgetX -= widgetSpacing;
        if (ModConfig.enableKitsButton) { addArchipelagoWidget(widgetX, this.topPos + 166, 11, 7, KITS_ICON_ID, KITS_ICON_HOVER_ID, "kits", "Kits"); widgetX -= widgetSpacing; }
        if (ModConfig.enableRSWWidget) { addArchipelagoWidget(widgetX, this.topPos + 166, 11, 7, RSW_ICON_ID, RSW_ICON_HOVER_ID, "rsw", "Resource World"); widgetX -= widgetSpacing; }
        if (ModConfig.enableMailboxButton) { addArchipelagoWidget(widgetX, this.topPos + 166, 11, 7, MAILBOX_ICON_ID, MAILBOX_ICON_HOVER_ID, "mailbox", "Mailbox"); widgetX -= widgetSpacing; }
        if (ModConfig.enableTrainercardButton) { addArchipelagoWidget(widgetX, this.topPos + 166, 11, 7, TRAINERCARD_ICON_ID, TRAINERCARD_ICON_HOVER_ID, "trainercard", "Trainercard"); widgetX -= widgetSpacing; }
        if (ModConfig.enableBalloonsWidget) { addArchipelagoWidget(widgetX, this.topPos + 166, 11, 7, BALLOONS_ICON_ID, BALLOONS_ICON_HOVER_ID, "balloons", "Balloons"); widgetX -= widgetSpacing; }
        if (ModConfig.enableVehiclesWidget) { addArchipelagoWidget(widgetX, this.topPos + 166, 11, 7, VEHICLE_ICON_ID, VEHICLE_ICON_HOVER_ID, "vehicle", "Vehicles"); widgetX -= widgetSpacing; }
        if (ModConfig.enableStonecutterWidget) { addArchipelagoWidget(widgetX, this.topPos + 166, 11, 7, STONECUTTER_ICON_ID, STONECUTTER_ICON_HOVER_ID, "stonecutter", "Stonecutter"); widgetX -= widgetSpacing; }
        if (ModConfig.enableAnvilWidget) { addArchipelagoWidget(widgetX, this.topPos + 166, 11, 7, ANVIL_ICON_ID, ANVIL_ICON_HOVER_ID, "anvil", "Anvil"); widgetX -= widgetSpacing; }
        if (ModConfig.enableHomeButton3) { addArchipelagoWidget(widgetX, this.topPos + 166, 11, 7, HOME_ICON_ID, HOME_ICON_HOVER_ID, getHomeCommand(ModConfig.homeName3, "home3"), ModConfig.homeName3); widgetX -= widgetSpacing; }
        if (ModConfig.enableHomeButton2) { addArchipelagoWidget(widgetX, this.topPos + 166, 11, 7, HOME_ICON_ID, HOME_ICON_HOVER_ID, getHomeCommand(ModConfig.homeName2, "home2"), ModConfig.homeName2); widgetX -= widgetSpacing; }
        if (ModConfig.enableHomeButton) { addArchipelagoWidget(widgetX, this.topPos + 166, 11, 7, HOME_ICON_ID, HOME_ICON_HOVER_ID, getHomeCommand(ModConfig.homeName, "home"), ModConfig.homeName); }
    }
    //Buttons with custom sprites.
    @Unique
    private boolean tryAddButton(boolean enabled, int x, int y, ResourceLocation icon, String cmd, String tooltip) {
        if (!enabled) return false;
        this.addArchipelagoButton(x, y, icon, cmd, tooltip);
        return true;
    }
    //Buttons with Item icons instead of sprites.
    @Unique
    private boolean tryAddItemButton(boolean enabled, int x, int y, String item, String cmd, String tooltip) {
        if (!enabled) return false;
        this.addArchipelagoItemButton(x, y, item, cmd, tooltip);
        return true;
    }
    @Unique
    private boolean tryAddMenuButton(boolean enabled, int x, int y, String item, Runnable action, String tooltip) {
        if (!enabled) return false;
        this.addArchipelagoMenuButton(x, y, item, action, tooltip);
        return true;
    }
    //Used for grabbing whatever the player set as their home.
    @Unique
    private String getHomeCommand(String name, String defaultCmd) {
        if (name == null || name.trim().isEmpty()) return defaultCmd;
        return "home " + name.trim();
    }

    @Inject(method = "renderBg", at = @At("HEAD"))
    private void drawSpriteBehindInventory(GuiGraphics graphics, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        graphics.blitSprite(UTILITY_BACKGROUND_ID, this.leftPos, this.topPos + 156, 176, 18);
    }
    //Handles background sprites. (Sprites behind the inventory)
    @Unique
    private void addArchipelagoButton(int x, int y, ResourceLocation icon, String command, String tooltipText) {
        this.addRenderableWidget(Button.builder(Component.empty(), button -> {
                    if (this.minecraft != null && this.minecraft.player != null) {
                        this.minecraft.player.connection.sendUnsignedCommand(command);
                    }
                })
                .bounds(x, y, 26, 26)
                .tooltip(Tooltip.create(Component.literal(tooltipText)))
                .build());

        this.addRenderableOnly((graphics, a, b, c) -> {
            graphics.blitSprite(icon, x + 5, y + 5, 16, 16);
        });
    }
    //Method to add buttons. Reused with every button. Might be a more efficient way, but eh, it works.
    @Unique
    private void addArchipelagoItemButton(int x, int y, String itemId, String command, String tooltipText) {
        this.addRenderableWidget(Button.builder(Component.empty(), button -> {
                    if (this.minecraft != null && this.minecraft.player != null) {
                        this.minecraft.player.connection.sendUnsignedCommand(command);
                    }
                })
                .bounds(x, y, 26, 26)
                .tooltip(Tooltip.create(Component.literal(tooltipText)))
                .build());
//Draws the icon on top of the button. From what i could tell, this seems to be the only way to do this?
        this.addRenderableOnly((graphics, a, b, c) -> {
            CustomButtonUtil.drawIcon(graphics, itemId, x + 5,  y + 5);
        });
    }
    //Just used for Skin Previewer Button atm.
    @Unique
    private void addArchipelagoMenuButton(int x, int y, String itemId, Runnable opPress, String tooltipText) {
        this.addRenderableWidget(Button.builder(Component.empty(), button -> opPress.run())
                .bounds(x, y, 26, 26)
                .tooltip(Tooltip.create(Component.literal(tooltipText)))
                .build());
        this.addRenderableOnly((graphics, a, b, c) -> {
            CustomButtonUtil.drawIcon(graphics, itemId, x + 5,  y + 5);
        });
    }
    // Settings Widget
    @Unique
    private void addArchipelagoWidget(int x, int y, int width, int height, ResourceLocation icon, ResourceLocation hoverIcon, Runnable opPress) {
        WidgetSprites sprites = new WidgetSprites(icon, hoverIcon);
        this.addRenderableWidget(new ImageButton(x, y, width, height, sprites, button -> opPress.run()));
    }

    @Unique
    private void addArchipelagoWidget(int x, int y, int width, int height, ResourceLocation icon, ResourceLocation hoverIcon, String command) {
        WidgetSprites sprites = new WidgetSprites(icon, hoverIcon);
        this.addRenderableWidget(new ImageButton(x, y, width, height, sprites, button -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.connection.sendUnsignedCommand(command);
            }
        }));
    }

    @Unique
    private void addArchipelagoWidget(int x, int y, int width, int height, ResourceLocation icon, ResourceLocation hoverIcon, String command, String tooltipText) {
        WidgetSprites sprites = new WidgetSprites(icon, hoverIcon);
        ImageButton widget = new ImageButton(x, y, width, height, sprites, button -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.connection.sendUnsignedCommand(command);
            }
        });
        if (tooltipText != null) widget.setTooltip(Tooltip.create(Component.literal(tooltipText)));
        this.addRenderableWidget(widget);
    }


    //Specifically used for Custom Buttons.
    @Unique
    private void addCustomCommandButton(int x, int y, CustomCommandButton entry) {
        this.addRenderableWidget(Button.builder(Component.empty(), button -> {
                    CustomButtonUtil.sendCommandIfPossible(entry.command);
                })
                .bounds(x, y, 26, 26)
                .tooltip(Tooltip.create(CustomButtonUtil.tooltipOrDefault(entry.name, entry.command)))
                .build());

        this.addRenderableOnly((graphics, a, b, c) -> {
            CustomButtonUtil.drawIcon(graphics, entry.iconItemId, x + 5, y + 5);
        });
    }
    //Literally only used to fix the Recipe book bug. Why did mojang add this feature...
    @Inject(method = "containerTick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        boolean currentState = this.getRecipeBookComponent().isVisible();
        if (currentState != this.lastRecipeBookState) {
            this.lastRecipeBookState = currentState;
            this.rebuildWidgets();
        }
    }
}