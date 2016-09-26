package com.kamildanak.minecraft.forgeeconomy;

import com.kamildanak.minecraft.forgeeconomy.commands.CommandBalance;
import com.kamildanak.minecraft.forgeeconomy.commands.CommandPay;
import com.kamildanak.minecraft.forgeeconomy.commands.CommandWallet;
import com.kamildanak.minecraft.forgeeconomy.economy.Account;
import com.kamildanak.minecraft.forgeeconomy.events.EventHandler;
import com.kamildanak.minecraft.forgeeconomy.proxy.Proxy;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

import java.io.File;
import java.io.IOException;

@Mod(modid=ForgeEconomy.modID, name=ForgeEconomy.modName, version=ForgeEconomy.version)
public class ForgeEconomy {
    public static final String modID = "forgeeconomy";
    static final String modName = "forgeeconomy";
    static final String version = "0.1";

    @Mod.Instance(modID)
    @SuppressWarnings("unused")
    public static ForgeEconomy instance;

    //public static GuiHandler guiHandler;
    //public static CreativeTabs tabEconomy;

    public static String currencyNameSingular;
    public static String currencyNameMultiple;
    public static MinecraftServer minecraftServer;

    private static Configuration config;

    @SidedProxy(clientSide = "com.kamildanak.minecraft.forgeeconomy.proxy.ProxyClient", serverSide = "com.kamildanak.minecraft.forgeeconomy.proxy.Proxy")
    public static Proxy proxy;

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        proxy.preInit();
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void init(FMLInitializationEvent event) {
        currencyNameSingular = config.getString("general", "currency name (singular)", "credit",
                "Currency name (displayed in HUD, max 20 char)");

        currencyNameMultiple = config.getString("general", "currency name (multiple)", "credits",
                "Currency name (displayed in HUD, max 20 char)");


        proxy.init();
        proxy.registerPackets();
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void postInit(FMLPostInitializationEvent event) {
        config.save();
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onServerStart(FMLServerStartingEvent event) {
        Account.clear();

        minecraftServer = event.getServer();
        File file = getWorldDir(minecraftServer.getEntityWorld());
        if (file == null) return;

        Account.setLocation(new File(file, "ForgeEconomy-accounts"));

        registerCommands(event);
    }

    private void registerCommands(FMLServerStartingEvent event)
    {
        MinecraftServer server = event.getServer();
        ICommandManager command = server.getCommandManager();
        ServerCommandManager manager = (ServerCommandManager) command;
        manager.registerCommand(new CommandWallet());
        manager.registerCommand(new CommandBalance());
        manager.registerCommand(new CommandPay());
    }

    private File getWorldDir(World world) {
        ISaveHandler handler = world.getSaveHandler();
        if (!(handler instanceof SaveHandler)) return null;
        return handler.getWorldDirectory();
    }
}
