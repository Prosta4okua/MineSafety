package net.doubledoordev.minesafety;

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.doubledoordev.d3core.util.ID3Mod;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import java.util.List;
import java.util.Random;

/**
 * @author Dries007
 */
@Mod(modid = MineSafety.MODID)
public class MineSafety implements ID3Mod
{
    public static final String          MODID        = "MineSafety";
    private             Random          random       = new Random();
    private             DamageSource    damageSource = new DamageSource("helmet").setDifficultyScaled();
    private             Configuration   configuration;
    private int yLevel;
    private float chance;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(this);

        configuration = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END) return;
        if (event.player.posY >= yLevel) return;
        if (event.player.getCurrentArmor(3) != null && event.player.getCurrentArmor(3).getItem() instanceof ItemArmor && ((ItemArmor) event.player.getCurrentArmor(3).getItem()).armorType == 0) return;
        if (event.player.worldObj.canBlockSeeTheSky((int) event.player.posX, (int) event.player.posY, (int) event.player.posZ)) return;
        if (random.nextFloat() > chance) return;
        if (event.player.attackEntityFrom(damageSource, 1.0f + 0.2f * random.nextFloat())) event.player.addChatComponentMessage(new ChatComponentText("You should wear a helmet..."));
    }

    @Override
    public void syncConfig()
    {
        configuration.setCategoryLanguageKey(MODID, "d3.mineSafety.config.mineSafety");
        //public int getInt(String name, String category, int defaultValue, int minValue, int maxValue, String comment, String langKey)
        yLevel = configuration.getInt("yLevel", MODID, 50, 0, 256, "The Y level at which you should wear a helmet.", "d3.mineSafety.config.yLevel");
        chance = configuration.getFloat("chance", MODID, 0.03f, 0.0f, 1.0f, "The chance you get damaged this tick, in percent.", "d3.mineSafety.config.chance");

        if (configuration.hasChanged()) configuration.save();
    }

    @Override
    public void addConfigElements(List<IConfigElement> configElements)
    {
        configElements.add(new ConfigElement(configuration.getCategory(MODID.toLowerCase())));
    }
}
