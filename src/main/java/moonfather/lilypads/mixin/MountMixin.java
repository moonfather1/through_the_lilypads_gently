package moonfather.lilypads.mixin;

import moonfather.lilypads.SwampMath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WaterlilyBlock.class, priority = 5)
public class MountMixin
{
    @Inject(at = @At(value = "TAIL"), method = "entityInside")
    private void horseCollision(BlockState state, Level world, BlockPos pos, Entity entity, CallbackInfo info)
    {
        if (world.isClientSide())
        {
            return;
        }
        if (pos.getX() == _lastX && pos.getZ() == _lastZ)
        {
            return;
        }
        _lastX = pos.getX();  _lastZ = pos.getZ();
        BlockState original = world.getBlockState(pos);
        if (SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, 0.0, original)
                || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, +Math.PI/4, original) //45d
                || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, -Math.PI/4, original)
                || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, +Math.PI/2, original) //90d
                || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, -Math.PI/2, original)
                || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.9, 0.0, original))
        {
        }
    }



    @Unique
    private static int _lastX = 0, _lastZ = 0;
}
