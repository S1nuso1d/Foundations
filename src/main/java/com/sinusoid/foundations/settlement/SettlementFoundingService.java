package com.sinusoid.foundations;

import com.sinusoid.foundations.settlement.SettlementBiome;
import com.sinusoid.foundations.settlement.SettlementBiomeResolver;
import com.sinusoid.foundations.settlement.SettlementFoundingException;
import com.sinusoid.foundations.settlement.SettlementFoundingRequest;
import com.sinusoid.foundations.settlement.SettlementRegistry;
import java.util.Objects;

/**
 * Coordinates the domain rules for founding a settlement.
 *
 * <p>This service deliberately does not know about Minecraft player objects,
 * levels or registries. Forge integration will act as an adapter around it.</p>
 */
public final class SettlementFoundingService {

    private final SettlementRegistry registry;
    private final SettlementBiomeResolver biomeResolver;

    public SettlementFoundingService(
        SettlementRegistry registry,
        SettlementBiomeResolver biomeResolver
    ) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.biomeResolver = Objects.requireNonNull(biomeResolver, "biomeResolver");
    }

    public com.sinusoid.foundations.settlement.Settlement found(SettlementFoundingRequest request) {
        Objects.requireNonNull(request, "request");

        if (registry.hasSettlement(request.owner().playerId())) {
            throw new SettlementFoundingException(
                "This player already owns a settlement."
            );
        }

        SettlementBiome biome = biomeResolver.resolve(request.biomeId())
            .orElseThrow(() -> new SettlementFoundingException(
                "This biome is not supported for settlement founding: "
                    + request.biomeId()
            ));

        return registry.create(
            request.name(),
            request.owner(),
            biome,
            request.location()
        );
    }
}
