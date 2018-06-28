package net.doubledoordev.minesafety;

import net.minecraftforge.common.config.Config;

@Config(modid = MineSafety.MOD_ID)
@Config.LangKey("mineSafety.config.title")
public class ModConfig
{
    @Config.LangKey("d3.minesafety.config.yLevel")
    @Config.Comment("The Y level at which you should wear a helmet.")
    @Config.RangeInt(min = 0, max = 256)
    public static int yLevel = 50;

    @Config.LangKey("d3.minesafety.config.chance")
    @Config.Comment("The chance you get damaged this tick, in percent.")
    @Config.RangeDouble(min = 0.0f, max = 1.0f)
    public static float chance = 0.03f;

    @Config.LangKey("d3.minesafety.config.timeout")
    @Config.Comment("The minimum time in seconds between 2 hits from this mod.")
    @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
    public static int timeout = 1;

    @Config.LangKey("d3.minesafety.config.message")
    @Config.Comment("The message displayed in-game when user takes damage from no helmet.")
    public static String message = "Ouch! Falling rocks... I should wear a helmet.";

    @Config.LangKey("d3.minesafety.config.dimlist")
    @Config.Comment("Dimension damage BLACKLIST, MineSafety damage will be disabled in these dimensions only!")
    public static int[] dims = new int[0];

}
