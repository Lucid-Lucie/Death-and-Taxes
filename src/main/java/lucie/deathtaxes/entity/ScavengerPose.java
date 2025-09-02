package lucie.deathtaxes.entity;

import lucie.deathtaxes.registry.ParticleTypeRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public enum ScavengerPose
{
    ATTACKING,
    APPEARING {
        @Override
        public boolean canUse(long tickCount)
        {
            return tickCount % 4 == 0;
        }

        @Override
        public void tryUse(Scavenger scavenger, Level level, RandomSource random, long tickCount)
        {
            float angleInRadians = scavenger.yBodyRot * ((float) Math.PI / 180.0F) + Mth.cos((float) level.getGameTime() * 0.65F) * 0.5F;
            float cosAngle = Mth.cos(angleInRadians);
            float sinAngle = Mth.sin(angleInRadians);
            
            double x = scavenger.getX();
            double y = scavenger.getY();
            double z = scavenger.getZ();
            
            level.addParticle(ParticleTypes.ENTITY_EFFECT, x + (double)cosAngle * 0.7D, y + 1.9D, z + (double)sinAngle * 0.7D, 0.6F, 0.6F, 0.4F);
            level.addParticle(ParticleTypes.ENTITY_EFFECT, x - (double)cosAngle * 0.7D, y + 1.9D, z - (double)sinAngle * 0.7D, 0.6F, 0.6F, 0.4F);
        }
    },
    CONSUMING {
        @Override
        public boolean canUse(long tickCount)
        {
            return tickCount % 8 == 0;
        }

        @Override
        public void tryUse(Scavenger scavenger, Level level, RandomSource random, long tickCount)
        {
            ItemStack itemStack = scavenger.getConsumingItemstack();

            if (itemStack.isEmpty())
            {
                return;
            }

            float yawRad = -scavenger.yBodyRot * Mth.DEG_TO_RAD;
            Vec3 forwardOffset = new Vec3(0.0D, 0.0D, 0.5D).yRot(yawRad);

            for (int i = 0; i < 4; i++)
            {
                Vec3 velocity = new Vec3((random.nextFloat() - 0.5F) * 0.1F,random.nextFloat() * 0.1F + 0.05F,(random.nextFloat() - 0.5F) * 0.1F);
                Vec3 mouthPos = scavenger.position().add(0, scavenger.getBbHeight() * 0.85F, 0).add(forwardOffset).add(0, -0.25F, 0);
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemStack), mouthPos.x, mouthPos.y, mouthPos.z, velocity.x, velocity.y, velocity.z);
            }
        }
    },
    OFFERING {
        @Override
        public boolean canUse(long tickCount)
        {
            return tickCount % 30 == 0;
        }

        @Override
        public void tryUse(Scavenger scavenger, Level level, RandomSource random, long tickCount)
        {
            double x = scavenger.getX() + random.nextDouble() * 2.5D - 1.25D;
            double y = scavenger.getY() + random.nextDouble() * 2.5D;
            double z = scavenger.getZ() + random.nextDouble() * 2.5D - 1.25D;
            level.addParticle(ParticleTypeRegistry.FLY.get(), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    },
    LANTERN {
        @Override
        public boolean canUse(long tickCount)
        {
            return tickCount % 10 == 0;
        }

        @Override
        public void tryUse(Scavenger scavenger, Level level, RandomSource random, long tickCount)
        {
            double radius = (scavenger.getBbWidth() / 2.0D) + 0.2D + random.nextDouble() * 0.5D;
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double height = scavenger.getY() + (random.nextDouble() * scavenger.getBbHeight());
            level.addParticle(ParticleTypeRegistry.EMBER.get(), scavenger.getX() + Math.cos(angle) * radius, height, scavenger.getZ() + Math.sin(angle) * radius, 0, 0, 0);
        }
    },
    IDLE;

    public boolean canUse(long tickCount)
    {
        return false;
    }

    public void tryUse(Scavenger scavenger, Level level, RandomSource random, long tickCount)
    {
        throw new UnsupportedOperationException("ScavengerPose#tryUse is not implemented for " + this);
    }

    protected static ScavengerPose getInstance(Scavenger scavenger)
    {
        if (scavenger.isAggressive())
        {
            return ATTACKING;
        }

        if (!scavenger.getConsumingItemstack().isEmpty())
        {
            return CONSUMING;
        }

        if (scavenger.isAppearing())
        {
            return APPEARING;
        }

        if (scavenger.isOffering())
        {
            return OFFERING;
        }

        long nightStart = 13000L;

        if (scavenger.level().getDayTime() >= nightStart)
        {
            return LANTERN;
        }

        return IDLE;
    }
}
