package com.sinusoid.foundations.settlement;

import java.util.Collection;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

/** Versioned NBT representation of settlements and their territories. */
public final class SettlementStorageCodec {

    private static final String DATA_VERSION = "data_version";
    private static final String SETTLEMENTS = "settlements";
    private static final String TERRITORIES = "territories";
    private static final int CURRENT_VERSION = 1;

    private SettlementStorageCodec() {
    }

    public record DecodedData(SettlementRegistry settlements, TerritoryRegistry territories) {
        public DecodedData {
            Objects.requireNonNull(settlements, "settlements");
            Objects.requireNonNull(territories, "territories");
        }
    }

    public static CompoundTag encode(Collection<Settlement> settlements) {
        Objects.requireNonNull(settlements, "settlements");
        CompoundTag root = new CompoundTag();
        root.putInt(DATA_VERSION, CURRENT_VERSION);
        ListTag entries = new ListTag();
        for (Settlement settlement : settlements) {
            entries.add(SettlementNbtCodec.encode(settlement));
        }
        root.put(SETTLEMENTS, entries);
        return root;
    }

    public static CompoundTag encode(SettlementRegistry settlements, TerritoryRegistry territories) {
        Objects.requireNonNull(settlements, "settlements");
        Objects.requireNonNull(territories, "territories");
        CompoundTag root = encode(settlements.all());
        ListTag entries = new ListTag();
        for (SettlementTerritory territory : territories.all()) {
            entries.add(TerritoryNbtCodec.encode(territory));
        }
        root.put(TERRITORIES, entries);
        return root;
    }

    public static DecodedData decodeAll(CompoundTag root) {
        Objects.requireNonNull(root, "root");
        int version = root.getIntOr(DATA_VERSION, 0);
        if (version > CURRENT_VERSION) {
            throw new IllegalArgumentException(
                "Settlement data version " + version
                    + " is newer than this version of Foundations supports."
            );
        }

        SettlementRegistry settlements = new SettlementRegistry();
        ListTag entries = root.getListOrEmpty(SETTLEMENTS);
        for (int index = 0; index < entries.size(); index++) {
            settlements.register(SettlementNbtCodec.decode(entries.getCompoundOrEmpty(index)));
        }

        TerritoryRegistry territories = new TerritoryRegistry();
        ListTag entries = root.getListOrEmpty(TERRITORIES);
        for (int index = 0; index < entries.size(); index++) {
                SettlementTerritory territory = TerritoryNbtCodec.decode(entries.getCompoundOrEmpty(index));
                if (settlements.find(territory.settlementId()).isEmpty()) {
                    throw new IllegalArgumentException(
                        "Saved territory references a settlement that does not exist."
                    );
                }
                territories.register(territory);
        }
        return new DecodedData(settlements, territories);
    }

    /** Backwards-compatible settlement-only view of the decoded data. */
    public static SettlementRegistry decode(CompoundTag root) {
        return decodeAll(root).settlements();
    }

    public static int currentVersion() {
        return CURRENT_VERSION;
    }
}
