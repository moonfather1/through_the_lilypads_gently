package moonfather.lilypads.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlantBlock.class)
public interface PlantBlockAccessor {
    @Invoker("canPlaceAt")
    public boolean invokeCanPlaceAt(BlockState state, WorldView world, BlockPos pos);
}
