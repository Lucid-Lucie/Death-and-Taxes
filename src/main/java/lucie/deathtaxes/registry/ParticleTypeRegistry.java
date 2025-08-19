package lucie.deathtaxes.registry;

import com.mojang.serialization.Codec;
import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.client.particle.FootprintParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class ParticleTypeRegistry
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, DeathTaxes.MOD_ID);

    public static final RegistryObject<SimpleParticleType> EMBER = PARTICLE_TYPES.register("ember", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> FLY = PARTICLE_TYPES.register("fly", () -> new SimpleParticleType(false));

    public static final RegistryObject<ParticleType<FootprintParticleOption>> FOOTPRINT = PARTICLE_TYPES.register("footprint", () -> new ParticleType<>(false, FootprintParticleOption.DESERIALIZER)
    {
        @Nonnull
        @Override
        public Codec<FootprintParticleOption> codec()
        {
            return FootprintParticleOption.CODEC;
        }
    });
}
