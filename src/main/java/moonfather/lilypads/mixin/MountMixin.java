package moonfather.lilypads.mixin;

import com.mojang.logging.LogUtils;
import moonfather.lilypads.SwampMath;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WaterlilyBlock.class, priority = 5)
public class MountMixin
{
    @Inject(at = @At(value = "TAIL"), method = "entityInside")
    private void horseCollision(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier something, boolean intersects, CallbackInfo info)
    {
        if (world.isClientSide())
        {
            return;
        }
        if (pos.getX() == lastX && pos.getZ() == lastZ)
        {
            return;
        }
        lastX = pos.getX();  lastZ = pos.getZ();
        boolean bigEnough = entity.getBoundingBox().getXsize() >= 1.1 && entity.getBoundingBox().getZsize() >= 0.5 || entity.getBoundingBox().getXsize() >= 0.5 && entity.getBoundingBox().getZsize() >= 1.1;
        // not the thing is - cows, sheep and pigs are all the same size (0.89999) i wanted cows but not the other two...  shelved.
        if (bigEnough || entity.getType().is(EntityTypeTags.CAN_WEAR_NAUTILUS_ARMOR))
        {
            BlockState original = world.getBlockState(pos);
            if (SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, 0.0, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, +Math.PI / 4, original) //45d
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.0, -Math.PI / 4, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, +Math.PI / 2, original) //90d
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.1, -Math.PI / 2, original)
                    || SwampMath.tryMoveLilypadByBoat(pos, entity, world, 1.9, 0.0, original))
            {
            }
        }
    }



    private static final Logger LOGGER = LogUtils.getLogger();
    @Unique
    private static int lastX = 0, lastZ = 0;
}
