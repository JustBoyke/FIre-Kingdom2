package me.boykev.kingdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.bukkit.inventory.meta.SkullMeta;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import me.codedred.playtimes.api.TimelessPlayer;

public class KoningenSysteem implements Listener, CommandExecutor {
	
	private static Main instance;
	KingdomCraft kdc = KingdomCraftProvider.get();
	private ConfigManager cm;
	
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
	
    public static ItemStack createCivilizationItem(String name, boolean bordersClosed, OfflinePlayer p) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + name);

        // Replace "coenispro" with the appropriate player name for each civilization
        meta.setOwningPlayer(p);

        List<String> lore = new ArrayList<String>();

        if (bordersClosed) {
            lore.add(ChatColor.RED + "Borders are closed!");
            lore.add(ChatColor.GRAY + "Entering this Civilization can cause/can be seen as:");
            lore.add(ChatColor.GRAY + "- Death");
            lore.add(ChatColor.GRAY + "- Imprisonment");
            lore.add(ChatColor.GRAY + "- Act of war");
            lore.add(ChatColor.GRAY + "- Revenge");
        } else {
            lore.add(ChatColor.GREEN + "Borders are open!");
            lore.add(ChatColor.GRAY + "Please consult a duke or higher before visiting.");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
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
	
	public boolean isInt(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}

	
	public static HashMap<Player, Integer> invite = new HashMap<Player, Integer>();
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("civadmin")) {
			if(args.length < 1) {
				p.sendMessage(ChatColor.RED + "Please use /civdadmin [playername]");
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
		if(cmd.getName().equalsIgnoreCase("civsethome")) {
			if(args.length < 1) {
				p.sendMessage(ChatColor.RED + "Please use /civsethome [number from 1 - 4]");
				return false;
			}
			if(this.isInt(args[0]) == false) {
				p.sendMessage(ChatColor.RED + "Please use a number from 1 to 4");
				return false;
			}
			int number = Integer.parseInt(args[0]);
			User ku = kdc.getOnlineUser(p.getName());
			if(ku.getKingdom() == null) {
				p.sendMessage(ChatColor.RED + "You don`t have a Civilization.");
				return false;
			}
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
			if(ku.getRank().getName().equalsIgnoreCase("king") || ku.getRank().getName().equalsIgnoreCase("queen")) {
				String kdname = ku.getKingdom().getName();
				cm = new ConfigManager(instance);
				Location loc = p.getLocation();
				cm.getConfig().set(kdname + ".homes." + number, loc);
				cm.save();
				p.sendMessage(ChatColor.GREEN + "Home " + number + " has been set to your current location!");
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
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "civ setkingdom " + p.getName() + " " + args[1]);
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
		if(cmd.getName().equalsIgnoreCase("removebox")) {
			if(args.length < 1) {
				p.sendMessage(ChatColor.RED + "Use /removebox [user]");
				return false;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				p.sendMessage(ChatColor.RED + "This player does not exist or is not online!");
				return false;
			}
			cm = new ConfigManager(instance);
			cm.editConfig().set("snailmail." + target.getUniqueId(), false);
			cm.save();
			p.sendMessage(ChatColor.GREEN + "Box removed for " + target.getName());
			target.sendMessage(ChatColor.GREEN + "A admin has removed your snailmail box, you are now able to replace the box!");
			return false;
			
			
		}
		if(cmd.getName().equalsIgnoreCase("online")) {
			if(!p.hasPermission("civ.online")) {
				p.sendMessage(ChatColor.RED + "Im sorry, you don`t have the permissions for this command!");
				return false;
			}
			Kingdom solaria = kdc.getKingdom("Solaria");
			int solariaonline = 0;
			Kingdom entrava = kdc.getKingdom("Entrava");
			int entravaonline = 0;
			Kingdom sparta = kdc.getKingdom("Sparta");
			int spartaonline = 0;
			Kingdom ladier = kdc.getKingdom("Ladier");
			int ladieronline = 0;
			Kingdom ridderhof = kdc.getKingdom("Ridderhof");
			int ridderhofonline = 0;
			
			for(Player pl : Bukkit.getOnlinePlayers()) {
				User ku = kdc.getOnlineUser(pl.getName());
				if(ku.getKingdom().getName().equalsIgnoreCase(solaria.getName())) {
					solariaonline = solariaonline +1;
				}
				if(ku.getKingdom().getName().equalsIgnoreCase(entrava.getName())) {
					entravaonline = entravaonline +1;
				}
				if(ku.getKingdom().getName().equalsIgnoreCase(sparta.getName())) {
					spartaonline = spartaonline +1;
				}
				if(ku.getKingdom().getName().equalsIgnoreCase(ladier.getName())) {
					ladieronline = ladieronline +1;
				}
				if(ku.getKingdom().getName().equalsIgnoreCase(ridderhof.getName())) {
					ridderhofonline = ridderhofonline +1;
				}
			}
			
			int solmembers = solaria.getMemberCount() + 2;
			
//			int solcalc = solmembers / 2;
			int entrcalc = entrava.getMemberCount() / 2;
			int spacalc = sparta.getMemberCount() / 2;
			int ladcalc = ladier.getMemberCount() / 2;
			int ridcalc = ridderhof.getMemberCount() / 2;
			
//			int solwarcalc = solaria.getMaxMembers() - 2;
			
			//Solaria Item creation
			ItemStack sol = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta solim = (SkullMeta) sol.getItemMeta();
			OfflinePlayer solp = Bukkit.getOfflinePlayer("coenispro");
			solim.setOwner(solp.getName());
			solim.setDisplayName(ChatColor.GOLD + "Civilization " + ChatColor.translateAlternateColorCodes('&', solaria.getDisplay()));
			List<String> sollore = new ArrayList<String>();
			sollore.add(ChatColor.GREEN + "King: " + ChatColor.GOLD + solp.getName());
			sollore.add(ChatColor.GREEN + "Members: " + ChatColor.GOLD + solmembers);
			if(solariaonline >= 5) {
				sollore.add(ChatColor.LIGHT_PURPLE + "Trolling allowed");
			}else {
				sollore.add(ChatColor.GRAY + "Trolling prohibited");
			}
			if(solariaonline > 5) {
		    	sollore.add(ChatColor.LIGHT_PURPLE + "War allowed");
		    }else {
		    	sollore.add(ChatColor.GRAY + "War prohibited");
		    }
			solim.setLore(sollore);
			sol.setItemMeta(solim);
			//end of item creation for solaria
			
		    // Entrava Item creation
		    ItemStack ent = new ItemStack(Material.PLAYER_HEAD);
		    SkullMeta entim = (SkullMeta) ent.getItemMeta();
		    OfflinePlayer entp = Bukkit.getOfflinePlayer("Murrunnn");
		    entim.setOwner(entp.getName());
		    entim.setDisplayName(ChatColor.GOLD + "Civilization " + ChatColor.translateAlternateColorCodes('&', entrava.getDisplay()));
		    List<String> entlore = new ArrayList<String>();
		    entlore.add(ChatColor.GREEN + "King: " + ChatColor.GOLD + entp.getName());
		    entlore.add(ChatColor.GREEN + "Members: " + ChatColor.GOLD + entrava.getMemberCount());
		    if(entravaonline >= entrcalc) {
		    	entlore.add(ChatColor.LIGHT_PURPLE + "Trolling allowed");
			}else {
				entlore.add(ChatColor.GRAY + "Trolling prohibited");
			}
		    if(entravaonline > entrcalc) {
		    	entlore.add(ChatColor.LIGHT_PURPLE + "War allowed");
		    }else {
		    	entlore.add(ChatColor.GRAY + "War prohibited");
		    }
		    entim.setLore(entlore);
		    ent.setItemMeta(entim);
		    
		    // Sparta Item creation
		    ItemStack spa = new ItemStack(Material.PLAYER_HEAD);
		    SkullMeta spaim = (SkullMeta) spa.getItemMeta();
		    OfflinePlayer spap = Bukkit.getOfflinePlayer("BARACOS123456789");
		    spaim.setOwner(spap.getName());
		    spaim.setDisplayName(ChatColor.GOLD + "Civilization " + ChatColor.translateAlternateColorCodes('&', sparta.getDisplay()));
		    List<String> spalore = new ArrayList<String>();
		    spalore.add(ChatColor.GREEN + "King: " + ChatColor.GOLD + spap.getName());
		    spalore.add(ChatColor.GREEN + "Members: " + ChatColor.GOLD + sparta.getMemberCount());
		    if(spartaonline >= spacalc) {
		    	spalore.add(ChatColor.LIGHT_PURPLE + "Trolling allowed");
			}else {
				spalore.add(ChatColor.GRAY + "Trolling prohibited");
			}
		    if(spartaonline > spacalc) {
		    	spalore.add(ChatColor.LIGHT_PURPLE + "War allowed");
		    }else {
		    	spalore.add(ChatColor.GRAY + "War prohibited");
		    }
		    spaim.setLore(spalore);
		    spa.setItemMeta(spaim);
		    
		    // Ladier Item creation
		    ItemStack lad = new ItemStack(Material.PLAYER_HEAD);
		    SkullMeta ladim = (SkullMeta) lad.getItemMeta();
		    OfflinePlayer ladp = Bukkit.getOfflinePlayer("DeathStep1");
		    ladim.setOwner(ladp.getName());
		    ladim.setDisplayName(ChatColor.GOLD + "Civilization " + ChatColor.translateAlternateColorCodes('&', ladier.getDisplay()));
		    List<String> ladlore = new ArrayList<String>();
		    ladlore.add(ChatColor.GREEN + "King: " + ChatColor.GOLD + ladp.getName());
		    ladlore.add(ChatColor.GREEN + "Members: " + ChatColor.GOLD + ladier.getMemberCount());
		    if(ladieronline >= ladcalc) {
		    	ladlore.add(ChatColor.LIGHT_PURPLE + "Trolling allowed");
			}else {
				ladlore.add(ChatColor.GRAY + "Trolling prohibited");
			}
		    if(ladieronline > ladcalc) {
		    	ladlore.add(ChatColor.LIGHT_PURPLE + "War allowed");
		    }else {
		    	ladlore.add(ChatColor.GRAY + "War prohibited");
		    }
		    ladim.setLore(ladlore);
		    lad.setItemMeta(ladim);
		    
		    // Ridderhof Item creation
		    ItemStack rid = new ItemStack(Material.PLAYER_HEAD);
		    SkullMeta ridim = (SkullMeta) rid.getItemMeta();
		    OfflinePlayer ridp = Bukkit.getOfflinePlayer("OfficialJoemp");
		    ridim.setDisplayName(ChatColor.GOLD + "Civilization " + ChatColor.translateAlternateColorCodes('&', ridderhof.getDisplay()));
		    ridim.setOwner(ridp.getName());
		    List<String> ridlore = new ArrayList<String>();
		    ridlore.add(ChatColor.GREEN + "King: " + ChatColor.GOLD + ridp.getName());
		    ridlore.add(ChatColor.GREEN + "Members: " + ChatColor.GOLD + ridderhof.getMemberCount());
		    if(ridderhofonline >= ridcalc) {
		    	ridlore.add(ChatColor.LIGHT_PURPLE + "Trolling allowed");
			}else {
				ridlore.add(ChatColor.GRAY + "Trolling prohibited");
			}
		    if(ridderhofonline > ridcalc) {
		    	ridlore.add(ChatColor.LIGHT_PURPLE + "War allowed");
		    }else {
		    	ridlore.add(ChatColor.GRAY + "War prohibited");
		    }
		    ridim.setLore(ridlore);
		    rid.setItemMeta(ridim);
		    
		    Inventory mainmenu = makeInv(p, ChatColor.RED + "Civilization Onlinemenu", 9);
		    mainmenu.setItem(0, sol);
		    mainmenu.setItem(1, ent);
		    mainmenu.setItem(2, spa);
		    mainmenu.setItem(3, lad);
		    mainmenu.setItem(4, rid);
		    p.openInventory(mainmenu);
			
		}
		if(cmd.getName().equalsIgnoreCase("borders")) {
			cm = new ConfigManager(instance);
			
			if(args.length > 0) {
				
				if(args.length > 1) {
					p.sendMessage(ChatColor.RED + "You added to much arguments!");
					return false;
				}
				
				User ku = kdc.getOnlineUser(p.getUniqueId());
				Rank kr = ku.getRank();
				int level = kdc.getOnlineUser(p.getUniqueId()).getRank().getLevel();
				if(ku.getKingdom() == null) {
					p.sendMessage(ChatColor.RED + "You can't do this if you are not in a civilization!");
					return false;
				}
				System.out.println(kdc.getOnlineUser(p.getUniqueId()).getRank().getLevel());
				if(!(level >= 28)) {
					p.sendMessage(ChatColor.RED + "This command is available for a duke or higer!");
					p.sendMessage(ChatColor.GRAY + "You are a: " + kr.getName());
					return false;
				}
				String kingdom = ku.getKingdom().getName();
				String kingdomLowcase = kingdom.toLowerCase();
				if(!args[0].equalsIgnoreCase("true") && !args[0].equalsIgnoreCase("false")) {
					p.sendMessage(ChatColor.RED + "Please use true or false!");
					return false;
				}
				if(args[0].equalsIgnoreCase("true")) {
					if(cm.getConfig().getBoolean("border." + kingdomLowcase) == true) {
						p.sendMessage(ChatColor.RED + "Borders already closed!");
						return false;
					}
					cm.editConfig().set("border." + kingdomLowcase, true);
					cm.save();
					p.sendMessage(ChatColor.RED + "Borders are now closed!");
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ku.getKingdom().getDisplay() + ChatColor.GRAY + " has closed their borders!"));
				}else {
					if(cm.getConfig().getBoolean("border." + kingdomLowcase) == false) {
						p.sendMessage(ChatColor.RED + "Borders already open!");
						return false;
					}
					cm.editConfig().set("border." + kingdomLowcase, false);
					cm.save();
					p.sendMessage(ChatColor.GREEN + "Borders are now opened!");
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ku.getKingdom().getDisplay() + ChatColor.GRAY + " has opened their borders!"));
				}
				return false;
			}else {
				boolean solaria = cm.getConfig().getBoolean("border.solaria") || false;
				boolean entrava = cm.getConfig().getBoolean("border.entrava") || false;
				boolean sparta = cm.getConfig().getBoolean("border.sparta") || false;
				boolean ridderhof = cm.getConfig().getBoolean("border.ridderhof") || false;
				boolean ladier = cm.getConfig().getBoolean("border.ladier") || false;
				
				ItemStack solariaItem = createCivilizationItem("Solaria", solaria, Bukkit.getOfflinePlayer("coenispro"));
				ItemStack entravaItem = createCivilizationItem("Entrava", entrava, Bukkit.getOfflinePlayer("Murrunnn"));
				ItemStack spartaItem = createCivilizationItem("Sparta", sparta, Bukkit.getOfflinePlayer("BARACOS123456789"));
		        ItemStack ridderhofItem = createCivilizationItem("Ridderhof", ridderhof, Bukkit.getOfflinePlayer("officialjoemp"));
		        ItemStack ladierItem = createCivilizationItem("Ladier", ladier, Bukkit.getOfflinePlayer("deathstep1"));
		        
		        Inventory menu = makeInv(p, ChatColor.RED + "Civilization Border State", 9);
		        menu.setItem(0, solariaItem);
		        menu.setItem(1, entravaItem);
		        menu.setItem(2, spartaItem);
		        menu.setItem(3, ridderhofItem);
		        menu.setItem(4, ladierItem);
				p.openInventory(menu);
				return false;
			}
		}
		
		return false;
	}
	
	
}
