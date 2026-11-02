package com.sinusoid.foundations.item;

import com.sinusoid.foundations.settlement.Settlement;
import com.sinusoid.foundations.settlement.SettlementId;
import com.sinusoid.foundations.settlement.SettlementSavedData;
import com.sinusoid.foundations.settlement.SettlementTerritory;
import com.sinusoid.foundations.settlement.SettlementWorldDataAccess;
import com.sinusoid.foundations.settlement.SettlementWorldTerritoryService;
import com.sinusoid.foundations.settlement.TerritoryBoundaryEdge;
import com.sinusoid.foundations.settlement.TerritoryClaimResult;
import com.sinusoid.foundations.settlement.TerritoryCell;
import com.sinusoid.foundations.settlement.TerritoryGrid;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Reusable tool for claiming settlement territory and briefly revealing its
 * outer boundary with vanilla particles.
 */
public final class TerritorySurveyorItem extends Item {

    private static final TerritoryGrid TERRITORY_GRID = new TerritoryGrid();
    private static final SettlementWorldTerritoryService TERRITORY_SERVICE =
        new SettlementWorldTerritoryService();
    private static final int PARTICLE_VIEW_DISTANCE_BLOCKS = 48;
    private static final int PARTICLE_SPACING_BLOCKS = 2;

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

        // Sneak + right-click inspects ownership without changing the world.
        if (player.isShiftKeyDown()) {
            inspectCell(serverLevel, player, worldData, settlement.get(), candidate);
            return InteractionResult.CONSUME;
        }

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
            showBoundary(serverLevel, player, result.territory());
            return InteractionResult.CONSUME;
        }

        String resultMessage = result.status() == TerritoryClaimResult.Status.OWNED_BY_OTHER_SETTLEMENT
            ? ownershipMessage(worldData, candidate)
            : messageFor(result);
        player.displayClientMessage(Component.literal(resultMessage), true);
        if (result.status() == TerritoryClaimResult.Status.ALREADY_CLAIMED) {
            worldData.territories()
                .find(settlement.get().id())
                .ifPresent(territory -> showBoundary(serverLevel, player, territory));
        }
        return InteractionResult.CONSUME;
    }

    /**
     * Reports cell ownership without claiming it. The player's own territory
     * outline is shown as a visual reference when available.
     */
    private void inspectCell(
        ServerLevel level,
        Player player,
        SettlementSavedData worldData,
        Settlement settlement,
        TerritoryCell cell
    ) {
        Optional<SettlementId> owner =
            TERRITORY_SERVICE.findOwner(worldData, cell);

        String ownership = owner.isEmpty()
            ? "This territory cell is unclaimed."
            : owner.get().equals(settlement.id())
                ? "This cell belongs to your settlement."
                : ownershipMessage(worldData, cell);
        String coordinates = "Cell: " + cell.x() + ", " + cell.z()
            + " (" + cell.dimension() + ")";
        player.displayClientMessage(
            Component.literal(ownership + " " + coordinates),
            true
        );

        worldData.territories()
            .find(settlement.id())
            .ifPresent(territory -> showBoundary(level, player, territory));
    }

    private String ownershipMessage(SettlementSavedData worldData, TerritoryCell cell) {
        Optional<SettlementId> owner = TERRITORY_SERVICE.findOwner(worldData, cell);
        if (owner.isEmpty()) {
            return "This territory cell is unclaimed.";
        }

        String settlementName = worldData.registry()
            .find(owner.get())
            .map(Settlement::name)
            .orElse("another settlement");
        return "This cell belongs to " + settlementName + ".";
    }

    /**
     * Emits a short-lived vanilla particle outline around exposed cell sides.
     * Only nearby edge samples are sent, keeping large settlements inexpensive.
     */
    private void showBoundary(
        ServerLevel level,
        Player player,
        SettlementTerritory territory
    ) {
        double centerX = player.getX();
        double centerZ = player.getZ();
        double maxDistanceSquared =
            (double) PARTICLE_VIEW_DISTANCE_BLOCKS * PARTICLE_VIEW_DISTANCE_BLOCKS;
        int cellSize = TERRITORY_GRID.cellSizeBlocks();

        for (TerritoryBoundaryEdge edge : territory.boundaryEdges()) {
            double cellMinX = (double) edge.cell().x() * cellSize;
            double cellMinZ = (double) edge.cell().z() * cellSize;

            for (int offset = 0; offset <= cellSize; offset += PARTICLE_SPACING_BLOCKS) {
                double x;
                double z;
                switch (edge.direction()) {
                    case NORTH -> {
                        x = cellMinX + offset;
                        z = cellMinZ;
                    }
                    case EAST -> {
                        x = cellMinX + cellSize;
                        z = cellMinZ + offset;
                    }
                    case SOUTH -> {
                        x = cellMinX + offset;
                        z = cellMinZ + cellSize;
                    }
                    case WEST -> {
                        x = cellMinX;
                        z = cellMinZ + offset;
                    }
                    default -> throw new IllegalStateException("Unknown territory edge direction.");
                }

                double dx = x - centerX;
                double dz = z - centerZ;
                if (dx * dx + dz * dz <= maxDistanceSquared) {
                    int sampleX = (int) Math.floor(x);
                    int sampleZ = (int) Math.floor(z);

                    // Sample just inside the claimed cell so the outline follows
                    // its terrain rather than the neighboring unclaimed column.
                    switch (edge.direction()) {
                        case EAST -> sampleX--;
                        case SOUTH -> sampleZ--;
                        default -> {
                        }
                    }

                    double y = level.getHeight(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        sampleX,
                        sampleZ
                    ).getY() + 0.12;

                    level.sendParticles(
                        ParticleTypes.END_ROD,
                        x,
                        y,
                        z,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                    );
                }
            }
        }
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
