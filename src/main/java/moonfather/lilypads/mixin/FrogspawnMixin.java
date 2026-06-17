package moonfather.lilypads.mixin;

import moonfather.lilypads.SwampMath;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FrogspawnBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FrogspawnBlock.class)
public abstract class FrogspawnMixin
{
    @Inject(method = "entityInside", at = @At(value = "INVOKE", target = "net/minecraft/world/level/block/FrogspawnBlock.destroyBlock (Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), cancellable = true)
    private void slidingBlockCollision(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, CallbackInfo info)
    {
        if (entity instanceof FallingBlockEntity fbe && fbe.isNoGravity())
        {
            info.cancel();
        }
    }

    @Inject(method = "entityInside", at = @At(value = "TAIL"), cancellable = false)
    private void otherCollision(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, CallbackInfo info)
    {
        if (world.isClientSide())
        {
            return;
        }
        if (pos.getX() == lastX && pos.getZ() == lastZ)
        {
            return;
        }
        BlockState original = world.getBlockState(pos);
        if (entity.hasControllingPassenger() && world instanceof ServerLevel) // last one for cast
        {
            lastX = pos.getX();  lastZ = pos.getZ();
            if (SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, 0.0, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, +Math.PI/4, original) //45d
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, -Math.PI/4, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, +Math.PI/2, original) //90d
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, -Math.PI/2, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.9, 0.0, original)) //todo: move to utility class
            {
            }
        }
    }

    ////////////////////////////////////////////////////////

    @Unique
    private static int lastX = 0, lastZ = 0;
}
