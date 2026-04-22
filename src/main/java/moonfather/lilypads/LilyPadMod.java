package moonfather.lilypads;

import com.mojang.logging.LogUtils;
import moonfather.lilypads.block_sliding.TaskScheduler;
import moonfather.lilypads.other.ConfigManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Constants.MODID)
public class LilyPadMod
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public LilyPadMod(IEventBus modEventBus)
    {
        NeoForge.EVENT_BUS.addListener(TaskScheduler::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ConfigManager::load);
    }
}
