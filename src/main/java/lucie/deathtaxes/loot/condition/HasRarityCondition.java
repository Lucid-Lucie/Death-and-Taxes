package lucie.deathtaxes.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lucie.deathtaxes.registry.LootConditionRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import javax.annotation.Nonnull;
import java.util.Optional;

public record HasRarityCondition(Optional<Rarity> rarity) implements LootItemCondition
{
    public static final MapCodec<HasRarityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Rarity.CODEC.optionalFieldOf("rarity").forGetter(HasRarityCondition::rarity)
    ).apply(instance, HasRarityCondition::new));

    @Nonnull
    @Override
    public LootItemConditionType getType()
    {
        return LootConditionRegistry.HAS_RARITY.get();
    }

    @Override
    public boolean test(LootContext context)
    {
        return Optional.ofNullable(context.getOptionalParameter(LootContextParams.TOOL))
                .map(stack -> rarity
                .map(value -> stack.getRarity().equals(value))
                .orElseGet(() -> !stack.getRarity().equals(Rarity.COMMON)))
                .orElse(false);
    }
}
