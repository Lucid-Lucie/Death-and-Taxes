package lucie.deathtaxes.event;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.entity.Scavenger;
import lucie.deathtaxes.registry.EntityTypeRegistry;
import lucie.deathtaxes.registry.ItemRegistry;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = DeathTaxes.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CommonEvent
{
    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS))
        {
            event.insertAfter(Items.SALMON_SPAWN_EGG.getDefaultInstance(), new ItemStack(ItemRegistry.SCAVENGER_SPAWN_EGG), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event)
    {
        event.put(EntityTypeRegistry.SCAVENGER.value(), Scavenger.attributes());
    }

    @SubscribeEvent
    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event)
    {
        event.register(EntityTypeRegistry.SCAVENGER.value(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
    }
}
