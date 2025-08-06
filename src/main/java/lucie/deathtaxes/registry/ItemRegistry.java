package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, DeathTaxes.MOD_ID);

    public static final RegistryObject<Item> SCAVENGER_SPAWN_EGG = ITEMS.register("scavenger_spawn_egg", () ->
            new ForgeSpawnEggItem(EntityTypeRegistry.SCAVENGER, 0xb6a895, 0x2d2a28, new Item.Properties()));
}
