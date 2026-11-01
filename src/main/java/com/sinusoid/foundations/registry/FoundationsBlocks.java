package com.sinusoid.foundations.registry;

import com.sinusoid.foundations.Foundations;
import com.sinusoid.foundations.block.FoundationMarkerBlock;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry of all Foundations blocks.
 */
public final class FoundationsBlocks {

    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, Foundations.MOD_ID);

    public static final RegistryObject<Block> FOUNDATION_MARKER =
        BLOCKS.register("foundation_marker", FoundationMarkerBlock::new);

    private FoundationsBlocks() {
    }
}
