package com.alkia.archipelago.tooltip;

import com.alkia.archipelago.config.ModConfig;
import com.alkia.archipelago.util.KeyUtil;
import com.alkia.archipelago.util.PokemonUtil;
import com.alkia.archipelago.util.RenderFlags;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;

import java.util.HashSet;
import java.util.Set;


//A lot of this code is made through guesswork looking at Cobblemon's Summary Screen and whatever the PokemonRenderer was.
//Hopefully it makes sense!

public class ClientPokemonSkinTooltipComponent implements ClientTooltipComponent {
    private final PokemonSkinTooltipComponent data;
    private static int lastSpeciesIndex = -1;
    private static int manualSpeciesIndex = 0;
    private static String lastSpeciesId = "";
    private ItemStack pokemonStack = ItemStack.EMPTY;
    private boolean lastWasShiny = false;
    private static int formIndex = 0;
    private static int megaIndex = 0;
    private static boolean lastWasFormPressed = false;
    private static boolean lastWasMegaPressed = false;
    private static boolean lastWasEvolutionPressed = false;

    private static String lastLoggedSpeciesId = "";
    private static Set<String> lastLoggedAspects = new HashSet<>();
    private Set<String> lastAspects = new HashSet<>();
    private String lastCachedSpeciesId = "";

    public ClientPokemonSkinTooltipComponent(PokemonSkinTooltipComponent data) {
        this.data = data;
    }

    private void ensurePokemon() {
        int speciesCount =  data.pokemonIds().size();
        if (speciesCount == 0) return;

        int currentSpeciesIndex;
        if (ModConfig.enableEvolutionScrollling) {
            currentSpeciesIndex = (int) ((System.currentTimeMillis() / 2000) % speciesCount);
        } else {
            currentSpeciesIndex = Math.min(Math.max(0, manualSpeciesIndex), speciesCount - 1);
        }

        String currentSpeciesId = data.pokemonIds().get(currentSpeciesIndex);


        // Reset toggles only if the species actually changed
        if (!currentSpeciesId.equals(lastSpeciesId)) {
            formIndex = 0;
            megaIndex = 0;
            if (ModConfig.enableEvolutionScrollling) {
                manualSpeciesIndex = currentSpeciesIndex;
            }
            lastSpeciesIndex = currentSpeciesIndex;
            lastSpeciesId = currentSpeciesId;
        }

        boolean isFormDown = KeyUtil.isModifierKeyDown(ModConfig.formKey);
        boolean isMegaDown = KeyUtil.isModifierKeyDown(ModConfig.megaKey);
        boolean isEvolutionDown = KeyUtil.isModifierKeyDown(ModConfig.evolutionKey);

        //Love me some flipflops
        if (isFormDown && !lastWasFormPressed) {
            formIndex++;
            if (ModConfig.enableSkinDebug) {
                System.out.println("[ARCHIPELAGO] Form key pressed. New formIndex: " + formIndex);
            }
        }
        if (isMegaDown && !lastWasMegaPressed) {
            megaIndex++;
            if (ModConfig.enableSkinDebug) {
                System.out.println("[ARCHIPELAGO] Mega key pressed. New megaIndex: " + megaIndex);
            }
        }
        if (isEvolutionDown && !lastWasEvolutionPressed) {
            manualSpeciesIndex = (manualSpeciesIndex + 1) % speciesCount;
            if (ModConfig.enableEvolutionScrollling) {
            }
            if (ModConfig.enableSkinDebug) {
                System.out.println("[ARCHIPELAGO] Evolution key pressed. New manualSpeciesIndex: " + manualSpeciesIndex);
            }
        }

        lastWasFormPressed = isFormDown;
        lastWasMegaPressed = isMegaDown;
        lastWasEvolutionPressed = isEvolutionDown;
        this.lastWasShiny = KeyUtil.isModifierKeyDown(ModConfig.shinyKey);

        ResourceLocation speciesId = ResourceLocation.tryParse(currentSpeciesId);
        if (speciesId != null) {
            Species species = PokemonUtil.getSpecies(currentSpeciesId);
            if (species != null) {
                Set<String> aspects = new HashSet<>(data.whitelistAspects());
                aspects.addAll(data.aspects());
                if (this.lastWasShiny) {
                    aspects.add("shiny");
                }
                applyFormLogic(currentSpeciesId, aspects);
                applyMegaLogic(currentSpeciesId, aspects);

                if (ModConfig.enableSkinDebug) {
                    if (!currentSpeciesId.equals(lastLoggedSpeciesId) || !aspects.equals(lastLoggedAspects)) {
                        System.out.println("[ARCHIPELAGO] Species: " + speciesId.getPath());
                        System.out.println("[ARCHIPELAGO] Aspects: " + aspects);
                        lastLoggedSpeciesId = currentSpeciesId;
                        lastLoggedAspects = new HashSet<>(aspects);
                    }
                }

                if (!currentSpeciesId.equals(lastCachedSpeciesId) || !aspects.equals(lastAspects)) {
                    this.pokemonStack = PokemonItem.from(species, aspects, 1, null);
                    lastAspects = new HashSet<>(aspects);
                    lastCachedSpeciesId = currentSpeciesId;
                }


            }
        }
    }

