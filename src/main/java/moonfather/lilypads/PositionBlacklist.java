package moonfather.lilypads;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.LinkedList;

public class PositionBlacklist
{
    public static boolean isInBlacklist(Level world, BlockPos pos)
    {
        purgeOldItems(world);
        for (Location l : list)
        {
            if (l.world == world.hashCode() && pos.equals(l.position))
            {
                return true;
            }
        }
        return false;
    }

    public static void put(Level world, BlockPos pos)
    {
        purgeOldItems(world);
        list.add(new Location(pos, world.hashCode(), world.getGameTime()));
    }

    private static void purgeOldItems(Level world)
    {
        list.removeIf(l -> l.world == world.hashCode() && world.getGameTime() > l.age + 5 * 20);
    }

    private static final LinkedList<Location> list = new LinkedList<>();
    private record Location(BlockPos position, int world, long age)
    {
    }
}
