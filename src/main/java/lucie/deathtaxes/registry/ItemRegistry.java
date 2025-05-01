package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(DeathTaxes.MODID);

    public static final Holder<Item> SCAVENGER_SPAWN_EGG = ITEMS.register("scavenger_spawn_egg", location -> new SpawnEggItem(EntityTypeRegistry.SCAVENGER.value(), new Item.Properties()
            .setId(ResourceKey.create(Registries.ITEM, location))));
}
