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
The item-frame body is one such block model: its quads are flattened into the
same item feature path used by the displayed item, untinted quads receive a
generated alpha tint, and a translucent forward-Z-offset render type keeps the
frame above its supporting block.

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
