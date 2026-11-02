package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * Explicit outcome of classifying a biome for settlement generation.
 */
public record SettlementBiomeResolution(Status status, SettlementBiome biome) {

    public SettlementBiomeResolution {
        Objects.requireNonNull(status, "status");
        if ((status == Status.SUPPORTED) != (biome != null)) {
            throw new IllegalArgumentException(
                "Only a supported biome resolution may contain a settlement biome."
            );
        }
    }

    public static SettlementBiomeResolution supported(SettlementBiome biome) {
        return new SettlementBiomeResolution(Status.SUPPORTED, Objects.requireNonNull(biome, "biome"));
    }

    public static SettlementBiomeResolution undetermined() {
        return new SettlementBiomeResolution(Status.UNDETERMINED, null);
    }

    public static SettlementBiomeResolution unsupported() {
        return new SettlementBiomeResolution(Status.UNSUPPORTED, null);
    }

    public enum Status {
        SUPPORTED,
        UNDETERMINED,
        UNSUPPORTED
    }
}
