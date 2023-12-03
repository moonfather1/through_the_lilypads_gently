package moonfather.lilypads.mixin.falling;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class DamageCancelBase
{
    @SuppressWarnings("CancellableInjectionUsage")
    @Inject(at = @At("HEAD"), method = "fallOn", cancellable = true, remap = false)
    public void cancelDamage(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci)
    {
        // marker for override
    }
    /*
    this is not how this should be done. this should have been cancelled on collision test, not after the message handling but...
    ...but i'm tired and fabric-provided code doesn't match bytecode and breakpoints don't work (they're on random lines).
    ...maybe i'll give it another try later, but for now screw it.
     */
}