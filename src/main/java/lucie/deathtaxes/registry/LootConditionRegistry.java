package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.loot.condition.HasEnchantmentCondition;
import lucie.deathtaxes.loot.condition.HasRarityCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class LootConditionRegistry
{
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, DeathTaxes.MODID);

    public static final RegistryObject<LootItemConditionType> HAS_RARITY = LOOT_CONDITIONS.register("has_rarity", () -> new LootItemConditionType(new HasRarityCondition.Serialize()));
    public static final RegistryObject<LootItemConditionType> HAS_ENCHANTMENT = LOOT_CONDITIONS.register("has_enchantment", () -> new LootItemConditionType(new HasEnchantmentCondition.Serialize()));

}
