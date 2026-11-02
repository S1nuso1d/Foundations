package com.sinusoid.foundations;

import com.mojang.logging.LogUtils;
import com.sinusoid.foundations.registry.FoundationsBlocks;
import com.sinusoid.foundations.registry.FoundationsItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

/**
 * Bootstrap entry point for the Foundations mod.
 *
 * <p>Gameplay systems deliberately do not live here. This class owns mod
 * bootstrap, registry wiring and common lifecycle setup.</p>
 */
@Mod(Foundations.MOD_ID)
public final class Foundations {

    public static final String MOD_ID = "foundations";

    private static final Logger LOGGER = LogUtils.getLogger();

    public Foundations(IEventBus modEventBus) {
        FoundationsBlocks.BLOCKS.register(modEventBus);
        FoundationsItems.ITEMS.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Foundations common setup complete.");
    }
}
