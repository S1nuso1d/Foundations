package com.sinusoid.foundations.settlement;

/**
 * Environmental identity chosen when a settlement is founded.
 *
 * <p>This is a gameplay classification, not a raw Minecraft biome registry key.
 * Several Minecraft biomes may belong to the same settlement environment.
 * Keeping this domain type independent from Minecraft's biome API makes the
 * settlement model stable and testable.</p>
 */
public enum SettlementBiome {
    PLAINS,
    FOREST,
    TAIGA,
    SAVANNA,
    DESERT,
    JUNGLE,
    SWAMP,
    SNOWY,
    MOUNTAIN,
    COASTAL,
    RIVER
}
