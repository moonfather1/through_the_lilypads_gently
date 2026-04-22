package moonfather.lilypads;

import com.mojang.logging.LogUtils;
import moonfather.lilypads.block_sliding.SlidingManager;
import moonfather.lilypads.mixin.PlantBlockInvoker;
import moonfather.lilypads.mixin.sliding.FrogspawnAccessor;
import moonfather.lilypads.other.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrogspawnBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.slf4j.Logger;

public class SwampMath
{
    public static boolean tryMoveLilypadByBoat(BlockPos blockPos, Entity entity, Level world, double distanceFactor, double angleDelta, BlockState original)
    {
        double angle = Math.atan2(blockPos.getZ() + 0.5d - entity.getZ(), blockPos.getX() + 0.5d - entity.getX());
        double movement_angle = (entity.getYRot() + 90.0) * Math.PI / 180.0; //we took the other axis as base. doesn't matter.
        //System.out.println("~~boat_angle==" + movement_angle+ "  angle_to_block==" + angle);
        if (Math.abs(angle + angleDelta - movement_angle) < Math.PI/9  // 20 deg
            || Math.abs(angle + angleDelta - movement_angle + 2*Math.PI) < Math.PI/9  // 20 deg over boundary
            || Math.abs(angle + angleDelta - movement_angle - 2*Math.PI) < Math.PI/9) // should not happen
        {
            return false;  // too close to boat movement angle
        }
        return tryMoveLilypadCore(blockPos, angle, world, distanceFactor, angleDelta, original);
    }

    public static boolean tryMoveLilypadByLanding(BlockPos blockPos, Entity entity, Level world, double distanceFactor, double angleDelta, BlockState original)
    {
        double angle = Math.atan2(blockPos.getZ() + 0.5d - entity.getZ(), blockPos.getX() + 0.5d - entity.getX());
        //System.out.println("~~angle_to_block==" + angle);
        return tryMoveLilypadCore(blockPos, angle, world, distanceFactor, angleDelta, original);
    }

