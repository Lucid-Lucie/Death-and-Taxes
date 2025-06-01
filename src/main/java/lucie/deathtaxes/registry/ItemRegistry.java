package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(DeathTaxes.MODID);

    public static final DeferredHolder<Item, DeferredSpawnEggItem> SCAVENGER_SPAWN_EGG = ITEMS.register("scavenger_spawn_egg", () -> new DeferredSpawnEggItem(EntityTypeRegistry.SCAVENGER, 0x5f5954, 0x6c1d2e, new Item.Properties()));
}
