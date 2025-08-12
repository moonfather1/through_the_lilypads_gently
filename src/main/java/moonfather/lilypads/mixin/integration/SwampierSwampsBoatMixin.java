package moonfather.lilypads.mixin.integration;

import andrews.swampier_swamps.objects.blocks.SmallLilyPadBlock;
import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = SmallLilyPadBlock.class)
public class SwampierSwampsBoatMixin
{
    @Inject(at = @At(value = "HEAD"), method = "getOutlineShape", cancellable = true)
    private void shape(CallbackInfoReturnable<VoxelShape> info)
    {
        info.setReturnValue(AABB2);
    }
    @Unique
    private static final VoxelShape AABB2 = Block.createCuboidShape(0.1, 0.1, 0.0, 15.9, 1.4, 15.9);
}
