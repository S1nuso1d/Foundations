package com.sinusoid.foundations.registry;

import com.sinusoid.foundations.Foundations;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry of all Foundations items.
 */
public final class FoundationsItems {

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, Foundations.MOD_ID);

    public static final RegistryObject<Item> FOUNDATION_MARKER =
        ITEMS.register(
            "foundation_marker",
            () -> new BlockItem(
                FoundationsBlocks.FOUNDATION_MARKER.get(),
                new Item.Properties()
            )
        );

    private FoundationsItems() {
    }
}
