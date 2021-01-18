package me.boykev.kingdom;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.ChatColor;

public class InventoryManager implements CommandExecutor{
	
	
	private static Main instance;
	private ConfigManager cm;
	private static UserManager um;
	public InventoryManager(Main main) {
		InventoryManager.instance = main;
	}
	
	
	public void saveUserInventory(Player p, Inventory inv) {
		um = new UserManager(instance, p);
		um.editConfig().set("inventory.save", inv);
		um.save();
		return;
	}
	
	public Inventory loadUserInventory(Player p) {
		um = new UserManager(instance, p);
		
		
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		cm = new ConfigManager(instance);
		if(cmd.getName().equalsIgnoreCase("save-inv")) {
			
			if(!sender.hasPermission("inventory.save")) { sender.sendMessage(ChatColor.RED + "Je hebt hier geen permissies voor!"); return false; }
			
			
		}
		
		
		return false;
	}

}
