package net.favouriteless.tfc_trotting_wagons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

/** Shapeless repair. A wheel already at full durability does not match. The hammer stays and loses one use. */
public final class WheelRepairRecipe implements CraftingRecipe {

    public static final MapCodec<WheelRepairRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("wheel").forGetter(recipe -> recipe.wheel),
            Ingredient.CODEC_NONEMPTY.fieldOf("material").forGetter(recipe -> recipe.material),
            Ingredient.CODEC_NONEMPTY.fieldOf("fastener").forGetter(recipe -> recipe.fastener),
            Codec.INT.fieldOf("restore").forGetter(recipe -> recipe.restore)
    ).apply(instance, WheelRepairRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WheelRepairRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, recipe -> recipe.wheel,
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.material,
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.fastener,
            ByteBufCodecs.VAR_INT, recipe -> recipe.restore,
            WheelRepairRecipe::new
    );

    public static final RecipeSerializer<WheelRepairRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public MapCodec<WheelRepairRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WheelRepairRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    private static final TagKey<Item> HAMMER_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/hammer"));
    private static final Ingredient HAMMER = Ingredient.of(HAMMER_TAG);
    private static final TagKey<Item> STAINLESS_NAILS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "nails/handful/stainless_steel"));
    private static final TagKey<Item> STAINLESS_SCREWS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "screws/handful/stainless_steel"));
    private static final int BLACK_STEEL_NAIL_RESTORE = 100;
    private static final int BLACK_STEEL_SCREW_RESTORE = 300;

    private final ItemStack wheel;
    private final Ingredient material;
    private final Ingredient fastener;
    private final int restore;
    private final ItemStack example;

    public WheelRepairRecipe(ItemStack wheel, Ingredient material, Ingredient fastener, int restore) {
        this.wheel = wheel.copy();
        this.material = material;
        this.fastener = fastener;
        this.restore = restore;
        ItemStack shown = new ItemStack(wheel.getItem());
        int max = Math.max(shown.getMaxDamage(), 1);
        shown.setDamageValue(max - Math.min(max, restore));
        this.example = shown;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if(input.ingredientCount() != 4)
            return false;
        boolean wheelFound = false;
        boolean materialFound = false;
        boolean fastenerFound = false;
        boolean hammerFound = false;
        for(int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if(stack.isEmpty())
                continue;
            if(!wheelFound && stack.is(wheel.getItem()) && stack.getDamageValue() > 0) {
                wheelFound = true;
            } else if(!materialFound && material.test(stack)) {
                materialFound = true;
            } else if(!fastenerFound && acceptsFastener(stack)) {
                fastenerFound = true;
            } else if(!hammerFound && HAMMER.test(stack)) {
                hammerFound = true;
            } else {
                return false;
            }
        }
        return wheelFound && materialFound && fastenerFound && hammerFound;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for(int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if(stack.isEmpty() || !stack.is(wheel.getItem()))
                continue;
            ItemStack repaired = stack.copy();
            repaired.setCount(1);
            int max = repaired.getMaxDamage();
            int remaining = Math.max(0, max - repaired.getDamageValue());
            int filled = Math.min(max, remaining + restore);
            repaired.setDamageValue(max - filled);
            return repaired;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 4;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return example.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(Ingredient.of(wheel.copy()));
        list.add(material);
        list.add(shownFastener());
        list.add(HAMMER);
        return list;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for(int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if(!stack.isEmpty() && HAMMER.test(stack))
                remaining.set(i, usedHammer(stack));
        }
        return remaining;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TfcTrottingWagons.WHEEL_REPAIR.get();
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    /** Black-steel handfuls, plus stainless handfuls when FirmaLife is installed. */
    private boolean acceptsFastener(ItemStack stack) {
        if(fastener.test(stack))
            return true;
        if(!ModList.get().isLoaded("firmalife"))
            return false;
        if(restore == BLACK_STEEL_NAIL_RESTORE)
            return stack.is(STAINLESS_NAILS);
        return restore == BLACK_STEEL_SCREW_RESTORE && stack.is(STAINLESS_SCREWS);
    }

    private Ingredient shownFastener() {
        if(!ModList.get().isLoaded("firmalife"))
            return fastener;
        if(restore == BLACK_STEEL_NAIL_RESTORE)
            return CompoundIngredient.of(fastener, Ingredient.of(STAINLESS_NAILS));
        if(restore == BLACK_STEEL_SCREW_RESTORE)
            return CompoundIngredient.of(fastener, Ingredient.of(STAINLESS_SCREWS));
        return fastener;
    }

    private static ItemStack usedHammer(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);
        if(copy.has(DataComponents.UNBREAKABLE) || copy.getMaxDamage() <= 0)
            return copy;
        int damage = copy.getDamageValue() + 1;
        if(damage >= copy.getMaxDamage())
            return ItemStack.EMPTY;
        copy.setDamageValue(damage);
        return copy;
    }

}
