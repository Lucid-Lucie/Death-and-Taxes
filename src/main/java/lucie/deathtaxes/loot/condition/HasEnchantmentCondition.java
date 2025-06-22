package lucie.deathtaxes.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
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

public class HasEnchantmentCondition implements LootItemCondition
{
    @Override
    @Nonnull
    public LootItemConditionType getType()
    {
        return LootConditionRegistry.HAS_ENCHANTMENT.get();
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
        return itemStack != null && itemStack.isEnchanted();
    }

    public static class Serialize implements Serializer<HasEnchantmentCondition>
    {
        @Override
        public void serialize(@Nonnull JsonObject jsonObject, @Nonnull HasEnchantmentCondition hasRarityCondition, @Nonnull JsonSerializationContext context)
        {
        }

        @Override
        @Nonnull
        public HasEnchantmentCondition deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext context)
        {
            return new HasEnchantmentCondition();
        }
    }
}
