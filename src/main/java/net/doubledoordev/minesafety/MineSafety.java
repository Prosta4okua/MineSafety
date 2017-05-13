package net.doubledoordev;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.util.List;
import java.util.Random;

@Mod(modid = MineSafety.MOD_ID, name = MineSafety.MOD_NAME, version = MineSafety.VERSION)
public class MineSafety
{
    public static final String MOD_NAME = "MineSafety";
    public static final String VERSION  = "1.1.0";
    public static final String       MOD_ID        = "minesafety";
    private                 Random       random       = new Random();
    private                 DamageSource damageSource = new DamageSource("helmet").setDifficultyScaled();
    private             Configuration   configuration;
    private File folder;
    private int yLevel;
    private float chance;
    private int timeout;
    private String message;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);

        folder = new File(event.getModConfigurationDirectory(), MOD_ID);
        folder.mkdir();
        configuration = new Configuration(new File(folder, event.getSuggestedConfigurationFile().getName()));
        updateConfig();
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END) return;
        EntityPlayer player = event.player;

        NBTTagCompound data = player.getEntityData();
        if (data.hasKey(MOD_ID))
        {
            if (data.getInteger(MOD_ID) == 0) data.removeTag(MOD_ID);
            else data.setInteger(MOD_ID, data.getInteger(MOD_ID) - 1);
        }
        else
        {
            if (player.posY >= yLevel) return;
            if (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) !=null && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof  ItemArmor) return;
            if (player.getEntityWorld().canBlockSeeSky(new BlockPos(player.posX, player.posY, player.posZ))) return;
            if (random.nextFloat() > chance) return;
            if (player.attackEntityFrom(damageSource, 1.0f + 0.2f * random.nextFloat()))
            {
                player.sendStatusMessage(new TextComponentString(message));
                data.setInteger(MOD_ID, 20 * timeout);
            }
        }
    }

    //@Override
    public void addConfigElements(List<IConfigElement> configElements)
    {
        configElements.add(new ConfigElement(configuration.getCategory(MOD_ID.toLowerCase())));
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event){
        if (event.getModID().equals(MOD_ID)) updateConfig();
    }

    private void updateConfig()
    {
        configuration.setCategoryLanguageKey(MOD_ID, "d3.mineSafety.config.mineSafety").setCategoryComment(MOD_ID, "MineSafety Configs");;
        yLevel = configuration.getInt("yLevel", MOD_ID, 50, 0, 256, "The Y level at which you should wear a helmet.", "d3.mineSafety.config.yLevel");
        chance = configuration.getFloat("chance", MOD_ID, 0.03f, 0.0f, 1.0f, "The chance you get damaged this tick, in percent.", "d3.mineSafety.config.chance");
        timeout = configuration.getInt("timeout", MOD_ID, 1, 0, Integer.MAX_VALUE, "The minimum time between 2 hits from this mod", "d3.mineSafety.config.timeout");
        message = configuration.getString("message", MOD_ID, "Ouch! Falling rocks... I should wear a helmet.","The message displayed ingame when user takes damage from no helmet", "d3.mineSafety.config.message");

        if (configuration.hasChanged()) configuration.save();
    }

}

