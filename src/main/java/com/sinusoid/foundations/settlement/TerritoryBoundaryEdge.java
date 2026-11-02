package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * One exposed side of a territory cell.
 *
 * <p>Only sides without a same-territory neighbor are boundary edges. This
 * representation is independent of block rendering and can be used by maps,
 * particles, or a future boundary overlay.</p>
 */
public record TerritoryBoundaryEdge(TerritoryCell cell, Direction direction) {

    public TerritoryBoundaryEdge {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(direction, "direction");
    }

    public enum Direction {
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0);

        private final int offsetX;
        private final int offsetZ;

        Direction(int offsetX, int offsetZ) {
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
        }

        public int offsetX() {
            return offsetX;
        }

        public int offsetZ() {
            return offsetZ;
        }
    }
}
