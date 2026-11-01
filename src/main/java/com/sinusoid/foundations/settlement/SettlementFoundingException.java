package com.sinusoid.foundations.settlement;

/**
 * Signals that a settlement could not legally be founded.
 */
public final class SettlementFoundingException extends RuntimeException {

    public SettlementFoundingException(String message) {
        super(message);
    }
}
