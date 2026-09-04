# Render Hide 2.5.0-alpha.1 — Minecraft 26.2 port

Transparency rework based on the supplied `render-hide-1.21.11-2.4.5`
release JAR. This repository ports that exact behavior to Minecraft 26.2.
The original filtering, entity handling, lighting, GUI, commands, keybindings,
selection outlines, and opacity rules remain the baseline.

## Requirements

- Java 25
- Minecraft 26.2
- Fabric Loader 0.19.3 or newer
- Fabric API 0.158.0+26.2 or newer

## Build

```bash
./gradlew build
```

The built mod JAR is created in `build/libs`.

The supplied release JAR used Fabric intermediary names. The port keeps those
references where Minecraft 26.2 still exposes the same intermediary API and
updates changed APIs explicitly.

See `docs/TRANSPARENCY_ARCHITECTURE.md` for renderer boundaries and the
required validation matrix.
