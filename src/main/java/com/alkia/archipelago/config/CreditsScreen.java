package com.alkia.archipelago.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.List;

public class CreditsScreen extends Screen {
    private final Screen parent;
    private static final List<String> SUPPORTERS = List.of(
            "_stormreign", "Driplocaulus", "AgentExcadrill",
            "PMJP", "Novasam622", "121_Jiggawatts", "MacRaeDog", "Teahoneycat", "SoloGutman",
            "AnArtchist", "Khan121", "Flabs", "Jesse_Turtles", "Kygron"
    );

    public CreditsScreen(Screen parent) {
        super(Component.literal("Credits"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        
        // Add buttons in init() so they aren't recreated every frame. Yes, That's how it used to work.
        this.addRenderableWidget(Button.builder(Component.literal("Back"), btn -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(parent);
            }
        }).bounds(centerX - 49, this.height - 30, 98, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        int centerX = this.width / 2;
        int y = 50;

        guiGraphics.drawCenteredString(this.font, "Thank you to all of Archipelago's supporters!", centerX, 20, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, "Archipelago couldn't exist without you!", centerX, 30, 0xFFFFFF);

        for (String name : SUPPORTERS) {
            guiGraphics.drawCenteredString(this.font, name, centerX, y, 0xA0A0A0);
            y += 12;
        }
    }
}
// - _stormreign - 1 mil pokecoins!
// - Driplocaulus - 3 mil pokecoins!
// - AgentExcadrill - 500k + Shiny Maractus
// - PMJP - 3 mil pokecoins!
// - Novasam622 - 4 mil pokecoins!
// 121_Jiggawatts - 5 mil pokecoins!!!
// MacRaeDog - 1.2mil
// Teahoneycat - 1mil
// SoloGutman 1.5 mil Pokecoins + Aphrodite Gardevoir set + Skin Tokens!
// AnArtchist - 2 Mil Pokecoins!
// Jesse_Turtles - 1 Mil Pokecoins!
// Kygron - 3 mil pokecoins!
// Khan121 - 2 mil pokecoins!!
// Flabs - 1 mil pokecoins!
// Thumberking - 2 mil pokecoins!
// Lilcardawg - 250k pokecoins!
// Spectra82 - 5 mil Pokecoins!

//Leaderboard:
// 121 Jiggawatts Spectra82 5mil
// NovaSam622 4mil
// Driplocaulus, PMJP, Kygron 3mil
// Khan121, AnArtchist Thumberking 2mil
// SoloGutman 1.5mil