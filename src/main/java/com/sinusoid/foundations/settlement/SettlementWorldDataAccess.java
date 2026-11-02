package com.sinusoid.foundations.settlement;

import java.util.Objects;
import net.minecraft.server.level.ServerLevel;

/** Entry point for retrieving Foundations data attached to a server level. */
public final class SettlementWorldDataAccess {
    private SettlementWorldDataAccess() {}

    public static SettlementSavedData get(ServerLevel level) {
        Objects.requireNonNull(level, "level");
        return level.getDataStorage().computeIfAbsent(SettlementSavedData.TYPE);
    }
}
