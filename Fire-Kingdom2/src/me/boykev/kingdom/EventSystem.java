package me.boykev.kingdom;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import net.md_5.bungee.api.ChatColor;

public class EventSystem implements Listener{
	
	public EventSystem(Main main) {
		EventSystem.instance = main;
	}
	
	KingdomCraft kdc = KingdomCraftProvider.get();
	
	@SuppressWarnings("unused")
	private static Main instance;
	
	
	@EventHandler
	public void kingdomKoningClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		InventoryView inv = e.getView();
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
