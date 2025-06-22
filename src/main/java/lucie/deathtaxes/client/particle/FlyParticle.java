package lucie.deathtaxes.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class FlyParticle extends TextureSheetParticle
{
    public FlyParticle(ClientLevel level, double x, double y, double z, double zSpeed, double ySpeed, double xSpeed)
    {
        super(level, x, y, z, zSpeed, ySpeed, xSpeed);
        this.speedUpWhenYMotionIsBlocked = true;
        this.friction = 0.96F;
        this.quadSize *= 0.75F;
        this.xd *= 0.8D;
        this.yd *= 0.8D;
        this.zd *= 0.8D;
    }

    @Nonnull
    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick()
    {
        super.tick();
        
        if (!this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir())
        {
            this.remove();
        }
        else
        {
            if (Math.random() > 0.95F || this.age == 1)
            {
                this.setParticleSpeed((double)-0.05F + (double)0.1F * Math.random(), (double)-0.05F + (double)0.1F * Math.random(), (double)-0.05F + (double)0.1F * Math.random());
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public static class FlyProvider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprite;

        public FlyProvider(SpriteSet sprite)
        {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@Nonnull SimpleParticleType simpleParticleType, @Nonnull ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            FlyParticle particle = new FlyParticle(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprite);
            particle.setLifetime(clientLevel.random.nextIntBetweenInclusive(36, 180));
            particle.scale(1.5F);
            return particle;
        }
    }
}
