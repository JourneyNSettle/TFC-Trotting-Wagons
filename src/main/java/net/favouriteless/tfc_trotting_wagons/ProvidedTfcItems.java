package net.favouriteless.tfc_trotting_wagons;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The same ids as TFC More Items, registered only when that mod is absent.
 * FirmaLife's stainless parts are included only when FirmaLife itself is present.
 */
public final class ProvidedTfcItems {

    private static final String[] METALS = {"bronze", "wrought_iron", "steel", "black_steel"};

    private ProvidedTfcItems() {}

    public static void register(IEventBus bus) {
        if(present("tfc_items"))
            return;
        DeferredRegister<Item> items = DeferredRegister.create(Registries.ITEM, "tfc_items");
        registerMetals(items, METALS);
        if(present("firmalife"))
            registerMetals(items, new String[]{"stainless_steel"});
        items.register("wrought_iron_wire", () -> new Item(new Item.Properties()));
        items.register("long_stick", () -> new Item(new Item.Properties()));
        items.register("short_wooden_handle", () -> new Item(new Item.Properties()));
        items.register("leather_strip", () -> new Item(new Item.Properties()));
        items.register("leather_strap", () -> new Item(new Item.Properties()));
        items.register(bus);
    }

    private static void registerMetals(DeferredRegister<Item> items, String[] metals) {
        for(String metal : metals) {
            items.register(metal + "_stamen", () -> new Item(new Item.Properties()));
            items.register(metal + "_nail", () -> new Item(new Item.Properties()));
            items.register(metal + "_screw", () -> new Item(new Item.Properties()));
            items.register("handful_" + metal + "_nails", () -> new Item(new Item.Properties()));
            items.register("handful_" + metal + "_screws", () -> new Item(new Item.Properties()));
        }
    }

    /** True when the jar is installed, even if its constructor has not run yet. */
    private static boolean present(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }

}
