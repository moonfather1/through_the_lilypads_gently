package moonfather.lilypads;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwampMath
{
    public static boolean tryMoveLilypad(BlockPos blockPos, Entity entity, World world, double distanceFactor, double angleDelta, BlockState original)
    {
        double angle = Math.atan2(blockPos.getZ() + 0.5d - entity.getZ(), blockPos.getX() + 0.5d - entity.getX());
        double movement_angle = (entity.getYaw() + 90.0) * Math.PI / 180.0; //we took the other axis as base. doesn't matter.
        if (Math.abs(angle + angleDelta - movement_angle) < Math.PI/9  // 20 deg
            || Math.abs(angle + angleDelta - movement_angle + 2*Math.PI) < Math.PI/9  // 20 deg over boundary
            || Math.abs(angle + angleDelta - movement_angle - 2*Math.PI) < Math.PI/9) // should not happen
        {
            return false;  // too close to boat movement angle
        }
        BlockPos targetPos = blockPos.add((int) Math.round(Math.cos(angle + angleDelta) * distanceFactor), 0, (int) Math.round(Math.sin(angle + angleDelta) * distanceFactor));
        BlockState target = world.getBlockState(targetPos);
        if (target.isAir() && original.getBlock() instanceof PlantBlock plant && plant.canPlaceAt(original, world, targetPos))
        {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
            world.setBlockState(targetPos, original, 3);
            return true;
        }
        return false;
    }

}
