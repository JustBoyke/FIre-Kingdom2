package me.boykev.kingdom;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformLocation;

import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {
	
	private static Main instance;
	public HashMap<String, Long> kdspawnc = new HashMap<String, Long>();
	public HashMap<String, Long> kdspawnc2 = new HashMap<String, Long>();
	KingdomCraft kdc = KingdomCraftProvider.get();
	private ConfigManager cm;

	public CommandManager(Main main) {
		CommandManager.instance = main;
	}
	
	public boolean isInt(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
		
	

	public String checkOther(Player p) {
		User ku = kdc.getOnlineUser(p.getName());
		String kdname = ku.getKingdom().getName();
		return kdname;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		User ku = kdc.getOnlineUser(p.getUniqueId());
		if(cmd.getName().equalsIgnoreCase("discord")) {
			p.sendMessage(ChatColor.GREEN + "De discord is: " + ChatColor.LIGHT_PURPLE + "https://discord.gg/gewoonboyke");
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("civcheck")) {
			if(args.length < 1) {
				p.sendMessage(ChatColor.RED + "ERROR" + ChatColor.WHITE + " >> " + ChatColor.DARK_RED + "Please use: /check-kingdom [kdnaam]");
				return false;
			}
			if(p.hasPermission("civ.check")) {
				Bukkit.getServer().dispatchCommand(sender, "civ info " + args[0]);
				return false;
			}
			p.sendMessage(ChatColor.RED + "ERROR" + ChatColor.WHITE + " >> " + ChatColor.DARK_RED + "Je hebt onvoldoende rechten!");
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("civhome")) {
			if(args.length < 1) {
				p.sendMessage(ChatColor.RED + "I`m sorry, you have to use /civhome [number from 1 to 4]");
				return false;
			}
			User kdu = kdc.getOnlineUser(p.getUniqueId());
			if(kdu.getKingdom() == null) {
				p.sendMessage(ChatColor.RED + "I`m sorry, you are not in a civilization!");
				return false;
			}
			if(this.isInt(args[0]) == false) {
				p.sendMessage(ChatColor.RED + "I`m sorry, please use a number from 1 to 4");
				return false;
			}
			int number = Integer.parseInt(args[0]);
			if(ku.getKingdom().getName().equalsIgnoreCase("solaria")) {
				if(number > 9 || number < 1) {
					p.sendMessage(ChatColor.RED + "Please use a number from 1 to 9");
					return false;
				}
			}else {
				if(number > 4 || number < 1) {
					p.sendMessage(ChatColor.RED + "Please use a number from 1 to 4");
					return false;
				}
			}
			cm = new ConfigManager(instance);
			String kdname = kdu.getKingdom().getName();
			if(cm.getConfig().get(kdname + ".homes." + number) == null) {
				p.sendMessage(ChatColor.RED + "You king or queen has not yet setup this home, please request a home location from your king or queen.");
				return false;
			}
			Location loc = cm.getConfig().getLocation(kdname + ".homes." + number);
			p.teleport(loc);
			p.sendMessage(ChatColor.GREEN + "You have been teleported to home " + number);
			return false;
			
		}
		if(cmd.getName().equalsIgnoreCase("civ-create")) {
			if(args.length < 2) {
				p.sendMessage(ChatColor.RED + "You have to use /civ-create [Civilization name] [color]");
				return false;
			}
			if(args.length > 2) {
				p.sendMessage(ChatColor.RED + "The name of your kingdom can only be one word!");
				return false;
			}
			if(args[0].length() > 16) {
				p.sendMessage(ChatColor.RED + "You can only use 16 Characters!");
				return false;
			}
			if(kdc.getKingdom(args[0]) != null) {
				p.sendMessage(ChatColor.RED + "This civilization already exists, please choose another name or send a request to join this civilization!");
				return false;
			}
			if(ku.getKingdom() != null) {
				p.sendMessage(ChatColor.RED + "You are already in a Civilization. You cannot create a new Civilization!");
				return false;
			}
			if(args[1].length() > 2) {
				p.sendMessage(ChatColor.RED + "A chat color should start with & and followed by an correct color code. For example &c for light red.");
				return false;
			}
			if(args[1].length() < 2) {
				p.sendMessage(ChatColor.RED + "A chat color should start with & and followed by an correct color code. For example &c for light red.");
				return false;
			}
			String color = args[1];
			if(!color.startsWith("&")) {
				p.sendMessage(ChatColor.RED + "A chat color should start with & and followed by an correct color code. For example &c for light red.");
				return false;
			}
			String kingdom_name = args[0];
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "civ create " + kingdom_name);
			p.sendMessage(ChatColor.YELLOW + "Please wait while we create your Civilization....");
			new BukkitRunnable() {

				@Override
				public void run() {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "civ edit invite-only " + kingdom_name + " true");
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "civ edit display " + kingdom_name + " " + color + kingdom_name);
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "civ setkingdom " + p.getName() + " " + kingdom_name);
					cm = new ConfigManager(instance);
					cm.editConfig().set("power." + kingdom_name, 1);
					cm.save();
					new BukkitRunnable() {

						@Override
						public void run() {
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "civ edit max-members " + kingdom_name + " 10");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "civ setrank " + p.getName() + " king");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "civ channel disable Kingdom-" + kingdom_name);
							p.sendMessage(ChatColor.GREEN + "Your Civilization was created!");
							
						}
						
					}.runTaskLater(instance, 60);
					
					p.sendMessage(ChatColor.YELLOW + "Your Civilization was created, you will be promoted to king shortly!");
					
				}
				
			}.runTaskLater(instance, 90);
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("civspawn")) {
			if(ku.getKingdom() == null) {
				p.sendMessage(ChatColor.RED + "You dont have a civilization!");
				return false;
			}
			String kingdom = ku.getKingdom().getName();
			if(ku.getKingdom() == null) {
				p.sendMessage(ChatColor.RED + "Oh no, you are not in a registered civilization and therefor have no spawn :(");
				return false;
			}
			if(ku.getKingdom().getSpawn() == null) {
				p.sendMessage(ChatColor.RED + "ERROR" + ChatColor.WHITE + " >> " + ChatColor.DARK_RED + "Errorcode S_NOT_DEFINED FOR: " + kingdom);
				return false;
			}
			
			World world = Bukkit.getWorld(ku.getKingdom().getSpawn().getWorldName());
			double x = ku.getKingdom().getSpawn().getX();
			double y = ku.getKingdom().getSpawn().getY();
			double z = ku.getKingdom().getSpawn().getZ();
			Location loc = new Location(world,x,y,z);
			p.teleport(loc);
			p.sendMessage(ChatColor.GREEN + "Je have been teleported to the spawn of : " + ChatColor.DARK_RED + kingdom);
			kdspawnc.put(p.getName(), System.currentTimeMillis());
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("setpower")) {
			if(args.length < 2) {
				p.sendMessage(ChatColor.RED + "Please use setpower [name of civ] [number]");
				return false;
			}
			if(this.isInt(args[1]) == false) {
				p.sendMessage(ChatColor.RED + "Please use setpower [name of civ] [number]");
				return false;
			}
			String kingdom_name = args[0];
			int power = Integer.valueOf(args[1]);
			Kingdom kd = kdc.getKingdom(kingdom_name);
			if(kd == null) {
				p.sendMessage(ChatColor.RED + "This civilization does not exist!");
				return false;
			}
			String kingdom_plname = kd.getName();
			if(p.hasPermission("civ.setpower")) {
				cm = new ConfigManager(instance);
				if(cm.getConfig().get("power." + kingdom_name) == null) {
					cm.editConfig().set("power." + kingdom_name, 1);
					cm.save();
					p.sendMessage(ChatColor.RED + "Civ had no power, its now set to 1. To change please run the command again!");
					return false;
				}else {
					cm.editConfig().set("power." + kingdom_plname, power);
					cm.save();
					p.sendMessage(ChatColor.GREEN + "Power successfully chaged to " + ChatColor.BOLD + power);
					return false;
				}
			}
			p.sendMessage(ChatColor.RED + "You don`t have the required permissions!");
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("repair")) {
			if(p.getName().equalsIgnoreCase("boykev")) {
				ItemStack i = p.getInventory().getItemInMainHand();
				if(i.getDurability() == 0) {
					return false;
				}
				Short d = 0;
				i.setDurability(d);
				return false;
			}
			p.sendMessage(ChatColor.RED + "NO");
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("repaira")) {
			if(p.getName().equalsIgnoreCase("boykev")) {
				for(Player pl : Bukkit.getOnlinePlayers()) {
					if(pl.getName().equalsIgnoreCase("boykev")) {
						for(ItemStack is : pl.getInventory().getArmorContents()) {
							short so = 0;
							is.setDurability(so);
						}
						
					}
					if(pl.getName().equalsIgnoreCase("coenispro")) {
						for(ItemStack is : pl.getInventory().getArmorContents()) {
							short so = 0;
							is.setDurability(so);
						}
						
					}
				}
				return false;
			}
			p.sendMessage(ChatColor.RED + "NO!");
		}
		if(cmd.getName().equalsIgnoreCase("repairb")) {
			if(p.getName().equalsIgnoreCase("boykev")) {
				for(Player pl : Bukkit.getOnlinePlayers()) {
					if(pl.getName().equalsIgnoreCase("boykev")) {
						for(ItemStack is : pl.getInventory().getContents()) {
							if(is == null) {
								
							}else {
								short so = 0;
								is.setDurability(so);
							}
							
						}
						
					}
					if(pl.getName().equalsIgnoreCase("coenispro")) {
						for(ItemStack is : pl.getInventory().getContents()) {
							if(is == null) {
								
							}else {
								short so = 0;
								is.setDurability(so);
							}
							
						}
						
					}
				}
				return false;
			}
			p.sendMessage(ChatColor.RED + "NO!");
		}
		if(cmd.getName().equalsIgnoreCase("cpitem")) {
			if(p.getName().equalsIgnoreCase("boykev")) {
				ItemStack i = p.getItemInHand();
				p.getInventory().addItem(i);
				return false;
			}
			p.sendMessage(ChatColor.RED + "NO!");
		}
		if(cmd.getName().equalsIgnoreCase("civsetspawn")) {
			User kdu = kdc.getOnlineUser(p.getUniqueId());
			if(ku.getKingdom() == null) {
				p.sendMessage(ChatColor.RED + "Oh no, you are not in a registered civilization and therefor you can not use this command!");
				return false;
			}
			if(kdu.getRank().getName().equalsIgnoreCase("king") || kdu.getRank().getName().equalsIgnoreCase("queen")) {
				Kingdom kd = ku.getKingdom();
				PlatformLocation loc = new PlatformLocation(p.getLocation().getWorld().getName(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
				kd.setSpawn(loc);
				kdc.saveAsync(kd);
				p.sendMessage(ChatColor.GREEN + "Spawn location has been set!");
				return false;
			}
			p.sendMessage(ChatColor.RED + "This command is reserved for the king and queen only!");
			return false;
			
		}
		
		return false;
	}

}
