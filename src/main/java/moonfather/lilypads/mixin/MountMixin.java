package moonfather.lilypads.mixin;

import moonfather.lilypads.SwampMath;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LilyPadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LilyPadBlock.class, priority = 5)
public class MountMixin
{
    @Inject(at = @At(value = "TAIL"), method = "entityInside", cancellable = true)
    private void horseCollision(BlockState blockState, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, boolean intersects, CallbackInfo ci)
    {
        if (world.isClientSide())
        {
            return;
        }
        if (pos.getX() == lastX && pos.getZ() == lastZ && entity.getType().equals(lastType))
        {
            return;
        }
        lastX = pos.getX();    lastZ = pos.getZ();    lastType = entity.getType();
        BlockState original = world.getBlockState(pos);
        if ((entity.hasControllingPassenger() || entity.is(EntityTypeTags.CAN_WEAR_NAUTILUS_ARMOR))
                && world instanceof ServerLevel && original.getBlock() instanceof LilyPadBlock)
        {
            if (SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, 0.0, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, +Math.PI/4, original) //45d
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, -Math.PI/4, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, +Math.PI/2, original) //90d
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, -Math.PI/2, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.9, 0.0, original)) //todo: move to utility class
            {
            }
        }
    }

    @Unique
    private static int lastX = 0, lastZ = 0;
    @Unique
    private static EntityType<?> lastType = null;
}
