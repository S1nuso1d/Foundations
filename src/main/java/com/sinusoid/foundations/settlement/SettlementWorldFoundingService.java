package com.sinusoid.foundations.settlement;

import java.util.Objects;

/** Coordinates founding and initial land ownership against one world's data. */
public final class SettlementWorldFoundingService {

    private final SettlementBiomeResolver biomeResolver;
    private final TerritoryClaimService territoryClaimService = new TerritoryClaimService();

    public SettlementWorldFoundingService(SettlementBiomeResolver biomeResolver) {
        this.biomeResolver = Objects.requireNonNull(biomeResolver, "biomeResolver");
    }

    /**
     * Founds a settlement and claims its first cell as one server-thread operation.
     * If the cell cannot be claimed, the just-created settlement is rolled back.
     */
    public SettlementFoundingService.FoundingAttemptResult found(
        SettlementSavedData worldData,
        SettlementFoundingRequest request,
        TerritoryCell centerCell
    ) {
        Objects.requireNonNull(worldData, "worldData");
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(centerCell, "centerCell");

        if (!request.location().dimension().equals(centerCell.dimension())) {
            throw new IllegalArgumentException("The initial territory cell must match the founding dimension.");
        }

        SettlementFoundingService foundingService = new SettlementFoundingService(
            worldData.registry(), biomeResolver
        );
        SettlementFoundingService.FoundingAttemptResult result = foundingService.found(request);
        if (!result.isSuccess()) {
            return result;
        }

        Settlement settlement = result.success().settlement();
        TerritoryClaimResult claim = territoryClaimService.claimInitial(
            settlement.id(), centerCell, worldData.territories().all()
        );

        if (!claim.isClaimed()) {
            worldData.registry().unregister(settlement.id());
            return SettlementFoundingService.FoundingAttemptResult.failure(
                SettlementFoundingFailure.TERRITORY_OWNED_BY_OTHER_SETTLEMENT
            );
        }

        worldData.territories().register(claim.territory());
        worldData.markChanged();
        return result;
    }
}
