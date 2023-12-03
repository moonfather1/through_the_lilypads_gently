package moonfather.lilypads.mixin.falling;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaterlilyBlock.class)
public class DamageCancelVanilla extends DamageCancelBase
{
    @Override
    public void cancelDamage(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci)
    {
        entity.fallDistance = 0.1f;
        ci.cancel();
    }
    /*
    this is not how this should be done. this should have been cancelled on collision test, not after the message handling but...
    ...but i'm tired and fabric-provided code doesn't match bytecode and breakpoints don't work (they're on random lines).
    ...maybe i'll give it another try later, but for now screw it.
     */
}