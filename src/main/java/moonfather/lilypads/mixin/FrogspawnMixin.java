package moonfather.lilypads.mixin;

import moonfather.lilypads.SwampMath;
import net.minecraft.block.BlockState;
import net.minecraft.block.FrogspawnBlock;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FrogspawnBlock.class)
public abstract class FrogspawnMixin
{
    @Inject(method = "onEntityCollision", at = @At(value = "INVOKE", target = "net/minecraft/block/FrogspawnBlock.breakWithoutDrop (Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"), cancellable = true)
    private void slidingBlockCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo info)
    {
        if (entity instanceof FallingBlockEntity fbe && fbe.hasNoGravity())
        {
            info.cancel();
        }
    }

    @Inject(method = "onEntityCollision", at = @At(value = "TAIL"), cancellable = false)
    private void otherCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo info)
    {
        if (world.isClient())
        {
            return;
        }
        if (pos.getX() == lastX && pos.getZ() == lastZ)
        {
            return;
        }
        BlockState original = world.getBlockState(pos);
        if (entity.hasControllingPassenger() && world instanceof ServerWorld) // last one for cast
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

    @Invoker("canPlaceAt")
    public abstract boolean invokeCanPlaceAt(BlockState state, WorldView world, BlockPos pos);
}
