package com.sinusoid.foundations.settlement;

import java.util.Objects;
import java.util.UUID;

/**
 * Stable identity of a settlement.
 *
 * <p>The UUID is intentionally independent from a settlement's name or owner.
 * Names and ownership may change during gameplay; identity must not.</p>
 */
public record SettlementId(UUID value) {

    public SettlementId {
        Objects.requireNonNull(value, "value");
    }

    public static SettlementId random() {
        return new SettlementId(UUID.randomUUID());
    }
}
