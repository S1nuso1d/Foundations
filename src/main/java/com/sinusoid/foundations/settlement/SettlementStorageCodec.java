package com.sinusoid.foundations.settlement;

import java.util.Collection;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

/**
 * Durable representation of the complete settlement collection.
 *
 * <p>This class owns the save format for the settlement subsystem. The actual
 * Minecraft world storage mechanism is intentionally separate so the domain
 * format can be tested and evolved independently.</p>
 */
public final class SettlementStorageCodec {

    private static final String DATA_VERSION = "data_version";
    private static final String SETTLEMENTS = "settlements";
    private static final int CURRENT_VERSION = 1;

    private SettlementStorageCodec() {
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

    public static SettlementRegistry decode(CompoundTag root) {
        Objects.requireNonNull(root, "root");

        int version = root.contains(DATA_VERSION, Tag.TAG_INT)
            ? root.getInt(DATA_VERSION)
            : 0;

        if (version > CURRENT_VERSION) {
            throw new IllegalArgumentException(
                "Settlement data version " + version
                    + " is newer than this version of Foundations supports."
            );
        }

        SettlementRegistry registry = new SettlementRegistry();

        if (!root.contains(SETTLEMENTS, Tag.TAG_LIST)) {
            return registry;
        }

        ListTag entries = root.getList(SETTLEMENTS, Tag.TAG_COMPOUND);
        for (Tag entry : entries) {
            if (!(entry instanceof CompoundTag settlementTag)) {
                throw new IllegalArgumentException("Invalid settlement entry in saved data.");
            }

            registry.register(SettlementNbtCodec.decode(settlementTag));
        }

        return registry;
    }

    public static int currentVersion() {
        return CURRENT_VERSION;
    }
}
