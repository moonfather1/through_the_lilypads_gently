package moonfather.lilypads.mixin.integration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.orcinus.goodending.blocks.LargeLilyPadBlock;

@Pseudo
@Mixin(LargeLilyPadBlock.class)
public class GoodEndingShapeMixin extends GoodEndingShapeMixinBase
{
    @Override
    public void changeCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue(SHAPE);
    }
    @Unique
    private static final VoxelShape SHAPE = Block.createCuboidShape(1, 15, 1, 15, 16, 15);
}
