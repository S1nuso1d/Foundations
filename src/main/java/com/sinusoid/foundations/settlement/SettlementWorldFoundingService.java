package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * Bridges the settlement domain founding rules to authoritative world data.
 *
 * <p>Only successful founding attempts mark the world's settlement data dirty.
 * Expected failures leave saved data untouched.</p>
 */
public final class SettlementWorldFoundingService {

    private final SettlementBiomeResolver biomeResolver;

    public SettlementWorldFoundingService(SettlementBiomeResolver biomeResolver) {
        this.biomeResolver = Objects.requireNonNull(biomeResolver, "biomeResolver");
    }

    public SettlementFoundingService.FoundingAttemptResult found(
        SettlementSavedData worldData,
        SettlementFoundingRequest request
    ) {
        Objects.requireNonNull(worldData, "worldData");
        Objects.requireNonNull(request, "request");

        SettlementFoundingService service = new SettlementFoundingService(
            worldData.registry(),
            biomeResolver
        );

        SettlementFoundingService.FoundingAttemptResult result = service.found(request);

        if (result.isSuccess()) {
            worldData.markChanged();
        }

        return result;
    }
}
