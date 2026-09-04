# Render Hide 2.5.0-alpha.6 — Minecraft 26.2 port

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
