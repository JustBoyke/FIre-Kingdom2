package me.boykev.kingdom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import net.md_5.bungee.api.ChatColor;

public class LicenseCheck {
	private ConfigManager cm;
	private Main instance;
	public LicenseCheck(Main main) {
		this.instance = main;
	}
	
	public boolean licentie(String plname) {
		cm = new ConfigManager(instance);
		if(cm.getConfig().getString("key").equals("-")) {
			Bukkit.broadcastMessage(ChatColor.YELLOW + "The plugin is setting things up please lay back.....");
			Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
                @Override
                public void run() {
                	Bukkit.broadcastMessage(ChatColor.YELLOW + "Everything is fine, have fun using LightningSword by Boykev :)");
                }
            }, 100);
	    	int serverport = Bukkit.getServer().getPort();
	    	try {
	    		URL url = new URL("http://api.boykevanvugt.nl/keymanager.php?type=create&version=1&plname=" + plname + "&serverport=" + serverport);
	            URLConnection connection = url.openConnection();
	            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String text = in.readLine();
	            String licfinal = text.replace(" ", "");
	            cm.getConfig().set("key", licfinal);
	            cm.save();
	            in.close();
	            return true;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		if(!cm.getConfig().getString("key").equals("-")) {
	    	String key = cm.getConfig().getString("key");
	    	try {
	            URL url = new URL("http://api.boykevanvugt.nl/keymanager.php?type=read&key=" + key);
	            URLConnection connection = url.openConnection();
	            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String text = in.readLine();
	            String licfinal = text.replace(" ", "");
	            cm.getConfig().set("key", licfinal);
	            if(licfinal.equals("valid")) {
	    			return true;
	            }
	            if(licfinal.equals("abuse")) {
	            	Bukkit.broadcastMessage(ChatColor.YELLOW + "Deze server abused de " + plname + " Plugin (by Boykev)!");
	            	in.close();
	            	Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
	                    @Override
	                    public void run() {
	                    	Bukkit.broadcastMessage(ChatColor.DARK_RED+ "Je maakt misbruik van de " + plname + " Plugin (By Boykev), neem contact op met de developer.");
	                    }
	                }, 0, 1800);
	            	return false;
	            }
	            if(licfinal.equals("edit")) {
	            	Bukkit.broadcastMessage(ChatColor.YELLOW + "Deze server abused de " + plname + " Plugin! (By Boykev)");
	            	in.close();
	            	Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
	                    @Override
	                    public void run() {
	                    	Bukkit.broadcastMessage(ChatColor.DARK_RED+ "Je maakt misbruik van de " + plname + " Plugin (By Boykev), neem contact op met de developer.");
	                    }
	                }, 0, 1800);
	            	return false;
	            }
	            else { 
	            	Bukkit.getServer().getPluginManager().disablePlugin(instance);
		            Log.info(ChatColor.DARK_PURPLE + "Licentie FAILD" + licfinal);
		            return false;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            Log.info(ChatColor.DARK_PURPLE + "Licentie FAILD");
	            return false;
	        }
	}
		return false;
	}
	

	
}
