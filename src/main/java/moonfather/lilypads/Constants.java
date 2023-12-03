package moonfather.lilypads;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class Constants
{
    public static final String MODID = "through_the_lilypads_gently";

    public static class Tags
    {
        public static final TagKey<Block> LANTERNS = TagKey.create(Registries.BLOCK, new ResourceLocation("c", "lanterns"));;
        public static final TagKey<Block> TORCHES = TagKey.create(Registries.BLOCK, new ResourceLocation("c", "torches"));;
    }
}
