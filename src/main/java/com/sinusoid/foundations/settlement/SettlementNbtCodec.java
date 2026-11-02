package com.sinusoid.foundations.settlement;

import java.util.Objects;
import net.minecraft.nbt.CompoundTag;

/**
 * Serializes the stable settlement domain model to Minecraft NBT.
 *
 * <p>This codec deliberately contains no world-storage logic. It defines the
 * durable representation first; a Forge/Minecraft persistence adapter can then
 * store that representation without leaking NBT into the domain model.</p>
 */
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

    private SettlementNbtCodec() {
    }

    public static CompoundTag encode(Settlement settlement) {
        Objects.requireNonNull(settlement, "settlement");

        CompoundTag tag = new CompoundTag();
        tag.putUUID(ID, settlement.id().value());
        tag.putString(NAME, settlement.name());
        tag.putUUID(OWNER, settlement.owner().playerId());
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

        require(tag.hasUUID(ID), "Settlement is missing a valid ID.");
        require(tag.hasUUID(OWNER), "Settlement is missing a valid owner.");
        require(tag.contains(NAME), "Settlement is missing a name.");
        require(tag.contains(BIOME), "Settlement is missing a biome.");
        require(tag.contains(LOCATION, CompoundTag.TAG_COMPOUND),
            "Settlement is missing a location.");

        CompoundTag location = tag.getCompound(LOCATION);

        require(location.contains(DIMENSION), "Settlement location is missing a dimension.");
        require(location.contains(X), "Settlement location is missing X.");
        require(location.contains(Y), "Settlement location is missing Y.");
        require(location.contains(Z), "Settlement location is missing Z.");

        final SettlementBiome biome;
        try {
            biome = SettlementBiome.valueOf(tag.getString(BIOME));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                "Unknown settlement biome: " + tag.getString(BIOME),
                exception
            );
        }

        return new Settlement(
            new SettlementId(tag.getUUID(ID)),
            tag.getString(NAME),
            new SettlementOwner(tag.getUUID(OWNER)),
            biome,
            new SettlementLocation(
                location.getString(DIMENSION),
                location.getInt(X),
                location.getInt(Y),
                location.getInt(Z)
            )
        );
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
