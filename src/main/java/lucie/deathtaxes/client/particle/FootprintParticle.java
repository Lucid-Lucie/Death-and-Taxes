package lucie.deathtaxes.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FootprintParticle extends TextureSheetParticle
{
    private final float rotation;

    protected FootprintParticle(ClientLevel level, double x, double y, double z, float rotation)
    {
        super(level, x, y, z, 0, 0, 0);
        this.rotation = rotation;
        this.quadSize *= 2;
        this.lifetime = 80;
        this.hasPhysics = false;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.gravity = 0;
        this.friction = 1.0f;
    }

    @Override
    public void tick()
    {
        super.tick();

        float percentage = (float) age / lifetime;

        if (percentage > 0.7f)
        {
            this.setAlpha((1.0f - percentage) / 0.3f);
        }
    }

    @Override
    public void render(@Nonnull VertexConsumer consumer, @Nonnull Camera camera, float partialTick)
    {
        Vector3f[] vertices = new Vector3f[4];
        this.getQuadVertices(vertices, camera, partialTick);

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        int light = this.getLightColor(partialTick);

        consumer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    private void getQuadVertices(Vector3f[] vertices, Camera camera, float partialTick)
    {
        float cx = (float)(Mth.lerp(partialTick, this.xo, this.x) - camera.getPosition().x());
        float cy = (float)(Mth.lerp(partialTick, this.yo, this.y) - camera.getPosition().y());
        float cz = (float)(Mth.lerp(partialTick, this.zo, this.z) - camera.getPosition().z());

        float size = 0.3f;
        float height = 0.01f;
        float yawRad = (float)Math.toRadians(this.rotation + 180);

        float cos = Mth.cos(yawRad);
        float sin = Mth.sin(yawRad);

        Vector3f forward = new Vector3f(-sin, 0f,  cos);
        Vector3f right   = new Vector3f( cos, 0f,  sin);
        Vector3f center = new Vector3f(cx, cy + height, cz);

        vertices[0] = new Vector3f(center)
                .add(new Vector3f(right).mul(-size))
                .add(new Vector3f(forward).mul(-size)); // u0,v0

        vertices[1] = new Vector3f(center)
                .add(new Vector3f(right).mul(-size))
                .add(new Vector3f(forward).mul( size)); // u0,v1

        vertices[2] = new Vector3f(center)
                .add(new Vector3f(right).mul( size))
                .add(new Vector3f(forward).mul( size)); // u1,v1

        vertices[3] = new Vector3f(center)
                .add(new Vector3f(right).mul( size))
                .add(new Vector3f(forward).mul(-size)); // u1,v0
    }

    @Nonnull
    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<FootprintParticleOption>
    {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite)
        {
            this.sprite = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(@Nonnull FootprintParticleOption type, @Nonnull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            FootprintParticle particle = new FootprintParticle(level, x, y, z, type.rotation());
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}
