package me.boykev.kingdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.User;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import me.codedred.playtimes.api.TimelessPlayer;

public class KoningenSysteem implements Listener, CommandExecutor {
	
	@SuppressWarnings("unused")
	private static Main instance;
	KingdomCraft kdc = KingdomCraftProvider.get();
	
	public KoningenSysteem(Main main) {
		KoningenSysteem.instance = main;
	}

	public static ItemStack makeItem(String name, ArrayList<String> lore, Material m, Integer aantal) {
		ItemStack item = new ItemStack(m, aantal);
		ItemMeta imeta = item.getItemMeta();
		imeta.setDisplayName(name);
		imeta.setLore(lore);
		item.setItemMeta(imeta);
		return item;
	}
	
	public Player checkPlayer(String player, String kuthoer) {
		if(Bukkit.getPlayer(player) == null) {
			
		}
		
		return null;
	}
	
	public static Inventory makeInv(Player player, String name, Integer size) {
		Inventory menu = Bukkit.createInventory(player, size, name);
		return menu;
	}
	
	public static int checkOtherTime(Player p) {
	    TimelessPlayer player = new TimelessPlayer(p);
	    long playtimeInSeconds = player.getRawPlayTimeInSeconds();
	    double playtimeInHours = playtimeInSeconds / 3600.0; // Use 3600.0 for floating-point division
	    int roundedPlaytimeInHours = (int) Math.round(playtimeInHours);
	    return roundedPlaytimeInHours;
	}
	
	public static Player checkPlayer(String name) {
		if(Bukkit.getPlayer(name) == null) {
			@SuppressWarnings("deprecation")
			OfflinePlayer op = Bukkit.getOfflinePlayer(name);
			System.out.print("Player uit chache gehaald");
			Player p = op.getPlayer();
			return p;
		}
		Player p = Bukkit.getPlayer(name);
		return p;
	}
	
