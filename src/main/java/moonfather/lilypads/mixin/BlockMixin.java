package moonfather.lilypads.mixin;

import moonfather.lilypads.SwampMath;
import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LilyPadBlock.class)
public class BlockMixin {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z"), method = "onEntityCollision", cancellable = true)
	private void collision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo info) {
		BlockState original = world.getBlockState(pos);
		if (SwampMath.tryMoveLilypad(pos, entity, world, 1.0, 0.0, original)
			|| SwampMath.tryMoveLilypad(pos, entity, world, 1.4, +Math.PI/4, original) //45d
			|| SwampMath.tryMoveLilypad(pos, entity, world, 1.4, -Math.PI/4, original)
			|| SwampMath.tryMoveLilypad(pos, entity, world, 1.2, +Math.PI/2, original) //90d
			|| SwampMath.tryMoveLilypad(pos, entity, world, 1.2, -Math.PI/2, original)
			|| SwampMath.tryMoveLilypad(pos, entity, world, 1.9, 0.0, original))
		{
			info.cancel();
		}
	}
}