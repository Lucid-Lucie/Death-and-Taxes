package lucie.deathtaxes.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lucie.deathtaxes.registry.ParticleTypeRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Locale;

public record FootprintParticleOption(float rotation) implements ParticleOptions
{
    public static final Codec<FootprintParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("rotation").forGetter(FootprintParticleOption::rotation)
    ).apply(instance, FootprintParticleOption::new));

    @SuppressWarnings("deprecation")
    public static final Deserializer<FootprintParticleOption> DESERIALIZER = new Deserializer<>()
    {
        @Nonnull
        @Override
        public FootprintParticleOption fromCommand(@Nonnull ParticleType<FootprintParticleOption> particleType, @Nonnull StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            float i = reader.readFloat();
            return new FootprintParticleOption(i);
        }

        @Nonnull
        @Override
        public FootprintParticleOption fromNetwork(@Nonnull ParticleType<FootprintParticleOption> particleType, @Nonnull FriendlyByteBuf buffer)
        {
            return new FootprintParticleOption(buffer.readFloat());
        }
    };

    @Nonnull
    @Override
    public ParticleType<?> getType()
    {
        return ParticleTypeRegistry.FOOTPRINT.get();
    }

    @Override
    public void writeToNetwork(@Nonnull FriendlyByteBuf buffer)
    {
        buffer.writeFloat(this.rotation);
    }

    @Nonnull
    @Override
    public String writeToString()
    {
        return String.format(Locale.ROOT, "%s %f", ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()), this.rotation);
    }
}
