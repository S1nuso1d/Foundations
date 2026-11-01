package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * All domain data required to found a settlement.
 *
 * <p>The Minecraft/Forge integration layer is responsible for obtaining the
 * player's UUID, position, dimension and biome before creating this request.</p>
 */
public record SettlementFoundingRequest(
    String name,
    SettlementOwner owner,
    SettlementLocation location,
    String biomeId
) {

    public SettlementFoundingRequest {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(biomeId, "biomeId");

        biomeId = biomeId.trim();

        if (biomeId.isEmpty()) {
            throw new IllegalArgumentException("Biome ID must not be blank.");
        }
    }
}
