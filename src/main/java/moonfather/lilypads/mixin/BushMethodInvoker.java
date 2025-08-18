package moonfather.lilypads.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BushBlock.class)
public interface BushMethodInvoker
{
    @Invoker("mayPlaceOn")
    public boolean checkMayPlaceOn(BlockState p_51042_, BlockGetter p_51043_, BlockPos p_51044_);
}
