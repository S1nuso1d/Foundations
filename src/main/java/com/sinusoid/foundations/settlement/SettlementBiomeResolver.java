package com.sinusoid.foundations.settlement;

/**
 * Resolves an external biome identifier into a Foundations settlement biome.
 *
 * <p>The domain layer receives a namespaced identifier rather than a
 * Minecraft registry object. Implementations must distinguish an unknown
 * classification from a biome they explicitly reject.</p>
 */
@FunctionalInterface
public interface SettlementBiomeResolver {

    /**
     * @param biomeId namespaced biome identifier, for example {@code minecraft:plains}
     * @return an explicit supported, undetermined, or unsupported outcome
     */
    SettlementBiomeResolution resolve(String biomeId);
}
