package moonfather.lilypads.integration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.orcinus.goodending.blocks.LargeLilyPadBlock")
public class GoodEndingMixin3 extends GoodEndingMixin2
{
    @Override
    public void changeCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue(SHAPE);
    }
    private static final VoxelShape SHAPE = Block.createCuboidShape(1, 15, 1, 15, 16, 15);
}
