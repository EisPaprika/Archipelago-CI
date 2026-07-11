package com.alkia.archipelago.mixin;

import com.alkia.archipelago.ItemFrameCache;
import com.alkia.archipelago.util.PokemonUtil;
import com.alkia.archipelago.util.RenderFlags;
import com.alkia.archipelago.util.SkinTokenUtil;
import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.client.entity.PokemonClientDelegate;
import com.cobblemon.mod.common.client.render.models.blockbench.repository.VaryingModelRepository;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameEntityRendererMixin extends EntityRenderer<ItemFrame> {

    protected ItemFrameEntityRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderCobblemonInFrame(ItemFrame itemFrame, float entityYaw, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, CallbackInfo ci) {
        if (!com.alkia.archipelago.config.ModConfig.enableItemFrameSkins) return;

        ItemStack stack = itemFrame.getItem();
        if (stack.isEmpty() || !stack.has(DataComponents.CUSTOM_DATA)) return;

        if (!(itemFrame instanceof ItemFrameCache cache)) return;

        try {
            RenderFlags.isRenderingSkinPreview = true;
            renderCobblemonInFrameInternal(itemFrame, entityYaw, tickDelta, matrices, buffer, light, ci, stack, cache);
        } finally {
            RenderFlags.isRenderingSkinPreview = false;
        }
    }
    @Unique
    String getAttackAnimInstrument(NoteBlockInstrument instrument) {
        if (instrument == null) return null;
        return switch (instrument) {
            case BASS -> "physical";
            case FLUTE -> "special";
            case SNARE -> "status";
            case HAT -> "recoil";
            case BELL -> "cry";
            default -> null;
        };
    }
    @Unique
    private void renderCobblemonInFrameInternal(ItemFrame itemFrame, float entityYaw, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, CallbackInfo ci, ItemStack stack, ItemFrameCache cache) {
        // Note Block Logic
        Direction dir = itemFrame.getDirection();
        BlockPos blockBehind = itemFrame.blockPosition().relative(dir.getOpposite());
        BlockState blockState = itemFrame.level().getBlockState(blockBehind);

        boolean hasNoteBlock = blockState.is(Blocks.NOTE_BLOCK);
        int stateHash = 0;
        NoteBlockInstrument instrument = null;
        int note = 0;
        boolean isGlowFrame = itemFrame instanceof net.minecraft.world.entity.decoration.GlowItemFrame;

        BlockPos activatorPos = blockBehind.below();
        BlockPos depthPos = activatorPos.below();
        BlockState depthState = itemFrame.level().getBlockState(depthPos);
        BlockState activatorState = itemFrame.level().getBlockState(activatorPos);
        boolean hasAttackAnimBlock = activatorState.is(Blocks.NOTE_BLOCK);
        NoteBlockInstrument attackInstrument = null;
        if(hasAttackAnimBlock) {
            attackInstrument = activatorState.getValue(NoteBlock.INSTRUMENT);
        }


        boolean hasNoteBlock2 = depthState.is(Blocks.NOTE_BLOCK);
        int note2 = 0;
        NoteBlockInstrument instrument2 = null;
        if (hasNoteBlock2) {
            note2 = depthState.getValue(NoteBlock.NOTE);
            instrument2 = depthState.getValue(NoteBlock.INSTRUMENT);
        }

        BlockPos heightPos = depthPos.below();
        BlockState heightState = itemFrame.level().getBlockState(heightPos);
        boolean hasHeightBlock = heightState.is(Blocks.NOTE_BLOCK);
        int heightNote = 0;
        if (hasHeightBlock) {
            heightNote = heightState.getValue(NoteBlock.NOTE);
        }

        if (hasNoteBlock) {
            instrument = blockState.getValue(NoteBlock.INSTRUMENT);
            note = blockState.getValue(NoteBlock.NOTE);


            int stackContentHash = SkinTokenUtil.getSkinTokenTag(stack).map(CompoundTag::hashCode).orElse(stack.getItem().hashCode());
            stateHash = Objects.hash(instrument, note, stackContentHash, isGlowFrame, activatorPos.hashCode(), depthState.getBlock(), hasNoteBlock2, instrument2, note2, attackInstrument, hasHeightBlock, heightNote);

        } else {
            return;
        }

        if (cache.archipelago$getCachedEntity() == null || cache.archipelago$getCachedStateHash() != stateHash) {
            if (com.alkia.archipelago.config.ModConfig.enableSkinDebug) {
                if (cache.archipelago$getCachedEntity() == null) {
                    System.out.println("[ARCHIPELAGO-DEBUG] Creating new entity for item frame (cache null)");
                } else {
                    System.out.println("[ARCHIPELAGO-DEBUG] Re-creating entity for item frame (hash mismatch: " + cache.archipelago$getCachedStateHash() + " -> " + stateHash + ")");
                }
            }
            cache.archipelago$setCachedStateHash(stateHash);
            try {
                Optional<CompoundTag> skinTokenOpt = SkinTokenUtil.getSkinTokenTag(stack);
                if (skinTokenOpt.isPresent()) {
                    CompoundTag tokenData = skinTokenOpt.get();

                    List<String> whitelistIds = PokemonUtil.getWhitelistFromToken(tokenData);

                    if (!whitelistIds.isEmpty()) {
                        List<String> aspects = PokemonUtil.getAspectsFromToken(tokenData);

                        int evolutionDepth = 0;
                        int formIndex = 0;
                        if (hasNoteBlock2) {
                            evolutionDepth = switch (instrument2) {
                                case IRON_XYLOPHONE -> 1;
                                case BELL -> 2;
                                case CHIME -> 3;
                                case BIT -> 4;
                                case COW_BELL -> 5;
                                case XYLOPHONE -> 6;
                                case GUITAR -> 7;
                                case FLUTE -> 8;
                                case BASS -> 9;
                                case SNARE -> 10;
                                case HAT -> 11;
                                case BASEDRUM -> 12;
                                default -> 0;
                            };
                            formIndex = note2;
                        } else if (hasNoteBlock) {
                            if (depthState.is(Blocks.IRON_BLOCK)) evolutionDepth = 1;
                            else if (depthState.is(Blocks.GOLD_BLOCK)) evolutionDepth = 2;
                            else if (depthState.is(Blocks.DIAMOND_BLOCK)) evolutionDepth = 3;
                        }

                        // Use whitelist for evolution. Was originally gonna get evos from species but those aren't
                        // Exposed to client for some reason???
                        int speciesIndex = Math.min(evolutionDepth, whitelistIds.size() - 1);
                        String speciesString = whitelistIds.get(speciesIndex);

                        if (!speciesString.contains(":")) {
                            speciesString = "cobblemon:" + speciesString;
                        }

                        Species species = PokemonUtil.getSpecies(speciesString);

                        if (hasNoteBlock2) {
                            String[] filteredForms = PokemonUtil.getFilteredForms(species);
                            if (filteredForms.length > 0) {
                                aspects.add(filteredForms[formIndex % filteredForms.length]);
                            }
                        }

                        if (hasNoteBlock) {
                            switch (instrument) {
                                case BIT -> {
                                    List<String> megaForms = PokemonUtil.getMegaForms(species);
                                    if (!megaForms.isEmpty()) {
                                        aspects.add(megaForms.get(0));
                                    }
                                }
                                case XYLOPHONE -> {
                                    List<String> megaForms = PokemonUtil.getMegaForms(species);
                                    if (megaForms.size() > 1) {
                                        aspects.add(megaForms.get(1));
                                    }
                                }
                                case COW_BELL -> {
                                    List<String> megaForms = PokemonUtil.getMegaForms(species);
                                    if (megaForms.size() > 2) {
                                        aspects.add(megaForms.get(2));
                                    }
                                }
                                default -> {
                                }
                            }

                            // Apply shiny if it's a Glow Item Frame
                            if (isGlowFrame && !aspects.contains("shiny")) {
                                aspects.add("shiny");
                            }
                        }

                        Pokemon pokemon = PokemonUtil.createPokemon(speciesString, aspects);
                        if (pokemon != null) {
                            if (com.alkia.archipelago.config.ModConfig.enableSkinDebug) {
                                System.out.println("[ARCHIPELAGO] Created pokemon: " + pokemon.getSpecies().getName());
                            }
                            PokemonEntity pokemonEntity = new PokemonEntity(itemFrame.level(), pokemon, CobblemonEntities.POKEMON);
                            pokemonEntity.setPos(0, 0, 0);
                            pokemonEntity.xo = 0;
                            pokemonEntity.yo = 0;
                            pokemonEntity.zo = 0;
                            pokemonEntity.xOld = 0;
                            pokemonEntity.yOld = 0;
                            pokemonEntity.zOld = 0;

                            pokemonEntity.setEnablePoseTypeRecalculation(false);
                            RenderFlags.previewEntities.add(pokemonEntity);
                            cache.archipelago$setCachedEntity(pokemonEntity);

                            PokemonClientDelegate delegate = (PokemonClientDelegate) pokemonEntity.getDelegate();
                            delegate.initialize(pokemonEntity); // Initialize delegate with entity
                            delegate.setCurrentAspects(new HashSet<>(aspects));

                            // Pitch to Pose mapping
                            PosableModel posableModel = VaryingModelRepository.INSTANCE.getPoser(pokemon.getSpecies().getResourceIdentifier(), delegate);
                            if (posableModel != null) {
                                delegate.setCurrentModel(posableModel); // Set model on delegate
                                List<String> poses = PokemonUtil.getAvailablePoses(pokemon.getSpecies(), delegate);
                                if (!poses.isEmpty()) {
                                    int poseIndex = note % poses.size();
                                    String pose = poses.get(poseIndex);
                                    String attackAnim = getAttackAnimInstrument(attackInstrument);
                                    cache.archipelago$setActiveAttackAnim(attackAnim);
                                    if (!Objects.equals(cache.archipelago$getActivePose(), pose)) {
                                        cache.archipelago$setActivePose(pose);

                                        delegate.setPose(pose);

                                        // Sync the entity data if it matches a PoseType to prevent validatePose override
                                        PoseType type = PokemonUtil.getPoseType(pose, posableModel);

                                        if (type != null) {
                                            pokemonEntity.getEntityData().set(PokemonEntity.Companion.getPOSE_TYPE(), type);
                                        }
                                    }

                                    delegate.setPose(cache.archipelago$getActivePose());
                                    if (delegate.getCurrentModel() == null) {
                                        delegate.setCurrentModel(posableModel);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                cache.archipelago$setCachedEntity(null);
            }
        }

        PokemonEntity cachedPokemonEntity = cache.archipelago$getCachedEntity();

        // Render Logic
        if (cachedPokemonEntity != null) {
            // Update animation state
            PokemonClientDelegate clientDelegate = (PokemonClientDelegate) cachedPokemonEntity.getDelegate();

            // Advance age based on real time if the entity isn't ticking (CI has item frames with disabled ticking iirc?)
            clientDelegate.updateAge(itemFrame.tickCount);
            clientDelegate.updatePartialTicks(tickDelta);

            // Ensure model is set on delegate for validatePose
            if (clientDelegate.getCurrentModel() == null) {
                clientDelegate.setCurrentModel(VaryingModelRepository.INSTANCE.getPoser(cachedPokemonEntity.getPokemon().getSpecies().getResourceIdentifier(), clientDelegate));
            }
            clientDelegate.setCurrentAspects(new HashSet<>(cachedPokemonEntity.getPokemon().getAspects()));
            String attackAnim = cache.archipelago$getActiveAttackAnim();
            if (attackAnim != null && !attackAnim.isEmpty()) {
                if (!cachedPokemonEntity.isBattling()) {
                    cachedPokemonEntity.setBattleId(java.util.UUID.randomUUID());
                }
                if (clientDelegate.getPrimaryAnimation() == null) {
                    clientDelegate.addFirstAnimation(java.util.Set.of(attackAnim));
                }
            }
            // Forces pose, just in case.
            String activePose = cache.archipelago$getActivePose();
            if (activePose != null && !activePose.isEmpty()) {
                if (!Objects.equals(clientDelegate.getCurrentPose(), activePose)) {
                    clientDelegate.setPose(activePose);
                }


                com.cobblemon.mod.common.entity.PoseType type = PokemonUtil.getPoseType(activePose, clientDelegate.getCurrentModel());

                if (type != null && cachedPokemonEntity.getEntityData().get(PokemonEntity.Companion.getPOSE_TYPE()) != type) {
                    cachedPokemonEntity.getEntityData().set(PokemonEntity.Companion.getPOSE_TYPE(), type);
                }

                // Handle q.in_battle MoLang query by syncing isBattling state
                // Fun fact, pokemon HAVE to be "in battle" to use their battle pose.
                boolean isBattlePose = activePose.toLowerCase().contains("battle");
                if (isBattlePose) {
                    if (!cachedPokemonEntity.isBattling()) {
                        cachedPokemonEntity.setBattleId(java.util.UUID.randomUUID());
                    }
                } else {
                    if (cachedPokemonEntity.isBattling()) {
                        cachedPokemonEntity.setBattleId(null);
                    }
                }
            }

            clientDelegate.preRender();

            cachedPokemonEntity.tickCount = itemFrame.tickCount;
            cachedPokemonEntity.xo = cachedPokemonEntity.getX();
            cachedPokemonEntity.yo = cachedPokemonEntity.getY();
            cachedPokemonEntity.zo = cachedPokemonEntity.getZ();
            cachedPokemonEntity.xOld = cachedPokemonEntity.getX();
            cachedPokemonEntity.yOld = cachedPokemonEntity.getY();
            cachedPokemonEntity.zOld = cachedPokemonEntity.getZ();

            // Sync rotations
            cachedPokemonEntity.yRotO = 0.0f;
            cachedPokemonEntity.setYRot(0.0f);
            cachedPokemonEntity.setYBodyRot(0.0f);
            cachedPokemonEntity.yBodyRotO = 0.0f;
            cachedPokemonEntity.setYHeadRot(0.0f);
            cachedPokemonEntity.yHeadRotO = 0.0f;

            matrices.pushPose();

            // Rotate to face away from wall or stand on floor/ceiling
            if (dir == Direction.UP) {
                float rotation = itemFrame.getRotation() * 45.0f;
                matrices.mulPose(Axis.YP.rotationDegrees(180.0f + rotation));
            } else if (dir == Direction.DOWN) {
                matrices.mulPose(Axis.XP.rotationDegrees(180.0f));
                float rotation = itemFrame.getRotation() * 45.0f;
                matrices.mulPose(Axis.YP.rotationDegrees(180.0f - rotation));
            } else {
                matrices.mulPose(Axis.YP.rotationDegrees(-dir.toYRot()));
                matrices.translate(0.0, 0.0, 0.4375); // Adjusted translation
            }

            float heightOffset = hasHeightBlock ? (float) heightNote : 0.0f;
            matrices.translate(0.0, 0.3 + heightOffset, 0.0);

            Minecraft.getInstance().getEntityRenderDispatcher().render(
                    cachedPokemonEntity,
                    0.0, 0.0, 0.0,
                    0.0f,
                    tickDelta,
                    matrices,
                    buffer,
                    light
            );

            matrices.popPose();
            ci.cancel();
        }
    }
}