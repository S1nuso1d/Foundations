package com.sinusoid.foundations;

import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

/**
 * Main entry point for the Foundations mod.
 *
 * <p>This class intentionally remains small. Gameplay systems must live in
 * dedicated modules so the bootstrap layer never becomes a global dumping
 * ground.</p>
 */
@Mod(Foundations.MOD_ID)
public final class Foundations {

    public static final String MOD_ID = "foundations";

    private static final Logger LOGGER = LogUtils.getLogger();

    public Foundations() {
        LOGGER.info("Foundations is loading.");
    }
}
