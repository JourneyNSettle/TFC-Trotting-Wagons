package net.favouriteless.tfc_trotting_wagons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/** Shaped craft whose result may be more than the item's stack size. A durable item cannot say count 2 in a vanilla recipe. */
public final class CountedShapedRecipe extends ShapedRecipe {

    private static final MapCodec<ItemStack> RESULT = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
            Codec.INT.optionalFieldOf("damage", 0).forGetter(ItemStack::getDamageValue)
    ).apply(instance, CountedShapedRecipe::output));

    private static final StreamCodec<RegistryFriendlyByteBuf, ItemStack> RESULT_STREAM = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ITEM), ItemStack::getItem,
            ByteBufCodecs.VAR_INT, ItemStack::getCount,
            ByteBufCodecs.VAR_INT, ItemStack::getDamageValue,
            CountedShapedRecipe::output
    );

    public static final MapCodec<CountedShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
            RESULT.fieldOf("result").forGetter(recipe -> recipe.getResultItem(null))
    ).apply(instance, CountedShapedRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CountedShapedRecipe> STREAM_CODEC = StreamCodec.composite(
            ShapedRecipePattern.STREAM_CODEC, recipe -> recipe.pattern,
            RESULT_STREAM, recipe -> recipe.getResultItem(null),
            CountedShapedRecipe::new
    );

    public static final RecipeSerializer<CountedShapedRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public MapCodec<CountedShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CountedShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    public CountedShapedRecipe(ShapedRecipePattern pattern, ItemStack result) {
        super("", CraftingBookCategory.MISC, pattern, result);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TfcTrottingWagons.COUNTED_SHAPED.get();
    }

    private static ItemStack output(Item item, int count, int damage) {
        ItemStack stack = new ItemStack(item, count);
        if(damage > 0)
            stack.set(DataComponents.DAMAGE, damage);
        return stack;
    }

}
