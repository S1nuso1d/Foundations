package com.sinusoid.foundations.settlement;

import java.util.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Server-side world data for Foundations settlements.
 *
 * <p>The server owns the authoritative registry. Clients must never mutate this
 * object directly; future synchronization will expose read-only snapshots or
 * dedicated network messages.</p>
 */
public final class SettlementSavedData extends SavedData {

    private final SettlementRegistry registry;

    private SettlementSavedData(SettlementRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    public static SettlementSavedData create() {
        return new SettlementSavedData(new SettlementRegistry());
    }

    public static SettlementSavedData load(
        CompoundTag tag,
        HolderLookup.Provider registries
    ) {
        return new SettlementSavedData(SettlementStorageCodec.decode(tag));
    }

    public SettlementRegistry registry() {
        return registry;
    }

    /**
     * Marks settlement world data dirty after a successful mutation.
     */
    public Settlement createSettlement(
        String name,
        SettlementOwner owner,
        SettlementBiome biome,
        SettlementLocation location
    ) {
        Settlement settlement = registry.create(name, owner, biome, location);
        setDirty();
        return settlement;
    }

    @Override
    public CompoundTag save(
        CompoundTag tag,
        HolderLookup.Provider registries
    ) {
        CompoundTag encoded = SettlementStorageCodec.encode(registry.all());
        tag.merge(encoded);
        return tag;
    }
}
