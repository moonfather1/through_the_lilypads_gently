package moonfather.lilypads;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class SwampMath
{
    public static boolean tryMoveLilypadByBoat(BlockPos blockPos, Entity entity, World world, double distanceFactor, double angleDelta, BlockState original)
    {
        double angle = Math.atan2(blockPos.getZ() + 0.5d - entity.getZ(), blockPos.getX() + 0.5d - entity.getX());
        double movement_angle = (entity.getYaw() + 90.0) * Math.PI / 180.0; //we took the other axis as base. doesn't matter.
        //System.out.println("~~boat_angle==" + movement_angle+ "  angle_to_block==" + angle);
        if (Math.abs(angle + angleDelta - movement_angle) < Math.PI/9  // 20 deg
            || Math.abs(angle + angleDelta - movement_angle + 2*Math.PI) < Math.PI/9  // 20 deg over boundary
            || Math.abs(angle + angleDelta - movement_angle - 2*Math.PI) < Math.PI/9) // should not happen
        {
            return false;  // too close to boat movement angle
        }
        return tryMoveLilypadCore(blockPos, angle, world, distanceFactor, angleDelta, original);
    }

    public static boolean tryMoveLilypadByLanding(BlockPos blockPos, Entity entity, World world, double distanceFactor, double angleDelta, BlockState original)
    {
        double angle = Math.atan2(blockPos.getZ() + 0.5d - entity.getZ(), blockPos.getX() + 0.5d - entity.getX());
        //System.out.println("~~angle_to_block==" + angle);
        return tryMoveLilypadCore(blockPos, angle, world, distanceFactor, angleDelta, original);
    }

    private static boolean tryMoveLilypadCore(BlockPos blockPos, double angle, World world, double distanceFactor, double angleDelta, BlockState original)
    {
        distanceFactor += getDistanceFactorAdjustment(angle, angleDelta);
        System.out.println("~~for angle delta " + (angleDelta*180/Math.PI) + "  added " + getDistanceFactorAdjustment(angle, angleDelta) + "to distance");
        BlockPos targetPos = blockPos.add((int) Math.round(Math.cos(angle + angleDelta) * distanceFactor), 0, (int) Math.round(Math.sin(angle + angleDelta) * distanceFactor));
        BlockState target = world.getBlockState(targetPos);
        if (target.isAir() && original.getBlock() instanceof PlantBlock plant && plant.canPlaceAt(original, world, targetPos))
        {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
            world.setBlockState(targetPos, original, 3);
            return true;
        }
        if (target.isOf(Blocks.WATER) && original.getBlock() instanceof PlantBlock plant && plant.canPlaceAt(original, world, targetPos) && world.getBlockState(targetPos.up()).isAir())
        {
            world.setBlockState(targetPos, original, 3);
            BlockState maybeCandle = world.getBlockState(blockPos.up());
            if (maybeCandle.isIn(BlockTags.CANDLES) || maybeCandle.isIn(TORCHES) || maybeCandle.isIn(LANTERNS))
            {
                world.setBlockState(targetPos.up(), maybeCandle, 3);
                world.setBlockState(blockPos.up(), Blocks.AIR.getDefaultState(), 3);
            }
            world.setBlockState(blockPos, Blocks.WATER.getDefaultState(), 3);
            return true;
        }
        return false;
    }

    //////////////////////////////

    private static final TagKey<Block> LANTERNS = TagKey.of(Registry.BLOCK_KEY, new Identifier("c", "lanterns"));;
    private static final TagKey<Block> TORCHES = TagKey.of(Registry.BLOCK_KEY, new Identifier("c", "torches"));;

    /////////////////////////

    private static double getDistanceFactorAdjustment(double angle, double angleDelta)
    {
        if (isDiagonal(angle) == isDiagonal(angleDelta))
        {
            return 0;
        }
        else
        {
            return 0.42;  // from 1 to sq(2)
        }
    }
    private static boolean isDiagonal(double angle)
    {
        angle = angle / Math.PI * 180;
        return ((int) Math.abs(Math.round(angle / 45))) % 2 == 1;
    }
}
