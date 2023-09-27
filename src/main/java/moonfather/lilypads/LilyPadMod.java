package moonfather.lilypads;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

public class LilyPadMod implements ModInitializer {
	@Override
	public void onInitialize() {
		// nothing
		ServerLivingEntityEvents.ALLOW_DAMAGE.register(
				(entity, source, amount) ->
				{
					if (source.isFromFalling())
					{
						return false;
					}
					return true;
				} );
	}
}