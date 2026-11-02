package com.sinusoid.foundations.block;

import com.sinusoid.foundations.settlement.SettlementFoundingRequest;
import com.sinusoid.foundations.settlement.SettlementLocation;
import com.sinusoid.foundations.settlement.SettlementOwner;
import com.sinusoid.foundations.settlement.SettlementWorldDataAccess;
import com.sinusoid.foundations.settlement.SettlementWorldFoundingService;
import com.sinusoid.foundations.settlement.TerritoryGrid;
import com.sinusoid.foundations.settlement.VanillaSettlementBiomeResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Physical founding point for a settlement.
 *
 * <p>The server performs all authoritative founding work. The client only
 * reports the interaction and displays the result supplied by the server-side
 * logic.</p>
 */
public final class FoundationMarkerBlock extends Block {

    private static final SettlementWorldFoundingService FOUNDING_SERVICE =
        new SettlementWorldFoundingService(new VanillaSettlementBiomeResolver());
    private static final TerritoryGrid TERRITORY_GRID = new TerritoryGrid();

    public FoundationMarkerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hitResult
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        Identifier biomeId = serverLevel.registryAccess()
            .lookupOrThrow(Registries.BIOME)
            .getKey(serverLevel.getBiome(pos).value());

        String settlementName = player.getName().getString() + "'s Settlement";

        SettlementFoundingRequest request = new SettlementFoundingRequest(
            settlementName,
            new SettlementOwner(player.getUUID()),
            new SettlementLocation(
                serverLevel.dimension().identifier().toString(),
                pos.getX(),
                pos.getY(),
                pos.getZ()
            ),
            biomeId == null ? "foundations:undetermined" : biomeId.toString()
        );

        String dimensionId = serverLevel.dimension().identifier().toString();
        var centerCell = TERRITORY_GRID.cellAt(dimensionId, pos.getX(), pos.getZ());
        var result = FOUNDING_SERVICE.found(
            SettlementWorldDataAccess.get(serverLevel),
            request,
            centerCell
        );

        if (result.isSuccess()) {
            player.sendOverlayMessage(
                Component.literal("Settlement founded: " + result.success().settlement().name())
            );
        } else {
            player.sendOverlayMessage(
                Component.literal("Cannot found settlement: " + result.failure())
            );
        }

        return InteractionResult.CONSUME;
    }
}
