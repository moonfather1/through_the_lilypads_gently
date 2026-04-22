package moonfather.lilypads.other;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConfigManager
{
    public static class ConfigInstance
    {
        private boolean slidingValue;
        public boolean slidingEnabled() { return this.slidingValue; }
    }

    //*************************//

    public static ConfigInstance getConfig()
    {
        return instance;
    }
    private static ConfigInstance instance;

    //*************************//

    public static void load(ServerStartingEvent event)
    {
        Path configPath = Path.of(FMLConfig. defaultConfigPath(), "..", "config", "through-the-lilypads-gently.json");

        ConfigInner loaded = null;
        if (configPath.toFile().exists())
        {
            try
            {
                Gson gson = new Gson();
                loaded = gson.fromJson(Files.readString(configPath), ConfigInner.class);
            }
            catch (IOException ignored)
            {
            }
        }
        if (loaded == null)
        {
            loaded = new ConfigInner();
        }
        instance = new ConfigInstance();
        instance.slidingValue = loaded.sliding_enabled;
        if (! configPath.toFile().exists())
        {
            try
            {
                Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
                String text = gson.toJson(loaded, ConfigInner.class);
                Files.writeString(configPath, text, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            }
            catch (IOException ignored)
            {
            }
        }
    }

    ////////////////////////////////////////////////////////////////

    private static class ConfigInner
    {
        public String sliding_comment = "Should blocks visually slide on top of water?";
        public boolean sliding_enabled = true;
    }
}
