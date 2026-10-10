# ScoutKit (Fabric, Minecraft 1.21.4)

Right Shift = open/close menu, F6 = toggle freecam (rebindable in Controls).

Freecam: WASD + Space/Shift to fly, Ctrl to go faster. Your body stays put, attacks/placing/mining
are blocked, and freecam auto-ends if you take damage.

## Build
1. Generate a fresh template at https://fabricmc.net/develop (MC 1.21.4, Yarn mappings, package com.example.scoutkit, mod id scoutkit).
2. Copy this project's `src/` over the template's `src/` (replace fabric.mod.json and the mixins json).
3. Make sure gradle.properties versions match what the generator gave you.
4. `./gradlew build`  ->  build/libs/scoutkit-1.0.0.jar
5. Put the jar + Fabric API in `.minecraft/mods` using a Fabric 1.21.4 profile.
