package com.sinusoid.foundations.settlement;

import java.util.Objects;
import java.util.UUID;

/**
 * Identity of the player who founded or currently owns a settlement.
 *
 * <p>Only the stable player UUID is stored in the domain model. Display names
 * are presentation data and may change independently.</p>
 */
public record SettlementOwner(UUID playerId) {

    public SettlementOwner {
        Objects.requireNonNull(playerId, "playerId");
    }
}
