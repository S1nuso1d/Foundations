package com.sinusoid.foundations.settlement;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Coordinates territory queries and expansion against one world's saved data.
 *
 * <p>Claim rules remain in {@link TerritoryClaimService}; this adapter commits
 * successful changes and marks the world data dirty for persistence.</p>
 */
public final class SettlementWorldTerritoryService {

    private final TerritoryClaimService claimService;

    public SettlementWorldTerritoryService() {
        this(new TerritoryClaimService());
    }

    SettlementWorldTerritoryService(TerritoryClaimService claimService) {
        this.claimService = Objects.requireNonNull(claimService, "claimService");
    }

    /**
     * Attempts to add one adjacent cell to a settlement's territory.
     *
     * @return empty when the settlement has no registered territory; otherwise
     *         the domain claim result, including any rejection reason
     */
    public Optional<TerritoryClaimResult> claimAdjacentCell(
        SettlementSavedData worldData,
        SettlementId settlementId,
        TerritoryCell candidate
    ) {
        Objects.requireNonNull(worldData, "worldData");
        Objects.requireNonNull(settlementId, "settlementId");
        Objects.requireNonNull(candidate, "candidate");

        Optional<SettlementTerritory> current = worldData.territories().find(settlementId);
        if (current.isEmpty()) {
            return Optional.empty();
        }

        TerritoryClaimResult result = claimService.claim(
            current.get(),
            candidate,
            worldData.territories().all()
        );

        if (result.isClaimed()) {
            worldData.territories().replace(result.territory());
            worldData.markChanged();
        }

        return Optional.of(result);
    }

    /** Returns the owner of a cell, if any, in this world's territory registry. */
    public Optional<SettlementId> findOwner(
        SettlementSavedData worldData,
        TerritoryCell cell
    ) {
        Objects.requireNonNull(worldData, "worldData");
        Objects.requireNonNull(cell, "cell");
        return worldData.territories().findOwner(cell);
    }

    /**
     * Returns the exposed outer edges for rendering or map presentation.
     * Shared sides between neighboring owned cells are not included.
     */
    public Optional<List<TerritoryBoundaryEdge>> findBoundary(
        SettlementSavedData worldData,
        SettlementId settlementId
    ) {
        Objects.requireNonNull(worldData, "worldData");
        Objects.requireNonNull(settlementId, "settlementId");
        return worldData.territories()
            .find(settlementId)
            .map(SettlementTerritory::boundaryEdges);
    }
}
