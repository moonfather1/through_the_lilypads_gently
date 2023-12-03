package moonfather.lilypads.mixin.falling;

import moonfather.lilypads.SwampMath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaterlilyBlock.class)
public class FallingMixin
{
    @Inject(at = @At(value = "TAIL"), method = "entityInside", cancellable = false, remap = false)
    public void collision2(BlockState state, Level world, BlockPos pos, Entity entity, CallbackInfo ci)
    {
        if (entity.fallDistance > 2.9 && ! world.isClientSide())
        {
            if (entity instanceof Player p && p.isCreative())
            {
                return;
            }
            double size = Math.max(entity.getBbWidth(), 1.0);
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