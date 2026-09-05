package com.ruok0214.renderhide;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import com.ruok0214.renderhide.mixin.RenderSetupAccessor;
import com.ruok0214.renderhide.mixin.RenderSetupTextureBindingAccessor;
import com.ruok0214.renderhide.mixin.RenderTypeAccessor;

/** Applies Render Hide opacity to all geometry submitted by one entity renderer. */
public final class EntityAlphaSubmitNodeCollector extends EntityAlphaOrderedSubmitNodeCollector
        implements SubmitNodeCollector {
    private final SubmitNodeCollector root;

    public EntityAlphaSubmitNodeCollector(SubmitNodeCollector root, float opacity) {
        super(root, opacity);
        this.root = root;
    }

    @Override
    public OrderedSubmitNodeCollector order(int order) {
        return new EntityAlphaOrderedSubmitNodeCollector(
                this.root.order(order), this.opacity);
    }
}

class EntityAlphaOrderedSubmitNodeCollector implements OrderedSubmitNodeCollector {
        protected final OrderedSubmitNodeCollector delegate;
        protected final float opacity;
        private final int alphaMultiplier;

        EntityAlphaOrderedSubmitNodeCollector(
                OrderedSubmitNodeCollector delegate, float opacity) {
            this.delegate = delegate;
            this.opacity = Math.max(0.0F, Math.min(1.0F, opacity));
            this.alphaMultiplier = ARGB.white(this.opacity);
        }

        private int color(int original) {
            return ARGB.multiply(original, this.alphaMultiplier);
        }

        private int[] colors(int[] original) {
            if (original.length == 0) {
                return original;
            }
            int[] adjusted = original.clone();
            for (int i = 0; i < adjusted.length; i++) {
                adjusted[i] = color(adjusted[i]);
            }
            return adjusted;
        }

        @Override
        public void submitShadow(PoseStack poseStack, float strength,
                List<EntityRenderState.ShadowPiece> pieces) {
            this.delegate.submitShadow(poseStack, strength * this.opacity, pieces);
        }

        @Override
        public void submitNameTag(PoseStack poseStack, Vec3 attachment, int yOffset,
                net.minecraft.network.chat.Component text, boolean discrete, int light,
                CameraRenderState cameraState) {
            this.delegate.submitNameTag(
                    poseStack, attachment, yOffset, text, discrete, light, cameraState);
        }

        @Override
        public void submitText(PoseStack poseStack, float x, float y,
                FormattedCharSequence text, boolean shadow, Font.DisplayMode displayMode,
                int color, int backgroundColor, int light, int outlineColor) {
            this.delegate.submitText(poseStack, x, y, text, shadow, displayMode,
                    color(color), color(backgroundColor), light, color(outlineColor));
        }

        @Override
        public void submitFlame(PoseStack poseStack, EntityRenderState state,
                Quaternionf rotation) {
            this.delegate.submitFlame(poseStack, state, rotation);
        }

