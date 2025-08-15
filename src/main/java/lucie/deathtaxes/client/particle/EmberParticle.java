package lucie.deathtaxes.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EmberParticle extends TextureSheetParticle
{
    private static final Vector3f[] COLOR_STOPS = new Vector3f[]{
            rgb(0xFFFFD5),
            rgb(0xF6C562),
            rgb(0xEF8447),
            rgb(0x8B5230),
            rgb(0x814023)
    };

    protected EmberParticle(ClientLevel level, double x, double y, double z, double zSpeed, double ySpeed, double xSpeed)
    {
        super(level, x, y, z, zSpeed, ySpeed, xSpeed);
        this.speedUpWhenYMotionIsBlocked = true;
        this.lifetime = this.random.nextIntBetweenInclusive(30, 60);
        this.friction = 0.96F;
        this.quadSize *= 1.25F;
        this.xd *= 0.25;
        this.yd *= 0.125;
        this.zd *= 0.25;
    }

    @Override
    public void tick()
    {
        super.tick();

        float t = (float) age / lifetime;

        // Buoyancy force
        double buoyancy = 0.008 * (1.0 - t * 0.7);

        // Air turbulence
        if (age % 3 == 0)
        {
            double turbulence = 0.006 * random.nextGaussian();
            xd += turbulence;
            zd += turbulence;
        }

        // Thermal updrafts
        double updraft = Math.sin(age * 0.08) * 0.002;
        this.yd += buoyancy + updraft;

        // Horizontal drift
        this.xd += (random.nextDouble() - 0.5) * 0.001;
        this.zd += (random.nextDouble() - 0.5) * 0.001;

        // Air resistance
        this.xd *= 0.96;
        this.zd *= 0.96;
        this.yd *= 0.98;

        // Gravity
        this.yd -= 0.002 * t;

        // Particle color cooldown
        Vector3f col = getInterpolatedColor(t);
        this.setColor(col.x(), col.y(), col.z());

        if (t > 0.7f)
        {
            this.setAlpha((1.0f - t) / 0.3f);
        }
    }

    @Nonnull
    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick)
    {
        float t = ((float) this.age + partialTick) / (float) this.lifetime;

        int blockLight = (int) Mth.lerp(t, 15, 3);

        return blockLight << 4;
    }

    private static Vector3f rgb(int hex)
    {
        return new Vector3f(
                ((hex >> 16) & 0xFF) / 255f,
                ((hex >> 8) & 0xFF) / 255f,
                (hex & 0xFF) / 255f
        );
    }

    private Vector3f getInterpolatedColor(float t)
    {
        int segmentCount = COLOR_STOPS.length - 1;
        float segment = t * segmentCount;
        int idx = Math.min((int) segment, segmentCount - 1);
        float localT = segment - idx;

        Vector3f start = COLOR_STOPS[idx];
        Vector3f end = COLOR_STOPS[idx + 1];

        return new Vector3f(
                Mth.lerp(localT, start.x(), end.x()),
                Mth.lerp(localT, start.y(), end.y()),
                Mth.lerp(localT, start.z(), end.z())
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static class EmberProvider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprites;

        public EmberProvider(SpriteSet spriteSet)
        {
            this.sprites = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(@Nonnull SimpleParticleType particleType, @Nonnull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            EmberParticle particle = new EmberParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
