package moonfather.lilypads.block_sliding;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.LinkedList;

public class TaskScheduler
{
    public static void queueDelayedEvent(int ticksToWait, World level, BlockPos pos, Object extraData, LocationConsumer action)
    {
        ScheduledEvent event = new ScheduledEvent();
        event.action = action;
        event.tickCount = ticksToWait;
        event.pos = new BlockPos(pos);
        event.level = level;
        event.extra = extraData;
        list.add(event);
    }

    private static final LinkedList<ScheduledEvent> list = new LinkedList<>();
    private static final LinkedList<ScheduledContinuousEvent> list2 = new LinkedList<>();

    public static void queueContinuousEvent(int ticksToPerform, Object entity, Object extra, World level, ContinuousConsumer action)
    {
        ScheduledContinuousEvent event = new ScheduledContinuousEvent(ticksToPerform, entity, extra, level, action);
        list2.add(event);
    }

    @FunctionalInterface
    public interface ContinuousConsumer
    {
        void apply(int currentTick, int maxTicks, Object entity, Object extra);
    }
    @FunctionalInterface
    public interface LocationConsumer
    {
        void apply(World level, BlockPos pos, Object extra);
    }

    private static class ScheduledEvent
    {
        private int tickCount;
        private LocationConsumer action;
        private World level;
        private BlockPos pos;
        private Object extra;
    }


    private static class ScheduledContinuousEvent
    {
        private int currentTick;
        private final int maxTicks;
        private final Object entity, extra;
        private final ContinuousConsumer action;
        private final World level;

        public ScheduledContinuousEvent(int ticksToPerform, Object entity, Object extra, World world, ContinuousConsumer action)
        {
            this.currentTick = 0;
            this.maxTicks = ticksToPerform;
            this.entity = entity;
            this.extra = extra;
            this.action = action;
            this.level = world;
        }

        private void incrementTicks() { this.currentTick++;}
    }


    /////////////////////////////////////////////
//    @SubscribeEvent
//    public static void onServerTick(TickEvent.ServerTickEvent event)
//    {
//        if (event.phase.equals(TickEvent.Phase.END) && event.getServer().getTickCount() % 5  == 3)
    public static void onStartTick(ServerWorld sw)
    {
        Iterator<ScheduledEvent> i = list.iterator();
        while (i.hasNext())
        {
            ScheduledEvent e = i.next();
            if (! e.level.equals(sw)) { continue; }
            e.tickCount -= 1;
            if (e.tickCount <= 0)
            {
                e.action.apply(e.level, e.pos, e.extra);
                i.remove();
            }
        }
        ///////////
        Iterator<ScheduledContinuousEvent> i2 = list2.iterator();
        while (i2.hasNext())
        {
            ScheduledContinuousEvent e = i2.next();
            if (! sw.equals(e.level)) { continue; }
            e.currentTick += 1;
            e.action.apply(e.currentTick, e.maxTicks, e.entity, e.extra);
            if (e.currentTick == e.maxTicks)
            {
                i2.remove();
            }
        }
    }
}
