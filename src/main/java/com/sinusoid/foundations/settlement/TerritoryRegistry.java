package com.sinusoid.foundations.settlement;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** World-scoped index of settlement territories. */
public final class TerritoryRegistry {

    private final Map<SettlementId, SettlementTerritory> territories = new HashMap<>();

    public void register(SettlementTerritory territory) {
        Objects.requireNonNull(territory, "territory");
        if (territories.containsKey(territory.settlementId())) {
            throw new IllegalStateException("Territory for this settlement is already registered.");
        }
        for (SettlementTerritory existing : territories.values()) {
            for (TerritoryCell cell : territory.cells()) {
                if (existing.contains(cell)) {
                    throw new IllegalStateException("Territory cell is already owned: " + cell);
                }
            }
        }
        territories.put(territory.settlementId(), territory);
    }

    public void replace(SettlementTerritory territory) {
        Objects.requireNonNull(territory, "territory");
        if (!territories.containsKey(territory.settlementId())) {
            throw new IllegalStateException("Cannot update an unregistered territory.");
        }
        for (SettlementTerritory existing : territories.values()) {
            if (existing.settlementId().equals(territory.settlementId())) {
                continue;
            }
            for (TerritoryCell cell : territory.cells()) {
                if (existing.contains(cell)) {
                    throw new IllegalStateException("Territory cell is already owned: " + cell);
                }
            }
        }
        territories.put(territory.settlementId(), territory);
    }

    public Optional<SettlementTerritory> find(SettlementId settlementId) {
        Objects.requireNonNull(settlementId, "settlementId");
        return Optional.ofNullable(territories.get(settlementId));
    }

    public Collection<SettlementTerritory> all() {
        return List.copyOf(territories.values());
    }

    public int size() {
        return territories.size();
    }
}
