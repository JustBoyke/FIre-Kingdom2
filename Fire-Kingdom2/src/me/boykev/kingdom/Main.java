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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


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
		
		
		
		PluginManager pm = Bukkit.getPluginManager();
		cm = new ConfigManager(this);
		cm.LoadDefaults();
		
		if(cm.getConfig().getString("key").equals("-")) {
			Bukkit.broadcastMessage(ChatColor.YELLOW + "The plugin is setting things up please lay back.....");
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                	Bukkit.broadcastMessage(ChatColor.YELLOW + "Everything is fine, have fun using InkoopPlugin :)");
                }
            }, 100);
			String serverid = Bukkit.getServer().getServerId();
	    	int serverport = Bukkit.getServer().getPort();
	    	String plname = "Fire-Kingdom";
	    	String bukkitip = Bukkit.getServer().getIp();
	    	try {
	    		URL url = new URL("http://api.boykevanvugt.nl/keymanager.php?type=create&version=1&plname=" + plname + "&serverport=" + serverport);
	            URLConnection connection = url.openConnection();
	            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String text = in.readLine();
	            String licfinal = text.replace(" ", "");
	            cm.getConfig().set("key", licfinal);
	            cm.save();
	            in.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		if(!cm.getConfig().getString("key").equals("-")) {
	    	String key = cm.getConfig().getString("key");
	    	try {
	            URL url = new URL("http://api.boykevanvugt.nl/keymanager.php?type=read&key=" + key);
	            URLConnection connection = url.openConnection();
	            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String text = in.readLine();
	            String licfinal = text.replace(" ", "");
	            cm.getConfig().set("key", licfinal);
	            if(licfinal.equals("valid")) {
	            	this.Status = "Valid";
	            	in.close();
	            	
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
	            	getCommand("kd-selector").setExecutor(new CommandManager(this));
	            	getCommand("kdspawn").setExecutor(new CommandManager(this));
	            	getCommand("kdsetspawn").setExecutor(new CommandManager(this));
	            	getCommand("setkingdom").setExecutor(new CommandManager(this));
	            	getCommand("kd-kick").setExecutor(new CommandManager(this));
	            	getCommand("check-kingdom").setExecutor(new CommandManager(this));
	            	getCommand("kd-admin").setExecutor(new KoningenSysteem(this));
	            	getCommand("kd-invite").setExecutor(new KoningenSysteem(this));
	            	getCommand("kd-invitemanager").setExecutor(new KoningenSysteem(this));
	    			return;
	            }
	            if(licfinal.equals("abuse")) {
	            	Bukkit.broadcastMessage(ChatColor.YELLOW + "Deze server abused de InkoopPlugin!");
	            	in.close();
	            	pm.registerEvents(new JoinEvent(this), this);
	            	this.Status = "Abuse";
	            	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	                    @Override
	                    public void run() {
	                    	Bukkit.broadcastMessage(ChatColor.DARK_RED+ "Je maakt misbruik van de Fire-Kingdom, neem contact op met de developer.");
	                    }
	                }, 0, 1800);
	            	return;
	            }
	            if(licfinal.equals("edit")) {
	            	Bukkit.broadcastMessage(ChatColor.YELLOW + "Deze server abused de Fire-Kingdom door edits te maken!");
	            	in.close();
	            	pm.registerEvents(new JoinEvent(this), this);
	            	this.Status = "Edit";
	            	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	                    @Override
	                    public void run() {
	                    	Bukkit.broadcastMessage(ChatColor.DARK_RED + "Je hebt wijzigingen aangebracht in de Fire-Kingdom, dit is niet toegestaan neem contact op met de developer.");
	                    }
	                }, 0, 1800);
	            	return;
	            }
	            else { 
	            	Bukkit.getServer().getPluginManager().disablePlugin(this);
		            Log.info(ChatColor.DARK_PURPLE + "Licentie FAILD" + licfinal);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            Log.info(ChatColor.DARK_PURPLE + "Licentie FAILD");
	        }
		}
		
		
		    
		    
		
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
	
	

	
    @EventHandler
    public void craftItem(PrepareItemCraftEvent e) {
    	Material itemType = e.getRecipe().getResult().getType();
//    	Byte itemData = e.getRecipe().getResult().getData().getData();
    	if(itemType == Material.ENDER_CHEST || itemType == Material.BEACON) {
    		e.getInventory().setResult(new ItemStack(Material.AIR));
    		for (HumanEntity he : e.getViewers()) {
    			if (he instanceof Player) {
    				he.sendMessage(ChatColor.RED + "Dit item kan je niet craften!");
    				return;
    			}
    		}
    	}
    }
	
    
    
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-------[Fire-Kingdom]------");
    	Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Plugin wordt uitgeschakeld");
    	Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Made with <3 by Fire-Development (boykev)");
    	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-----------------------------------");
	}
    
	
}