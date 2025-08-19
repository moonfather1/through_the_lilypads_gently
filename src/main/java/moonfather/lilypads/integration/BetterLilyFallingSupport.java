package moonfather.lilypads.integration;

import moonfather.lilypads.Constants;
import moonfather.lilypads.SwampMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

public class BetterLilyFallingSupport
{
    public static void cancelDamage(Level world, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci)
    {
        if (entity.fallDistance <= 2.9 || world.isClientSide)
        {
            return;
        }
        if (! modCheckDone)
        {
            modPresent = ModList.get().isLoaded("amendments");
            modCheckDone = true;
            if (modPresent)
            {
                modBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("amendments", "water_lily_pad"));
            }
            else
            {
                modBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.withDefaultNamespace("air"));
            }
        }
        if (! modPresent)
        {
            return;
        }
        if (state.is(BlockTags.CANDLES) || state.is(Constants.Tags.TORCHES) || state.is(Constants.Tags.LANTERNS))
        {
            pos = pos.below();
            state = world.getBlockState(pos);
        }
        if (! state.is(modBlock.get().value()))
        {
            return;
        }
        if (entity instanceof Frog)
        {
            entity.fallDistance /= 5;
            return;
        }
        if (! (entity instanceof Player p && p.isCreative()))
        {
            double size = Math.max(entity.getBbWidth(), 1.0);
            BlockState original = world.getBlockState(pos);
            if (SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.1 * size, 0.0, original)
                    || SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.1 * size, +Math.PI / 4, original) //45d
                    || SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.1 * size, -Math.PI / 4, original)
                    || SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.1 * size, +Math.PI / 2, original) //90d
                    || SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.1 * size, -Math.PI / 2, original)
                    || SwampMath.tryMoveLilypadByLanding(pos, entity, world, 1.9 * size, 0.0, original))
            {
            }
        }

        // moved the lily pad. now about damage:
        entity.fallDistance = 0.1f;
        ci.cancel();
    }

    private static boolean modCheckDone = false, modPresent = false;
    private static Optional<Holder.Reference<Block>> modBlock = null;
}
