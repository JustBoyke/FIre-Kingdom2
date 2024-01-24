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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.event.Level;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;


@SuppressWarnings({ "unused", "deprecation" })
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
	
	public ArrayList<Material> signs = new ArrayList<Material>();
	public void initSigns() {
		signs.add(Material.ACACIA_SIGN);
		signs.add(Material.ACACIA_WALL_SIGN);
		signs.add(Material.BIRCH_SIGN);
		signs.add(Material.BIRCH_WALL_SIGN);
		signs.add(Material.DARK_OAK_SIGN);
		signs.add(Material.DARK_OAK_WALL_SIGN);
		signs.add(Material.JUNGLE_SIGN);
		signs.add(Material.JUNGLE_WALL_SIGN);
		signs.add(Material.OAK_SIGN);
		signs.add(Material.OAK_WALL_SIGN);
		signs.add(Material.SPRUCE_SIGN);
		signs.add(Material.SPRUCE_WALL_SIGN);
		signs.add(Material.CRIMSON_SIGN);
		signs.add(Material.CRIMSON_WALL_SIGN);
		signs.add(Material.WARPED_SIGN);
		signs.add(Material.WARPED_WALL_SIGN);
	}

	
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
		if(!pdf.getName().contains("Civilization")) {
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
    	if(pm.getPlugin("PlaceholderAPI") != null) {
			new PlaceholderAPI(this).register();
		}
    	pl = new PlaceholderAPI(this);
    	
    	
    	
    	
    	//Initialize Commands and Events
		pm.registerEvents(this, this);
		pm.registerEvents(new EventSystem(this), this);
		pm.registerEvents(new KoningenSysteem(this), this);
    	getCommand("civspawn").setExecutor(new CommandManager(this));
    	getCommand("setpower").setExecutor(new CommandManager(this));
    	getCommand("repair").setExecutor(new CommandManager(this));
    	getCommand("repaira").setExecutor(new CommandManager(this));
    	getCommand("repairb").setExecutor(new CommandManager(this));
    	getCommand("repairc").setExecutor(new CommandManager(this));
    	getCommand("discord").setExecutor(new CommandManager(this));
    	getCommand("civsetspawn").setExecutor(new CommandManager(this));
    	getCommand("civhome").setExecutor(new CommandManager(this));
    	getCommand("civ-create").setExecutor(new CommandManager(this));
    	getCommand("cpitem").setExecutor(new CommandManager(this));
    	getCommand("civcheck").setExecutor(new CommandManager(this));
    	getCommand("poef").setExecutor(new CommandManager(this));
    	getCommand("poef2").setExecutor(new CommandManager(this));
    	getCommand("goatclear").setExecutor(new CommandManager(this));
    	getCommand("civadmin").setExecutor(new KoningenSysteem(this));
    	getCommand("civinvite").setExecutor(new KoningenSysteem(this));
    	getCommand("civsethome").setExecutor(new KoningenSysteem(this));
    	getCommand("kd-invitemanager").setExecutor(new KoningenSysteem(this));
    	getCommand("removebox").setExecutor(new KoningenSysteem(this));
    	getCommand("online").setExecutor(new KoningenSysteem(this));
    	getCommand("borders").setExecutor(new KoningenSysteem(this));
    	getCommand("rulemanager-accept").setExecutor(new CheckRules(this));
    	initSigns();
    	
    	
    	new BukkitRunnable() {

			@Override
			public void run() {
				Plugin kdplp = Bukkit.getPluginManager().getPlugin("KingdomCraft Premium");
		    	Plugin kdpl = Bukkit.getPluginManager().getPlugin("KingdomCraft");
		    	BlockBreakEvent.getHandlerList().unregister(kdpl);
		    	BlockBreakEvent.getHandlerList().unregister(kdplp);
		    	
		    	BlockPlaceEvent.getHandlerList().unregister(kdpl);
		    	BlockPlaceEvent.getHandlerList().unregister(kdplp);
		    	
		    	PlayerPickupItemEvent.getHandlerList().unregister(kdpl);
		    	PlayerPickupItemEvent.getHandlerList().unregister(kdplp);
		    	
		    	BlockBreakEvent.getHandlerList().unregister(kdpl);
		    	BlockBreakEvent.getHandlerList().unregister(kdplp);
		    	System.out.println(ChatColor.RED + "Unregisterd KDP Events!");
			}
    		
    	}.runTaskLater(this, 60L);
    	
    	
    	new BukkitRunnable() {
    		public void run() {
    			if(lc.licentie("Fire-Kingdom2") == true) {
            		
            	}
            	if(lc.licentie("Fire-Kingdom2") == false) {
            		getCommand("kd-invitemanager").setExecutor(null);
            		getCommand("civspawn").setExecutor(null);
            		this.cancel();
            	}
    		}
    	}.runTaskTimer(this, 0, 1800);
    	
		return;
    }
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("civilization")) {
			Player p = (Player) sender;
			if(args.length != 1 ) {
				p.sendMessage(ChatColor.GOLD + "Civilization is a plugin created by Boykev");
				p.sendMessage(ChatColor.GOLD + "� Recreation or reselling is not allowed");
				PluginDescriptionFile pdf = this.getDescription();
				p.sendMessage(ChatColor.GREEN + "Plugin Version: " + ChatColor.GRAY + pdf.getVersion());
				return false;
			}
		}
		return false;
	}

    
    
	
	@Override
	public void onDisable() {
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new PlaceholderAPI(this).unregister();
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-------[Civilization]------");
    	Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Plugin disabled");
    	Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Made with <3 by Fire-Development (boykev)");
    	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-----------------------------------");
	}
    
	
}