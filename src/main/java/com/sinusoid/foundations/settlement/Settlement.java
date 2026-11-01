package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * Minimal domain representation of a Foundations settlement.
 *
 * <p>A settlement's environment is fixed at founding time. The environment
 * influences future architectural, economic and visual systems, while the
 * settlement itself remains independent from Minecraft registry objects.</p>
 */
public record Settlement(
    SettlementId id,
    String name,
    SettlementOwner owner,
    SettlementBiome biome
) {

    public Settlement {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(biome, "biome");

        name = name.trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Settlement name must not be blank.");
        }

        if (name.length() > 64) {
            throw new IllegalArgumentException("Settlement name must not exceed 64 characters.");
        }
    }

    public static Settlement create(String name, SettlementOwner owner, SettlementBiome biome) {
        return new Settlement(SettlementId.random(), name, owner, biome);
    }
}
