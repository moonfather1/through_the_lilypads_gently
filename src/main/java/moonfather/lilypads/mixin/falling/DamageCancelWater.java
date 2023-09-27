package moonfather.lilypads.mixin.falling;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidBlock.class)
public class DamageCancelWater extends DamageCancelBase
{
    @Override
    public void cancelDamage(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci)
    {
        entity.fallDistance = 0.1f;
        ci.cancel();
    }
    /*
    maybe i should make this optional? if someone has a conflict, they can just make a mixin with priority 1001 or call me and i'll change this.
     */
}
