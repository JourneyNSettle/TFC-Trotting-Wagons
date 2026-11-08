package net.favouriteless.tfc_trotting_wagons;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.config.TFCConfig;
import net.favouriteless.trotting_wagons.TrottingWagons;
import net.favouriteless.trotting_wagons.common.entities.WagonBuild;
import net.favouriteless.trotting_wagons.common.entities.wagons.BarrelWagon;
import net.favouriteless.trotting_wagons.common.entities.base.AbstractWagon;
import net.favouriteless.trotting_wagons.common.entities.wagons.LogCargo;
import net.favouriteless.trotting_wagons.common.init.TWCreativeTabs;
import net.favouriteless.trotting_wagons.common.init.TWItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
    private static final TagKey<Item> LUMBER = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("tfc", "lumber"));
    private static final TagKey<Item> STEEL_NAILS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "nails/steel"));
    private static final TagKey<Item> WROUGHT_IRON_NAILS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "nails/wrought_iron"));
    private static final ResourceLocation BRASS_MECHANISMS = ResourceLocation.fromNamespaceAndPath("tfc", "brass_mechanisms");
    private static final TagKey<Item> REPAIR_STRING = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "repair_string"));
    private static final TagKey<Item> REPAIR_CLOTH = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "repair_cloth"));
    private static final TagKey<Item> REPAIR_NAILS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "repair_nails"));
    private static final ResourceLocation GLUE = ResourceLocation.fromNamespaceAndPath("tfc", "glue");
    private static final ResourceLocation LEATHER_STRIP = ResourceLocation.fromNamespaceAndPath("tfc_items", "leather_strip");
    private static final ResourceLocation OAK_LUMBER = ResourceLocation.fromNamespaceAndPath("tfc", "wood/lumber/oak");
    private static final ResourceLocation WOOL_CLOTH = ResourceLocation.fromNamespaceAndPath("tfc", "wool_cloth");
    private static final ResourceLocation IRON_NAIL_HANDFUL = ResourceLocation.fromNamespaceAndPath("tfc_items", "handful_wrought_iron_nails");
    private static final TagKey<Item> BUILD_NAILS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "build_nails"));
    private static final TagKey<Item> BUILD_SHEETS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "build_sheets"));
    private static final TagKey<Item> AXLES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("trotting_wagons", "axles"));
    private static final ResourceLocation ROPE = ResourceLocation.fromNamespaceAndPath("tfc", "rope");
    private static final ResourceLocation STRAW = ResourceLocation.fromNamespaceAndPath("tfc", "straw");
    private static final ResourceLocation WIRE = ResourceLocation.fromNamespaceAndPath("tfc_items", "wrought_iron_wire");
    private static final ResourceLocation SHEET = ResourceLocation.fromNamespaceAndPath("tfc", "metal/sheet/wrought_iron");
    private static final ResourceLocation DOOR = ResourceLocation.fromNamespaceAndPath("tfc", "wood/door/oak");
    private static final ResourceLocation AXLE = ResourceLocation.fromNamespaceAndPath("tfc", "wood/axle/oak");
    private static final ResourceLocation NAIL = ResourceLocation.fromNamespaceAndPath("tfc_items", "wrought_iron_nail");

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WheelRepairRecipe>> WHEEL_REPAIR =
            RECIPE_SERIALIZERS.register("wheel_repair", () -> WheelRepairRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CountedShapedRecipe>> COUNTED_SHAPED =
            RECIPE_SERIALIZERS.register("counted_shaped", () -> CountedShapedRecipe.SERIALIZER);

    public TfcTrottingWagons(IEventBus bus, ModContainer container) {
        RECIPE_SERIALIZERS.register(bus);
        ProvidedTfcItems.register(bus);
        BarrelWagon.setContentsOpen(false);
        LogCargo.setLimit(LogCargo.CAPACITY / 4);
        AbstractWagon.addDraftCheck(TfcTrottingWagons::hasOutlinedHeart);
        AbstractWagon.setWheelLoss(TfcTrottingWagons::keepBrokenWheel);
        WagonBuild.setChassisBill(
                new WagonBuild.Need("lumber", stack -> stack.is(LUMBER), 40),
                new WagonBuild.Need("nails", stack -> stack.is(STEEL_NAILS) || stack.is(WROUGHT_IRON_NAILS), 40),
                new WagonBuild.Need("brass_mechanisms", TfcTrottingWagons::isBrassMechanisms, 4),
                new WagonBuild.Need("gearbox", stack -> stack.is(GEARBOXES), 2));
        registerWagonBills();
        AbstractWagon.setRepairKind("planks", stack -> stack.is(LUMBER) && !oldRepairItem(stack), () -> item(OAK_LUMBER),
                () -> Component.translatable("gui.trotting_wagons.repair.lumber"));
        AbstractWagon.setRepairKind("iron", stack -> stack.is(REPAIR_NAILS) && !oldRepairItem(stack), () -> item(IRON_NAIL_HANDFUL),
                () -> Component.translatable("gui.trotting_wagons.repair.nails"));
        AbstractWagon.setRepairKind("wool", stack -> stack.is(REPAIR_CLOTH) && !oldRepairItem(stack), () -> item(WOOL_CLOTH));
        AbstractWagon.setRepairKind("leather", stack -> isItem(stack, LEATHER_STRIP) && !oldRepairItem(stack), () -> item(LEATHER_STRIP));
        AbstractWagon.setRepairKind("string", stack -> stack.is(REPAIR_STRING) && !oldRepairItem(stack), () -> new ItemStack(Items.STRING),
                () -> Component.translatable("gui.trotting_wagons.repair.string"));
        AbstractWagon.setRepairKind("slime", stack -> isItem(stack, GLUE) && !oldRepairItem(stack), () -> item(GLUE));
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

    private static boolean isBrassMechanisms(ItemStack stack) {
        return isItem(stack, BRASS_MECHANISMS);
    }

    private static void registerWagonBills() {
        WagonBuild.setChoiceBill("animal_wagon",
                sticks(6), lumber(48), nails(20), cloth(1), straw(8));
        WagonBuild.setChoiceBill("barrel_wagon",
                sticks(4), lumber(32), planks(8), nails(20), rope(2), cloth(1));
        WagonBuild.setChoiceBill("log_wagon",
                sticks(16), lumber(32), planks(12), nails(40), cloth(1));
        WagonBuild.setChoiceBill("big_cart",
                lumber(12), planks(12), rope(2), string(8), cloth(1), nails(16));
        WagonBuild.setChoiceBill("ware_wagon",
                planks(32), lumber(16), nails(40), wire(2), rope(2), string(8), cloth(1));
        WagonBuild.setChoiceBill("passenger_wagon",
                planks(40), lumber(32), nails(48), string(16), cloth(1));
        WagonBuild.setChoiceBill("conestoga_wagon",
                sticks(18), lumber(48), nails(20), cloth(16), rope(8), chest(2));
        WagonBuild.setChoiceBill("royal_wagon",
                lumber(48), nails(24), string(16), rope(2), cloth(16), chest(1));
        WagonBuild.setChoiceBill("armored_wagon",
                sticks(12), lumber(64), planks(32), rope(8), sheet(10));
        WagonBuild.setChoiceBill("kibitka_wagon",
                sticks(12), lumber(32), planks(24), door(1), chest(1), cloth(4), bed(1), axle(2), brick(40));
        WagonBuild.setSalvageFallback("lumber", () -> BuiltInRegistries.ITEM.get(OAK_LUMBER));
        WagonBuild.setSalvageFallback("nails", () -> BuiltInRegistries.ITEM.get(NAIL));
        WagonBuild.setSalvageFallback("cloth", () -> BuiltInRegistries.ITEM.get(WOOL_CLOTH));
        WagonBuild.setSalvageFallback("straw", () -> BuiltInRegistries.ITEM.get(STRAW));
        WagonBuild.setSalvageFallback("rope", () -> BuiltInRegistries.ITEM.get(ROPE));
        WagonBuild.setSalvageFallback("wire", () -> BuiltInRegistries.ITEM.get(WIRE));
        WagonBuild.setSalvageFallback("sheet", () -> BuiltInRegistries.ITEM.get(SHEET));
        WagonBuild.setSalvageFallback("door", () -> BuiltInRegistries.ITEM.get(DOOR));
        WagonBuild.setSalvageFallback("axle", () -> BuiltInRegistries.ITEM.get(AXLE));
    }

    private static WagonBuild.Need sticks(int count) {
        return new WagonBuild.Need("sticks", stack -> stack.is(TrottingWagons.LONG_STICKS), count);
    }

    private static WagonBuild.Need lumber(int count) {
        return new WagonBuild.Need("lumber", stack -> stack.is(LUMBER), count);
    }

    private static WagonBuild.Need planks(int count) {
        return new WagonBuild.Need("planks", stack -> stack.is(ItemTags.PLANKS), count);
    }

    private static WagonBuild.Need nails(int count) {
        return new WagonBuild.Need("nails", stack -> stack.is(BUILD_NAILS), count);
    }

    private static WagonBuild.Need cloth(int count) {
        return new WagonBuild.Need("cloth", stack -> stack.is(REPAIR_CLOTH), count);
    }

    private static WagonBuild.Need straw(int count) {
        return new WagonBuild.Need("straw", stack -> isItem(stack, STRAW), count);
    }

    private static WagonBuild.Need rope(int count) {
        return new WagonBuild.Need("rope", stack -> isItem(stack, ROPE), count);
    }

    private static WagonBuild.Need string(int count) {
        return new WagonBuild.Need("string", stack -> stack.is(REPAIR_STRING), count);
    }

    private static WagonBuild.Need wire(int count) {
        return new WagonBuild.Need("wire", stack -> isItem(stack, WIRE), count);
    }

    private static WagonBuild.Need sheet(int count) {
        return new WagonBuild.Need("sheet", stack -> stack.is(BUILD_SHEETS), count);
    }

    private static WagonBuild.Need door(int count) {
        return new WagonBuild.Need("door", stack -> stack.is(ItemTags.WOODEN_DOORS), count);
    }

    private static WagonBuild.Need chest(int count) {
        return new WagonBuild.Need("chest", stack -> stack.is(TrottingWagons.CHESTS), count);
    }

    private static WagonBuild.Need bed(int count) {
        return new WagonBuild.Need("bed", stack -> stack.is(TrottingWagons.BEDS), count);
    }

    private static WagonBuild.Need axle(int count) {
        return new WagonBuild.Need("axle", stack -> stack.is(AXLES), count);
    }

    private static WagonBuild.Need brick(int count) {
        return new WagonBuild.Need("brick", stack -> stack.is(TrottingWagons.BRICKS), count);
    }

    /** Vanilla repair items. String stays allowed. */
    private static boolean oldRepairItem(ItemStack stack) {
        return stack.is(ItemTags.PLANKS) || stack.is(ItemTags.WOOL) || stack.is(Items.LEATHER)
                || stack.is(Items.SLIME_BALL) || stack.is(TrottingWagons.IRON_INGOTS);
    }

    private static boolean isItem(ItemStack stack, ResourceLocation id) {
        return id.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    private static ItemStack item(ResourceLocation id) {
        return new ItemStack(BuiltInRegistries.ITEM.get(id));
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
