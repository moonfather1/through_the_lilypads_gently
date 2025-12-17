package moonfather.lilypads.mixin.integration;

import moonfather.lilypads.SwampMath;
import moonfather.lilypads.mixin.falling.DamageCancelBase;
import net.mehvahdjukaar.amendments.common.block.WaterloggedLilyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(WaterloggedLilyBlock.class)
public class BetterLilyNewFallingMixin extends DamageCancelBase             // does this work? done diff in neoforge
{
    @Override
    public void cancelDamage(Level world, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci)
    {
        if (entity.fallDistance > 2.9 && ! world.isClientSide())
        {
            if (! (entity instanceof Player p && p.isCreative()))
            {
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

            // moved the lily pad. now about damage:
            entity.fallDistance = 0.1f;
            ci.cancel();
        }
    }
}
