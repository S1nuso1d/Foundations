package com.sinusoid.foundations.registry;

import com.sinusoid.foundations.Foundations;
import com.sinusoid.foundations.block.FoundationMarkerBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry of all Foundations blocks.
 */
public final class FoundationsBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(Foundations.MOD_ID);

    public static final DeferredBlock<Block> FOUNDATION_MARKER =
        BLOCKS.register("foundation_marker", FoundationMarkerBlock::new);

    private FoundationsBlocks() {
    }
}
