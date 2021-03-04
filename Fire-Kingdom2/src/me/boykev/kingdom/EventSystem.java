package me.boykev.kingdom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

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
	
	public int loadBalance() {
		List<KingdomUser> scand = kapi.getKingdomHandler().getMembers(kapi.getKingdomHandler().getKingdom("Nagard"));
		List<KingdomUser> wam = kapi.getKingdomHandler().getMembers(kapi.getKingdomHandler().getKingdom("Histria"));
		List<KingdomUser> af = kapi.getKingdomHandler().getMembers(kapi.getKingdomHandler().getKingdom("Afrika"));
		int kd1 = scand.size();
		int kd2 = wam.size();
		int kd3 = af.size();
		
		int calc1 = kd1 + kd2 + kd3;
		int calc2 = (calc1 / 3) + 1;
		return calc2;
	}
	
	public boolean theBalancer(String kingdom) {
		List<KingdomUser> kd = kapi.getKingdomHandler().getMembers(kapi.getKingdomHandler().getKingdom(kingdom));
		int kdcount = kd.size();
		int balancecount = this.loadBalance();
		if(kdcount > balancecount) {
			return false;
		}
		return true;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		Inventory inv = e.getInventory();
		cm = new ConfigManager(instance);
		um = new UserManager(instance, p);
		if(inv.getName().equals(ChatColor.RED + "Kingdom Selector")) {
			e.setCancelled(true);
				if(item.getType() == Material.STONE && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Nagard")) {
					if(this.theBalancer("Scandinavie") == false) {
						Bukkit.broadcastMessage("test1");
						p.sendMessage(ChatColor.RED + "Dit kingdom is momenteel vol, probeer een andere te joinen");
						return;
					}
					um.editConfig().set("status.kingdom", "Nagard");
					um.save();
					World world = Bukkit.getWorld(cm.getConfig().getString("kdspawn.nagard.world"));
					double x = cm.getConfig().getDouble("kdspawn.nagard.x");
					double y = cm.getConfig().getDouble("kdspawn.nagard.y");
					double z = cm.getConfig().getDouble("kdspawn.nagard.z");
					Location loc = new Location(world,x,y,z);
					KingdomUser user = kapi.getUserHandler().getUser(p);
					Kingdom kd = kapi.getKingdomHandler().getKingdom("Nagard");
					kapi.getUserHandler().setKingdom(user, kd);
					p.teleport(loc);
					return;
				}
				if(item.getType() == Material.SAND && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Histria")) {
					if(this.theBalancer("Histria") == false) {
						p.sendMessage(ChatColor.RED + "Dit kingdom is momenteel vol, probeer een andere te joinen");
						return;
					}
					um.editConfig().set("status.kingdom", "Histria");
					um.save();
					World world = Bukkit.getWorld(cm.getConfig().getString("kdspawn.histria.world"));
					double x = cm.getConfig().getDouble("kdspawn.histria.x");
					double y = cm.getConfig().getDouble("kdspawn.histria.y");
					double z = cm.getConfig().getDouble("kdspawn.histria.z");
					Location loc = new Location(world,x,y,z);
					KingdomUser user = kapi.getUserHandler().getUser(p);
					Kingdom kd = kapi.getKingdomHandler().getKingdom("Histria");
					kapi.getUserHandler().setKingdom(user, kd);
					p.teleport(loc);
					return;
				}
				if(item.getType() == Material.DEAD_BUSH && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Ravary")) {
					if(this.theBalancer("Ravary") == false) {
						p.sendMessage(ChatColor.RED + "Dit kingdom is momenteel vol, probeer een andere te joinen");
						return;
					}
					um.editConfig().set("status.kingdom", "Ravary");
					um.save();
					World world = Bukkit.getWorld(cm.getConfig().getString("kdspawn.ravary.world"));
					double x = cm.getConfig().getDouble("kdspawn.ravary.x");
					double y = cm.getConfig().getDouble("kdspawn.ravary.y");
					double z = cm.getConfig().getDouble("kdspawn.ravary.z");
					Location loc = new Location(world,x,y,z);
					KingdomUser user = kapi.getUserHandler().getUser(p);
					Kingdom kd = kapi.getKingdomHandler().getKingdom("Ravary");
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
					ItemStack l1 = KoningenSysteem.makeItem(ChatColor.RED + "Generaal", list, Material.DIAMOND_SWORD, 1);
					ItemStack l2 = KoningenSysteem.makeItem(ChatColor.RED + "Luitenant", list, Material.IRON_SWORD, 1);
					ItemStack l3 = KoningenSysteem.makeItem(ChatColor.RED + "Soldaat", list, Material.GOLD_SWORD, 1);
					
					ItemStack h1 = KoningenSysteem.makeItem(ChatColor.DARK_GREEN + "Hertog", list, Material.DIAMOND_BLOCK, 1);
					ItemStack h2 = KoningenSysteem.makeItem(ChatColor.DARK_GREEN + "Raadgever", list, Material.IRON_BLOCK, 1);
					ItemStack h3 = KoningenSysteem.makeItem(ChatColor.DARK_GREEN + "Handelaar", list, Material.GOLD_BLOCK, 1);
					ItemStack h4 = KoningenSysteem.makeItem(ChatColor.DARK_GREEN + "Stuurmeester", list, Material.POTION, 1);
					ItemStack h5 = KoningenSysteem.makeItem(ChatColor.GRAY + "Dorpeling", list, Material.STONE, 1);
					
					
					uinv.setItem(0, h5);
					uinv.setItem(1, l3);
					uinv.setItem(2, h3);
					uinv.setItem(3, l2);
					uinv.setItem(4, h2);
					uinv.setItem(5, l1);
					uinv.setItem(6, h1);
					uinv.setItem(7, h4);
					p.openInventory(uinv);
					
				}
		}
		if(inv.getName().equals(ChatColor.GREEN + "Rangen Menu")) {
			if(e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				e.setCancelled(true);
				return;
			}
			if(item.getType() == Material.DIAMOND_SWORD && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Generaal")) {
				List<String> lore = item.getItemMeta().getLore();
				String pl = ChatColor.stripColor(lore.get(1).toString());
				Player player = Bukkit.getPlayer(pl);
				if(player == null) {
					p.sendMessage(ChatColor.RED + "Er is iets fout gegaan, contact een admin!");
					return;
				}
				player.sendMessage(ChatColor.RED + "Je bent door koning " + ChatColor.BLUE + p.getName() + ChatColor.RED + " naar generaal gezet!");
				p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " successvol gewijzigd naar Generaal!");
				KingdomUser ku = kapi.getUserHandler().getUser(player);
				ku.setRank("Generaal");
				kapi.getUserHandler().save(ku);
				p.closeInventory();
				return;
			}
			
			if(item.getType() == Material.IRON_SWORD && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Luitenant")) {
				List<String> lore = item.getItemMeta().getLore();
				String pl = ChatColor.stripColor(lore.get(1).toString());
				Player player = Bukkit.getPlayer(pl);
				if(player == null) {
					p.sendMessage(ChatColor.RED + "Er is iets fout gegaan, contact een admin!");
					return;
				}
				player.sendMessage(ChatColor.RED + "Je bent door koning " + ChatColor.BLUE + p.getName() + ChatColor.RED + " naar Luitenant gezet!");
				p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " successvol gewijzigd naar Luitenant!");
				KingdomUser ku = kapi.getUserHandler().getUser(player);
				ku.setRank("Luitenant");
				kapi.getUserHandler().save(ku);
				p.closeInventory();
				return;
			}
			
			if(item.getType() == Material.GOLD_SWORD && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Soldaat")) {
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
			
			if(item.getType() == Material.DIAMOND_BLOCK && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "Hertog")) {
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
			
			if(item.getType() == Material.IRON_BLOCK && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "Raadgever")) {
				List<String> lore = item.getItemMeta().getLore();
				String pl = ChatColor.stripColor(lore.get(1).toString());
				Player player = Bukkit.getPlayer(pl);
				if(player == null) {
					p.sendMessage(ChatColor.RED + "Er is iets fout gegaan, contact een admin!");
					return;
				}
				player.sendMessage(ChatColor.RED + "Je bent door koning " + ChatColor.BLUE + p.getName() + ChatColor.RED + " naar Raadgever gezet!");
				p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " successvol gewijzigd naar Raadgever!");
				KingdomUser ku = kapi.getUserHandler().getUser(player);
				ku.setRank("Raadgever");
				kapi.getUserHandler().save(ku);
				p.closeInventory();
				return;
			}
			
			if(item.getType() == Material.GOLD_BLOCK && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "Handelaar")) {
				List<String> lore = item.getItemMeta().getLore();
				String pl = ChatColor.stripColor(lore.get(1).toString());
				Player player = Bukkit.getPlayer(pl);
				if(player == null) {
					p.sendMessage(ChatColor.RED + "Er is iets fout gegaan, contact een admin!");
					return;
				}
				player.sendMessage(ChatColor.RED + "Je bent door koning " + ChatColor.BLUE + p.getName() + ChatColor.RED + " naar Handelaar gezet!");
				p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " successvol gewijzigd naar Handelaar!");
				KingdomUser ku = kapi.getUserHandler().getUser(player);
				ku.setRank("Handelaar");
				kapi.getUserHandler().save(ku);
				p.closeInventory();
				return;
			}
			
			if(item.getType() == Material.POTION && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "Stuurmeester")) {
				List<String> lore = item.getItemMeta().getLore();
				String pl = ChatColor.stripColor(lore.get(1).toString());
				Player player = Bukkit.getPlayer(pl);
				if(player == null) {
					p.sendMessage(ChatColor.RED + "Er is iets fout gegaan, contact een admin!");
					return;
				}
				player.sendMessage(ChatColor.RED + "Je bent door koning " + ChatColor.BLUE + p.getName() + ChatColor.RED + " naar Stuurmeester gezet!");
				p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " successvol gewijzigd naar Stuurmeester!");
				KingdomUser ku = kapi.getUserHandler().getUser(player);
				ku.setRank("Stuurmeester");
				kapi.getUserHandler().save(ku);
				p.closeInventory();
				return;
			}
			
			if(item.getType() == Material.STONE && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "Dorpeling")) {
				List<String> lore = item.getItemMeta().getLore();
				String pl = ChatColor.stripColor(lore.get(1).toString());
				Player player = Bukkit.getPlayer(pl);
				if(player == null) {
					p.sendMessage(ChatColor.RED + "Er is iets fout gegaan, contact een admin!");
					return;
				}
				player.sendMessage(ChatColor.RED + "Je bent door koning " + ChatColor.BLUE + p.getName() + ChatColor.RED + " naar Dorpeling gezet!");
				p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " successvol gewijzigd naar Dorpeling!");
				KingdomUser ku = kapi.getUserHandler().getUser(player);
				ku.setRank("Dorpeling");
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
		if(p.getName().equalsIgnoreCase("MyrAdonis")) {
			p.setFoodLevel(25);
			e.setCancelled(true);
			return;
		}
		
	}
	

	@EventHandler
	public void godfallDamage(EntityDamageEvent e) {
		Entity entity = e.getEntity();
	    if(entity.getName().equalsIgnoreCase("MyrAdonis")) {
	    	Player p = Bukkit.getPlayer("MyrAdonis");
	    	if(!p.hasPotionEffect(PotionEffectType.WEAKNESS)) {
	    		if (e.getCause().equals(DamageCause.FALL)){
					e.setDamage(0.0);
					e.setCancelled(true);
					return;
				}
	    	}
	      }
		
		Player p = Bukkit.getPlayer(entity.getName());
		if(p == null) { return; }
		if(p.getInventory().contains(Material.BLAZE_ROD)){
			if (e.getCause().equals(DamageCause.FALL)){
				e.setDamage(0.0);
				e.setCancelled(false);
				return;
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(e.getBlock().getType() == Material.BEACON) {
			if(!e.getPlayer().hasPermission("break.beacon")) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "Je kan een beacon niet breken, deze wordt beschermd door godelijke krachten!");
				return;
			}
		}
	}
	
}
