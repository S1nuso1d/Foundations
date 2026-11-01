package com.sinusoid.foundations.block;

import net.minecraft.world.level.block.Block;

/**
 * Physical marker used as the future founding point of a settlement.
 *
 * <p>The first iteration intentionally contains no founding logic. Its purpose
 * is to establish a real, testable in-game object before interaction rules are
 * connected in the next iteration.</p>
 */
public final class FoundationMarkerBlock extends Block {

    public FoundationMarkerBlock() {
        super(Properties.of().strength(3.5F));
    }
}
