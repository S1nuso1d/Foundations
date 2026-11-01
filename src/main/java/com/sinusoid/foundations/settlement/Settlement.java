package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * Minimal domain representation of a Foundations settlement.
 *
 * <p>This first version deliberately contains only stable identity, a name,
 * and ownership. Territory, population, buildings, resources, and persistence
 * will be added as separate concerns rather than turning this class into a
 * catch-all container.</p>
 */
public record Settlement(
    SettlementId id,
    String name,
    SettlementOwner owner
) {

    public Settlement {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(owner, "owner");

        name = name.trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Settlement name must not be blank.");
        }

        if (name.length() > 64) {
            throw new IllegalArgumentException("Settlement name must not exceed 64 characters.");
        }
    }

    public static Settlement create(String name, SettlementOwner owner) {
        return new Settlement(SettlementId.random(), name, owner);
    }
}