        @Override
        public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState state) {
            this.delegate.submitLeash(poseStack, state);
        }

        @Override
        public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack,
                RenderType renderType, int light, int overlay, int color,
                TextureAtlasSprite sprite, int outlineColor,
                ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
            this.delegate.submitModel(model, state, poseStack,
                    translucentEntityType(renderType), light, overlay, color(color),
                    sprite, outlineColor, crumblingOverlay);
        }

        @Override
        public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState state,
                int outlineColor) {
            MovingBlockOpacityAccess access = (MovingBlockOpacityAccess) state;
            float blockOpacity = access.renderhide$getOpacity();
            access.renderhide$setOpacity(Float.isNaN(blockOpacity)
                    ? this.opacity : blockOpacity * this.opacity);
            this.delegate.submitMovingBlock(poseStack, state, outlineColor);
        }

        @Override
        public void submitBlockModel(PoseStack poseStack, RenderType renderType,
                List<BlockStateModelPart> parts, int[] tints, int light, int overlay,
                int outlineColor) {
            this.delegate.submitBlockModel(poseStack, RenderTypes.translucentMovingBlock(),
                    parts, colors(tints), light, overlay, outlineColor);
        }

        @Override
        public void submitBreakingBlockModel(PoseStack poseStack,
                List<BlockStateModelPart> parts, int stage) {
            this.delegate.submitBreakingBlockModel(poseStack, parts, stage);
        }

        @Override
        public void submitShapeOutline(PoseStack poseStack, VoxelShape shape,
                RenderType renderType, int color, float lineWidth, boolean depthTest) {
            this.delegate.submitShapeOutline(
                    poseStack, shape, renderType, color(color), lineWidth, depthTest);
        }

        @Override
        public void submitItem(PoseStack poseStack, ItemDisplayContext displayContext,
                int light, int overlay, int outlineColor, int[] tints,
                List<BakedQuad> quads, ItemStackRenderState.FoilType foilType) {
            int alphaTintIndex = tints.length;
            int[] adjustedTints = Arrays.copyOf(tints, tints.length + 1);
            for (int i = 0; i < tints.length; i++) {
                adjustedTints[i] = color(tints[i]);
            }
            adjustedTints[alphaTintIndex] = this.alphaMultiplier;

            List<BakedQuad> adjustedQuads = new ArrayList<>(quads.size());
            for (BakedQuad quad : quads) {
                BakedQuad.MaterialInfo material = quad.materialInfo();
                int tintIndex = material.isTinted()
                        ? material.tintIndex() : alphaTintIndex;
                BakedQuad.MaterialInfo adjustedMaterial = new BakedQuad.MaterialInfo(
                        material.sprite(), ChunkSectionLayer.TRANSLUCENT,
                        translucentItemType(material.itemRenderType()), tintIndex,
                        material.shade(), material.lightEmission());
                adjustedQuads.add(new BakedQuad(
                        quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                        quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                        quad.direction(), adjustedMaterial));
            }

            this.delegate.submitItem(poseStack, displayContext, light, overlay,
                    outlineColor, adjustedTints, adjustedQuads,
                    ItemStackRenderState.FoilType.NONE);
        }

        @Override
        public void submitCustomGeometry(PoseStack poseStack, RenderType renderType,
                SubmitNodeCollector.CustomGeometryRenderer renderer) {
            this.delegate.submitCustomGeometry(poseStack,
                    translucentEntityType(renderType),
                    (pose, consumer) -> renderer.render(
                            pose, new AlphaVertexConsumer(consumer, this.opacity)));
        }

        @Override
        public void submitQuadParticleGroup(QuadParticleRenderState state) {
            this.delegate.submitQuadParticleGroup(state);
        }

        @Override
        public void submitGizmoPrimitives(DrawableGizmoPrimitives.Group group,
                CameraRenderState cameraState, boolean alwaysOnTop) {
            this.delegate.submitGizmoPrimitives(group, cameraState, alwaysOnTop);
        }

        private static RenderType translucentEntityType(RenderType original) {
            if (original.hasBlending()) {
                return original;
            }

            RenderSetup setup = ((RenderTypeAccessor) (Object) original).renderhide$getState();
            Map<String, Object> textures =
                    ((RenderSetupAccessor) (Object) setup).renderhide$getTextures();
            Object binding = textures.get("Sampler0");
            if (binding == null && !textures.isEmpty()) {
                binding = textures.values().iterator().next();
            }
            if (binding == null) {
                return original;
            }

            Identifier texture = ((RenderSetupTextureBindingAccessor) binding)
                    .renderhide$getLocation();
            return RenderTypes.entityTranslucent(texture);
        }

        private static RenderType translucentItemType(RenderType original) {
            if (original.hasBlending()) {
                return original;
            }

            RenderSetup setup = ((RenderTypeAccessor) (Object) original).renderhide$getState();
            Map<String, Object> textures =
                    ((RenderSetupAccessor) (Object) setup).renderhide$getTextures();
            Object binding = textures.get("Sampler0");
            if (binding == null && !textures.isEmpty()) {
                binding = textures.values().iterator().next();
            }
            if (binding == null) {
                return original;
            }

            Identifier texture = ((RenderSetupTextureBindingAccessor) binding)
                    .renderhide$getLocation();
            return RenderTypes.itemTranslucent(texture);
        }
}
