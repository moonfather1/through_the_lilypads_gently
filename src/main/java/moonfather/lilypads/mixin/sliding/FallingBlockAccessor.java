package moonfather.lilypads.mixin.sliding;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FallingBlockEntity.class)
public interface FallingBlockAccessor
{
    @Accessor("blockState")
    void lilypads$setBlockState(BlockState state);
}
