package me.boykev.kingdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.KingdomCraftApi;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;

import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {
	
	private static Main instance;
	private ConfigManager cm;
	private static UserManager um;
	private int kdspawntime = 60;
	private int kdspawntime2 = 1800;
	public HashMap<String, Long> kdspawnc = new HashMap<String, Long>();
	public HashMap<String, Long> kdspawnc2 = new HashMap<String, Long>();
	KingdomCraft kdc = (KingdomCraft) Bukkit.getPluginManager().getPlugin("KingdomCraft");
	KingdomCraftApi kapi = kdc.getApi();

	public CommandManager(Main main) {
		CommandManager.instance = main;
	}
	
	public static String checkKD(Player p) {
		um = new UserManager(instance, p);
		String KD = um.getConfig().getString("status.kingdom");
		if(KD == null) {
			return "NO-KD";
		}
		if(KD == "-") {
			return "NO-KD";
		}
		return um.getConfig().getString("status.kingdom");
	}
	
	public Inventory createInv(Player player) {
		Inventory menu = Bukkit.createInventory(player, 9, ChatColor.RED + "Kingdom Selector");
		return menu;
	}
	
	public ItemStack mikeItem(String name, Player p, Material m) {
		ItemStack item = new ItemStack(m, 1);
		ItemMeta imeta = item.getItemMeta();
		ArrayList<String> ilore = new ArrayList<String>();
		imeta.setDisplayName(ChatColor.GOLD + name);
		ilore.add(ChatColor.GREEN + "Klik om dit kingdom te joinen");
		imeta.setLore(ilore);
		item.setItemMeta(imeta);
		return item;
	}
	
	public static void editOther(Player p, String config, String info) {
		um = new UserManager(instance, p);
		um.editConfig().set(config, info);
		um.save();
	}
	public static String checkOther(Player p, String config) {
		um = new UserManager(instance, p);
		return um.getConfig().getString(config);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		cm = new ConfigManager(instance);
		um = new UserManager(instance, p);
		if(cmd.getName().equalsIgnoreCase("kd-selector")) {
			if(!checkKD(p).equalsIgnoreCase("NO-KD")) {
				p.sendMessage(ChatColor.RED + "Je kan dit commando niet uitvoeren omdat je al een keuze hebt gemaakt!");
				return false;
			}
			Inventory menu = this.createInv(p);
			ItemStack kd1 = this.mikeItem("Noord", p, Material.JUNGLE_WOOD_STAIRS);
			ItemStack kd2 = this.mikeItem("Oost", p, Material.STONE);
			ItemStack kd3 = this.mikeItem("Zuid", p, Material.ACACIA_STAIRS);
			ItemStack kd4 = this.mikeItem("West", p, Material.GRASS);
			
			menu.setItem(0, kd1);
			menu.setItem(1, kd2);
			menu.setItem(2, kd3);
			menu.setItem(3, kd4);
			p.openInventory(menu);
			
		}
		if(cmd.getName().equalsIgnoreCase("check-kingdom")) {
			if(args.length < 1) {
				p.sendMessage(ChatColor.RED + "ERROR" + ChatColor.WHITE + " >> " + ChatColor.DARK_RED + "Het commando is onjuist gebruikt: /check-kingdom [kdnaam]");
				return false;
			}
			if(p.hasPermission("kingdom.check")) {
				int count = cm.getConfig().getInt("kdcount." + args[0].toLowerCase());
				if(count == ' ') {
					p.sendMessage(ChatColor.RED + "Kingdom is niet gevonden in de limit list!");
					return false;
				}
				Bukkit.getServer().dispatchCommand(sender, "kd info " + args[0]);
				return false;
			}
			p.sendMessage(ChatColor.RED + "ERROR" + ChatColor.WHITE + " >> " + ChatColor.DARK_RED + "Je hebt onvoldoende rechten!");
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("kdspawn")) {
			String kingdom = um.getConfig().getString("status.kingdom").toLowerCase();
			if(checkKD(p).equalsIgnoreCase("NO-KD")) {
				p.sendMessage(ChatColor.RED + "Oeps, je zit niet in een kingdom dus kan je dit commando niet gebruiken!");
				return false;
			}
			if(!cm.getConfig().contains("kdspawn." + kingdom)) {
				p.sendMessage(ChatColor.RED + "ERROR" + ChatColor.WHITE + " >> " + ChatColor.DARK_RED + "Foutcode S_NOT_DEFINED FOR: " + kingdom);
				return false;
			}
			
			String time = um.getConfig().getString("cooldowndata." + p.getUniqueId().toString() + ".time");
			String cooldowntime = um.getConfig().getString("cooldowndata." + p.getUniqueId().toString() + ".cooldown");
			if(!(time == null)) { 
				Long tijd = Long.valueOf(time);
				Long cdt = Long.valueOf(cooldowntime);
				long left = (tijd/1000 + cdt) - (System.currentTimeMillis()/1000);
				if(left > 0) {
					p.sendMessage(ChatColor.RED + "Je moet nog " + left + " seconden wachten tot je weer kdspawn kan gebruiken!");
					return false;
				}
			}
			if(kdspawnc.containsKey(p.getName())) {
				long left = ((kdspawnc.get(p.getName())/1000)+kdspawntime) - (System.currentTimeMillis()/1000);
				if(left > 0) {
					p.sendMessage(ChatColor.RED + "Je moet nog " + left + " seconden wachten tot je weer kdspawn kan gebruiken!");
					return false;
				}
			}
			
			World world = Bukkit.getWorld(cm.getConfig().getString("kdspawn." + kingdom + ".world"));
			double x = cm.getConfig().getDouble("kdspawn." + kingdom + ".x");
			double y = cm.getConfig().getDouble("kdspawn." + kingdom + ".y");
			double z = cm.getConfig().getDouble("kdspawn." + kingdom + ".z");
			Location loc = new Location(world,x,y,z);
			p.teleport(loc);
			p.sendMessage(ChatColor.GREEN + "Je bent naar de kingdom spawn geteleporteerd van: " + ChatColor.DARK_RED + kingdom);
			kdspawnc.put(p.getName(), System.currentTimeMillis());
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("kdsetspawn")) {
			if(p.hasPermission("kingdom.setspawn")) {
				if(args.length < 1) {
					p.sendMessage(ChatColor.RED + "Geef de naam van een kingdom op!");
					return false;
				}
				String world = p.getWorld().getName();
				double x = p.getLocation().getX();
				double y = p.getLocation().getY();
				double z = p.getLocation().getZ();
				cm.editConfig().set("kdspawn." + args[0].toLowerCase() + ".world", world);
				cm.editConfig().set("kdspawn." + args[0].toLowerCase() + ".x", x);
				cm.editConfig().set("kdspawn." + args[0].toLowerCase() + ".y", y);
				cm.editConfig().set("kdspawn." + args[0].toLowerCase() + ".z", z);
				cm.save();
				p.sendMessage(ChatColor.GREEN + "Spawn voor " + args[0] + " opgeslagen!");
				return false;
			}
			p.sendMessage(ChatColor.RED + "Helaas, je hebt niet het recht dit commando te gebruiken!");
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("setkingdom")) {
			if(p.hasPermission("kingdom.setkingdom")) {
				if(args.length < 2) {
					p.sendMessage(ChatColor.RED + "Dit commando is niet juist gebruikt! " + ChatColor.GREEN + "/setkingdom [speler] [Kingdom]");
					return false;
				}				
				Player target = Bukkit.getPlayer(args[0]);
				if(target == null) {
					p.sendMessage(ChatColor.RED + "Dit commando is niet juist gebruikt! " + ChatColor.GREEN + "/setkingdom [speler] [Kingdom]");
					return false;
				}
				Kingdom kd = kapi.getKingdomHandler().getKingdom(args[1]);
				if(kd == null) {
					p.sendMessage(ChatColor.RED + "ERROR" + ChatColor.WHITE + " >> " + ChatColor.DARK_RED + "Dit kingdom bestaat niet in de config!: ");
					return false;
				}
				
				editOther(target, "status.kingdom", args[1].toUpperCase());
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kd set " + target.getName() + " " + args[1]);
				
				p.sendMessage(ChatColor.GREEN + "Kingdom van " + ChatColor.RED + target.getName() + ChatColor.GREEN + " aangepast naar: " + ChatColor.RED + args[1]);
				
				return false;
			}
			p.sendMessage(ChatColor.RED + "Helaas, je hebt niet het recht dit commando te gebruiken!");
			return false;
		}
		
		if(cmd.getName().equalsIgnoreCase("godhp")) {
			if(p.getName().equalsIgnoreCase("boykev") || p.getName().equalsIgnoreCase("OfficialJoemp") || p.getName().equalsIgnoreCase("PurpleMaster007") || p.getName().equalsIgnoreCase("Herman_Brood")) {
				AttributeInstance ha = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				AttributeInstance ha2 = p.getAttribute(Attribute.GENERIC_ARMOR);
				AttributeInstance ha3 = p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
				if(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() > 20.0) {
					ha.setBaseValue(ha.getDefaultValue());
					ha2.setBaseValue(ha2.getDefaultValue());
					ha3.setBaseValue(ha3.getDefaultValue());
					return false;
				}
				ha.setBaseValue(140.0);
				ha2.setBaseValue(120.0);
				ha3.setBaseValue(120.0);
				p.setHealth(140.0);
				return false;
			}
			p.sendMessage(ChatColor.RED + "Dit commando is alleen beschikbaar voor goden! Ben je van mening dat dit een fout is, neem contact op met de PL");
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("kdsetcolor")) {
			if(!p.hasPermission("kingdom.setcolor")) { p.sendMessage(ChatColor.RED + "Je hebt hier geen permissies voor!"); return false; }
			if(args.length < 2) { p.sendMessage(ChatColor.RED + "Gebruik /kdsetcolor [kingdom] [colorcode]"); return false; }
			String arg1 = args[0].toUpperCase();
			String color = args[1];
			cm.editConfig().set("colors." + arg1, "&" + color);
			cm.save();
			String test = cm.getConfig().getString("colors." + arg1) + "test";
			p.sendMessage("Kleur aangepast naar: " + color + " voor: " + arg1 + " " + ChatColor.translateAlternateColorCodes('&', test));
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("steal")) {
			
			if(kdspawnc2.containsKey(p.getName())) {
				long left = ((kdspawnc.get(p.getName())/1000)+kdspawntime2) - (System.currentTimeMillis()/1000);
				if(left > 0) {
					p.sendMessage(ChatColor.RED + "Je moet nog " + left + " seconden wachten tot je weer een relic mag stelen, leg de huidige relic terug, doe je dit niet kan dit bannable zijn!");
					return false;
				}
			}
			String ukd = kapi.getUserHandler().getUser(p.getName()).getKingdom();
			Kingdom kd = kapi.getKingdomHandler().getKingdom(ukd);
			List<Player> cul = kapi.getKingdomHandler().getOnlineMembers(kd);
			for(Player pl : cul) {
				pl.sendMessage(ChatColor.RED + "/kdspawn is 30 minuten uitgescakeld voor je kingdom door het stelen van een relic!");
				editOther(pl, "cooldowndata." + pl.getUniqueId().toString() + ".time", String.valueOf(System.currentTimeMillis()));
				editOther(pl, "cooldowndata." + pl.getUniqueId().toString() + ".cooldown", "1800");
			}
			return false;
			
			
		}
		
//		if(cmd.getName().equalsIgnoreCase("kd-kick")) {
//			if(args.length < 1) {
//				p.sendMessage(ChatColor.RED + "Dit commando is niet juist gebruikt! " + ChatColor.GREEN + "/kd-kick [speler]");
//				return false;
//			}
//			Player target = Bukkit.getPlayer(args[0]);
//			if(target == null) {
//				p.sendMessage(ChatColor.RED + "Dit commando is niet juist gebruikt! " + ChatColor.GREEN + "/kd-kick [speler]");
//				return false;
//			}
//			String ownkd = um.getConfig().getString("status.kingdom");
//			String otherkd = checkOther(target, "status.kingdom");
//			
//			if(ownkd == otherkd || p.hasPermission("kingdom.admin")) {
//				if(p.hasPermission("kingdom.koning")) {
//					Bukkit.getServer().dispatchCommand(p, "kd kick " + target.getName());
//					editOther(target, "status.kingdom", "NO-KD");
//					p.sendMessage(ChatColor.GREEN + "Je hebt " + ChatColor.RED + target.getName() + ChatColor.GREEN + " uit je kingdom verwijderd! ");
//					return false;
//				}
//				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cERROR &e>>> &4Alleen een koning kan dit commando uitvoeren in je kingdom!"));
//				target.sendMessage(ChatColor.RED + "Je bent door koning " + ChatColor.BLUE + p.getName() + ChatColor.RED + " verwijderd uit je kingdom!");
//				return false;
//			}
//			p.sendMessage(ChatColor.RED + "Dit commando is alleen te gebruiken als je in een kingdom zit!");
//			return false;
//		}
		
		return false;
	}

}
