package moonfather.lilypads.block_sliding;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;

public class SlidingManager
{
    public static void simpleRelocation(Level world, BlockPos blockPos, BlockPos targetPos, BlockState original)
    {
        PreparedParameters prepped = PreparedParameters.create(15, blockPos, targetPos);
        TaskScheduler.queueDelayedEvent(prepped.tickCount, world, targetPos, null, (w, pos, nothing) -> w.setBlock(pos, original, 3));
        FallingBlockEntity faller = NonFallingBlockEntity.fromBlock(world, blockPos, original, prepped.vx, prepped.vz);
        TaskScheduler.queueContinuousEvent(prepped.tickCount, faller, targetPos, world, SlidingManager::slidingBlockTickAction);
    }

    public static void jointRelocationWithBlockAbove(Level world, BlockPos blockPos, BlockPos targetPos, BlockState original, BlockState candle)
    {
        PreparedParameters prepped = PreparedParameters.create(25, blockPos, targetPos);
        TaskScheduler.queueDelayedEvent(prepped.tickCount, world, targetPos, null, (w, pos, nothing) ->
        {
            w.setBlock(pos, original, 3);
            w.setBlock(pos.above(), candle, 3);
        });
        int lightLevel = world.getBrightness(LightLayer.BLOCK, blockPos.above());
        world.setBlockAndUpdate(targetPos.above(), Blocks.LIGHT.defaultBlockState().setValue(BlockStateProperties.LEVEL, lightLevel));
        FallingBlockEntity faller2 = NonFallingBlockEntity.fromBlock(world, blockPos.above(), candle, prepped.vx, prepped.vz);
        TaskScheduler.queueContinuousEvent(prepped.tickCount+1, faller2, targetPos.above(), world, SlidingManager::slidingBlockTickAction);
        FallingBlockEntity faller = NonFallingBlockEntity.fromBlock(world, blockPos, original, prepped.vx, prepped.vz);
        TaskScheduler.queueContinuousEvent(prepped.tickCount, faller, targetPos, world, SlidingManager::slidingBlockTickAction);
    }

    public static void relocationWithBlockEntitySupport(Level world, BlockPos blockPos, BlockPos targetPos, BlockState original, boolean willMoveBlockAbove, BlockState maybeCandle, ValueInput nbt)
    {
        PreparedParameters prepped = PreparedParameters.create(25, blockPos, targetPos);
        if (willMoveBlockAbove)
        {
            int lightLevel = world.getBrightness(LightLayer.BLOCK, blockPos.above());
            world.setBlockAndUpdate(targetPos.above(), Blocks.LIGHT.defaultBlockState().setValue(BlockStateProperties.LEVEL, lightLevel));
            FallingBlockEntity faller = NonFallingBlockEntity.fromBlock(world, blockPos.above(), maybeCandle, prepped.vx, prepped.vz, Blocks.TRIPWIRE.defaultBlockState(), 0);
            TaskScheduler.queueContinuousEvent(prepped.tickCount+1, faller, targetPos, world, SlidingManager::slidingBlockTickAction);
            TaskScheduler.queueDelayedEvent(prepped.tickCount, world, targetPos, null, (w, pos, data) -> w.setBlock(pos.above(), maybeCandle, 3) );
        }
        TaskScheduler.queueDelayedEvent(prepped.tickCount, world, targetPos, nbt, (w, pos, data) ->
        {
            w.setBlock(pos, original, 3);
            BlockEntity be2 = w.getBlockEntity(pos);
            if (be2 != null && data != null)
            {
                be2.loadWithComponents((ValueInput) data);
            }
        });
        int deltaY = 0; // move amendments block up ?
        BlockState blockToSlide = original;
        if (checkAmendments(original, world.registryAccess()))
        {
            blockToSlide = Blocks.LILY_PAD.defaultBlockState();
            deltaY = 1;
        }
        FallingBlockEntity faller = NonFallingBlockEntity.fromBlock(world, blockPos, blockToSlide, prepped.vx, prepped.vz, Blocks.WATER.defaultBlockState(), deltaY);  // puts water in case of enchanting vanilla mod anyway but we'll force it.
        TaskScheduler.queueContinuousEvent(prepped.tickCount, faller, targetPos, world, SlidingManager::slidingBlockTickAction);
        TaskScheduler.queueDelayedEvent(2, world, blockPos.above(), null, (w, pos, nothing) -> w.setBlock(pos, Blocks.AIR.defaultBlockState(), 3));
    }

    /////////////////////////////////////////////////////////

    private record PreparedParameters(int tickCount, double vx, double vz)
    {
        public static PreparedParameters create(int baseDuration, BlockPos blockPos, BlockPos targetPos)
        {
            int tickCount = (targetPos.getX() == blockPos.getX() || targetPos.getZ() == blockPos.getZ()) ? baseDuration : Math.round(baseDuration * 1.45f);
            return new PreparedParameters(
                    tickCount,
                    1.25 * (targetPos.getX() - blockPos.getX()) / (double) tickCount,
                    1.25 * (targetPos.getZ() - blockPos.getZ()) / (double) tickCount
            );
        }
    }

    private static void slidingBlockTickAction(int currentTick, int maxTicks, Object entity, Object extra)
    {
        FallingBlockEntity faller = (FallingBlockEntity) entity;
        if (currentTick == maxTicks)
        {
            faller.discard();
            return;
        }
        if (faller.getDeltaMovement().x > 0.05)
        {
            faller.setDeltaMovement(faller.getDeltaMovement().multiply(0.95, 0.95, 0.95));
        }
        if (currentTick > maxTicks / 2)
        {
            preventOvershoot(faller, (BlockPos) extra);
        }
    }

    private static void preventOvershoot(Entity entity, BlockPos targetPos)
    {
        double target = targetPos.getX() + 0.5;
        if (entity.getDeltaMovement().x > 0 && entity.getX() > target || entity.getDeltaMovement().x < 0 && entity.getX() < target)
        {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0, 0, 1));
        }
        target = targetPos.getZ() + 0.5;
        if (entity.getDeltaMovement().z > 0 && entity.getZ() > target || entity.getDeltaMovement().z < 0 && entity.getZ() < target)
        {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0, 0));
        }
    }

    /////////////////////////////////////////

    private static boolean checkAmendments(BlockState original, RegistryAccess registryManager)
    {
        if (! amendmentsBugCheckDone)
        {
            amendmentsBugCheckCache = registryManager.getOrThrow(Registries.BLOCK).value().getValue(Identifier.fromNamespaceAndPath("amendments", "water_lily_pad"));
            amendmentsBugCheckDone = true;
        }
        if (amendmentsBugCheckCache != null && original.is(amendmentsBugCheckCache))
        {
            return true;   // so sliding block is invisible.  not really but it's 1 block lower so you don't see it slide underwater.  hence this waste of time.
        }
        return false;
    }
    private static boolean amendmentsBugCheckDone = false;
    private static Block amendmentsBugCheckCache = null;
}
