package com.sinusoid.foundations.settlement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Domain aggregate describing the cells controlled by one settlement.
 *
 * <p>This model deliberately does not decide cell size, adjacency, or how
 * borders are rendered. It does provide the geometric outer edge of the
 * territory so different presentation adapters can render it consistently.</p>
 */
public final class SettlementTerritory {

    private static final List<TerritoryBoundaryEdge.Direction> EDGE_DIRECTIONS =
        List.of(
            TerritoryBoundaryEdge.Direction.NORTH,
            TerritoryBoundaryEdge.Direction.EAST,
            TerritoryBoundaryEdge.Direction.SOUTH,
            TerritoryBoundaryEdge.Direction.WEST
        );

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

    /**
     * Returns only exposed sides: an edge shared with another cell in this
     * territory is omitted. Results are sorted for stable rendering.
     */
    public List<TerritoryBoundaryEdge> boundaryEdges() {
        List<TerritoryBoundaryEdge> edges = new ArrayList<>();
        for (TerritoryCell cell : cells) {
            for (TerritoryBoundaryEdge.Direction direction : EDGE_DIRECTIONS) {
                TerritoryCell neighbor = new TerritoryCell(
                    dimension,
                    cell.x() + direction.offsetX(),
                    cell.z() + direction.offsetZ()
                );
                if (!cells.contains(neighbor)) {
                    edges.add(new TerritoryBoundaryEdge(cell, direction));
                }
            }
        }

        edges.sort(
            Comparator
                .comparingInt((TerritoryBoundaryEdge edge) -> edge.cell().x())
                .thenComparingInt(edge -> edge.cell().z())
                .thenComparingInt(edge -> edge.direction().ordinal())
        );
        return List.copyOf(edges);
    }

    public int boundaryEdgeCount() {
        return boundaryEdges().size();
    }

    public int cellCount() {
        return cells.size();
    }
}
