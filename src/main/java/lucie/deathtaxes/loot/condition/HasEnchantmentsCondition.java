package lucie.deathtaxes.loot.condition;

import com.mojang.serialization.MapCodec;
import lucie.deathtaxes.registry.LootConditionRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import javax.annotation.Nonnull;
import java.util.Optional;

public record HasEnchantmentsCondition() implements LootItemCondition
{
    public static final MapCodec<HasEnchantmentsCondition> CODEC = MapCodec.unit(HasEnchantmentsCondition::new);

    @Nonnull
    @Override
    public LootItemConditionType getType()
    {
        return LootConditionRegistry.HAS_ENCHANTMENTS.get();
    }

    @Override
    public boolean test(LootContext context)
    {
        Optional<ItemStack> itemStack = Optional.ofNullable(context.getOptionalParameter(LootContextParams.TOOL));

        return itemStack.isPresent() && itemStack.get().isEnchanted();
    }
}