	public static HashMap<Player, Integer> invite = new HashMap<Player, Integer>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("civadmin")) {
			if(args.length < 1) {
				p.sendMessage(ChatColor.RED + "Please use /cidadmin [playername]");
				return false;
			}
			Player target = checkPlayer(args[0]);
			if (target == null) {
				p.sendMessage(ChatColor.RED + "The player is not found!");
				return false;
			}
			User ku = kdc.getOnlineUser(p.getName());
			if(ku.getKingdom() == null) {
				p.sendMessage(ChatColor.RED + "You don`t have a Civilization.");
				return false;
			}
			if(ku.getRank().getName().equalsIgnoreCase("king") || ku.getRank().getName().equalsIgnoreCase("queen") ||  p.hasPermission("kingdom.admin")) {
				String kd1 = ku.getKingdom().getName();
				if(kd1 == null) {
					p.sendMessage(ChatColor.RED + "You don`t have a Civilization or you are not the king or queen.");
					return false;
				}
				User kdo = kdc.getOnlineUser(target.getName());
				if(kdo.getKingdom() == null) {
					p.sendMessage(ChatColor.RED + "This player can not be managed. He/She is not part of a Civilization");
					return false;
				}
				String kd2 = kdo.getKingdom().getName();
				if(kd1.equalsIgnoreCase(kd2) || p.hasPermission("kingdom.admin")) {
					Inventory mainmenu = makeInv(p, ChatColor.RED + "Civilization Administrator", 9);
					ArrayList<String> list = new ArrayList<String>();
					ArrayList<String> list2 = new ArrayList<String>();
					list.add(ChatColor.RED + "Change the role of: ");
					list.add(ChatColor.BLUE + target.getName());
					list2.add(ChatColor.RED + "Kick player: ");
					list2.add(ChatColor.BLUE + target.getName());
					ItemStack i1 = makeItem(ChatColor.GREEN + "Player Role", list, Material.ARROW, 1);
					ItemStack i2 = makeItem(ChatColor.GREEN + "Kick Player", list2, Material.BARRIER, 1);
					mainmenu.setItem(0, i1);
					mainmenu.setItem(2, i2);
					p.openInventory(mainmenu);
					return false;
				}
				p.sendMessage(ChatColor.RED + "You can only change players in your own Civilization");
				return false;
			}
			p.sendMessage(ChatColor.RED + "Sorry, this command is reserverd for the King and Queen!");
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("civinvite")) {
			User ku = kdc.getOnlineUser(p.getName());
			if(ku.getRank().getName().equalsIgnoreCase("king") || ku.getRank().getName().equalsIgnoreCase("queen") || p.hasPermission("kingdom.invite")) {
				if(args.length < 1) {
					p.sendMessage(ChatColor.RED + "You did not specify a player");
					return false;
				}
				if(ku.getKingdom() == null) {
					p.sendMessage(ChatColor.RED + "You don`t have a Civilization");
					return false;
				}
				Player target = Bukkit.getPlayer(args[0]);
				if (target == null) {
					p.sendMessage(ChatColor.RED + "The player you enterd is not online or does not exist!");
					return false;
				}
				String kd = ku.getKingdom().getName();
				Integer max = kdc.getKingdom(kd).getMaxMembers();
				Integer curr = kdc.getKingdom(kd).getMemberCount();
				if(curr == max || curr > max) {
					p.sendMessage(ChatColor.RED + "Your Civilization is full, the invite has been canceled!");
					return false;
				}
				User kdo = kdc.getOnlineUser(target.getName());
				if(kdo.getKingdom() == ku.getKingdom()) { p.sendMessage(ChatColor.RED + "This player is already in your Civilization"); return false; }
				p.sendMessage(ChatColor.GREEN + "You have an invite for Civilization: " + ChatColor.BLUE + kd);
				TextComponent msg = new TextComponent(ChatColor.GREEN + "To accept " + ChatColor.BLUE + "(Click this)");
				msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kd-invitemanager accept " + kd.toLowerCase()));
				
				TextComponent msg2 = new TextComponent(ChatColor.RED + "To deny " + ChatColor.BLUE + "(Click this)");
				msg2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kd-invitemanager deny " + p.getName() + " " + kd));
				target.spigot().sendMessage(msg);
				target.spigot().sendMessage(msg2);
//				invite.put(target, true);
				invite.put(target, 1);
				return false;
			}
			p.sendMessage(ChatColor.RED + "You don`t have the permissions for that!");
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("kd-invitemanager")) {
			for(Entry<Player, Integer> e : invite.entrySet()) {
				if(e.getKey().equals(p)) {
					if(args[0].equalsIgnoreCase("accept")) {
						User ku = kdc.getOnlineUser(p.getName());
						if(ku.getKingdom() != null) {
							if(ku.getKingdom().getName().equalsIgnoreCase(args[1])) { p.sendMessage(ChatColor.RED + "You are already in this Civilization!"); invite.remove(p); return false; }
						}
						ku.setKingdom(kdc.getKingdom(args[1]));
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "civ set " + p.getName() + " " + args[1]);
						p.sendMessage(ChatColor.GREEN + "You joined the Civilization");
						invite.remove(p);
						return false;
					}
					if(args[0].equalsIgnoreCase("deny")) {
						User ku = kdc.getOnlineUser(p.getName());
						if(ku.getKingdom().getName().equalsIgnoreCase(args[2])) { p.sendMessage(ChatColor.RED + "You are already in this Civilization"); invite.remove(p); return false; }
						Player tg = Bukkit.getPlayer(args[1]);
						tg.sendMessage(ChatColor.RED + p.getName() + " has declined your invite!");
						p.sendMessage(ChatColor.RED + "You have declined the invite!");
						invite.remove(p);
						return false;
					}
				}
			}
			p.sendMessage(ChatColor.RED + "You can run this command only if you have an invite for a Civilization");
			return false;
		}
		return false;
	}
	
	
}
