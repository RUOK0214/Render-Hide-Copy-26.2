package com.ruok0214.renderhide;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;

/** Draws flush translucent entity block models after translucent terrain. */
public final class DeferredEntityBlockRenderer {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final List<Submission> PENDING = new ArrayList<>();

    private DeferredEntityBlockRenderer() {
    }

    public static void submit(PoseStack.Pose pose, RenderType renderType,
            List<BlockStateModelPart> parts, int[] tints, int light, int overlay,
            int outlineColor) {
        PENDING.add(new Submission(pose, renderType, parts, tints,
                light, overlay, outlineColor));
    }

    public static void render(MultiBufferSource.BufferSource bufferSource,
            OutlineBufferSource outlineBufferSource) {
        if (PENDING.isEmpty()) {
            return;
        }

        List<Submission> submissions = List.copyOf(PENDING);
        PENDING.clear();
        boolean hasOutline = false;

        for (Submission submission : submissions) {
            VertexConsumer buffer = bufferSource.getBuffer(submission.renderType());
            VertexConsumer outlineBuffer = null;
            if (submission.outlineColor() != 0) {
                outlineBufferSource.setColor(submission.outlineColor());
                outlineBuffer = outlineBufferSource.getBuffer(submission.renderType());
                hasOutline = true;
            }

            QuadInstance instance = new QuadInstance();
            instance.setLightCoords(submission.light());
            instance.setOverlayCoords(submission.overlay());
            for (BlockStateModelPart part : submission.parts()) {
                for (Direction direction : DIRECTIONS) {
                    putQuads(part.getQuads(direction), submission.pose(), instance,
                            submission.tints(), buffer, outlineBuffer);
                }
                putQuads(part.getQuads(null), submission.pose(), instance,
                        submission.tints(), buffer, outlineBuffer);
            }
        }

        if (hasOutline) {
            outlineBufferSource.endOutlineBatch();
        }
    }

    private static void putQuads(List<BakedQuad> quads, PoseStack.Pose pose,
            QuadInstance instance, int[] tints, VertexConsumer buffer,
            VertexConsumer outlineBuffer) {
        for (BakedQuad quad : quads) {
            int tintIndex = quad.materialInfo().tintIndex();
            instance.setColor(tintIndex >= 0 && tintIndex < tints.length
                    ? tints[tintIndex] : -1);
            buffer.putBakedQuad(pose, quad, instance);
            if (outlineBuffer != null) {
                outlineBuffer.putBakedQuad(pose, quad, instance);
            }
        }
    }

    private record Submission(PoseStack.Pose pose, RenderType renderType,
            List<BlockStateModelPart> parts, int[] tints, int light, int overlay,
            int outlineColor) {
    }
}
