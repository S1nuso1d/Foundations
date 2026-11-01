package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * Successful result of a settlement founding attempt.
 */
public record SettlementFoundingResult(Settlement settlement) {

    public SettlementFoundingResult {
        Objects.requireNonNull(settlement, "settlement");
    }
}
