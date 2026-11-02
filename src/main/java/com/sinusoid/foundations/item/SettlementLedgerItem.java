package com.sinusoid.foundations.item;

import com.sinusoid.foundations.settlement.Settlement;
import com.sinusoid.foundations.settlement.SettlementSavedData;
import com.sinusoid.foundations.settlement.SettlementTerritory;
import com.sinusoid.foundations.settlement.SettlementWorldDataAccess;
import com.sinusoid.foundations.settlement.TerritoryGrid;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * A simple in-world settlement ledger that reports the owner's settlement
 * overview without changing its data.
 */
public final class SettlementLedgerItem extends Item {

    public SettlementLedgerItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel) || player == null) {
            return InteractionResult.PASS;
        }

        SettlementSavedData worldData = SettlementWorldDataAccess.get(serverLevel);
        Optional<Settlement> settlement = worldData.registry().findByOwner(player.getUUID());
        if (settlement.isEmpty()) {
            player.sendSystemMessage(
                Component.literal("You have not founded a settlement yet."),
                false
            );
            return InteractionResult.CONSUME;
        }

        Settlement founded = settlement.get();
        Optional<SettlementTerritory> territory =
            worldData.territories().find(founded.id());

        TerritoryGrid grid = new TerritoryGrid();
        String territorySummary = territory
            .map(value -> {
                long cellArea = (long) grid.cellSizeBlocks() * grid.cellSizeBlocks();
                long area = value.cellCount() * cellArea;
                long perimeter = (long) value.boundaryEdgeCount() * grid.cellSizeBlocks();
                return value.cellCount() + " claimed cells"
                    + "\nArea: " + area + " blocks²"
                    + "\nOuter perimeter: " + perimeter + " blocks";
            })
            .orElse("No registered territory");

        String overview = String.join("\n",
            "=== " + founded.name() + " ===",
            "Environment: " + founded.biome().name(),
            "Founded at: " + founded.location().x() + ", "
                + founded.location().y() + ", " + founded.location().z(),
            "Dimension: " + founded.location().dimension(),
            "Territory: " + territorySummary
        );

        player.sendSystemMessage(Component.literal(overview), false);
        return InteractionResult.CONSUME;
    }
}
