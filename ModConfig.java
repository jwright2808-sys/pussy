package com.example.scoutkit;

/** Simple in-memory settings (not persisted between launches). */
public final class ModConfig {
    private ModConfig() {}

    public static boolean playerEsp = false;
    public static boolean espBoxes = true;
    public static boolean espGlow = true;

    public static boolean activity = false;
    /** Mined blocks per chunk before the chunk is highlighted. */
    public static int activityThreshold = 3;
    /** How long activity is remembered. */
    public static long activityTtlMs = 10L * 60L * 1000L;
    /** Only count blocks at least this far below the surface (filters surface digging/trees). */
    public static int undergroundDepth = 5;
}
