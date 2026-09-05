# Transparency architecture

This branch starts from Render Hide 2.5.0-alpha.1 and preserves its filter and
lighting rules. Opacity is global and stored separately in
`renderhide-opacity.json`.

## Required behavior

| Opacity | Static blocks | Moving piston blocks |
| --- | --- | --- |
| 0% | Keep the real state and skip final geometry emission | Keep the render state and cancel final submission |
| 1-99% | Keep the real state/model, force a translucent layer, multiply vertex alpha | Keep the moving model, submit it to the translucent phase, multiply vertex alpha |
| 100% | Use the unchanged normal renderer path | Use the unchanged normal renderer path |

Blocks and entities listed in a global or regional visible filter always render
at 100%. Unfiltered entities inside an active region use the same global opacity
value as hidden blocks.

The same decision is used everywhere:

- `NORMAL`: Render Hide does not alter layer, alpha, geometry, block entity, or
  occlusion behavior. This covers Render Hide OFF, visible filters, and 100%.
- `TRANSLUCENT`: retain the real block state/model, stop opaque occlusion, use
  the translucent layer, and multiply the existing vertex alpha.
- `SKIP`: retain the real block state/model for lookup and filter logic, but do
  not emit final geometry. Block entities and moving-block submissions are
  canceled at their renderer boundaries. No AIR state is introduced.

## Renderer boundaries

- Vanilla static chunks and fluids: `SectionBuilderMixin`,
  `BlockModelRendererMixin`
- Fabric Indigo static chunks: `IndigoTerrainRendererMixin`
- Vanilla/Fabric virtual light: `BlockRenderManagerMixin`,
  `IndigoAoCalculatorMixin`
- Moving blocks and pistons: `PistonBlockEntityRendererMixin`,
  `MovingBlockRenderStateMixin`, `MovingBlockSubmissionMixin`,
  `FallingBlockCommandRendererMixin`
- Sodium chunks and fluids: `SodiumSectionCompilerMixin`,
  `SodiumGhostBlockMixin`, `SodiumFluidRendererMixin`,
  `SodiumBlockOcclusionMixin`, `SodiumVirtualLightMixin`,
  `SodiumLightPipelineMixin`
- Entities: `EntityRenderManagerMixin`, `EntityRenderStateMixin`,
  `EntityAlphaSubmitNodeCollector`

Moving render states normally report AIR for every neighbouring position.
Render Hide supplies the states of adjacent piston-moved blocks for every
visible opacity, including blocks kept opaque by a visible filter, and enables
the moving renderer's culling pass so the normal rules can remove shared
internal faces.

At partial opacity, a normal/filter block keeps the same shared-face culling it
had before Render Hide was enabled. Boundary faces are exposed only when the
neighbour is fully skipped at 0%; exposing them beside a partially transparent
neighbour creates the dark filter seam this design avoids.

Entity renderers may submit block models as well as ordinary entity models.
The item-frame's displayed content stays an item-feature submission. At partial
opacity, its body bypasses the block-model feature batch and a custom feature
emits the original `BlockStateModelPart` baked quads directly. This preserves
resource-pack geometry, tint layers, light, overlay, and the vanilla forward Z
layering without reconstructing the frame or routing it through the item model
path. At full opacity the renderer is left completely vanilla.

Minecraft 26.2 renders translucent entity features before translucent terrain.
For flush-mounted models this lets the supporting translucent block cover the
feature even when both have correct alpha and depth settings. Forward-offset
entity block models are therefore queued as custom features and their original
baked quads are emitted from the post-terrain phase through Fabric's
submit-phase API. They target the main framebuffer so a later cross-target depth
composite cannot cover them again; their original pose, tint, light, overlay,
outline, and forward Z layering are preserved. A small local positive-Z offset
keeps thin flush-mounted bodies in front of the supporting surface's depth.

Changing vertex alpha without moving an opaque block to a translucent layer is
not sufficient. Likewise, changing a layer after a face or model was removed is
too late. Both rules must be preserved when these hooks are updated.

AIR substitution is intentionally forbidden in this design. It changes the
state seen by later render stages, bypasses filter decisions, and can leave a
cached empty section after Render Hide is disabled.

## Known validation targets

- Opaque static blocks at 0%, 30%, and 100%
- Originally translucent static blocks at the same values
- Piston extension and retraction, including body and head
- A filtered and an unfiltered carried block
- Normal and sticky pistons
- Item frames with empty, item, and map contents
- Fabric renderer with and without Sodium
