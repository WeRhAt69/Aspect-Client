# Aspect Client for Fabric 1.21.11

A client-side animated dropdown-style menu inspired by the supplied layout references. All included modules are harmless stateful placeholders.

## Requirements
- Minecraft 1.21.11
- Java 21
- Fabric Loader 0.19+
- Fabric API 0.141.6+1.21.11

## Build
```bash
./gradlew build
```
The distributable JAR is created in `build/libs/` (use the file without `-sources`).

## Use
1. Install Fabric Loader and Fabric API.
2. Put the Aspect Client JAR into Minecraft's `mods` folder.
3. Press **Right Shift** in game.
4. Click categories in the left panel. Their module panels only appear after selection.
5. Click a module row or its switch to toggle its saved placeholder state.
6. Drag panels by their headers. Press Escape to close.

The key can be changed in Minecraft's Controls settings.
