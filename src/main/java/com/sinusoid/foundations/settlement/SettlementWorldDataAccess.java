package com.sinusoid.foundations.settlement;

import java.util.Objects;
import net.minecraft.server.level.ServerLevel;

/**
 * Entry point for retrieving Foundations settlement data from a server level.
 *
 * <p>This class is the only place outside persistence code that should know how
 * settlement SavedData is attached to a Minecraft level.</p>
 */
public final class SettlementWorldDataAccess {

    private static final String DATA_NAME = "foundations_settlements";

    private SettlementWorldDataAccess() {
    }

    public static SettlementSavedData get(ServerLevel level) {
        Objects.requireNonNull(level, "level");

        return level.getDataStorage().computeIfAbsent(
            new net.minecraft.world.level.saveddata.SavedData.Factory<>(
                SettlementSavedData::create,
                SettlementSavedData::load,
                null
            ),
            DATA_NAME
        );
    }
}
