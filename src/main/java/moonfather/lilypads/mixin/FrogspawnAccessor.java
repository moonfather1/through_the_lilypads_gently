package moonfather.lilypads.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FrogspawnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FrogspawnBlock.class)
public interface FrogspawnAccessor
{
    @Invoker("canPlaceAt")
    boolean invokeCanPlaceAt(BlockState state, WorldView world, BlockPos pos);
}
