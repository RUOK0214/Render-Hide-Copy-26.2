# Transparency architecture

This branch starts from Render Hide 2.4.5 and preserves its filter and lighting
rules. Opacity is global and stored separately in `renderhide-opacity.json`.

## Required behavior

| Opacity | Static blocks | Moving piston blocks |
| --- | --- | --- |
| 0% | Keep the 2.4.5 AIR/cancel behavior | Remove the moving render state |
| 1-99% | Keep the real state and model, use a translucent layer, multiply vertex alpha | Store opacity on the moving render state and apply it in the moving renderer |
| 100% | Render normally | Render normally |

Blocks listed in a global or regional visible filter always render at 100%.
Entity filtering remains independent from block opacity.

## Renderer boundaries

- Vanilla/Fabric static chunks: `ChunkRendererRegionMixin`,
  `BlockRenderManagerMixin`
- Moving blocks and pistons: `PistonBlockEntityRendererMixin`,
  `MovingBlockRenderStateMixin`, `FallingBlockCommandRendererMixin`
- Sodium chunks: `SodiumLevelSliceMixin`, `SodiumGhostBlockMixin`,
  `SodiumBlockOcclusionMixin`

Changing vertex alpha without moving an opaque block to a translucent layer is
not sufficient. Likewise, changing a layer after a face or model was removed is
too late. Both rules must be preserved when these hooks are updated.

## Known validation targets

- Opaque static blocks at 0%, 30%, and 100%
- Originally translucent static blocks at the same values
- Piston extension and retraction, including body and head
- A filtered and an unfiltered carried block
- Normal and sticky pistons
- Fabric renderer with and without Sodium

