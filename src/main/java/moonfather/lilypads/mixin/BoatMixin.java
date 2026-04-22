package moonfather.lilypads.mixin;

import moonfather.lilypads.SwampMath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LilyPadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LilyPadBlock.class, priority = 5)
public class BoatMixin
{
	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/world/level/Level.destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"), method = "entityInside", cancellable = true)
	private void collision(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier something, boolean intersects, CallbackInfo info)
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