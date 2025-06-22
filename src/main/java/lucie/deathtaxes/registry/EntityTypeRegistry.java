package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EntityTypeRegistry
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, DeathTaxes.MODID);

    public static final RegistryObject<EntityType<Scavenger>> SCAVENGER = ENTITY_TYPES.register("scavenger", () -> EntityType.Builder.of(Scavenger::new, MobCategory.MISC)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(8)
            .build("scavenger"));
}
