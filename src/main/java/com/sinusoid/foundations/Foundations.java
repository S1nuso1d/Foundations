package com.sinusoid.foundations;

import com.mojang.logging.LogUtils;
import com.sinusoid.foundations.registry.FoundationsBlocks;
import com.sinusoid.foundations.registry.FoundationsItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

    public Foundations(FMLJavaModLoadingContext context) {
        FoundationsBlocks.BLOCKS.register(context.getModBusGroup());
        FoundationsItems.ITEMS.register(context.getModBusGroup());

        FMLCommonSetupEvent.getBus(context.getModBusGroup()).addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Foundations common setup complete.");
    }
}
