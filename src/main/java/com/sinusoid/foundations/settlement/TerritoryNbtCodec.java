package com.sinusoid.foundations.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

/** NBT codec for a single settlement territory. */
public final class TerritoryNbtCodec {

    private static final String SETTLEMENT_ID = "settlement_id";
    private static final String DIMENSION = "dimension";
    private static final String CELLS = "cells";

    private TerritoryNbtCodec() {
    }

    public static CompoundTag encode(SettlementTerritory territory) {
        Objects.requireNonNull(territory, "territory");
        CompoundTag tag = new CompoundTag();
        tag.putString(SETTLEMENT_ID, territory.settlementId().value().toString());
        tag.putString(DIMENSION, territory.dimension());

        ListTag cells = new ListTag();
        for (TerritoryCell cell : territory.cells()) {
            CompoundTag cellTag = new CompoundTag();
            cellTag.putInt("x", cell.x());
            cellTag.putInt("z", cell.z());
            cells.add(cellTag);
        }
        tag.put(CELLS, cells);
        return tag;
    }

    public static SettlementTerritory decode(CompoundTag tag) {
        Objects.requireNonNull(tag, "tag");
        if (!tag.hasUUID(SETTLEMENT_ID)
            || !tag.contains(DIMENSION)
            || !tag.contains(CELLS)) {
            throw new IllegalArgumentException("Invalid saved settlement territory.");
        }

        ListTag encodedCells = tag.getListOrEmpty(CELLS);
        List<TerritoryCell> cells = new ArrayList<>(encodedCells.size());
        for (int index = 0; index < encodedCells.size(); index++) {
            CompoundTag cellTag = encodedCells.getCompoundOrEmpty(index);
            if (!cellTag.contains("x") || !cellTag.contains("z")) {
                throw new IllegalArgumentException("Invalid cell in saved settlement territory.");
            }
            cells.add(new TerritoryCell(tag.getStringOr(DIMENSION, ""),
                cellTag.getIntOr("x", 0), cellTag.getIntOr("z", 0)));
        }

        return new SettlementTerritory(
            new SettlementId(SettlementNbtCodec.readUuid(tag, SETTLEMENT_ID)),
            tag.getString(DIMENSION),
            cells
        );
    }
}
