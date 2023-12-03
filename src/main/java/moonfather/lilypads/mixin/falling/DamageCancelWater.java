package moonfather.lilypads.mixin.falling;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LiquidBlock.class)
public class DamageCancelWater extends DamageCancelBase
{
    @Override
    public void cancelDamage(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci)
    {
        entity.fallDistance = 0.1f;
        ci.cancel();
    }
    /*
    maybe i should make this optional? if someone has a conflict, they can just make a mixin with priority 1001 or call me and i'll change this.
     */
}