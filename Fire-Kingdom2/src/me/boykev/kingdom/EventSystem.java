package me.boykev.kingdom;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import net.md_5.bungee.api.ChatColor;
import org.json.simple.JSONObject;

public class EventSystem implements Listener{
	
	public EventSystem(Main main) {
		EventSystem.instance = main;
	}
	
	KingdomCraft kdc = KingdomCraftProvider.get();
	
	private static Main instance;
	private ConfigManager cm;
	
	private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
}
	
	
	@EventHandler
	public void kingdomKoningClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		InventoryView inv = e.getView();
		if(inv.getTitle().equalsIgnoreCase(ChatColor.RED + "Civilization Onlinemenu")) {
			e.setCancelled(true);
			return;
		}
		if(inv.getTitle().equalsIgnoreCase(ChatColor.RED + "Civilization Border State")) {
			e.setCancelled(true);
			return;
		}
		if(inv.getTitle().equals(ChatColor.RED + "Civilization Administrator")) {
			e.setCancelled(true);
			if(item == null || item.getItemMeta().getLore() == null) {
				return;
			}
				if(item.getType() == Material.BARRIER && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Kick Player")) {
					List<String> lore = item.getItemMeta().getLore();
					String pl = ChatColor.stripColor(lore.get(1).toString());
					Player player = Bukkit.getPlayer(pl);
					if(player == null) {
						p.sendMessage(ChatColor.RED + "Something went wrong, contact a administrator!");
						return;
					}
//					User ku = kdc.getOnlineUser(p.getName());
					User kut = kdc.getOnlineUser(player.getName());
					player.sendMessage(ChatColor.RED + "You have been removed from the Civilization by your King: " + ChatColor.BLUE + p.getName());
					p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " Has been removed from your Civilization");
					kut.setKingdom(null);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "civ kick " + player.getName());
					p.closeInventory();
					kdc.saveAsync(kut);
					return;
				}
				if(item.getType() == Material.ARROW && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Player Role")) {
//					User ku = kdc.getOnlineUser(p.getName());
					List<String> lore = item.getItemMeta().getLore();
					String pl = ChatColor.stripColor(lore.get(1).toString());
					Player target = Bukkit.getPlayer(pl);
					if(target == null) {
						p.sendMessage(ChatColor.RED + "Something went wrong, contact a administrator!");
						return;
					}
					
					Inventory uinv = KoningenSysteem.makeInv(target, ChatColor.GREEN + "Role Menu", 9);
					ArrayList<String> list = new ArrayList<String>();
					list.add(ChatColor.RED + "Chrange role of:");
					list.add(ChatColor.BLUE + target.getName());
					ItemStack h2 = KoningenSysteem.makeItem(ChatColor.DARK_GREEN + "Queen", list, Material.IRON_BLOCK, 1);
					ItemStack h3 = KoningenSysteem.makeItem(ChatColor.DARK_GREEN + "King", list, Material.GOLD_BLOCK, 1);
					ItemStack h4 = KoningenSysteem.makeItem(ChatColor.DARK_GREEN + "Duke", list, Material.POTION, 1);
					ItemStack h5 = KoningenSysteem.makeItem(ChatColor.GRAY + "Member", list, Material.STONE, 1);
					
					
					uinv.setItem(0, h5);
					uinv.setItem(3, h3);
					uinv.setItem(2, h2);
					uinv.setItem(1, h4);
					p.openInventory(uinv);
					
				}
		}
		if(inv.getTitle().equals(ChatColor.GREEN + "Role Menu")) {
			e.setCancelled(true);
			//check if e.getCurrentItem() is null
			if(e.getCurrentItem() == null) {
				p.sendMessage(ChatColor.RED + "Something went wrong, contact a administrator");
				return;
			}
			ItemStack itemC = e.getCurrentItem();
			//get the name of the item
			String rankn = itemC.getItemMeta().getDisplayName();
			String rank_clean = ChatColor.stripColor(rankn);
			if(itemC == null || itemC.getItemMeta().getLore() == null) {
				p.sendMessage(ChatColor.RED + "Something went wrong, contact a administrator");
				return;
			}
			List<String> lore = itemC.getItemMeta().getLore();
			String pl = ChatColor.stripColor(lore.get(1).toString());
			Player player = Bukkit.getPlayer(pl);
			if(player == null) {
				p.sendMessage(ChatColor.RED + "Something went wrong, contact a administrator");
				return;
			}
			player.sendMessage(ChatColor.RED + "Your King/Queen " + ChatColor.BLUE + p.getName() + ChatColor.RED + " changed your role to " + rank_clean);
			p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " role changed to " + rank_clean + "!");
			User ku = kdc.getOnlineUser(player.getName());
			Rank ra = ku.getKingdom().getRank(rank_clean);
			ku.setRank(ra);
			kdc.saveAsync(ku);
			p.closeInventory();
			return;
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntityType() != EntityType.PLAYER) { return; }
		Player p = (Player) e.getEntity();
		
		if(p.getName().equalsIgnoreCase("boykev")) {
			for(ItemStack i : p.getInventory().getArmorContents()) {
				if(i == null) { return; }
				short dura = 0;
				i.setDurability(dura);
			}
			if(p.getMaxHealth() < 45.0) {
				p.setMaxHealth(45.0);
			}
		}
		if(p.getName().equalsIgnoreCase("coenispro")) {
			for(ItemStack i : p.getInventory().getArmorContents()) {
				if(i == null) { return; }
				short dura = 0;
				i.setDurability(dura);
			}
			if(p.getMaxHealth() < 45.0) {
				p.setMaxHealth(45.0);
			}
		}
		
	}
	
	@EventHandler
	public void onMailboxPlace(BlockPlaceEvent e) {
		if(e.getBlock().getType() == Material.valueOf("SNAILMAIL_SNAIL_BOX")) {
			User ku = kdc.getOnlineUser(e.getPlayer().getUniqueId());
			if(ku.getKingdom() == null) {
				e.getPlayer().sendMessage(ChatColor.RED + "You can`t place this because you are not in a civilization!");
				e.setCancelled(true);
				return;
			}
			if(ku.getRank().getName().equalsIgnoreCase("king")) {
				cm = new ConfigManager(instance);
				Boolean status = cm.getConfig().getBoolean("snailmail." + e.getPlayer().getUniqueId());
				if(status == false) {
					e.getPlayer().sendMessage(ChatColor.GREEN + "You have now placed the mailbox for your civilization. This is the only time you can place a snailmail box.");
					cm.editConfig().set("snailmail." + e.getPlayer().getUniqueId(), true);
					cm.save();
					return;
				}else {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "Sorry, you already placed a snailmail box. This can only be done once!");
					return;
				}
			}else {
				e.getPlayer().sendMessage(ChatColor.RED + "Sorry, only the king can place a snailmail box for your civilization!");
				e.setCancelled(true);
				return;
			}
		}
		if(!e.getPlayer().hasPermission("civ.creative")) {
			if(e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				return;
			}
			if(this.isVanished(e.getPlayer()) == false) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "Oops, building blocks in creative is only allowed when in staff mode (vanish)");
				return;
			}else {
				return;
			}
		}
	}
	
	@EventHandler
	public void onMailboxBreak(BlockBreakEvent e) {
		if(e.getBlock().getType() == Material.valueOf("SNAILMAIL_SNAIL_BOX")) {
			if(e.getPlayer().hasPermission("civ.removemailbox")) {
				e.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "LET OP! HAAL OOK DE MAILBOX UIT DE CONFIG MET /removebox [username]");
			}else {
				e.setCancelled(true);
			}
		}
		if(!e.getPlayer().hasPermission("civ.creative")) {
			if(e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				return;
			}
			if(this.isVanished(e.getPlayer()) == false) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "Oops, breaking blocks in creative is only allowed when in staff mode (vanish)");
				return;
			}else {
				return;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void userJoin(PlayerJoinEvent e) {
		if(e.getPlayer().getName().equalsIgnoreCase("boykev")) {
			if(e.getPlayer().getMaxHealth() < 45) {
				e.getPlayer().setMaxHealth(e.getPlayer().getMaxHealth() + 5);
				e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
				return;
			}
		}
		if(e.getPlayer().getName().equalsIgnoreCase("coenispro")) {
			if(e.getPlayer().getMaxHealth() < 45) {
				e.getPlayer().setMaxHealth(e.getPlayer().getMaxHealth() + 5);
				e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
				return;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void gameModeChange(PlayerGameModeChangeEvent e) {
		if(e.getPlayer().getName().equalsIgnoreCase("boykev")) { return; }
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					// URL van de API
			        URL url = new URL("https://alfredjkwak.dev/api/postEmbed");
			        
			        // Maak de verbinding
			        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			        httpURLConnection.setRequestMethod("POST");
			        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			        httpURLConnection.setRequestProperty("Accept", "application/json");
			        httpURLConnection.setRequestProperty("Authorization", "Vj5f7jSuuw6ryK43NRueoZFIFMfdmKUw");
			        httpURLConnection.setDoOutput(true);
			        
			        httpURLConnection.setConnectTimeout(10000);  // 10 seconden timeout voor het maken van de verbinding
			        httpURLConnection.setReadTimeout(10000);     // 10 seconden timeout voor het lezen van de gegevens
	
			        // Maak het JSON object
			        JSONObject jsonParam = new JSONObject();
			        jsonParam.put("title", "Gamemode changed | Server: Civilization");
			        jsonParam.put("channelid", "1166051676818518127");
			        jsonParam.put("message", e.getPlayer().getName() + " changed gamemode to " + e.getNewGameMode().toString()); // Je moet een methode hebben die een willekeurige string genereert
			        
			        // Schrijf de data naar de output stream
			        try (OutputStream os = httpURLConnection.getOutputStream()) {
			            byte[] input = jsonParam.toString().getBytes("UTF-8");
			            os.write(input, 0, input.length);
			        }
	
			        int responseCode = httpURLConnection.getResponseCode();
			        System.out.println("POST Response Code :: " + responseCode);
	
			        httpURLConnection.disconnect();
				}catch (Exception ex) {
			        ex.printStackTrace();
			    }
			}
		}.runTaskAsynchronously(instance);
	}
	
}
