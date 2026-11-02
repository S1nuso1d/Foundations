package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * Outcome of an attempt to claim a territory cell.
 */
public record TerritoryClaimResult(Status status, SettlementTerritory territory) {

    public TerritoryClaimResult {
        Objects.requireNonNull(status, "status");
        if ((status == Status.CLAIMED) != (territory != null)) {
            throw new IllegalArgumentException(
                "Only a successful claim may contain an updated territory."
            );
        }
    }

    public static TerritoryClaimResult claimed(SettlementTerritory territory) {
        return new TerritoryClaimResult(Status.CLAIMED, Objects.requireNonNull(territory, "territory"));
    }

    public static TerritoryClaimResult rejected(Status status) {
        if (status == Status.CLAIMED) {
            throw new IllegalArgumentException("Use claimed() for a successful result.");
        }
        return new TerritoryClaimResult(status, null);
    }

    public boolean isClaimed() {
        return status == Status.CLAIMED;
    }

    public enum Status {
        CLAIMED,
        ALREADY_CLAIMED,
        OWNED_BY_OTHER_SETTLEMENT,
        NOT_ADJACENT,
        WRONG_DIMENSION
    }
}
