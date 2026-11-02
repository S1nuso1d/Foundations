package com.sinusoid.foundations.settlement;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Applies the initial ownership rules for settlement territory.
 *
 * <p>This service is pure domain logic: it does not know about Minecraft
 * chunks, blocks, players, world storage, economy, or rendering.</p>
 */
public final class TerritoryClaimService {

    /**
     * Creates the first territory cell at the settlement's founding position.
     * The operation is rejected if another settlement already owns that cell.
     */
    public TerritoryClaimResult claimInitial(
        SettlementId settlementId,
        TerritoryCell centerCell,
        Collection<SettlementTerritory> existingTerritories
    ) {
        Objects.requireNonNull(settlementId, "settlementId");
        Objects.requireNonNull(centerCell, "centerCell");
        Objects.requireNonNull(existingTerritories, "existingTerritories");

        if (isOwnedByAnotherSettlement(settlementId, centerCell, existingTerritories)) {
            return TerritoryClaimResult.rejected(
                TerritoryClaimResult.Status.OWNED_BY_OTHER_SETTLEMENT
            );
        }

        return TerritoryClaimResult.claimed(
            new SettlementTerritory(settlementId, centerCell.dimension(), Set.of(centerCell))
        );
    }

    /**
     * Claims one additional cell for an existing territory.
     * A new cell must be in the same dimension and share an edge with at least
     * one cell already owned by the settlement.
     */
    public TerritoryClaimResult claim(
        SettlementTerritory currentTerritory,
        TerritoryCell candidate,
        Collection<SettlementTerritory> existingTerritories
    ) {
        Objects.requireNonNull(currentTerritory, "currentTerritory");
        Objects.requireNonNull(candidate, "candidate");
        Objects.requireNonNull(existingTerritories, "existingTerritories");

        if (!currentTerritory.dimension().equals(candidate.dimension())) {
            return TerritoryClaimResult.rejected(
                TerritoryClaimResult.Status.WRONG_DIMENSION
            );
        }

        if (currentTerritory.contains(candidate)) {
            return TerritoryClaimResult.rejected(
                TerritoryClaimResult.Status.ALREADY_CLAIMED
            );
        }

        if (isOwnedByAnotherSettlement(
            currentTerritory.settlementId(), candidate, existingTerritories
        )) {
            return TerritoryClaimResult.rejected(
                TerritoryClaimResult.Status.OWNED_BY_OTHER_SETTLEMENT
            );
        }

        if (!isAdjacentToTerritory(currentTerritory, candidate)) {
            return TerritoryClaimResult.rejected(
                TerritoryClaimResult.Status.NOT_ADJACENT
            );
        }

        Set<TerritoryCell> updatedCells = new LinkedHashSet<>(currentTerritory.cells());
        updatedCells.add(candidate);

        return TerritoryClaimResult.claimed(
            new SettlementTerritory(
                currentTerritory.settlementId(),
                currentTerritory.dimension(),
                updatedCells
            )
        );
    }

    private boolean isOwnedByAnotherSettlement(
        SettlementId settlementId,
        TerritoryCell cell,
        Collection<SettlementTerritory> territories
    ) {
        for (SettlementTerritory territory : territories) {
            Objects.requireNonNull(territory, "existing territory");
            if (!territory.settlementId().equals(settlementId) && territory.contains(cell)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdjacentToTerritory(
        SettlementTerritory territory,
        TerritoryCell candidate
    ) {
        for (TerritoryCell ownedCell : territory.cells()) {
            long distance = Math.abs((long) ownedCell.x() - candidate.x())
                + Math.abs((long) ownedCell.z() - candidate.z());
            if (distance == 1L) {
                return true;
            }
        }
        return false;
    }
}
