package com.sinusoid.foundations.settlement;

import java.util.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

/** Server-authoritative, world-scoped settlement and territory data. */
public final class SettlementSavedData extends SavedData {

    private final SettlementRegistry registry;
    private final TerritoryRegistry territories;

    private SettlementSavedData(SettlementRegistry registry, TerritoryRegistry territories) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.territories = Objects.requireNonNull(territories, "territories");
    }

    public static SettlementSavedData create() {
        return new SettlementSavedData(new SettlementRegistry(), new TerritoryRegistry());
    }

    public static SettlementSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        SettlementStorageCodec.DecodedData decoded = SettlementStorageCodec.decodeAll(tag);
        return new SettlementSavedData(decoded.settlements(), decoded.territories());
    }

    public SettlementRegistry registry() {
        return registry;
    }

    public TerritoryRegistry territories() {
        return territories;
    }

    public void markChanged() {
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.merge(SettlementStorageCodec.encode(registry, territories));
        return tag;
    }
}
