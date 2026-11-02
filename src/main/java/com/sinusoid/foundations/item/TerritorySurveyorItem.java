package com.sinusoid.foundations.item;

import com.sinusoid.foundations.settlement.Settlement;
import com.sinusoid.foundations.settlement.SettlementSavedData;
import com.sinusoid.foundations.settlement.SettlementWorldDataAccess;
import com.sinusoid.foundations.settlement.SettlementWorldTerritoryService;
import com.sinusoid.foundations.settlement.TerritoryClaimResult;
import com.sinusoid.foundations.settlement.TerritoryGrid;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Starter tool for claiming territory one grid cell at a time.
 *
 * <p>Right-clicking a block attempts to claim the abstract grid cell containing
 * that block for the player's settlement. The item is reusable and all
 * authoritative checks run on the logical server.</p>
 */
public final class TerritorySurveyorItem extends Item {

    private static final TerritoryGrid TERRITORY_GRID = new TerritoryGrid();
    private static final SettlementWorldTerritoryService TERRITORY_SERVICE =
        new SettlementWorldTerritoryService();

    public TerritorySurveyorItem() {
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
            player.displayClientMessage(
                Component.literal("You must found a settlement before claiming territory."),
                true
            );
            return InteractionResult.CONSUME;
        }

        BlockPos clickedPos = context.getClickedPos();
        String dimensionId = serverLevel.dimension().location().toString();
        var candidate = TERRITORY_GRID.cellAt(
            dimensionId,
            clickedPos.getX(),
            clickedPos.getZ()
        );

        Optional<TerritoryClaimResult> attempt = TERRITORY_SERVICE.claimAdjacentCell(
            worldData,
            settlement.get().id(),
            candidate
        );

        if (attempt.isEmpty()) {
            player.displayClientMessage(
                Component.literal("Your settlement has no registered territory."),
                true
            );
            return InteractionResult.CONSUME;
        }

        TerritoryClaimResult result = attempt.get();
        if (result.isClaimed()) {
            player.displayClientMessage(
                Component.literal("Territory expanded: " + result.territory().cellCount() + " cells."),
                true
            );
            return InteractionResult.CONSUME;
        }

        player.displayClientMessage(
            Component.literal(messageFor(result)),
            true
        );
        return InteractionResult.CONSUME;
    }

    private String messageFor(TerritoryClaimResult result) {
        return switch (result.status()) {
            case ALREADY_CLAIMED -> "This cell already belongs to your settlement.";
            case OWNED_BY_OTHER_SETTLEMENT -> "This cell belongs to another settlement.";
            case NOT_ADJACENT -> "You can only claim a cell next to your territory.";
            case WRONG_DIMENSION -> "You cannot expand your settlement into another dimension.";
            case CLAIMED -> "Territory claimed.";
        };
    }
}
