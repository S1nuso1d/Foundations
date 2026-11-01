package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * Minimal domain representation of a Foundations settlement.
 *
 * <p>A settlement's founding environment and location are fixed at creation
 * time. Territory, population, buildings and resources remain separate
 * concerns and will be introduced in later iterations.</p>
 */
public record Settlement(
    SettlementId id,
    String name,
    SettlementOwner owner,
    SettlementBiome biome,
    SettlementLocation location
) {

    public Settlement {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(biome, "biome");
        Objects.requireNonNull(location, "location");

        name = name.trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Settlement name must not be blank.");
        }

        if (name.length() > 64) {
            throw new IllegalArgumentException("Settlement name must not exceed 64 characters.");
        }
    }

    public static Settlement create(
        String name,
        SettlementOwner owner,
        SettlementBiome biome,
        SettlementLocation location
    ) {
        return new Settlement(SettlementId.random(), name, owner, biome, location);
    }
}
