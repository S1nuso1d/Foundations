package com.sinusoid.foundations.settlement;

import java.util.Objects;

/**
 * Coordinates the domain rules for founding a settlement.
 *
 * <p>This service deliberately does not know about Minecraft player objects,
 * levels or registries. Forge integration will act as an adapter around it.</p>
 */
public final class SettlementFoundingService {

    public static final SettlementBiome DEFAULT_BIOME = SettlementBiome.PLAINS;

    private final SettlementRegistry registry;
    private final SettlementBiomeResolver biomeResolver;

    public SettlementFoundingService(
        SettlementRegistry registry,
        SettlementBiomeResolver biomeResolver
    ) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.biomeResolver = Objects.requireNonNull(biomeResolver, "biomeResolver");
    }

    public FoundingAttemptResult found(SettlementFoundingRequest request) {
        Objects.requireNonNull(request, "request");

        if (registry.hasSettlement(request.owner().playerId())) {
            return FoundingAttemptResult.failure(
                SettlementFoundingFailure.OWNER_ALREADY_HAS_SETTLEMENT
            );
        }

        SettlementBiomeResolution resolution = Objects.requireNonNull(
            biomeResolver.resolve(request.biomeId()),
            "biomeResolver result"
        );

        final SettlementBiome biome;
        switch (resolution.status()) {
            case SUPPORTED -> biome = resolution.biome();
            case UNDETERMINED -> biome = DEFAULT_BIOME;
            case UNSUPPORTED -> {
                return FoundingAttemptResult.failure(
                    SettlementFoundingFailure.UNSUPPORTED_BIOME
                );
            }
            default -> throw new IllegalStateException(
                "Unhandled biome resolution status: " + resolution.status()
            );
        }

        Settlement settlement = registry.create(
            request.name(),
            request.owner(),
            biome,
            request.location()
        );

        return FoundingAttemptResult.success(settlement);
    }

    public record FoundingAttemptResult(
        SettlementFoundingResult success,
        SettlementFoundingFailure failure
    ) {
        public FoundingAttemptResult {
            if ((success == null) == (failure == null)) {
                throw new IllegalArgumentException(
                    "A founding attempt must contain exactly one outcome."
                );
            }
        }

        public static FoundingAttemptResult success(Settlement settlement) {
            return new FoundingAttemptResult(
                new SettlementFoundingResult(settlement),
                null
            );
        }

        public static FoundingAttemptResult failure(SettlementFoundingFailure failure) {
            return new FoundingAttemptResult(null, failure);
        }

        public boolean isSuccess() {
            return success != null;
        }
    }
}