    private void applyFormLogic(String speciesName, Set<String> aspects) {
        Species pokemon = PokemonUtil.getSpecies(speciesName);

        if (pokemon != null) {
            String[] forms = PokemonUtil.getFilteredForms(pokemon);

            if (forms.length > 0) {
                // Safely select the form based on the index
                int index = Math.abs(formIndex) % forms.length;
                String selectedForm = forms[index];

                // Add the result to the aspects set
                if (selectedForm != null && !selectedForm.isEmpty()) {
                    aspects.add(selectedForm);
                }
            } else if (ModConfig.enableSkinDebug) {
                if (!speciesName.equals(lastLoggedSpeciesId)) {
                    System.out.println("[ARCHIPELAGO] No forms found for: " + pokemon.getName());
                }
            }
        }
    }

    private void applyMegaLogic(String speciesName, Set<String> aspects) {
        Species pokemon = PokemonUtil.getSpecies(speciesName);

        if (pokemon != null) {
            // Get official Megas using Util
            java.util.List<String> megaList = PokemonUtil.getMegaForms(pokemon);

            // Handle non-official/weirdly handled megas
            if (megaList.isEmpty()) {
                megaList.add("normal");
                megaList.add("mega");
            } else {
                // Adds basically an unmega state at the start of the array, otherwise it'd just be constantly mega in previews.
                megaList.add(0, "normal");
            }

            // Cycle through the list (Official or Fallback)
            String[] megaArray = megaList.toArray(new String[0]);
            int index = Math.abs(megaIndex) % megaArray.length;
            String selectedMega = megaArray[index];

            // Apply the aspect
            if (selectedMega != null && !selectedMega.equals("normal")) {
                aspects.add(selectedMega);
            }
        }

    }

    // We don't do anything here, but Minecraft wants em for the tooltip.
    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWidth(Font font) {
        return 0;
    }


    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        try {
            RenderFlags.isRenderingSkinPreview = true;
            renderImageInternal(font, x, y, guiGraphics);
        } finally {
            RenderFlags.isRenderingSkinPreview = false;
        }
    }

    private void renderImageInternal(Font font, int x, int y, GuiGraphics guiGraphics) {
        ensurePokemon();

        if (!pokemonStack.isEmpty()){
            boolean isZoomed = KeyUtil.isModifierKeyDown(ModConfig.zoomKey);
            float baseScale = isZoomed ? 8.0f : 4.0f;
            int currentX = isZoomed ? (x - 145) : (x - 85);


            // Default values used for mon/box/whatever size.
            float xOffsetRaw = 0;
            float yOffsetRaw = 0;
            float scaleMultiplier = 1.0f;
            int boxSizeAdjustment = 0;

            // Extract the current species ID
            if (lastSpeciesIndex >= 0 && lastSpeciesIndex < data.pokemonIds().size()) {
                String speciesId = data.pokemonIds().get(lastSpeciesIndex);

                // Was originally a specific edgecase for Mewtwo (I hate him.)
                switch (speciesId) {
                    case "cobblemon:mewtwo":
                        scaleMultiplier = 0.7f;
                        yOffsetRaw = 5f;
                        xOffsetRaw = 2f;
                        break;
                    case "cobblemon:rotom":
                        scaleMultiplier= 0.7f;
                        yOffsetRaw = 5f;
                        xOffsetRaw = 2f;
                        break;

                }

                //Example i'm keeping for future reference
                // if (data.tokenId().equals("labubucrate")) { ... }

                float finalScale = baseScale * scaleMultiplier;
                float finalXOffset = xOffsetRaw * baseScale;
                float finalYOffset = yOffsetRaw * baseScale;

                //I HATE MATHS

                if (ModConfig.enablePreviewBackground) {
                    int boxSize = (isZoomed ? 128 : 64) + boxSizeAdjustment;
                    int x1 = currentX - 6;
                    int yOffsetBox = isZoomed ? 25 : 15;
                    int y1 = y - 6 - yOffsetBox;
                    int width = boxSize + 16;
                    int height = boxSize + 12 + yOffsetBox;

                    // Render tooltip background
                    TooltipRenderUtil.renderTooltipBackground(guiGraphics, x1, y1, width, height, 400);
                }
                var matrices = guiGraphics.pose();
                matrices.pushPose();

                matrices.translate(currentX + finalXOffset, y + finalYOffset, 500);
                matrices.scale(finalScale, finalScale, 1.0f);

                guiGraphics.renderItem(pokemonStack, 0, 0);

                // PopPose is a funny word
                matrices.popPose();
            }

        }
    }
}


