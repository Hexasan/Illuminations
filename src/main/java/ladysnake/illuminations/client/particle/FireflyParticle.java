package ladysnake.illuminations.client.particle;

import ladysnake.illuminations.common.Illuminations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.LightType;

import java.util.HashMap;
import java.util.Random;

public class FireflyParticle extends SpriteBillboardParticle {
    private static final Identifier TEXTURE = new Identifier(Illuminations.MOD_ID, "textures/entity/firefly.png");
    private static final Identifier OVERLAY = new Identifier(Illuminations.MOD_ID, "textures/entity/firefly_overlay.png");
    private static final RenderLayer TEXTURE_LAYER = RenderLayer.getEntityTranslucent(TEXTURE);
    private static final RenderLayer OVERLAY_LAYER = RenderLayer.getEntityTranslucent(OVERLAY);

    private static final Random RANDOM = new Random();
    private final SpriteProvider spriteProvider;

    private FireflyParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;

        this.scale *= 0.25F + new Random().nextFloat() * 0.75F;
        this.maxAge = 99999999;
        this.collidesWithWorld = false;
        this.setSpriteForAge(spriteProvider);

        this.colorRed = (0.25F + new Random().nextFloat() * 0.75F) * 255;
        this.colorGreen = 1;
        this.colorBlue = 0;
    }

    int alpha = 0;
    int nextAlphaGoal = 0;

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        // alpha calculations
        if (age < 50) {
            // if just spawned
            alpha = 0;
        } else {
            // if day
            float tod = world.getLevelProperties().getTimeOfDay() % 24000;
            if (tod >= 1000 && tod < 13000) {
                nextAlphaGoal = 0;
            }

            if (alpha > nextAlphaGoal - 10 && alpha < nextAlphaGoal + 10) {
                nextAlphaGoal = new Random().nextInt(256);
            } else {
                if (nextAlphaGoal > alpha) {
                    alpha += 10;
                } else if (nextAlphaGoal < alpha) {
                    alpha -= 10;
                }
            }
        }

        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        Quaternion quaternion2;
        if (this.angle == 0.0F) {
            quaternion2 = camera.getRotation();
        } else {
            quaternion2 = new Quaternion(camera.getRotation());
            float i = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
            quaternion2.hamiltonProduct(Vector3f.POSITIVE_Z.getRadialQuaternion(i));
        }

        Vector3f vector3f = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f.rotate(quaternion2);
        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float j = this.getSize(tickDelta);

        for(int k = 0; k < 4; ++k) {
            Vector3f vector3f2 = vector3fs[k];
            vector3f2.rotate(quaternion2);
            vector3f2.scale(j);
            vector3f2.add(f, g, h);
        }

        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        int light = 15728880;

        // firefly
        vertexConsumer.vertex((double)vector3fs[0].getX(), (double)vector3fs[0].getY(), (double)vector3fs[0].getZ()).texture(maxU, maxV/2).color(this.colorRed, this.colorGreen, this.colorBlue, alpha).light(light).next();
        vertexConsumer.vertex((double)vector3fs[1].getX(), (double)vector3fs[1].getY(), (double)vector3fs[1].getZ()).texture(maxU, minV).color(this.colorRed, this.colorGreen, this.colorBlue, alpha).light(light).next();
        vertexConsumer.vertex((double)vector3fs[2].getX(), (double)vector3fs[2].getY(), (double)vector3fs[2].getZ()).texture(minU, minV).color(this.colorRed, this.colorGreen, this.colorBlue, alpha).light(light).next();
        vertexConsumer.vertex((double)vector3fs[3].getX(), (double)vector3fs[3].getY(), (double)vector3fs[3].getZ()).texture(minU, maxV/2).color(this.colorRed, this.colorGreen, this.colorBlue, alpha).light(light).next();

        // firefly overlay
        vertexConsumer.vertex((double)vector3fs[0].getX(), (double)vector3fs[0].getY(), (double)vector3fs[0].getZ()).texture(maxU, maxV).color(this.colorRed, this.colorGreen, this.colorBlue, alpha).light(light).next();
        vertexConsumer.vertex((double)vector3fs[1].getX(), (double)vector3fs[1].getY(), (double)vector3fs[1].getZ()).texture(maxU, maxV/2).color(this.colorRed, this.colorGreen, this.colorBlue, alpha).light(light).next();
        vertexConsumer.vertex((double)vector3fs[2].getX(), (double)vector3fs[2].getY(), (double)vector3fs[2].getZ()).texture(minU, maxV/2).color(this.colorRed, this.colorGreen, this.colorBlue, alpha).light(light).next();
        vertexConsumer.vertex((double)vector3fs[3].getX(), (double)vector3fs[3].getY(), (double)vector3fs[3].getZ()).texture(minU, maxV).color(this.colorRed, this.colorGreen, this.colorBlue, alpha).light(light).next();
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public DefaultFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new FireflyParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
    
    // Behaviour
    private double groundLevel;
    private BlockPos lightTarget;
    private double xTarget;
    private double yTarget;
    private double zTarget;
    private int targetChangeCooldown = 0;
    private boolean isAttractedByLight = false;

    public void tick() {
        super.tick();

        // despawn on daytime
//            float tod = this.world.getLevelProperties().getTimeOfDay() % 24000;
//            if (tod >= 1010 && tod < 12990) {
//                this.markDead();
//            }

        this.targetChangeCooldown -= (new Vec3d(x, y, z).squaredDistanceTo(prevPosX, prevPosY, prevPosZ) < 0.0125) ? 10 : 1;

        if ((this.world.getTime() % 20 == 0) && ((xTarget == 0 && yTarget == 0 && zTarget == 0) || new Vec3d(x, y, z).squaredDistanceTo(xTarget, yTarget, zTarget) < 9 || targetChangeCooldown <= 0)) {
            selectBlockTarget();
        }

        Vec3d targetVector = new Vec3d(this.xTarget - this.x, this.yTarget - this.y, this.zTarget - this.z);
        double length = targetVector.length();
        targetVector = targetVector.multiply(0.1 / length);


        if (!this.world.getBlockState(new BlockPos(this.x, this.y - 0.1, this.z)).getBlock().canMobSpawnInside()) {
            velocityX = (0.9) * velocityX + (0.1) * targetVector.x;
            velocityY = 0.05;
            velocityZ = (0.9) * velocityZ + (0.1) * targetVector.z;
        } else {
            velocityX = (0.9) * velocityX + (0.1) * targetVector.x;
            velocityY = (0.9) * velocityY + (0.1) * targetVector.y;
            velocityZ = (0.9) * velocityZ + (0.1) * targetVector.z;
        }
        if (!new BlockPos(x, y, z).equals(this.getTargetPosition())) {
            this.move(velocityX, velocityY, velocityZ);
        }
    }

    private void selectBlockTarget() {
        if (this.lightTarget == null || !this.isAttractedByLight) {
            this.groundLevel = 0;
            for (int i = 0; i < 20; i++) {
                BlockState checkedBlock = this.world.getBlockState(new BlockPos(this.x, this.y - i, this.z));
                if (!checkedBlock.getBlock().canMobSpawnInside()) {
                    this.groundLevel = this.y - i;
                }
                if (this.groundLevel != 0) break;
            }

            this.xTarget = this.x + random.nextGaussian() * 10;
            this.yTarget = Math.min(Math.max(this.y + random.nextGaussian() * 2, this.groundLevel), this.groundLevel + 4);
            this.zTarget = this.z + random.nextGaussian() * 10;

            BlockPos targetPos = new BlockPos(this.xTarget, this.yTarget, this.zTarget);
            if (this.world.getBlockState(targetPos).isFullCube(world, targetPos)
                    && this.world.getBlockState(targetPos).isSolidBlock(world, targetPos)) {
                this.yTarget += 1;
            }

            if (this.world.getLightLevel(LightType.SKY, new BlockPos(x, y, z)) > 8 && !this.world.isDay()) {
                this.lightTarget = getRandomLitBlockAround();
            }
        } else {
            this.xTarget = this.lightTarget.getX() + random.nextGaussian();
            this.yTarget = this.lightTarget.getY() + random.nextGaussian();
            this.zTarget = this.lightTarget.getZ() + random.nextGaussian();

            if (this.world.getLightLevel(LightType.BLOCK, new BlockPos(x, y, z)) > 8) {
                BlockPos possibleTarget = getRandomLitBlockAround();
                if (this.world.getLightLevel(LightType.BLOCK, possibleTarget) > this.world.getLightLevel(LightType.BLOCK, this.lightTarget))
                    this.lightTarget = possibleTarget;
            }

            if (this.world.getLightLevel(LightType.BLOCK, new BlockPos(x, y, z)) <= 8 || this.world.isDay())
                this.lightTarget = null;
        }

        targetChangeCooldown = random.nextInt() % 100;
    }

    public BlockPos getTargetPosition() {
        return new BlockPos(this.xTarget, this.yTarget + 0.5, this.zTarget);
    }

    private BlockPos getRandomLitBlockAround() {
        HashMap<BlockPos, Integer> randBlocks = new HashMap<>();
        for (int i = 0; i < 15; i++) {
            BlockPos randBP = new BlockPos(this.x + random.nextGaussian() * 10, this.y + random.nextGaussian() * 10, this.z + random.nextGaussian() * 10);
            randBlocks.put(randBP, this.world.getLightLevel(LightType.BLOCK, randBP));
        }
        return randBlocks.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
    }

}
