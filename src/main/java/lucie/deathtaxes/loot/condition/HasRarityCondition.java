package lucie.deathtaxes.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lucie.deathtaxes.registry.LootConditionRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import javax.annotation.Nonnull;
import java.util.Set;

public class HasRarityCondition implements LootItemCondition
{
    final Rarity rarity;

    public HasRarityCondition(Rarity rarity)
    {
        this.rarity = rarity;
    }

    @Override
    @Nonnull
    public LootItemConditionType getType()
    {
        return LootConditionRegistry.HAS_RARITY.get();
    }

    @Override
    @Nonnull
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    @Override
    public boolean test(LootContext lootContext)
    {
        ItemStack itemStack = lootContext.getParamOrNull(LootContextParams.TOOL);
        return itemStack != null && itemStack.getRarity() == this.rarity;
    }

    public static class Serialize implements Serializer<HasRarityCondition>
    {
        @Override
        public void serialize(@Nonnull JsonObject jsonObject, @Nonnull HasRarityCondition hasRarityCondition, @Nonnull JsonSerializationContext context)
        {
            jsonObject.addProperty("rarity", hasRarityCondition.rarity.toString().toUpperCase());
        }

        @Override
        @Nonnull
        public HasRarityCondition deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext context)
        {
            return new HasRarityCondition(Rarity.valueOf(jsonObject.get("rarity").getAsString().toUpperCase()));
        }
    }
}
