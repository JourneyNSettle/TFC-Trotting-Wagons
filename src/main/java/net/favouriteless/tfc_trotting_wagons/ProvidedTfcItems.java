package net.favouriteless.tfc_trotting_wagons;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The same ids as TFC More Items, registered only when that mod is absent.
 * FirmaLife's stainless parts are included only when FirmaLife itself is present.
 * JEI lists items from creative tabs, so these get their own tab.
 */
public final class ProvidedTfcItems {

    private static final String[] METALS = {"copper", "bronze", "wrought_iron", "steel", "black_steel"};
    /** Nails and screws only. Their handfuls are crafted into the plain bronze handful. */
    private static final String[] BRONZE_VARIANTS = {"bismuth_bronze", "black_bronze"};
    private static final List<DeferredHolder<Item, Item>> PROVIDED = new ArrayList<>();

    private ProvidedTfcItems() {}

    public static void register(IEventBus bus) {
        if(present("tfc_items"))
            return;
        DeferredRegister<Item> items = DeferredRegister.create(Registries.ITEM, "tfc_items");
        DeferredHolder<Item, Item> icon = item(items, "long_stick");
        item(items, "short_wooden_handle");
        item(items, "leather_strip");
        item(items, "leather_strap");
        item(items, "wrought_iron_wire");
        registerMetals(items, METALS);
        registerParts(items, BRONZE_VARIANTS);
        if(present("firmalife"))
            registerMetals(items, new String[]{"stainless_steel"});
        DeferredRegister<CreativeModeTab> tabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TfcTrottingWagons.MOD_ID);
        tabs.register("tfc_items", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.tfc_trotting_wagons.tfc_items"))
                .icon(() -> new ItemStack(icon.get()))
                .displayItems((params, output) -> {
                    for(DeferredHolder<Item, Item> provided : PROVIDED)
                        output.accept(provided.get());
                })
                .build());
        items.register(bus);
        tabs.register(bus);
    }

    private static DeferredHolder<Item, Item> item(DeferredRegister<Item> items, String name) {
        DeferredHolder<Item, Item> holder = items.register(name, () -> new Item(new Item.Properties()));
        PROVIDED.add(holder);
        return holder;
    }

    private static void registerMetals(DeferredRegister<Item> items, String[] metals) {
        registerParts(items, metals);
        for(String metal : metals) {
            item(items, "handful_" + metal + "_nails");
            item(items, "handful_" + metal + "_screws");
        }
    }

    private static void registerParts(DeferredRegister<Item> items, String[] metals) {
        for(String metal : metals) {
            item(items, metal + "_stamen");
            item(items, metal + "_nail");
            item(items, metal + "_screw");
        }
    }

    /** True when the jar is installed, even if its constructor has not run yet. */
    private static boolean present(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }

}
