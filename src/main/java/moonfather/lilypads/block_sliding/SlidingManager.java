package moonfather.lilypads.block_sliding;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SlidingManager
{
    public static void simpleRelocation(World world, BlockPos blockPos, BlockPos targetPos, BlockState original)
    {
        PreparedParameters prepped = PreparedParameters.create(20, blockPos, targetPos);
        TaskScheduler.queueDelayedEvent(prepped.tickCount, world, targetPos, null, (w, pos, nothing) -> w.setBlockState(pos, original, 3));
        FallingBlockEntity faller = NonFallingBlockEntity.fromBlock(world, blockPos, original, prepped.vx, prepped.vz);
        TaskScheduler.queueContinuousEvent(prepped.tickCount, faller, targetPos, world, SlidingManager::slidingBlockTickAction);
    }

    public static void jointRelocationWithBlockAbove(World world, BlockPos blockPos, BlockPos targetPos, BlockState original, BlockState candle)
    {
        PreparedParameters prepped = PreparedParameters.create(25, blockPos, targetPos);
        TaskScheduler.queueDelayedEvent(prepped.tickCount, world, targetPos, null, (w, pos, nothing) ->
            {
                w.setBlockState(pos, original, 3);
                w.setBlockState(pos.up(), candle, 3);
            });
        int lightLevel = world.getLightLevel(LightType.BLOCK, blockPos.up());
        world.setBlockState(targetPos.up(), Blocks.LIGHT.getDefaultState().with(Properties.LEVEL_15, lightLevel));
        FallingBlockEntity faller2 = NonFallingBlockEntity.fromBlock(world, blockPos.up(), candle, prepped.vx, prepped.vz);
        TaskScheduler.queueContinuousEvent(prepped.tickCount+1, faller2, targetPos.up(), world, SlidingManager::slidingBlockTickAction);
        FallingBlockEntity faller = NonFallingBlockEntity.fromBlock(world, blockPos, original, prepped.vx, prepped.vz);
        TaskScheduler.queueContinuousEvent(prepped.tickCount, faller, targetPos, world, SlidingManager::slidingBlockTickAction);
    }

    public static void relocationWithBlockEntitySupport(World world, BlockPos blockPos, BlockPos targetPos, BlockState original, boolean willMoveBlockAbove, BlockState maybeCandle, NbtCompound nbt)
    {
        PreparedParameters prepped = PreparedParameters.create(25, blockPos, targetPos);
        if (willMoveBlockAbove)
        {
            int lightLevel = world.getLightLevel(LightType.BLOCK, blockPos.up());
            world.setBlockState(targetPos.up(), Blocks.LIGHT.getDefaultState().with(Properties.LEVEL_15, lightLevel));
            FallingBlockEntity faller = NonFallingBlockEntity.fromBlock(world, blockPos.up(), maybeCandle, prepped.vx, prepped.vz, Blocks.TRIPWIRE.getDefaultState(), 0);
            TaskScheduler.queueContinuousEvent(prepped.tickCount+1, faller, targetPos, world, SlidingManager::slidingBlockTickAction);
            TaskScheduler.queueDelayedEvent(prepped.tickCount, world, targetPos, null, (w, pos, data) -> w.setBlockState(pos.up(), maybeCandle, 3) );
        }
        TaskScheduler.queueDelayedEvent(prepped.tickCount, world, targetPos, nbt, (w, pos, data) ->
        {
            w.setBlockState(pos, original, 3);
            BlockEntity be2 = w.getBlockEntity(pos);
            if (be2 != null && data != null)
            {
                be2.read((NbtCompound) data, w.getRegistryManager());
            }
        });
        int deltaY = 0; // move amendments block up ?
        BlockState blockToSlide = original;
        if (checkAmendments(original, world.getRegistryManager()))
        {
            blockToSlide = Blocks.LILY_PAD.getDefaultState();
            deltaY = 1;
        }
        FallingBlockEntity faller = NonFallingBlockEntity.fromBlock(world, blockPos, blockToSlide, prepped.vx, prepped.vz, Blocks.WATER.getDefaultState(), deltaY);  // puts water in case of enchanting vanilla mod anyway but we'll force it.
        TaskScheduler.queueContinuousEvent(prepped.tickCount, faller, targetPos, world, SlidingManager::slidingBlockTickAction);
        TaskScheduler.queueDelayedEvent(2, world, blockPos.up(), null, (w, pos, nothing) -> w.setBlockState(pos, Blocks.AIR.getDefaultState(), 3));
    }

    /////////////////////////////

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
        if (faller.getVelocity().x > 0.05)
        {
            faller.setVelocity(faller.getVelocity().multiply(0.95));
        }
        if (currentTick > maxTicks / 2)
        {
            preventOvershoot(faller, (BlockPos) extra);
        }
    }



    private static void preventOvershoot(Entity entity, BlockPos targetPos)
    {
        double target = targetPos.getX() + 0.5;
        if (entity.getVelocity().x > 0 && entity.getPos().x > target || entity.getVelocity().x < 0 && entity.getPos().x < target)
        {
            entity.setVelocity(entity.getVelocity().multiply(0, 0, 1));
        }
        target = targetPos.getZ() + 0.5;
        if (entity.getVelocity().z > 0 && entity.getPos().z > target || entity.getVelocity().z < 0 && entity.getPos().z < target)
        {
            entity.setVelocity(entity.getVelocity().multiply(1, 0, 0));
        }
    }

    /////////////////////////////////////////

    private static boolean checkAmendments(BlockState original, DynamicRegistryManager registryManager)
    {
        if (! amendmentsBugCheckDone)
        {
            amendmentsBugCheckCache = registryManager.get(RegistryKeys.BLOCK).get(Identifier.of("amendments", "water_lily_pad"));
            amendmentsBugCheckDone = true;
        }
        if (original.isOf(amendmentsBugCheckCache))
        {
            return true;   // so sliding block is invisible.  not really but it's 1 block lower so you don't see it slide underwater.  hence this waste of time.
        }
        return false;
    }
    private static boolean amendmentsBugCheckDone = false;
    private static Block amendmentsBugCheckCache = null;
}
