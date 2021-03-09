package me.boykev.kingdom;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;


@SuppressWarnings("unused")
public class Main extends JavaPlugin implements Listener {
	private ConfigManager cm;
	public static String PREFIX; 
	public String Status;
	public Economy economy;
	public Connection con;
	private String host, database, username, password;
	private int port;
	public List<Player> nover2 = new ArrayList<Player>();
	public List<Player> nokd2 = new ArrayList<Player>();
	public HashMap<Player, Boolean> nokd = new HashMap<Player, Boolean>();
	public HashMap<Player, Boolean> nover = new HashMap<Player, Boolean>();
	private LicenseCheck lc;
	private PlaceholderAPI pl;

	
	@Override
	public void onEnable() {
		//Initialize Plugin Manager
		
		
		PluginDescriptionFile pdf = this.getDescription();
		if(!pdf.getAuthors().contains("boykev")) {
			 Bukkit.broadcastMessage(ChatColor.RED + "Je hebt lopen kloten met de plugin.yml! Je mag deze plugin niet aanpassen, dit is een overtreding van de TOS, Je plugin is hierbij geblokeerd");
			 Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Je hebt lopen kloten met de plugin.yml! Je mag deze plugin niet aanpassen, dit is een overtreding van de TOS, Je plugin is hierbij geblokeerd");
			 this.getPluginLoader().disablePlugin(this);
			 Bukkit.getServer().getPluginManager().disablePlugin(this);
		}
		if(!pdf.getName().contains("Fire-Kingdom")) {
			 Bukkit.broadcastMessage(ChatColor.RED + "Je hebt lopen kloten met de plugin.yml! Je mag deze plugin niet aanpassen, dit is een overtreding van de TOS, Je plugin is hierbij geblokeerd");
			 Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Je hebt lopen kloten met de plugin.yml! Je mag deze plugin niet aanpassen, dit is een overtreding van de TOS, Je plugin is hierbij geblokeerd");
			 this.getPluginLoader().disablePlugin(this);
			 Bukkit.getServer().getPluginManager().disablePlugin(this);
		}
		
		cm = new ConfigManager(this);
		cm.LoadDefaults();
		PluginManager pm = Bukkit.getPluginManager();
		lc = new LicenseCheck(this);
		if(lc.licentie("Fire-Kingdom2") == false) {
			pm.disablePlugin(this);
			return;
		}
		//initizlize Vault
    	if(Bukkit.getPluginManager().getPlugin("Vault") instanceof Vault)
    	{
    		RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

    		if(service != null)
    			economy = service.getProvider();
    	}
    	
    	
    	
    	
    	//Initialize Commands and Events
		pm.registerEvents(this, this);
		pm.registerEvents(new EventSystem(this), this);
		pm.registerEvents(new KoningenSysteem(this), this);
		pm.registerEvents(new CheckRules(this), this);
    	getCommand("kd-selector").setExecutor(new CommandManager(this));
    	getCommand("kdspawn").setExecutor(new CommandManager(this));
    	getCommand("kdsetspawn").setExecutor(new CommandManager(this));
    	getCommand("setkingdom").setExecutor(new CommandManager(this));
    	getCommand("godhp").setExecutor(new CommandManager(this));
    	getCommand("check-kingdom").setExecutor(new CommandManager(this));
    	getCommand("kdsetcolor").setExecutor(new CommandManager(this));
    	getCommand("kd-admin").setExecutor(new KoningenSysteem(this));
    	getCommand("kd-invite").setExecutor(new KoningenSysteem(this));
    	getCommand("kd-invitemanager").setExecutor(new KoningenSysteem(this));
    	getCommand("steal").setExecutor(new CommandManager(this));
    	getCommand("rulemanager-accept").setExecutor(new CheckRules(this));
    	if(pm.getPlugin("PlaceholderAPI") != null) {
			new PlaceholderAPI(this).register();
		}
    	pl = new PlaceholderAPI(this);
    	
    	new BukkitRunnable() {
    		public void run() {
    			if(lc.licentie("Fire-Kingdom2") == true) {
            		System.out.println("Licentie Status Geupdate!");
            	}
            	if(lc.licentie("Fire-Kingdom2") == false) {
            		getCommand("kd-invitemanager").setExecutor(null);
            		getCommand("kdspawn").setExecutor(null);
            		getCommand("kd-selector").setExecutor(null);
            		this.cancel();
            	}
    		}
    	}.runTaskTimer(this, 0, 1800);
    	
		return;
    }
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("fire-kingdom")) {
			Player p = (Player) sender;
			if(args.length != 1 ) {
				p.sendMessage(ChatColor.GOLD + "Fire-Kingdom is een plugin gemaakt door boykev");
				p.sendMessage(ChatColor.GOLD + "� Namaken of Verkopen niet toegestaan");
				PluginDescriptionFile pdf = this.getDescription();
				p.sendMessage(ChatColor.GREEN + "Plugin Versie: " + ChatColor.GRAY + pdf.getVersion());
				return false;
			}
		}
		return false;
	}

    
    
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-------[Fire-Kingdom]------");
    	Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Plugin wordt uitgeschakeld");
    	Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Made with <3 by Fire-Development (boykev)");
    	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-----------------------------------");
	}
    
	
}