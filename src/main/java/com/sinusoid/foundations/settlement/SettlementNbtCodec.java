package com.sinusoid.foundations.settlement;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;

/** Serializes the stable settlement domain model to Minecraft NBT. */
public final class SettlementNbtCodec {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String OWNER = "owner";
    private static final String BIOME = "biome";
    private static final String LOCATION = "location";
    private static final String DIMENSION = "dimension";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";

    private SettlementNbtCodec() {}

    public static CompoundTag encode(Settlement settlement) {
        Objects.requireNonNull(settlement, "settlement");
        CompoundTag tag = new CompoundTag();
        tag.putString(ID, settlement.id().value().toString());
        tag.putString(NAME, settlement.name());
        tag.putString(OWNER, settlement.owner().playerId().toString());
        tag.putString(BIOME, settlement.biome().name());
        CompoundTag location = new CompoundTag();
        location.putString(DIMENSION, settlement.location().dimension());
        location.putInt(X, settlement.location().x());
        location.putInt(Y, settlement.location().y());
        location.putInt(Z, settlement.location().z());
        tag.put(LOCATION, location);
        return tag;
    }

    public static Settlement decode(CompoundTag tag) {
        Objects.requireNonNull(tag, "tag");
        require(tag.contains(ID), "Settlement is missing a valid ID.");
        require(tag.contains(OWNER), "Settlement is missing a valid owner.");
        require(tag.contains(NAME), "Settlement is missing a name.");
        require(tag.contains(BIOME), "Settlement is missing a biome.");
        require(tag.contains(LOCATION), "Settlement is missing a location.");
        CompoundTag location = tag.getCompoundOrEmpty(LOCATION);
        require(location.contains(DIMENSION) && location.contains(X)
            && location.contains(Y) && location.contains(Z), "Settlement location is incomplete.");
        String biomeName = tag.getStringOr(BIOME, "");
        final SettlementBiome biome;
        try {
            biome = SettlementBiome.valueOf(biomeName);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown settlement biome: " + biomeName, exception);
        }
        return new Settlement(
            new SettlementId(readUuid(tag, ID)),
            tag.getStringOr(NAME, ""),
            new SettlementOwner(readUuid(tag, OWNER)),
            biome,
            new SettlementLocation(location.getStringOr(DIMENSION, ""),
                location.getIntOr(X, 0), location.getIntOr(Y, 0), location.getIntOr(Z, 0))
        );
    }

    static UUID readUuid(CompoundTag tag, String key) {
        String encoded = tag.getStringOr(key, "");
        if (!encoded.isEmpty()) {
            try {
                return UUID.fromString(encoded);
            } catch (IllegalArgumentException ignored) {
                // Older saves encoded UUIDs as four 32-bit integers.
            }
        }
        int[] legacy = tag.getIntArray(key).orElse(null);
        if (legacy != null && legacy.length == 4) {
            ByteBuffer bytes = ByteBuffer.allocate(16);
            for (int part : legacy) bytes.putInt(part);
            bytes.flip();
            return new UUID(bytes.getLong(), bytes.getLong());
        }
        throw new IllegalArgumentException("Missing or invalid UUID field: " + key);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
}
