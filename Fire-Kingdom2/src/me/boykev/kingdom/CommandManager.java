package me.boykev.kingdom;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.User;
import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {
	
	@SuppressWarnings("unused")
	private static Main instance;
	public HashMap<String, Long> kdspawnc = new HashMap<String, Long>();
	public HashMap<String, Long> kdspawnc2 = new HashMap<String, Long>();
	KingdomCraft kdc = KingdomCraftProvider.get();

	public CommandManager(Main main) {
		CommandManager.instance = main;
	}
		
	

	public String checkOther(Player p) {
		User ku = kdc.getOnlineUser(p.getName());
		String kdname = ku.getKingdom().getName();
		return kdname;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		User ku = kdc.getOnlineUser(p.getName());
		if(cmd.getName().equalsIgnoreCase("civcheck")) {
			if(args.length < 1) {
				p.sendMessage(ChatColor.RED + "ERROR" + ChatColor.WHITE + " >> " + ChatColor.DARK_RED + "Het commando is onjuist gebruikt: /check-kingdom [kdnaam]");
				return false;
			}
			if(p.hasPermission("civ.check")) {
				Bukkit.getServer().dispatchCommand(sender, "kd info " + args[0]);
				return false;
			}
			p.sendMessage(ChatColor.RED + "ERROR" + ChatColor.WHITE + " >> " + ChatColor.DARK_RED + "Je hebt onvoldoende rechten!");
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
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "civ setrank " + p.getName() + " king");
					p.sendMessage(ChatColor.GREEN + "Your Civilization was created!");
					
				}
				
			}.runTaskLater(instance, 90);
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("civspawn")) {
			String kingdom = ku.getKingdom().getName();
			if(ku.getKingdom() == null) {
				p.sendMessage(ChatColor.RED + "Oeps, je zit niet in een kingdom dus kan je dit commando niet gebruiken!");
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
			p.sendMessage(ChatColor.GREEN + "Je bent naar de kingdom spawn geteleporteerd van: " + ChatColor.DARK_RED + kingdom);
			kdspawnc.put(p.getName(), System.currentTimeMillis());
			return false;
		}
		
		return false;
	}

}
