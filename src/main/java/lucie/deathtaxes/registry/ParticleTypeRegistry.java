package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ParticleTypeRegistry
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, DeathTaxes.MODID);

    public static final RegistryObject<ParticleType<SimpleParticleType>> FLY = PARTICLE_TYPES.register("fly", () -> new SimpleParticleType(false));
}
