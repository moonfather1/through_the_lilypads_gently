package moonfather.lilypads;

import moonfather.lilypads.block_sliding.TaskScheduler;
import moonfather.lilypads.other.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class LilyPadMod implements ModInitializer 
{
	@Override
	public void onInitialize() 
	{
		ServerTickEvents.START_WORLD_TICK.register(TaskScheduler::onStartTick);
		ServerLifecycleEvents.SERVER_STARTED.register(ConfigManager::load);
	}
}