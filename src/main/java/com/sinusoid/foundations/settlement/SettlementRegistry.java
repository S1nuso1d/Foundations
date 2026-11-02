package com.sinusoid.foundations.settlement;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory registry for settlement domain objects.
 *
 * <p>This class owns domain-level uniqueness and lookup rules only. Minecraft
 * world persistence is intentionally not implemented here and will be added as
 * a separate adapter in a later iteration.</p>
 */
public final class SettlementRegistry {

    private final Map<SettlementId, Settlement> settlementsById = new HashMap<>();
    private final Map<UUID, SettlementId> settlementByOwner = new HashMap<>();

    public void register(Settlement settlement) {
        Objects.requireNonNull(settlement, "settlement");

        if (settlementsById.containsKey(settlement.id())) {
            throw new IllegalStateException("A settlement with this ID is already registered.");
        }

        UUID ownerId = settlement.owner().playerId();
        if (settlementByOwner.containsKey(ownerId)) {
            throw new IllegalStateException("This owner already has a registered settlement.");
        }

        settlementsById.put(settlement.id(), settlement);
        settlementByOwner.put(ownerId, settlement.id());
    }

    public Settlement create(
        String name,
        SettlementOwner owner,
        SettlementBiome biome,
        SettlementLocation location
    ) {
        Settlement settlement = Settlement.create(name, owner, biome, location);
        register(settlement);
        return settlement;
    }

    public boolean unregister(SettlementId id) {
        Objects.requireNonNull(id, "id");
        Settlement removed = settlementsById.remove(id);
        if (removed == null) {
            return false;
        }
        settlementByOwner.remove(removed.owner().playerId(), id);
        return true;
    }

    public Optional<Settlement> find(SettlementId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(settlementsById.get(id));
    }

    public Optional<Settlement> findByOwner(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");

        SettlementId settlementId = settlementByOwner.get(playerId);
        return settlementId == null ? Optional.empty() : find(settlementId);
    }

    public boolean hasSettlement(UUID playerId) {
        return findByOwner(playerId).isPresent();
    }

    public int size() {
        return settlementsById.size();
    }

    public Collection<Settlement> all() {
        return List.copyOf(settlementsById.values());
    }
}
