package lucie.deathtaxes.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lucie.deathtaxes.registry.LootConditionRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import javax.annotation.Nonnull;
import java.util.Optional;

public record HasComponentCondition(Optional<DataComponentType<?>> componentType) implements LootItemCondition
{
    public static final MapCodec<HasComponentCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DataComponentType.CODEC.optionalFieldOf("component").forGetter(HasComponentCondition::componentType)
    ).apply(instance, HasComponentCondition::new));

    @Nonnull
    @Override
    public LootItemConditionType getType()
    {
        return LootConditionRegistry.HAS_COMPONENT.get();
    }

    @Override
    public boolean test(LootContext context)
    {
        Optional<ItemStack> itemStack = Optional.ofNullable(context.getOptionalParameter(LootContextParams.TOOL));

        return itemStack.isPresent() && componentType.isPresent() && itemStack.get().has(componentType.get());
    }
}
