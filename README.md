# Render Hide 2.5.0-alpha.3 — Minecraft 26.2 port

Port based on the supplied `render-hide-1.21.11-2.5.0-alpha.1.jar` and its
matching source revision. This repository ports that exact behavior to
Minecraft 26.2.
The original filtering, entity handling, lighting, GUI, commands, keybindings,
selection outlines, and opacity rules remain the baseline.

Alpha.3 removes block-state-to-AIR substitution. Static blocks, fluids, pistons,
and other moving blocks now share a renderer-only opacity decision: normal,
translucent, or skipped at the final geometry/submission boundary.

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
