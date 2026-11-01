package com.sinusoid.foundations.settlement;

import java.util.Optional;

/**
 * Resolves an external biome identifier into a Foundations settlement biome.
 *
 * <p>The domain layer intentionally receives a plain namespaced identifier
 * rather than a Minecraft registry object. A Forge-specific adapter can later
 * obtain the identifier from the biome registry without coupling settlement
 * rules to Minecraft internals.</p>
 */
@FunctionalInterface
public interface SettlementBiomeResolver {

    /**
     * Attempts to classify a biome.
     *
     * @param biomeId namespaced biome identifier, for example
     *                {@code minecraft:plains}
     * @return the Foundations environment, or empty when no rule exists
     */
    Optional<SettlementBiome> resolve(String biomeId);
}
