package moonfather.lilypads;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;

public class PositionBlacklist
{
    public static boolean isInBlacklist(World world, BlockPos pos)
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

    public static void put(World world, BlockPos pos)
    {
        purgeOldItems(world);
        list.add(new Location(pos, world.hashCode(), world.getTime()));
    }

    private static void purgeOldItems(World world)
    {
        list.removeIf(l -> l.world == world.hashCode() && world.getTime() > l.age + 5 * 20);
    }

    private static final LinkedList<Location> list = new LinkedList<>();
    private record Location(BlockPos position, int world, long age)
    {
    }
}
