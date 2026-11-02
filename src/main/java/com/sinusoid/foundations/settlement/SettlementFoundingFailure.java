package com.sinusoid.foundations.settlement;

/**
 * Expected, player-facing reasons why founding cannot proceed.
 *
 * <p>These are data, not exceptions: invalid player actions should be reported
 * cleanly without using exceptions for normal control flow.</p>
 */
public enum SettlementFoundingFailure {
    OWNER_ALREADY_HAS_SETTLEMENT,
    UNSUPPORTED_BIOME,
    TERRITORY_OWNED_BY_OTHER_SETTLEMENT
}
