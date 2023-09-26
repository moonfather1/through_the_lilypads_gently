package moonfather.lilypads.mixin.integration;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LilyPadBlock.class, priority = Integer.MAX_VALUE)
public class VanillaCollisionFixMixin extends PlantBlock
{
    // we need to forcefully override getCollisionShape to neutralize GoodEnding's override.
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }
    private static final VoxelShape SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    // nothing here; forced constructor.
    public VanillaCollisionFixMixin(Settings settings) { super(settings); }
}
