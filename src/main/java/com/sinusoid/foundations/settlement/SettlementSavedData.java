package com.sinusoid.foundations.settlement;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/** Server-authoritative, world-scoped settlement and territory data. */
public final class SettlementSavedData extends SavedData {
    public static final SavedDataType<SettlementSavedData> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath("foundations", "foundations_settlements"),
        SettlementSavedData::create,
        Codec.lazyInitialized(() -> CompoundTag.CODEC.xmap(
            SettlementSavedData::fromTag,
            data -> SettlementStorageCodec.encode(data.registry, data.territories)
        )),
        null
    );

    private final SettlementRegistry registry;
    private final TerritoryRegistry territories;

    private SettlementSavedData(SettlementRegistry registry, TerritoryRegistry territories) {
        this.registry = registry;
        this.territories = territories;
    }

    public static SettlementSavedData create() {
        return new SettlementSavedData(new SettlementRegistry(), new TerritoryRegistry());
    }

    private static SettlementSavedData fromTag(CompoundTag tag) {
        SettlementStorageCodec.DecodedData decoded = SettlementStorageCodec.decodeAll(tag);
        return new SettlementSavedData(decoded.settlements(), decoded.territories());
    }

    public SettlementRegistry registry() { return registry; }
    public TerritoryRegistry territories() { return territories; }
    public void markChanged() { setDirty(); }
}
