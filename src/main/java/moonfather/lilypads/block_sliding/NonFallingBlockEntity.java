package moonfather.lilypads.block_sliding;

import moonfather.lilypads.mixin.FallingBlockAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class NonFallingBlockEntity extends FallingBlockEntity
{
    private NonFallingBlockEntity(World world, double x, double y, double z, BlockState block)
    {
        this(EntityType.FALLING_BLOCK, world);
        ((FallingBlockAccessor) this).lilypads$setBlock(block);
        this.intersectionChecked = true;
        this.setPosition(x, y, z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setFallingBlockPos(this.getBlockPos());
    }

    private NonFallingBlockEntity(EntityType<FallingBlockEntity> type, World world)
    {
        super(type, world);
    }

    public static NonFallingBlockEntity fromBlock(World world, BlockPos pos, BlockState state, double vx, double vz)
    {
        return NonFallingBlockEntity.fromBlock(world, pos, state, vx, vz, null, 0);
    }
    public static NonFallingBlockEntity fromBlock(World world, BlockPos pos, BlockState state, double vx, double vz, BlockState blockToLeaveBehind, int adjustmentY)
    {
        NonFallingBlockEntity fallingBlockEntity = new NonFallingBlockEntity(world, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, state.contains(Properties.WATERLOGGED) ? (BlockState)state.with(Properties.WATERLOGGED, false) : state);
        if (blockToLeaveBehind == null) { blockToLeaveBehind = state.getFluidState().getBlockState(); }
        world.setBlockState(pos, blockToLeaveBehind, 3);
        fallingBlockEntity.setNoGravity(true);
        fallingBlockEntity.setPosition(fallingBlockEntity.getPos().add(0, adjustmentY, 0));
        fallingBlockEntity.setVelocity(vx, 0, vz);
        fallingBlockEntity.dropItem = false;
        world.spawnEntity(fallingBlockEntity);
        return fallingBlockEntity;
    }
}
