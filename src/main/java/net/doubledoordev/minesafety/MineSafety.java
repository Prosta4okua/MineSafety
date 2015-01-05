package net.doubledoordev.minesafety;

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.doubledoordev.d3core.util.ID3Mod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.Sys;

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
    private int timeout;

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
        EntityPlayer player = event.player;
        NBTTagCompound data = player.getEntityData();
        if (data.hasKey(MODID))
        {
            if (data.getInteger(MODID) == 0) data.removeTag(MODID);
            else data.setInteger(MODID, data.getInteger(MODID) - 1);
        }
        else
        {
            if (player.posY >= yLevel) return;
            if (player.getCurrentArmor(3) != null && player.getCurrentArmor(3).getItem() instanceof ItemArmor && ((ItemArmor) player.getCurrentArmor(3).getItem()).armorType == 0) return;
            if (player.worldObj.canBlockSeeTheSky((int) player.posX, (int) player.posY, (int) player.posZ)) return;
            if (random.nextFloat() > chance) return;
            if (player.attackEntityFrom(damageSource, 1.0f + 0.2f * random.nextFloat()))
            {
                player.addChatComponentMessage(new ChatComponentText(LanguageRegistry.instance().getStringLocalization("d3.mineSafety.message")));
                data.setInteger(MODID, 20 * timeout);
            }
        }
    }

    @Override
    public void syncConfig()
    {
        configuration.setCategoryLanguageKey(MODID, "d3.mineSafety.config.mineSafety");
        //public int getInt(String name, String category, int defaultValue, int minValue, int maxValue, String comment, String langKey)
        yLevel = configuration.getInt("yLevel", MODID, 50, 0, 256, "The Y level at which you should wear a helmet.", "d3.mineSafety.config.yLevel");
        chance = configuration.getFloat("chance", MODID, 0.03f, 0.0f, 1.0f, "The chance you get damaged this tick, in percent.", "d3.mineSafety.config.chance");
        timeout = configuration.getInt("timeout", MODID, 1, 0, Integer.MAX_VALUE, "The minimum time between 2 hits from this mod", "d3.mineSafety.config.timeout");

        if (configuration.hasChanged()) configuration.save();
    }

    @Override
    public void addConfigElements(List<IConfigElement> configElements)
    {
        configElements.add(new ConfigElement(configuration.getCategory(MODID.toLowerCase())));
    }
}
