package net.doubledoordev.minesafety;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

@Mod(modid = MineSafety.MOD_ID, name = MineSafety.MOD_NAME, version = MineSafety.VERSION)
public class MineSafety
{
    public static final String MOD_NAME = "minesafety";
    public static final String VERSION  = "1.2.0";
    public static final String MOD_ID = "minesafety";
    private Random random = new Random();
    private DamageSource damageSource = new DamageSource("helmet").setDifficultyScaled();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
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
            if (player.posY >= ModConfig.yLevel) return;
            if (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof  ItemArmor) return;
            if (player.getEntityWorld().canBlockSeeSky(new BlockPos(player.posX, player.posY, player.posZ))) return;
            if (random.nextFloat() > ModConfig.chance) return;
            if (player.attackEntityFrom(damageSource, 1.0f + 0.2f * random.nextFloat()))
            {
                player.sendStatusMessage(new TextComponentString(ModConfig.message),false);
                data.setInteger(MOD_ID, 20 * ModConfig.timeout);
            }
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event){
        if (event.getModID().equals(MOD_ID))
        {
            ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
        }
    }

}

