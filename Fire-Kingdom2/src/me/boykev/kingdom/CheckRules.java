package me.boykev.kingdom;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CheckRules implements Listener, CommandExecutor{
	
	private Main instance;
	private static UserManager um;
	public static HashMap<Player, Integer> rulemanager = new HashMap<Player, Integer>();

	public CheckRules(Main main) {
		this.instance = main;
	}
	
	@EventHandler
	public void ruleCheck(PlayerJoinEvent e) {
		um = new UserManager(instance, e.getPlayer());
		Player p = e.getPlayer();
		String checkup = um.getConfig().getString("server.regels");
		if(checkup == null) {
			p.sendMessage(ChatColor.DARK_RED + "----[RULES]----");
			p.sendMessage(ChatColor.RED + "Hey, je hebt onze regels en voorwaarden nog niet gelezen en");
			p.sendMessage(ChatColor.RED + "geaccepteerd. Je kan de regels lezen via: https://fire-mc.nl/kd");
			p.sendMessage(ChatColor.RED + "Onze voorwaarden vindt je op: https://fire-enterprise.nl/voorwaarden");
			TextComponent msg = new TextComponent(ChatColor.GREEN + "Om te accepteren " + ChatColor.BLUE + "(Klik Hier)");
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rulemanager-accept"));
			p.spigot().sendMessage(msg);
			rulemanager.put(p, 1);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					if(rulemanager.containsKey(p)) {
						p.sendMessage(ChatColor.DARK_RED + "----[RULES]----");
						p.sendMessage(ChatColor.RED + "Hey, je hebt onze regels en voorwaarden nog niet gelezen en");
						p.sendMessage(ChatColor.RED + "geaccepteerd. Je kan de regels lezen via: https://fire-mc.nl/kd");
						p.sendMessage(ChatColor.RED + "Onze voorwaarden vindt je op: https://fire-enterprise.nl/voorwaarden");
						TextComponent msg = new TextComponent(ChatColor.GREEN + "Om te accepteren " + ChatColor.BLUE + "(Klik Hier)");
						msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rulemanager-accept"));
						p.spigot().sendMessage(msg);
					}
					if(!rulemanager.containsKey(p)) {
						this.cancel();
					}
					
				}
			}.runTaskTimer(instance, 60L, 60L);
			
			return;
		}
		if(checkup == "v1") {
			p.sendMessage(ChatColor.DARK_RED + "----[RULES]----");
			p.sendMessage(ChatColor.RED + "Hey, je hebt onze regels en voorwaarden nog niet gelezen en");
			p.sendMessage(ChatColor.RED + "geaccepteerd. Je kan de regels lezen via: https://fire-mc.nl/kd");
			p.sendMessage(ChatColor.RED + "Onze voorwaarden vindt je op: https://fire-enterprise.nl/voorwaarden");
			TextComponent msg = new TextComponent(ChatColor.GREEN + "Om te accepteren " + ChatColor.BLUE + "(Klik Hier)");
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rulemanager-accept"));
			p.spigot().sendMessage(msg);
			rulemanager.put(p, 1);
			return;
		}	
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		um = new UserManager(instance, p);
		if(cmd.getName().equalsIgnoreCase("rulemanager-accept")) {
			if(rulemanager.containsKey(p)) {
				um.editConfig().set("server.regels", "v1");
				um.save();
				rulemanager.remove(p);
				p.sendMessage(ChatColor.GREEN + "Je hebt de regels en voorwaarden geacceteerd!");
				return false;
			}
			p.sendMessage(ChatColor.RED + "Je hebt onze regels en voowaarden al geaccepteerd!");
			return false;
		}
		
		return false;
	}
	
}
