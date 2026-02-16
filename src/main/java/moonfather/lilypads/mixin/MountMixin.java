package moonfather.lilypads.mixin;

import moonfather.lilypads.SwampMath;
import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LilyPadBlock.class, priority = 5)
public class MountMixin
{
    @Inject(at = @At(value = "TAIL"), method = "onEntityCollision", cancellable = true)
    private void horseCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo info)
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
        if (entity.hasControllingPassenger() && original.getBlock() instanceof LilyPadBlock && world instanceof ServerWorld) // last one for cast
        {
            lastX = pos.getX();  lastZ = pos.getZ();
            if (SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, 0.0, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, +Math.PI/4, original) //45d
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, -Math.PI/4, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, +Math.PI/2, original) //90d
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, -Math.PI/2, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.9, 0.0, original)) //todo: move to utility class
            {
                info.cancel(); // don't actually need this; we have side effect above.
            }
        }
    }

    @Unique
    private static int lastX = 0, lastZ = 0;
}
