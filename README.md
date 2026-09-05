# Render Hide 2.5.0-alpha.15 — Minecraft 26.2 port

Port based on the supplied `render-hide-1.21.11-2.5.0-alpha.1.jar` and its
matching source revision. This repository ports that exact behavior to
Minecraft 26.2.
The original filtering, entity handling, lighting, GUI, commands, keybindings,
selection outlines, and opacity rules remain the baseline.

Alpha.4 removes block-state-to-AIR substitution. Static blocks, fluids, pistons,
and other moving blocks now share a renderer-only opacity decision: normal,
translucent, or skipped at the final geometry/submission boundary.

Alpha.4 also applies that decision inside Fabric Indigo's alternate terrain
quad pipeline, which bypasses Vanilla's final `BlockQuadOutput` callbacks.

Alpha.5 removes internal faces between adjacent translucent moving blocks,
prevents hidden neighbours from darkening visible filter blocks in Indigo, and
applies the opacity slider to unfiltered entities and their submitted models.

Alpha.6 enables the moving renderer's actual face-culling pass so adjacent
piston-carried blocks do not darken at their shared face. It also preserves the
item frame body's forward Z offset and applies alpha to its untinted block-model
quads, keeping the frame and displayed item on the same opacity path.

Alpha.7 supplies neighbouring moving states to the culling pass for both
translucent and visible-filter blocks, stops exposing internal filter boundaries
at partial opacity, and submits the item-frame body through the proven item-quad
path while retaining its forward Z offset.

Alpha.8 uses Minecraft's standard translucent item render type for the
item-frame body—the same proven path as the displayed item—and applies a small
geometry offset toward the camera-facing side so the frame cannot disappear
into the supporting block's depth surface.

Alpha.9 keeps the item-frame body in its native block-model submission path so
Minecraft 26.2 computes translucent distance from the model centre. Its
translucent entity render type now targets the block-and-item composite while
retaining the vanilla forward Z layering.

Alpha.10 defers flush-mounted translucent entity block models such as the item
frame body until after translucent terrain. This prevents the supporting block,
which Minecraft 26.2 normally draws later, from covering the frame while its
displayed item remains visible.

Alpha.11 keeps the item-frame body's original block-model quads and tint layers
unchanged. Opacity is applied through Minecraft 26.2's block-model base tint
color instead, avoiding the material tint-index rewrite that could discard the
frame body while its separately submitted displayed item remained visible.

Alpha.12 draws the post-terrain item-frame body directly into the main target.
This prevents the later depth-based transparency composite from placing the
supporting translucent block over the frame even though the frame submission
itself was executed after terrain.

Alpha.13 bypasses Minecraft's block-model feature batch for forward-offset
entity models. The item-frame body is now emitted from its original baked model
quads by a post-terrain custom feature, preserving resource-pack geometry,
tints, light, overlay, and the vanilla forward Z offset while avoiding the
feature batch that continued to discard the frame at partial opacity.

Alpha.14 moves that forward-offset body one thirty-second of a block farther
toward the visible side before its post-terrain draw. This prevents the support
surface's existing depth value from rejecting the thin frame body. One-time
submission and execution diagnostics are also written to the game log so this
path can be distinguished from an earlier pipeline failure.

Alpha.15 identifies the item-frame body's exact
`ENTITY_SOLID_Z_OFFSET_FORWARD` pipeline directly. The earlier indirect
layering-state comparison did not recognize this submission, so the intended
post-terrain item-frame path was never entered despite the body being submitted
normally. The layering comparison remains only as a compatibility fallback.

## Requirements

- Java 25
- Minecraft 26.2
- Fabric Loader 0.19.3 or newer
- Fabric API 0.158.0+26.2 or newer
- Sodium 0.9.1 for Minecraft 26.2 (optional)

## Build

```bash
./gradlew build
```

The built mod JAR is created in `build/libs`.

The 26.2 port uses the current named Minecraft APIs and carries separate,
optional Sodium rendering hooks.

See `docs/TRANSPARENCY_ARCHITECTURE.md` for renderer boundaries and the
required validation matrix.
