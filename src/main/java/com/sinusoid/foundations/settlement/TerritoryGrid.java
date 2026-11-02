package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * Maps block positions onto the abstract territory grid.
 *
 * <p>The initial grid cell covers 16 by 16 horizontal blocks. It uses integer
 * grid coordinates only; territory ownership never depends on Minecraft's
 * chunk objects or chunk-loading state. The scale can later become configurable.</p>
 */
public final class TerritoryGrid {

    public static final int DEFAULT_CELL_SIZE_BLOCKS = 16;

    private final int cellSizeBlocks;

    public TerritoryGrid() {
        this(DEFAULT_CELL_SIZE_BLOCKS);
    }

    public TerritoryGrid(int cellSizeBlocks) {
        if (cellSizeBlocks < 1) {
            throw new IllegalArgumentException("Cell size must be positive.");
        }
        this.cellSizeBlocks = cellSizeBlocks;
    }

    public TerritoryCell cellAt(String dimension, int blockX, int blockZ) {
        Objects.requireNonNull(dimension, "dimension");
        return new TerritoryCell(
            dimension,
            Math.floorDiv(blockX, cellSizeBlocks),
            Math.floorDiv(blockZ, cellSizeBlocks)
        );
    }

    public int cellSizeBlocks() {
        return cellSizeBlocks;
    }
}
