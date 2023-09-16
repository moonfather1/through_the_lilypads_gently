package moonfather.lilypads.mixin;

import moonfather.lilypads.SwampMath;
import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LilyPadBlock.class)
public class FallingMixin
{
    @Inject(at = @At(value = "TAIL"), method = "onEntityCollision", cancellable = false)
    private void collision2(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci)
    {
        if (entity.fallDistance > 2.9 && ! world.isClient)
        {
            if (entity instanceof PlayerEntity p && p.isCreative())
            {
                return;
            }
            double size = Math.max(entity.getWidth(), 1.0);
            BlockState original = world.getBlockState(pos);
            if (SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.1 * size, 0.0, original)
                    || SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.1 * size, +Math.PI / 4, original) //45d
                    || SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.1 * size, -Math.PI / 4, original)
                    || SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.1 * size, +Math.PI / 2, original) //90d
                    || SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.1 * size, -Math.PI / 2, original)
                    || SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.9 * size, 0.0, original))
            {
            }
        }
    }
}
