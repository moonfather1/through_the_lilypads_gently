package moonfather.lilypads.mixin.sliding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FrogspawnBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FrogspawnBlock.class)
public interface FrogspawnAccessor
{
    @Invoker("mayPlaceOn")
    boolean invokeMayPlaceOn(BlockGetter level, BlockPos pos);
}
