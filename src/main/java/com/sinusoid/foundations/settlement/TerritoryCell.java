package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * A dimension-scoped, abstract territory cell.
 *
 * <p>Coordinates belong to the territory grid, not directly to Minecraft
 * chunks or blocks. The world adapter will define the grid scale and map
 * positions to cells. This keeps territory rules independent from storage and
 * from the eventual border shape.</p>
 */
public record TerritoryCell(String dimension, int x, int z) {

    public TerritoryCell {
        Objects.requireNonNull(dimension, "dimension");
        dimension = dimension.trim();
        if (dimension.isEmpty()) {
            throw new IllegalArgumentException("Dimension must not be blank.");
        }
    }
}
