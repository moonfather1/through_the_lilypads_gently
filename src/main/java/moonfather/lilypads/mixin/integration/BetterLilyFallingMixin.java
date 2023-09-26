package moonfather.lilypads.mixin.integration;

import moonfather.lilypads.mixin.falling.FallingMixin;
//import net.mehvahdjukaar.betterlily.WaterloggedLilyBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Pseudo
//@Mixin(WaterloggedLilyBlock.class)
public class BetterLilyFallingMixin extends FallingMixin
{
    //@Override
    public void collision2(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo info) {
        if (entity.fallDistance > 2.9 && ! world.isClient)
        {
            System.out.println("~~~~~~~~");
        }
    }
}
