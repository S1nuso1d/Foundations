package com.sinusoid.foundations.settlement;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Classification rules for known vanilla Minecraft biome identifiers.
 *
 * <p>Unknown identifiers deliberately return {@link Optional#empty()}.
 * Silently classifying every modded biome as a vanilla environment would make
 * settlement appearance unpredictable and hide missing compatibility rules.</p>
 */
public final class VanillaSettlementBiomeResolver implements SettlementBiomeResolver {

    private static final Map<String, SettlementBiome> RULES = Map.ofEntries(
        Map.entry("minecraft:plains", SettlementBiome.PLAINS),
        Map.entry("minecraft:sunflower_plains", SettlementBiome.PLAINS),
        Map.entry("minecraft:forest", SettlementBiome.FOREST),
        Map.entry("minecraft:flower_forest", SettlementBiome.FOREST),
        Map.entry("minecraft:birch_forest", SettlementBiome.FOREST),
        Map.entry("minecraft:old_growth_birch_forest", SettlementBiome.FOREST),
        Map.entry("minecraft:dark_forest", SettlementBiome.FOREST),
        Map.entry("minecraft:taiga", SettlementBiome.TAIGA),
        Map.entry("minecraft:snowy_taiga", SettlementBiome.TAIGA),
        Map.entry("minecraft:old_growth_pine_taiga", SettlementBiome.TAIGA),
        Map.entry("minecraft:old_growth_spruce_taiga", SettlementBiome.TAIGA),
        Map.entry("minecraft:savanna", SettlementBiome.SAVANNA),
        Map.entry("minecraft:savanna_plateau", SettlementBiome.SAVANNA),
        Map.entry("minecraft:windswept_savanna", SettlementBiome.SAVANNA),
        Map.entry("minecraft:desert", SettlementBiome.DESERT),
        Map.entry("minecraft:jungle", SettlementBiome.JUNGLE),
        Map.entry("minecraft:sparse_jungle", SettlementBiome.JUNGLE),
        Map.entry("minecraft:bamboo_jungle", SettlementBiome.JUNGLE),
        Map.entry("minecraft:swamp", SettlementBiome.SWAMP),
        Map.entry("minecraft:mangrove_swamp", SettlementBiome.SWAMP),
        Map.entry("minecraft:snowy_plains", SettlementBiome.SNOWY),
        Map.entry("minecraft:ice_spikes", SettlementBiome.SNOWY),
        Map.entry("minecraft:grove", SettlementBiome.SNOWY),
        Map.entry("minecraft:jagged_peaks", SettlementBiome.MOUNTAIN),
        Map.entry("minecraft:frozen_peaks", SettlementBiome.MOUNTAIN),
        Map.entry("minecraft:stony_peaks", SettlementBiome.MOUNTAIN),
        Map.entry("minecraft:meadow", SettlementBiome.MOUNTAIN),
        Map.entry("minecraft:windswept_hills", SettlementBiome.MOUNTAIN),
        Map.entry("minecraft:windswept_forest", SettlementBiome.MOUNTAIN),
        Map.entry("minecraft:windswept_gravelly_hills", SettlementBiome.MOUNTAIN),
        Map.entry("minecraft:beach", SettlementBiome.COASTAL),
        Map.entry("minecraft:snowy_beach", SettlementBiome.COASTAL),
        Map.entry("minecraft:stony_shore", SettlementBiome.COASTAL),
        Map.entry("minecraft:river", SettlementBiome.RIVER),
        Map.entry("minecraft:frozen_river", SettlementBiome.RIVER)
    );

    @Override
    public Optional<SettlementBiome> resolve(String biomeId) {
        Objects.requireNonNull(biomeId, "biomeId");
        return Optional.ofNullable(RULES.get(biomeId.trim().toLowerCase(Locale.ROOT)));
    }
}
