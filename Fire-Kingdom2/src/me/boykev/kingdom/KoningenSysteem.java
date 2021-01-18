package me.boykev.kingdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.KingdomCraftApi;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import me.codedred.playtimes.api.TimelessPlayer;

public class KoningenSysteem implements Listener, CommandExecutor {
	
	private static Main instance;
	private static UserManager um;
	KingdomCraft kdc = (KingdomCraft) Bukkit.getPluginManager().getPlugin("KingdomCraft");
	KingdomCraftApi kapi = kdc.getApi();
	
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
	public static String checkOther(Player p, String config) {
		um = new UserManager(instance, p);
		return um.getConfig().getString(config);
	}
	
	public static int checkOtherTime(Player p) {
		TimelessPlayer player = new TimelessPlayer(p);
		int kaas = player.getHours();
		return kaas;
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
		if(cmd.getName().equalsIgnoreCase("kd-admin")) {
			if(args.length < 1) {
				p.sendMessage(ChatColor.RED + "Je gebuikt het commando niet juist, Gebruik /kd-admin [player]");
				return false;
			}
			Player target = checkPlayer(args[0]);
			if (target == null) {
				p.sendMessage(ChatColor.RED + "De ingevoerde speler is niet gevonden!");
				return false;
			}
			if(kapi.getUserHandler().getUser(p).getRank().equalsIgnoreCase("koning") || p.hasPermission("kingdom.admin")) {
				um = new UserManager(instance, p);
				String kd1 = um.getConfig().getString("status.kingdom");
				String kd2 = checkOther(target, "status.kingdom");
				if(kd1.equals(kd2) || p.hasPermission("kingodm.admin")) {
					Inventory mainmenu = makeInv(p, ChatColor.RED + "Kingdom Administrator", 9);
					ArrayList<String> list = new ArrayList<String>();
					ArrayList<String> list2 = new ArrayList<String>();
					list.add(ChatColor.RED + "Wijzig de rang van: ");
					list.add(ChatColor.BLUE + target.getName());
					list2.add(ChatColor.RED + "Kick speler: ");
					list2.add(ChatColor.BLUE + target.getName());
					ItemStack i1 = makeItem(ChatColor.GREEN + "Speler Rank", list, Material.ARROW, 1);
					ItemStack i2 = makeItem(ChatColor.GREEN + "Kick Speler", list2, Material.BARRIER, 1);
					mainmenu.setItem(0, i1);
					mainmenu.setItem(2, i2);
					p.openInventory(mainmenu);
					return false;
				}
				p.sendMessage(ChatColor.RED + "Je kan alleen spelers aanpassen die in jouw kingdom zitten!");
				return false;
			}
			p.sendMessage(ChatColor.RED + "Sorry, dit commando is alleen beschikbaar voor de koning!");
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("kd-invite")) {
			if(kapi.getUserHandler().getUser(p).getRank().equalsIgnoreCase("koning") || p.hasPermission("kingdom.invite")) {
				um = new UserManager(instance, p);
				if(args.length < 1) {
					p.sendMessage(ChatColor.RED + "Je hebt geen speler opgegeven!");
					return false;
				}
				if(um.getConfig().getString("status.kingdom").equalsIgnoreCase("NO-KD")) {
					p.sendMessage(ChatColor.RED + "Je zit niet in een Kingdom");
					return false;
				}
				Player target = Bukkit.getPlayer(args[0]);
				if (target == null) {
					p.sendMessage(ChatColor.RED + "De speler die je hebt opegeven bestaat niet of is niet online!");
					return false;
				}
				if(checkOtherTime(target) < 3) { p.sendMessage(ChatColor.RED + "Deze speler heeft nog geen 3 uur playtime!" + ChatColor.GRAY + " Hij heeft nu: " + checkOtherTime(target) + " Uren"); return false; }
				String kd = um.getConfig().getString("status.kingdom");
				Integer max = kapi.getKingdomHandler().getKingdom(kd).getMaxMembers();
				List<KingdomUser> cul = kapi.getKingdomHandler().getMembers(kapi.getKingdomHandler().getKingdom(kd));
				Integer curr = cul.size();
				if(curr == max || curr > max) {
					p.sendMessage(ChatColor.RED + "Je kingdom zit vol, hierdoor kon je invite niet worden verzonden!");
					return false;
				}
				if(checkOther(target, "status.kingdom").equalsIgnoreCase(kd)) { p.sendMessage(ChatColor.RED + "Deze speler zit al in je Kingdom"); return false; }
				p.sendMessage(ChatColor.GREEN + "Je hebt een invite voor kingdom: " + ChatColor.BLUE + kd);
				TextComponent msg = new TextComponent(ChatColor.GREEN + "Om te accepteren " + ChatColor.BLUE + "(Klik Hier)");
				msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kd-invitemanager accept " + kd.toLowerCase()));
				
				TextComponent msg2 = new TextComponent(ChatColor.RED + "Om te wijgeren " + ChatColor.BLUE + "(Klik Hier)");
				msg2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kd-invitemanager deny " + p.getName() + " " + kd));
				target.spigot().sendMessage(msg);
				target.spigot().sendMessage(msg2);
//				invite.put(target, true);
				invite.put(target, 1);
				return false;
			}
			p.sendMessage(ChatColor.RED + "Je hebt helaas geen permissies om spelers te inviten voor je kingdom!");
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("kd-invitemanager")) {
			um = new UserManager(instance, p);
			for(Entry<Player, Integer> e : invite.entrySet()) {
				if(e.getKey().equals(p)) {
					if(args[0].equalsIgnoreCase("accept")) {
						if(um.getConfig().getString("status.kingdom").equalsIgnoreCase(args[1])) { p.sendMessage(ChatColor.RED + "Je zit al in dit Kingdom!"); invite.remove(p); return false; }
						um.editConfig().set("status.kingdom", args[1].toUpperCase());
						KingdomUser ku = kapi.getUserHandler().getUser(p);
						ku.setKingdom(args[1]);
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kd set " + p.getName() + " " + args[1]);
						um.save();
						p.sendMessage(ChatColor.GREEN + "Je bent het kingdom gejoined");
						invite.remove(p);
						return false;
					}
					if(args[0].equalsIgnoreCase("deny")) {
						if(um.getConfig().getString("status.kingdom").equalsIgnoreCase(args[2])) { p.sendMessage(ChatColor.RED + "Je zit al in dit Kingdom!"); invite.remove(p); return false; }
						Player tg = Bukkit.getPlayer(args[1]);
						tg.sendMessage(ChatColor.RED + p.getName() + " heeft je invite gewijgerd!");
						p.sendMessage(ChatColor.RED + "Je hebt de invite gewijgerd!");
						invite.remove(p);
						return false;
					}
				}
			}
			p.sendMessage(ChatColor.RED + "Je kan dit alleen uitvoeren als je een invite hebt!");
			return false;
		}
		return false;
	}
	
	
}
