package moonfather.lilypads;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
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
        BlockPos targetPos = blockPos.add((int) Math.round(Math.cos(angle + angleDelta) * distanceFactor), 0, (int) Math.round(Math.sin(angle + angleDelta) * distanceFactor));
        BlockState target = world.getBlockState(targetPos);
        if (PositionBlacklist.isInBlacklist(world, targetPos) || ! world.canSetBlock(targetPos)) { return false; }
        PositionBlacklist.put(world, targetPos);
        if (target.isAir() && original.getBlock() instanceof PlantBlock plant && plant.canPlaceAt(original, world, targetPos))
        {
            spawnParticles((ServerWorld) world, blockPos, angle + angleDelta);
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
            world.setBlockState(targetPos, original, 3);
            return true;
        }
        if (target.isOf(Blocks.WATER) && original.getBlock() instanceof PlantBlock plant && plant.canPlaceAt(original, world, targetPos) && world.getBlockState(targetPos.up()).isAir())
        {
            spawnParticles((ServerWorld) world, blockPos, angle + angleDelta);
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
        BlockPos above = targetPos.up();
        if (target.isOf(Blocks.WATER) && original.getBlock() instanceof PlantBlock plant && plant.canPlaceAt(original, world, above) && world.getBlockState(above).isAir())
        {
            spawnParticles((ServerWorld) world, blockPos.up(), angle + angleDelta);
            BlockState maybeCandle = world.getBlockState(blockPos.up());
            BlockEntity be1 = world.getBlockEntity(blockPos);
            NbtCompound nbt = null;
            if (be1 != null)
            {
                nbt = be1.createNbtWithId();
            }
            if (maybeCandle.isIn(BlockTags.CANDLES) || maybeCandle.isIn(TORCHES) || maybeCandle.isIn(LANTERNS))
            {
                world.setBlockState(blockPos.up(), Blocks.TRIPWIRE.getDefaultState(), 0);
            }
            world.setBlockState(targetPos, original, 3);
            BlockEntity be2 = world.getBlockEntity(targetPos);
            if (be2 != null && nbt != null)
            {
                be2.readNbt(nbt);
            }
            world.setBlockState(blockPos, Blocks.WATER.getDefaultState(), 3);
            if (maybeCandle.isIn(BlockTags.CANDLES) || maybeCandle.isIn(TORCHES) || maybeCandle.isIn(LANTERNS))
            {
                world.setBlockState(blockPos.up(), Blocks.AIR.getDefaultState(), 3);
                world.setBlockState(targetPos.up(), maybeCandle, 3);
            }
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
            return 0; // axis aligned
        }
        else
        {
            return 0.42;  // from 1 to sq(2)
        }
    }
    private static boolean isDiagonal(double angle)
    {
        angle = angle / Math.PI * 180;
        return ((int) Math.abs(Math.round(angle / 45))) % 2 == 1;  // diagonal or axis aligned
    }

    ////////////////////////////////////////////////////////////////////

    private static void spawnParticles(ServerWorld world, BlockPos blockPos, double angle)
    {
        world.spawnParticles(ParticleTypes.FISHING, blockPos.getX() + 0.25, blockPos.getY() + 0.01, blockPos.getZ() + 0.25d, 0, Math.cos(angle) * 0.31, 0.00, Math.sin(angle) * 0.31, 0.25d);
        world.spawnParticles(ParticleTypes.FISHING, blockPos.getX() + 0.25, blockPos.getY() + 0.01, blockPos.getZ() + 0.75d, 0, Math.cos(angle) * 0.31, 0.00, Math.sin(angle) * 0.31, 0.25d);
        world.spawnParticles(ParticleTypes.FISHING, blockPos.getX() + 0.75, blockPos.getY() + 0.01, blockPos.getZ() + 0.25d, 0, Math.cos(angle) * 0.31, 0.00, Math.sin(angle) * 0.31, 0.25d);
        world.spawnParticles(ParticleTypes.FISHING, blockPos.getX() + 0.75, blockPos.getY() + 0.01, blockPos.getZ() + 0.75d, 0, Math.cos(angle) * 0.31, 0.00, Math.sin(angle) * 0.31, 0.25d);
        world.spawnParticles(ParticleTypes.FISHING, blockPos.getX() + 0.50, blockPos.getY() + 0.01, blockPos.getZ() + 0.50d, 0, Math.cos(angle) * 0.31, 0.00, Math.sin(angle) * 0.31, 0.25d);
    }
}
