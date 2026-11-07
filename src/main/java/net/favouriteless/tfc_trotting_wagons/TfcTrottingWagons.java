package net.favouriteless.tfc_trotting_wagons;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.config.TFCConfig;
import net.favouriteless.trotting_wagons.common.entities.WagonBuild;
import net.favouriteless.trotting_wagons.common.entities.base.AbstractWagon;
import net.favouriteless.trotting_wagons.common.init.TWCreativeTabs;
import net.favouriteless.trotting_wagons.common.init.TWItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(TfcTrottingWagons.MOD_ID)
public class TfcTrottingWagons {

    public static final String MOD_ID = "tfc_trotting_wagons";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final TagKey<Item> GEARBOXES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "gearboxes"));

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WheelRepairRecipe>> WHEEL_REPAIR =
            RECIPE_SERIALIZERS.register("wheel_repair", () -> WheelRepairRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CountedShapedRecipe>> COUNTED_SHAPED =
            RECIPE_SERIALIZERS.register("counted_shaped", () -> CountedShapedRecipe.SERIALIZER);

    public TfcTrottingWagons(IEventBus bus, ModContainer container) {
        RECIPE_SERIALIZERS.register(bus);
        AbstractWagon.addDraftCheck(TfcTrottingWagons::hasOutlinedHeart);
        AbstractWagon.setWheelLoss(TfcTrottingWagons::keepBrokenWheel);
        WagonBuild.addChassisNeed("gearbox", stack -> stack.is(GEARBOXES), 1);
        bus.addListener(TfcTrottingWagons::hideWagonLongStick);
        bus.addListener(TfcTrottingWagons::addCreativeWagons);
        LOGGER.debug("TerraFirmaCraft livestock is added to trotting_wagons:draft_animals");
    }

    /** tfc_items:long_stick replaces this item. The crafting recipe is disabled in data. */
    private static void hideWagonLongStick(BuildCreativeModeTabContentsEvent event) {
        if(!event.getTabKey().equals(TWCreativeTabs.MAIN.getKey()))
            return;
        event.remove(new ItemStack(TWItems.LONG_STICK.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    /** Temporary. These are the same empty wagons the base builds: no wheels, harness, or fitted parts. */
    private static void addCreativeWagons(BuildCreativeModeTabContentsEvent event) {
        if(!event.getTabKey().equals(TWCreativeTabs.MAIN.getKey()))
            return;
        event.accept(TWItems.CONESTOGA_WAGON.get());
        event.accept(TWItems.ROYAL_WAGON.get());
        event.accept(TWItems.ARMORED_WAGON.get());
        event.accept(TWItems.ANIMAL_WAGON.get());
        event.accept(TWItems.LOG_WAGON.get());
        event.accept(TWItems.BARREL_WAGON.get());
        event.accept(TWItems.PASSENGER_WAGON.get());
        event.accept(TWItems.WARE_WAGON.get());
        event.accept(TWItems.KIBITKA_WAGON.get());
        event.accept(TWItems.BIG_CART_WAGON.get());
    }

    /** A spent wheel stays in the slot at zero durability. */
    private static ItemStack keepBrokenWheel(ItemStack wheel) {
        ItemStack kept = wheel.copy();
        kept.setCount(1);
        int max = kept.getMaxDamage();
        if(max <= 0)
            return ItemStack.EMPTY;
        kept.set(DataComponents.DAMAGE, max);
        return kept;
    }

    /** White or red outlined familiarity heart. Below the decay limit the heart has not settled. */
    private static boolean hasOutlinedHeart(Entity entity) {
        if(!(entity instanceof TFCAnimalProperties animal))
            return true;
        return animal.getFamiliarity() >= TFCConfig.SERVER.familiarityDecayLimit.get();
    }

}
