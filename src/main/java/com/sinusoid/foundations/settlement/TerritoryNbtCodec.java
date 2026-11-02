package com.sinusoid.foundations.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

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
        tag.putUUID(SETTLEMENT_ID, territory.settlementId().value());
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
            || !tag.contains(DIMENSION, Tag.TAG_STRING)
            || !tag.contains(CELLS, Tag.TAG_LIST)) {
            throw new IllegalArgumentException("Invalid saved settlement territory.");
        }

        ListTag encodedCells = tag.getList(CELLS, Tag.TAG_COMPOUND);
        List<TerritoryCell> cells = new ArrayList<>(encodedCells.size());
        for (Tag entry : encodedCells) {
            if (!(entry instanceof CompoundTag cellTag)
                || !cellTag.contains("x", Tag.TAG_INT)
                || !cellTag.contains("z", Tag.TAG_INT)) {
                throw new IllegalArgumentException("Invalid cell in saved settlement territory.");
            }
            cells.add(new TerritoryCell(tag.getString(DIMENSION),
                cellTag.getInt("x"), cellTag.getInt("z")));
        }

        return new SettlementTerritory(
            new SettlementId(tag.getUUID(SETTLEMENT_ID)),
            tag.getString(DIMENSION),
            cells
        );
    }
}
