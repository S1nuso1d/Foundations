package com.sinusoid.foundations.block;

import com.sinusoid.foundations.settlement.SettlementFoundingRequest;
import com.sinusoid.foundations.settlement.SettlementLocation;
import com.sinusoid.foundations.settlement.SettlementOwner;
import com.sinusoid.foundations.settlement.SettlementWorldDataAccess;
import com.sinusoid.foundations.settlement.SettlementWorldFoundingService;
import com.sinusoid.foundations.settlement.VanillaSettlementBiomeResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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

    public FoundationMarkerBlock() {
        super(Properties.of().strength(3.5F));
    }

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        ResourceLocation biomeId = serverLevel.registryAccess()
            .registryOrThrow(Registries.BIOME)
            .getKey(serverLevel.getBiome(pos).value());

        String settlementName = player.getName().getString() + "'s Settlement";

        SettlementFoundingRequest request = new SettlementFoundingRequest(
            settlementName,
            new SettlementOwner(player.getUUID()),
            new SettlementLocation(
                serverLevel.dimension().location().toString(),
                pos.getX(),
                pos.getY(),
                pos.getZ()
            ),
            biomeId == null ? "foundations:undetermined" : biomeId.toString()
        );

        var result = FOUNDING_SERVICE.found(
            SettlementWorldDataAccess.get(serverLevel),
            request
        );

        if (result.isSuccess()) {
            player.displayClientMessage(
                Component.literal("Settlement founded: " + result.settlement().name()),
                true
            );
        } else {
            player.displayClientMessage(
                Component.literal("Cannot found settlement: " + result.failure()),
                true
            );
        }

        return InteractionResult.CONSUME;
    }
}