    private static boolean tryMoveLilypadCore(BlockPos blockPos, double angle, Level world, double distanceFactor, double angleDelta, BlockState original)
    {
        distanceFactor += getDistanceFactorAdjustment(angle, angleDelta);
        BlockPos targetPos = blockPos.offset((int) Math.round(Math.cos(angle + angleDelta) * distanceFactor), 0, (int) Math.round(Math.sin(angle + angleDelta) * distanceFactor));
        BlockState target = world.getBlockState(targetPos);
        if (PositionBlacklist.isInBlacklist(world, targetPos) || ! SwampMath.canSetBlock(world, targetPos)) { return false; }
        PositionBlacklist.put(world, targetPos);
        if (
                target.isAir() && original.getBlock() instanceof VegetationBlock plant && ((PlantBlockInvoker) plant).checkMayPlaceOn(original, world, targetPos.below())
                ||         // or frogspawn - same as vanilla lily pads and derivatives
                target.isAir() && original.getBlock() instanceof FrogspawnBlock fs && ((FrogspawnAccessor)fs).invokeMayPlaceOn(world, targetPos.below())        )
        {
            // vanilla lily pads and derivatives
            spawnParticles((ServerLevel) world, blockPos, angle + angleDelta);
            if (ConfigManager.getConfig().slidingEnabled())
            {
                SlidingManager.simpleRelocation(world, blockPos, targetPos, original);
            }
            else
            {
                world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                world.setBlock(targetPos, original, 3);
            }
            return true;
        }
        if (target.is(Blocks.WATER) && original.getBlock() instanceof VegetationBlock plant && ((PlantBlockInvoker) plant).checkMayPlaceOn(original, world, targetPos.below()) && world.getBlockState(targetPos.above()).isAir())
        {
            // good ending support - big lily pads
            spawnParticles((ServerLevel) world, blockPos, angle + angleDelta);
            BlockState maybeCandle = world.getBlockState(blockPos.above());
            if (ConfigManager.getConfig().slidingEnabled())
            {
                if (maybeCandle.is(BlockTags.CANDLES) || maybeCandle.is(TORCHES) || maybeCandle.is(LANTERNS))
                {
                    SlidingManager.jointRelocationWithBlockAbove(world, blockPos, targetPos, original, maybeCandle);
                }
                else
                {
                    SlidingManager.simpleRelocation(world, blockPos, targetPos, original);
                }
            }
            else
            {
                world.setBlock(targetPos, original, 3);
                if (maybeCandle.is(BlockTags.CANDLES) || maybeCandle.is(TORCHES) || maybeCandle.is(LANTERNS))
                {
                    world.setBlock(targetPos.above(), maybeCandle, 3);
                    world.setBlock(blockPos.above(), Blocks.AIR.defaultBlockState(), 3);
                }
                world.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
            }
            return true;
        }
        BlockPos above = targetPos.above();
        if (target.is(Blocks.WATER) && original.getBlock() instanceof VegetationBlock plant && ((PlantBlockInvoker) plant).checkMayPlaceOn(original, world, targetPos) && world.getBlockState(above).isAir())
        {
            // better lily pads support - messy lilypads
            spawnParticles((ServerLevel) world, blockPos.above(), angle + angleDelta);
            BlockState maybeCandle = world.getBlockState(blockPos.above());
            BlockEntity be1 = world.getBlockEntity(blockPos);
            try (ProblemReporter.ScopedCollector stupidCollector = new ProblemReporter.ScopedCollector(() -> "some_path", LOGGER))
            {
                HolderLookup.Provider stupidLookup = world.registryAccess();
                TagValueOutput output = TagValueOutput.createWithContext(stupidCollector, stupidLookup);
                if (be1 != null)
                {
                    be1.saveWithId(output);
                }
                ValueInput input = TagValueInput.create(stupidCollector, stupidLookup, output.buildResult());
                if (ConfigManager.getConfig().slidingEnabled())
                {
                    boolean willMoveBlockAbove = maybeCandle.is(BlockTags.CANDLES) || maybeCandle.is(TORCHES) || maybeCandle.is(LANTERNS);
                    SlidingManager.relocationWithBlockEntitySupport(world, blockPos, targetPos, original, willMoveBlockAbove, maybeCandle, input);
                }
                else
                {
                    if (maybeCandle.is(BlockTags.CANDLES) || maybeCandle.is(TORCHES) || maybeCandle.is(LANTERNS))
                    {
                        world.setBlock(blockPos.above(), Blocks.TRIPWIRE.defaultBlockState(), 0);
                    }
                    world.setBlock(targetPos, original, 3);
                    BlockEntity be2 = world.getBlockEntity(targetPos);
                    if (be2 != null)
                    {
                        be2.loadWithComponents(input);
                    }
                    world.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                    if (maybeCandle.is(BlockTags.CANDLES) || maybeCandle.is(TORCHES) || maybeCandle.is(LANTERNS))
                    {
                        world.setBlock(blockPos.above(), Blocks.AIR.defaultBlockState(), 3);
                        world.setBlock(targetPos.above(), maybeCandle, 3);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean canSetBlock(Level world, BlockPos targetPos)
    {
        return world instanceof ServerLevel sw && sw.getWorldBorder().isWithinBounds(targetPos);
        //... or i could have just...
        //return true; // old canSetBlock(BlockPos) is no more.   there is world.canPlayerModifyAt() but i see no real point.
    }

    private static final Logger LOGGER = LogUtils.getLogger();

    //////////////////////////////

    private static final TagKey<Block> LANTERNS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("minecraft", "lanterns"));
    private static final TagKey<Block> TORCHES = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "torches"));

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

    private static void spawnParticles(ServerLevel world, BlockPos blockPos, double angle)
    {
        world.sendParticles(ParticleTypes.FISHING, blockPos.getX() + 0.25, blockPos.getY() + 0.01, blockPos.getZ() + 0.25d, 0, Math.cos(angle) * 0.31, 0.00, Math.sin(angle) * 0.31, 0.25d);
        world.sendParticles(ParticleTypes.FISHING, blockPos.getX() + 0.25, blockPos.getY() + 0.01, blockPos.getZ() + 0.75d, 0, Math.cos(angle) * 0.31, 0.00, Math.sin(angle) * 0.31, 0.25d);
        world.sendParticles(ParticleTypes.FISHING, blockPos.getX() + 0.75, blockPos.getY() + 0.01, blockPos.getZ() + 0.25d, 0, Math.cos(angle) * 0.31, 0.00, Math.sin(angle) * 0.31, 0.25d);
        world.sendParticles(ParticleTypes.FISHING, blockPos.getX() + 0.75, blockPos.getY() + 0.01, blockPos.getZ() + 0.75d, 0, Math.cos(angle) * 0.31, 0.00, Math.sin(angle) * 0.31, 0.25d);
        world.sendParticles(ParticleTypes.FISHING, blockPos.getX() + 0.50, blockPos.getY() + 0.01, blockPos.getZ() + 0.50d, 0, Math.cos(angle) * 0.31, 0.00, Math.sin(angle) * 0.31, 0.25d);
    }
}
