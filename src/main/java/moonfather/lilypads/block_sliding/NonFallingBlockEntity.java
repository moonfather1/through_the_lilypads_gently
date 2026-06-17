package moonfather.lilypads.block_sliding;

import moonfather.lilypads.mixin.sliding.FallingBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class NonFallingBlockEntity extends FallingBlockEntity
{
    private NonFallingBlockEntity(Level world, double x, double y, double z, BlockState block)
    {
        this(EntityTypes.FALLING_BLOCK, world);
        ((FallingBlockAccessor) this).lilypads$setBlockState(block);
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
    }

    private NonFallingBlockEntity(EntityType<FallingBlockEntity> type, Level world)
    {
        super(type, world);
    }

    public static NonFallingBlockEntity fromBlock(Level world, BlockPos pos, BlockState state, double vx, double vz)
    {
        return NonFallingBlockEntity.fromBlock(world, pos, state, vx, vz, null, 0);
    }
    public static NonFallingBlockEntity fromBlock(Level world, BlockPos pos, BlockState state, double vx, double vz, BlockState blockToLeaveBehind, int adjustmentY)
    {
        NonFallingBlockEntity fallingBlockEntity = new NonFallingBlockEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.5, state.hasProperty(BlockStateProperties.WATERLOGGED) ? (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, false) : state);
        if (blockToLeaveBehind == null) { blockToLeaveBehind = state.getFluidState().createLegacyBlock(); }
        world.setBlock(pos, blockToLeaveBehind, 3);
        fallingBlockEntity.setNoGravity(true);
        fallingBlockEntity.setPos(fallingBlockEntity.getX(), fallingBlockEntity.getY() + adjustmentY, fallingBlockEntity.getZ());
        fallingBlockEntity.setDeltaMovement(vx, 0, vz);
        fallingBlockEntity.dropItem = false;
        world.addFreshEntity(fallingBlockEntity);
        return fallingBlockEntity;
    }
}
