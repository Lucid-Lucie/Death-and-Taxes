package lucie.deathtaxes.entity.behavior;

import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Optional;

public class MoveAroundPoint
{
    public static OneShot<Scavenger> create(MemoryModuleType<GlobalPos> poiMemory, float speedModifier, int maxDistFromPoi)
    {
        MutableLong nextStrollTime = new MutableLong(0L);

        return BehaviorBuilder.create(context ->
                context.group(context.registered(MemoryModuleType.WALK_TARGET), context.present(poiMemory)).apply(context, (walkTargetAccessor, poiAccessor) -> (level, entity, gameTime) -> {
                            if (entity.getTradingPlayer() != null)
                            {
                                return false;
                            }

                            GlobalPos poi = context.get(poiAccessor);

                            if (!level.dimension().equals(poi.dimension()))
                            {
                                return false;
                            }

                            double distSqr = entity.blockPosition().distSqr(poi.pos());

                            if (distSqr > maxDistFromPoi * maxDistFromPoi)
                            {
                                walkTargetAccessor.set(new WalkTarget(poi.pos(), speedModifier, 1));
                            } else
                            {
                                if (gameTime >= nextStrollTime.getValue())
                                {
                                    Optional<Vec3> randomPos = Optional.ofNullable(LandRandomPos.getPos(entity, 8, 6));
                                    randomPos.ifPresent(pos ->
                                            walkTargetAccessor.set(new WalkTarget(pos, speedModifier, 1))
                                    );
                                    nextStrollTime.setValue(gameTime + 180L);
                                }
                            }

                            return true;
                        }
                )
        );
    }
}