package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, DeathTaxes.MODID);

    public static final RegistryObject<ForgeSpawnEggItem> SCAVENGER_SPAWN_EGG = ITEMS.register("scavenger_spawn_egg", () -> new ForgeSpawnEggItem(EntityTypeRegistry.SCAVENGER, 0xB6A895, 0x352E1D, new Item.Properties()));
}
