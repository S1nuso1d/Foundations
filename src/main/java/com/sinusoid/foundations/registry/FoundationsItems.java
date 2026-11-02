package com.sinusoid.foundations.registry;

import com.sinusoid.foundations.Foundations;
import com.sinusoid.foundations.item.SettlementLedgerItem;
import com.sinusoid.foundations.item.TerritorySurveyorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry of all Foundations items.
 */
public final class FoundationsItems {

    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(Foundations.MOD_ID);

    public static final DeferredItem<Item> FOUNDATION_MARKER =
        ITEMS.register("foundation_marker", () ->
            new BlockItem(FoundationsBlocks.FOUNDATION_MARKER.get(), new Item.Properties()));

    public static final DeferredItem<Item> TERRITORY_SURVEYOR =
        ITEMS.register("territory_surveyor", TerritorySurveyorItem::new);

    public static final DeferredItem<Item> SETTLEMENT_LEDGER =
        ITEMS.register("settlement_ledger", SettlementLedgerItem::new);

    private FoundationsItems() {
    }
}
