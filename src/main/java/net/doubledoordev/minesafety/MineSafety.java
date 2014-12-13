package net.doubledoordev.minesafety;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;

import java.util.Random;

/**
 * @author Dries007
 */
@Mod(modid = MineSafety.MODID)
public class MineSafety
{
    public static final String       MODID        = "MineSafety";
    private             Random       random       = new Random();
    private             DamageSource damageSource = new DamageSource("helmet").setDifficultyScaled();

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END) return;
        if (event.player.posY >= 50) return;
        if (event.player.getCurrentArmor(3) != null && event.player.getCurrentArmor(3).getItem() instanceof ItemArmor && ((ItemArmor) event.player.getCurrentArmor(3).getItem()).armorType == 0) return;
        if (event.player.worldObj.canBlockSeeTheSky((int) event.player.posX, (int) event.player.posY, (int) event.player.posZ)) return;
        if (random.nextDouble() < 0.97d) return;
        if (event.player.attackEntityFrom(damageSource, 1.0f + 0.2f * random.nextFloat())) event.player.addChatComponentMessage(new ChatComponentText("You should wear a helmet..."));
    }
}
