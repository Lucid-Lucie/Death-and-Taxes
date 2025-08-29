package lucie.deathtaxes.registry;

import com.mojang.serialization.Codec;
import lucie.deathtaxes.DeathTaxes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class MemoryModuleTypeRegistry
{
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, DeathTaxes.MOD_ID);

    public static final RegistryObject<MemoryModuleType<ItemStack>> CONSUMING_ITEM = MEMORY_MODULE_TYPES.register("consuming_time", () -> new MemoryModuleType<>(Optional.of(ItemStack.CODEC)));

    public static final RegistryObject<MemoryModuleType<Integer>> CONSUMING_COOLDOWN = MEMORY_MODULE_TYPES.register("consuming_cooldown", () -> new MemoryModuleType<>(Optional.of(Codec.INT)));
}
