package com.sinusoid.foundations.settlement;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Domain aggregate describing the cells controlled by one settlement.
 *
 * <p>This model deliberately does not decide cell size, adjacency, border
 * rendering, or how cells are persisted. Those are policies/adapters that can
 * evolve independently.</p>
 */
public final class SettlementTerritory {

    private final SettlementId settlementId;
    private final String dimension;
    private final Set<TerritoryCell> cells;

    public SettlementTerritory(
        SettlementId settlementId,
        String dimension,
        Collection<TerritoryCell> cells
    ) {
        this.settlementId = Objects.requireNonNull(settlementId, "settlementId");
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(cells, "cells");

        this.dimension = dimension.trim();
        if (this.dimension.isEmpty()) {
            throw new IllegalArgumentException("Dimension must not be blank.");
        }

        LinkedHashSet<TerritoryCell> copy = new LinkedHashSet<>();
        for (TerritoryCell cell : cells) {
            Objects.requireNonNull(cell, "territory cell");
            if (!this.dimension.equals(cell.dimension())) {
                throw new IllegalArgumentException(
                    "All territory cells must belong to the territory dimension."
                );
            }
            copy.add(cell);
        }

        if (copy.isEmpty()) {
            throw new IllegalArgumentException("A settlement territory must contain at least one cell.");
        }

        this.cells = Set.copyOf(copy);
    }

    public SettlementId settlementId() {
        return settlementId;
    }

    public String dimension() {
        return dimension;
    }

    public Set<TerritoryCell> cells() {
        return cells;
    }

    public boolean contains(TerritoryCell cell) {
        Objects.requireNonNull(cell, "cell");
        return dimension.equals(cell.dimension()) && cells.contains(cell);
    }

    public int cellCount() {
        return cells.size();
    }
}
