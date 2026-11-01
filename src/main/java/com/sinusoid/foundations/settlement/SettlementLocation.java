package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * Immutable founding location of a settlement.
 *
 * <p>The location stores only stable spatial identity. Territory ownership and
 * settlement borders are separate systems and must not be inferred from this
 * class.</p>
 */
public record SettlementLocation(
    String dimension,
    int x,
    int y,
    int z
) {

    public SettlementLocation {
        Objects.requireNonNull(dimension, "dimension");

        dimension = dimension.trim();

        if (dimension.isEmpty()) {
            throw new IllegalArgumentException("Dimension must not be blank.");
        }
    }
}
