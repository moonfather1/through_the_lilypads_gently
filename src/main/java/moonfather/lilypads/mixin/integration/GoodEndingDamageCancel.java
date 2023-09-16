package moonfather.lilypads.mixin.integration;

import moonfather.lilypads.SwampMath;
import moonfather.lilypads.mixin.falling.DamageCancelBase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orcinus.goodending.blocks.LargeLilyPadBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LargeLilyPadBlock.class)
public class GoodEndingDamageCancel extends DamageCancelBase
{
    @Override
    public void cancelDamage(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci)
    {
        if (entity.fallDistance > 2.9 && ! world.isClient)
        {
            if (! (entity instanceof PlayerEntity p && p.isCreative()))
            {
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
        // moved the lily pad. now about damage:
        ci.cancel(); // no damage
    }
}
