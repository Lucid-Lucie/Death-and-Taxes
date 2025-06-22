package lucie.deathtaxes.event;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.capability.DespawnTimerInitializer;
import lucie.deathtaxes.capability.DroppedLootInitializer;
import lucie.deathtaxes.entity.Scavenger;
import lucie.deathtaxes.registry.EntityTypeRegistry;
import lucie.deathtaxes.registry.ItemRegistry;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeathTaxes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEvent
{
    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS))
        {
            event.accept(ItemRegistry.SCAVENGER_SPAWN_EGG.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event)
    {
        event.put(EntityTypeRegistry.SCAVENGER.get(), Scavenger.registerAttributes());
    }

    @SubscribeEvent
    public static void onRegisterSpawnPlacements(SpawnPlacementRegisterEvent event)
    {
        event.register(EntityTypeRegistry.SCAVENGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(DroppedLootInitializer.class);
        event.register(DespawnTimerInitializer.class);
    }
}
