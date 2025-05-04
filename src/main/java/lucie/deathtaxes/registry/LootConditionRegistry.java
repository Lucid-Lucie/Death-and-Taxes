package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.loot.condition.HasComponentCondition;
import lucie.deathtaxes.loot.condition.HasEnchantmentsCondition;
import lucie.deathtaxes.loot.condition.HasRarityCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class LootConditionRegistry
{
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, DeathTaxes.MODID);

    public static final Supplier<LootItemConditionType> HAS_COMPONENT = LOOT_CONDITIONS.register("has_component", () -> new LootItemConditionType(HasComponentCondition.CODEC));

    public static final Supplier<LootItemConditionType> HAS_ENCHANTMENTS = LOOT_CONDITIONS.register("has_enchantments", () -> new LootItemConditionType(HasEnchantmentsCondition.CODEC));

    public static final Supplier<LootItemConditionType> HAS_RARITY = LOOT_CONDITIONS.register("has_rarity", () -> new LootItemConditionType(HasRarityCondition.CODEC));
}
