package moonfather.lilypads.mixin_message_removal;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ConfigPlugin implements IMixinConfigPlugin
{
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (mixinClassName.equals("moonfather.lilypads.mixin.integration.SwampierSwampsBoatMixin"))
        {
            return FabricLoader.getInstance().isModLoaded("swampier_swamps");
        }
        if (mixinClassName.equals("moonfather.lilypads.mixin.integration.BetterLilyNewFallingMixin"))
        {
            return FabricLoader.getInstance().isModLoaded("amendments");
        }
        if (mixinClassName.startsWith("moonfather.lilypads.mixin.integration.Good"))
        {
            return FabricLoader.getInstance().isModLoaded("goodending");
        }
        return true;
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onLoad(String mixinPackage) { }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
