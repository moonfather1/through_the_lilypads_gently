package moonfather.lilypads.mixin.integration;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = LilyPadBlock.class, priority = 10000)
public abstract class GoodEndingCollisionRudenessMixin extends PlantBlock
{
    @Shadow public abstract VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);

    // we need to forcefully override getCollisionShape to neutralize GoodEnding's override.
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return this.getOutlineShape(state, world, pos, context);
        //return SHAPE;
    }
    //private static final VoxelShape SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    // nothing here; forced constructor.
    public GoodEndingCollisionRudenessMixin(Settings settings) { super(settings); }
}
