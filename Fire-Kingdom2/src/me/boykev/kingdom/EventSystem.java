package me.boykev.kingdom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.KingdomCraftApi;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;

import net.md_5.bungee.api.ChatColor;

public class EventSystem implements Listener{
	
	public EventSystem(Main main) {
		EventSystem.instance = main;
	}
	
	KingdomCraft kdc = (KingdomCraft) Bukkit.getPluginManager().getPlugin("KingdomCraft");
	KingdomCraftApi kapi = kdc.getApi();
	
	private static Main instance;
	private static UserManager um;
	private ConfigManager cm;
	
	@EventHandler
	public void FirstJoin(PlayerJoinEvent e) {
		Player p = (Player) e.getPlayer();
		um = new UserManager(instance, p);
		File configFile = new File(instance.getDataFolder() + File.separator + "users", p.getUniqueId().toString() + ".yml");
		if(!configFile.exists()) {
			um.LoadDefaults();
			um.editConfig().set("PlayerName", p.getName().toString());
			um.editConfig().set("status.kingdom", "NO-KD");
			um.save();
			return;
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		Inventory inv = e.getInventory();
		cm = new ConfigManager(instance);
		um = new UserManager(instance, p);
		if(inv.getName().equals(ChatColor.RED + "Kingdom Selector")) {
			if(e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				e.setCancelled(true);
				return;
			}
				if(item.getType() == Material.JUNGLE_WOOD_STAIRS && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Noord")) {
					um.editConfig().set("status.kingdom", "NOORD");
					um.save();
					World world = Bukkit.getWorld(cm.getConfig().getString("kdspawn.noord.world"));
					double x = cm.getConfig().getDouble("kdspawn.noord.x");
					double y = cm.getConfig().getDouble("kdspawn.noord.y");
					double z = cm.getConfig().getDouble("kdspawn.noord.z");
					Location loc = new Location(world,x,y,z);
					KingdomUser user = kapi.getUserHandler().getUser(p);
					Kingdom kd = kapi.getKingdomHandler().getKingdom(um.getConfig().getString("status.kingdom").toLowerCase());
					kapi.getUserHandler().setKingdom(user, kd);
					p.teleport(loc);
					return;
				}
				if(item.getType() == Material.STONE && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Oost")) {
					um.editConfig().set("status.kingdom", "OOST");
					um.save();
					World world = Bukkit.getWorld(cm.getConfig().getString("kdspawn.oost.world"));
					double x = cm.getConfig().getDouble("kdspawn.oost.x");
					double y = cm.getConfig().getDouble("kdspawn.oost.y");
					double z = cm.getConfig().getDouble("kdspawn.oost.z");
					Location loc = new Location(world,x,y,z);
					KingdomUser user = kapi.getUserHandler().getUser(p);
					Kingdom kd = kapi.getKingdomHandler().getKingdom(um.getConfig().getString("status.kingdom").toLowerCase());
					kapi.getUserHandler().setKingdom(user, kd);
					p.teleport(loc);
					return;
				}
				if(item.getType() == Material.ACACIA_STAIRS && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Zuid")) {
					um.editConfig().set("status.kingdom", "ZUID");
					um.save();
					World world = Bukkit.getWorld(cm.getConfig().getString("kdspawn.zuid.world"));
					double x = cm.getConfig().getDouble("kdspawn.zuid.x");
					double y = cm.getConfig().getDouble("kdspawn.zuid.y");
					double z = cm.getConfig().getDouble("kdspawn.zuid.z");
					Location loc = new Location(world,x,y,z);
					KingdomUser user = kapi.getUserHandler().getUser(p);
					Kingdom kd = kapi.getKingdomHandler().getKingdom(um.getConfig().getString("status.kingdom").toLowerCase());
					kapi.getUserHandler().setKingdom(user, kd);
					p.teleport(loc);
					return;
				}
				if(item.getType() == Material.GRASS && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "West")) {
					um.editConfig().set("status.kingdom", "WEST");
					um.save();
					World world = Bukkit.getWorld(cm.getConfig().getString("kdspawn.west.world"));
					double x = cm.getConfig().getDouble("kdspawn.west.x");
					double y = cm.getConfig().getDouble("kdspawn.west.y");
					double z = cm.getConfig().getDouble("kdspawn.west.z");
					Location loc = new Location(world,x,y,z);
					KingdomUser user = kapi.getUserHandler().getUser(p);
					Kingdom kd = kapi.getKingdomHandler().getKingdom(um.getConfig().getString("status.kingdom").toLowerCase());
					kapi.getUserHandler().setKingdom(user, kd);
					p.teleport(loc);
					return;
				}
				return;
		}
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
	@EventHandler
	public void kingdomKoningClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		Inventory inv = e.getInventory();
		cm = new ConfigManager(instance);
		um = new UserManager(instance, p);
		if(inv.getName().equals(ChatColor.RED + "Kingdom Administrator") || inv.getName().equals(ChatColor.GREEN + "Rangen Menu")) {
			if(e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				e.setCancelled(true);
				return;
			}
				if(item.getType() == Material.BARRIER && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Kick Speler")) {
					List<String> lore = item.getItemMeta().getLore();
					String pl = ChatColor.stripColor(lore.get(1).toString());
					Player player = Bukkit.getPlayer(pl);
					if(player == null) {
						p.sendMessage(ChatColor.RED + "Er is iets fout gegaan, contact een admin!");
						return;
					}
					editOther(player, "status.kingdom", "NO-KD");
					player.sendMessage(ChatColor.RED + "Je bent door koning " + ChatColor.BLUE + p.getName() + ChatColor.RED + " uit het Kingdom gezet!");
					p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " successvol verwijderd uit je kingdom!");
					KingdomUser ku = kapi.getUserHandler().getUser(player);
					ku.setKingdom(null);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kd kick " + player.getName());
					p.closeInventory();
					kapi.getUserHandler().save(ku);
					String kd = um.getConfig().getString("status.kingdom");
					if(cm.getConfig().getConfigurationSection("limit." + kd.toLowerCase()) != null) {
						Integer current = cm.getConfig().getInt("players." + kd.toLowerCase());
						cm.editConfig().set("players." + kd.toLowerCase(), current -1);
						cm.save();
					}
					return;
				}
				if(item.getType() == Material.ARROW && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Speler Rank")) {
					List<String> lore = item.getItemMeta().getLore();
					String pl = ChatColor.stripColor(lore.get(1).toString());
					Player target = Bukkit.getPlayer(pl);
					if(target == null) {
						p.sendMessage(ChatColor.RED + "Er is iets fout gegaan, contact een admin!");
						return;
					}
					
					
					Inventory uinv = KoningenSysteem.makeInv(target, ChatColor.GREEN + "Rangen Menu", 9);
					ArrayList<String> list = new ArrayList<String>();
					list.add(ChatColor.RED + "Wijzig rang van:");
					list.add(ChatColor.BLUE + target.getName());
					ItemStack i1 = KoningenSysteem.makeItem(ChatColor.GRAY + "Soldaat", list, Material.WOOD_SWORD, 1);
					ItemStack i2 = KoningenSysteem.makeItem(ChatColor.RED + "Generaal", list, Material.STONE_SWORD, 1);
					ItemStack i3 = KoningenSysteem.makeItem(ChatColor.DARK_GREEN + "Hertog", list, Material.GOLD_SWORD, 1);
					
					uinv.setItem(0, i1);
					uinv.setItem(1, i2);
					uinv.setItem(2, i3);
					p.openInventory(uinv);
					
				}
		}
		if(inv.getName().equals(ChatColor.GREEN + "Rangen Menu")) {
			if(e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				e.setCancelled(true);
				return;
			}
				if(item.getType() == Material.WOOD_SWORD && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "Soldaat")) {
					List<String> lore = item.getItemMeta().getLore();
					String pl = ChatColor.stripColor(lore.get(1).toString());
					Player player = Bukkit.getPlayer(pl);
					if(player == null) {
						p.sendMessage(ChatColor.RED + "Er is iets fout gegaan, contact een admin!");
						return;
					}
					player.sendMessage(ChatColor.RED + "Je bent door koning " + ChatColor.BLUE + p.getName() + ChatColor.RED + " naar soldaat gezet!");
					p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " successvol gewijzigd naar Soldaat!");
					KingdomUser ku = kapi.getUserHandler().getUser(player);
					ku.setRank("Soldaat");
					kapi.getUserHandler().save(ku);
					p.closeInventory();
					return;
				}
				if(item.getType() == Material.STONE_SWORD && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Generaal")) {
					List<String> lore = item.getItemMeta().getLore();
					String pl = ChatColor.stripColor(lore.get(1).toString());
					Player player = Bukkit.getPlayer(pl);
					if(player == null) {
						p.sendMessage(ChatColor.RED + "Er is iets fout gegaan, contact een admin!");
						return;
					}
					player.sendMessage(ChatColor.RED + "Je bent door koning " + ChatColor.BLUE + p.getName() + ChatColor.RED + " naar Generaal gezet!");
					p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " successvol gewijzigd naar Generaal!");
					KingdomUser ku = kapi.getUserHandler().getUser(player);
					ku.setRank("Generaal");
					kapi.getUserHandler().save(ku);
					p.closeInventory();
					return;
				}
				if(item.getType() == Material.GOLD_SWORD && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "Hertog")) {
					List<String> lore = item.getItemMeta().getLore();
					String pl = ChatColor.stripColor(lore.get(1).toString());
					Player player = Bukkit.getPlayer(pl);
					if(player == null) {
						p.sendMessage(ChatColor.RED + "Er is iets fout gegaan, contact een admin!");
						return;
					}
					player.sendMessage(ChatColor.RED + "Je bent door koning " + ChatColor.BLUE + p.getName() + ChatColor.RED + " naar Hertog gezet!");
					p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " successvol gewijzigd naar Hertog!");
					KingdomUser ku = kapi.getUserHandler().getUser(player);
					ku.setRank("Hertog");
					kapi.getUserHandler().save(ku);
					p.closeInventory();
					return;
				}
		}
	}
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		if(p == null) {
			return;
		}
		if(p.getName().equalsIgnoreCase("boykev") || p.getName().equalsIgnoreCase("OfficialJoemp")) {
			p.setFoodLevel(25);
			e.setCancelled(true);
			return;
		}
		
	}
	
	
}
