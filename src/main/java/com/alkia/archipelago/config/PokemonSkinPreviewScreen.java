package com.alkia.archipelago.config;

import com.alkia.archipelago.util.PokemonUtil;
import com.alkia.archipelago.util.RenderFlags;
import com.alkia.archipelago.util.SkinResolverUtil;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel;
import com.cobblemon.mod.common.client.render.models.blockbench.repository.VaryingModelRepository;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.client.gui.summary.widgets.ModelWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PokemonSkinPreviewScreen extends Screen {
    private final Screen parent;
    private EditBox pokemonInput;
    private EditBox tokenInput;
    private Checkbox shinyCheckbox;
    private int formIndex = 0;
    private int megaIndex = 0;
//    private int gmaxIndex = 0;
    private int skinIndex = 0;
    private int animationIndex = 0;
    private String selectedAnimation = "";
    private float rotationY = 35f;
    private float modelScale = 5.0f;
    private boolean isDragging = false;
    private double lastMouseX = 0;
    private ModelWidget modelWidget;
    private List<SkinResolverUtil.SkinVariation> availableSkins = new ArrayList<>();
    private String lastSpecies = "";
    private List<String> cachedPoses = new ArrayList<>();

    public PokemonSkinPreviewScreen(Screen parent) {
        super(Component.literal("Pokemon Skin Previewer"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int leftX = 10;
        int centerX = this.width / 2;

        // Pokemon name input
        this.pokemonInput = new EditBox(this.font, leftX, 25, 150, 20, Component.literal("Pokemon"));
        this.pokemonInput.setResponder(text -> {
            formIndex = 0;
            megaIndex = 0;
//            gmaxIndex = 0;
            skinIndex = 0;
            animationIndex = 0;
            selectedAnimation = "";
            rotationY = 35f;
            if (this.modelWidget != null) {
                this.modelWidget.setRotationY(this.rotationY);
                this.modelWidget.getRotationVector().set(13.0f, this.rotationY, 0.0f);
            }
            updateAvailableSkins();
            updatePreview();
        });
        this.pokemonInput.setMaxLength(2048);
        this.addRenderableWidget(this.pokemonInput);

        // Token input
        this.tokenInput = new EditBox(this.font, leftX, 60, 150, 20, Component.literal("Skin Token"));
        this.tokenInput.setResponder(text -> {
            rotationY = 35f;
            if (this.modelWidget != null) {
                this.modelWidget.setRotationY(this.rotationY);
                this.modelWidget.getRotationVector().set(13.0f, this.rotationY, 0.0f);
            }
            updatePreview();
        });
        this.tokenInput.setMaxLength(2048);
        this.addRenderableWidget(this.tokenInput);

        // Form Cycle Button
        this.addRenderableWidget(Button.builder(Component.literal("Next Form"), button -> {
            formIndex++;
            rotationY = 35f;
            if (this.modelWidget != null) {
                this.modelWidget.setRotationY(this.rotationY);
                this.modelWidget.getRotationVector().set(13.0f, this.rotationY, 0.0f);
            }
            updatePreview();
        }).bounds(leftX, 85, 75, 20).build());

        // Shiny Checkbox
        this.shinyCheckbox = Checkbox.builder(Component.literal("Shiny"), this.font)
                .pos(leftX + 155, 26)
                .onValueChange((checkbox, selected) -> {
                    rotationY = 35f;
                    if (this.modelWidget != null) {
                        this.modelWidget.setRotationY(this.rotationY);
                        this.modelWidget.getRotationVector().set(13.0f, this.rotationY, 0.0f);
                    }
                    updatePreview();
                })
                .build();
        this.addRenderableWidget(this.shinyCheckbox);

        // Mega Button
        this.addRenderableWidget(Button.builder(Component.literal("Mega"), button -> {
            megaIndex++;
            rotationY = 35f;
            if (this.modelWidget != null) {
                this.modelWidget.setRotationY(this.rotationY);
                this.modelWidget.getRotationVector().set(13.0f, this.rotationY, 0.0f);
            }
            updatePreview();
        }).bounds(leftX + 75, 85, 75, 20).build());

        // Animation Button
        this.addRenderableWidget(Button.builder(Component.literal("Next Anim"), button -> {
            animationIndex++;
            updatePreview();
        }).bounds(leftX, 110, 75, 20).build());

        // Scale Buttons
        this.addRenderableWidget(Button.builder(Component.literal("-"), button -> {
            modelScale = Math.max(0.5f, modelScale - 0.5f);
            
            updatePreview();
        }).bounds(leftX, this.height - 30, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("+"), button -> {
            modelScale = Math.min(20.0f, modelScale + 0.5f);
            updatePreview();
        }).bounds(leftX + 25, this.height - 30, 20, 20).build());

//        // Gmax Button
//        this.addRenderableWidget(Button.builder(Component.literal("Gmax"), button -> {
//            gmaxIndex++;
//            updatePreview();
//        }).bounds(leftX + 114, 85, 36, 20).build());

        // Skin Selector Button
        this.addRenderableWidget(Button.builder(Component.literal("Cycle Skin"), button -> {
            if (!availableSkins.isEmpty()) {
                skinIndex++;
                SkinResolverUtil.SkinVariation selected = availableSkins.get(Math.abs(skinIndex) % availableSkins.size());
               // Find skin- aspect
               String skinToken = selected.aspects.stream()
                        .filter(a -> a.startsWith("skin-"))
                        .findFirst()
                        .orElse("");
                if (!skinToken.isEmpty()) {
                    tokenInput.setValue(skinToken);
                } else {
                    tokenInput.setValue("");
                }
            }
        }).bounds(leftX + 75, 110, 75,20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Back"), button -> {
            if (this.minecraft != null) this.minecraft.setScreen(parent);
        }).bounds(leftX, 135, 150, 20).build());

        updateAvailableSkins();
        updatePreview();
    }


    private void applySpeciesSpecificOffsets(RenderablePokemon pokemon) {
        float xOffsetRaw = 0;
        float yOffsetRaw = 0;
        float scaleMultiplier = 1.0f;

        String speciesId = pokemon.getSpecies().getResourceIdentifier().toString();
        switch (speciesId) {
            case "cobblemon:mewtwo":
                scaleMultiplier = 0.7f;
                yOffsetRaw = 11f;
                xOffsetRaw = 2f;
                break;
            case "cobblemon:rotom":
                scaleMultiplier = 0.7f;
                yOffsetRaw = 5f;
                xOffsetRaw = 2f;
                break;
        }

        float finalScale = this.modelScale * scaleMultiplier;

        double scaleCompensationY = (5.0 - this.modelScale) * 10.0; 
        double finalOffsetY = (yOffsetRaw * this.modelScale) + scaleCompensationY;

        this.modelWidget = new ModelWidget(
                (int) (this.width / 2 - 100 + (xOffsetRaw * this.modelScale)),
                this.height / 2 - 80,
                200,
                200,
                pokemon,
                finalScale,
                this.rotationY,
                finalOffsetY,
                false,
                false
        );
        this.modelWidget.getRotationVector().set(13.0f, this.rotationY, 0.0f);
    }

    private void updateAvailableSkins() {
        String speciesName = pokemonInput.getValue().toLowerCase().trim();
        if (!speciesName.equals(lastSpecies)) {
            availableSkins = SkinResolverUtil.getSkinsForSpecies(speciesName);
            lastSpecies = speciesName;
        }
    }

    private void updatePreview() {
        String speciesName = pokemonInput.getValue().toLowerCase().trim();
        String token = tokenInput.getValue().trim();

        try {
            Species species = PokemonUtil.getSpecies(speciesName);
            if (species != null) {
                List<String> aspects = new ArrayList<>();

                if (!token.isEmpty()) {
                    aspects.add(PokemonUtil.normalizeAspect(token));
                }
                if (shinyCheckbox != null && shinyCheckbox.selected()) {
                    aspects.add("shiny");
                }

                String[] filteredForms = PokemonUtil.getFilteredForms(species);
                if (filteredForms.length > 0) {
                    aspects.add(filteredForms[Math.abs(formIndex) % filteredForms.length]);
                }

                List<String> megaList = PokemonUtil.getMegaForms(species);
                List<String> cycleMegaList = new ArrayList<>();
                cycleMegaList.add("normal");
                cycleMegaList.addAll(megaList);
                String selectedMega = cycleMegaList.get(Math.abs(megaIndex) % cycleMegaList.size());
                if (!selectedMega.equals("normal")) {
                    aspects.add(selectedMega);
                }

                RenderablePokemon renderable = new RenderablePokemon(species, new HashSet<>(aspects), ItemStack.EMPTY);
                applySpeciesSpecificOffsets(renderable);

                if (this.modelWidget != null) {
                    PosableModel model = VaryingModelRepository.INSTANCE.getPoser(species.getResourceIdentifier(), this.modelWidget.getState());
                    if (model != null) {
                        List<String> poses = new ArrayList<>(model.getPoses().keySet());
                        poses.sort((a, b) -> {
                            String aLower = a.toLowerCase();
                            String bLower = b.toLowerCase();
                            if (aLower.equals("standing")) return -1;
                            if (bLower.equals("standing")) return 1;

                            if (aLower.contains("battle") && !bLower.contains("battle")) return 1;
                            if (!aLower.contains("battle") && bLower.contains("battle")) return -1;

                            return a.compareTo(b);
                        });
                        cachedPoses = poses;
                        if (!cachedPoses.isEmpty()) {
                            selectedAnimation = cachedPoses.get(Math.abs(animationIndex) % cachedPoses.size());
                        }
                    }
                }
            } else {
                selectedAnimation = "";
                this.modelWidget = null;
            }
        } catch (Exception e) {
            selectedAnimation = "";
            this.modelWidget = null;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        try {
            RenderFlags.isRenderingSkinPreview = true;
            renderInternal(graphics, mouseX, mouseY, delta);
        } finally {
            RenderFlags.isRenderingSkinPreview = false;
        }
    }

    private void renderInternal(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        int leftX = 10;
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        graphics.drawString(this.font, "Pokemon Name", leftX, 15, 0xAAAAAA);
        graphics.drawString(this.font, "Skin Token ID", leftX, 50, 0xAAAAAA);
        graphics.drawString(this.font, String.format("Scale: %.1f", modelScale), leftX + 50, this.height - 25, 0xAAAAAA);

        try {
            String speciesName = pokemonInput.getValue().toLowerCase().trim();
            Species species = PokemonUtil.getSpecies(speciesName);
            if (species != null) {
                String[] filteredForms = PokemonUtil.getFilteredForms(species);
                if (filteredForms.length > 0) {
                    String selectedForm = filteredForms[Math.abs(formIndex) % filteredForms.length];
                    graphics.drawString(this.font, "Current Form: " + selectedForm, leftX, 184, 0x03e3fc);
                }

                List<String> megaList = PokemonUtil.getMegaForms(species);
                List<String> cycleMegaList = new ArrayList<>();
                cycleMegaList.add("Normal");
                cycleMegaList.addAll(megaList);
                String selectedMega = cycleMegaList.get(Math.abs(megaIndex) % cycleMegaList.size());
                graphics.drawString(this.font, "Mega Status: " + selectedMega, leftX, 196, 0x03e3fc);

                if (!selectedAnimation.isEmpty()) {
                    graphics.drawString(this.font, "Animation: " + selectedAnimation, leftX, 208, 0x03e3fc);
                }
            }
        } catch (Exception ignored) {}
                if (!availableSkins.isEmpty()) {
                    SkinResolverUtil.SkinVariation current = availableSkins.get(Math.abs(skinIndex) % availableSkins.size());
                    graphics.drawString(this.font, "Skin: " + current.name, leftX, 160, 0x03e3fc);
                    graphics.drawString(this.font, (Math.abs(skinIndex) % availableSkins.size() + 1) + "/" + availableSkins.size(), leftX, 172, 0xAAAAAA);
                }

        if (this.modelWidget != null) {
            if (!selectedAnimation.isEmpty()) {
                ((com.alkia.archipelago.util.ArchipelagoPosableState) (Object) this.modelWidget.getState()).archipelago$setForcedPose(selectedAnimation);
                this.modelWidget.getState().getRuntime().getEnvironment().query.addFunction("is_battling", (args) -> {
                    return new com.bedrockk.molang.runtime.value.DoubleValue(selectedAnimation.toLowerCase().contains("battle") ? 1.0 : 0.0);
                });
            } else {
                ((com.alkia.archipelago.util.ArchipelagoPosableState) (Object) this.modelWidget.getState()).archipelago$setForcedPose(null);
            }
            this.modelWidget.render(graphics, mouseX, mouseY, delta);
        } else if (!pokemonInput.getValue().isEmpty()) {
            graphics.drawCenteredString(this.font, "Invalid Pokemon Species", this.width / 2, this.height / 2, 0xFF5555);
        }
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.modelWidget != null) {
            if (this.modelWidget.isHovered()) {
                this.isDragging = true;
                this.lastMouseX = mouseX;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.isDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isDragging && this.modelWidget != null) {
            this.rotationY -= (float) (mouseX - this.lastMouseX) * 2.0f;
            this.modelWidget.setRotationY(this.rotationY);
            this.modelWidget.getRotationVector().set(13.0f, this.rotationY, 0.0f);
            this.lastMouseX = mouseX;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(parent);
    }
}
