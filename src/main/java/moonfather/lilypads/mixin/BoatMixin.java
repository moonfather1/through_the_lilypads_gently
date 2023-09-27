package moonfather.lilypads.mixin;

import moonfather.lilypads.SwampMath;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LilyPadBlock.class, priority = 5)
public class BoatMixin
{
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z"), method = "onEntityCollision", cancellable = true)
	private void collision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo info)
	{
		BlockState original = world.getBlockState(pos);
		if (SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, 0.0, original)
			|| SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, +Math.PI/4, original) //45d
			|| SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, -Math.PI/4, original)
			|| SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, +Math.PI/2, original) //90d
			|| SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, -Math.PI/2, original)
			|| SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.9, 0.0, original))
		{
			info.cancel();
		}
	}
}