package me.boykev.kingdom;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;

/**
 * This class will be registered through the register-method in the 
 * plugins onEnable-method.
 */
public class PlaceholderAPI extends PlaceholderExpansion {

    private Main plugin;
    private ConfigManager cm;
    KingdomCraft kdc = KingdomCraftProvider.get();

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public PlaceholderAPI(Main plugin){
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "Civ";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if(player == null){
            return "";
        }

        // %someplugin_placeholder1%
        if(identifier.equals("kdname")){
        	User ku = kdc.getOnlineUser(player.getName());
        	Kingdom kd = ku.getKingdom();
        	if(kd == null) {
        		return ChatColor.GRAY + "No Civilization";
        	}
        	String kdname = kd.getDisplay();
            return kdname;
        }

        // %someplugin_placeholder2%
        if(identifier.equals("kdrank")){
        	User ku = kdc.getOnlineUser(player.getName());
        	Kingdom kd = ku.getKingdom();
        	if(kd == null) {
        		return ChatColor.GRAY + "No Civilization";
        	}
        	Rank kr = ku.getRank();
        	String print = kr.getDisplay();
        	return print;
        }
        if(identifier.equals("members")) {
        	User ku = kdc.getOnlineUser(player.getName());
        	if(ku == null) {
        		return ChatColor.LIGHT_PURPLE + "No Data";
        	}
        	if(ku.getKingdom() == null) {
        		return ChatColor.LIGHT_PURPLE + "No Data";
        	}
        	Kingdom kd = ku.getKingdom();
        	String members = String.valueOf(kd.getMemberCount());
        	return members;
        }
        if(identifier.equals("maxmembers")) {
        	User ku = kdc.getOnlineUser(player.getName());
        	if(ku.getKingdom() == null) {
        		return ChatColor.LIGHT_PURPLE + "No Data";
        	}
        	Kingdom kd = ku.getKingdom();
        	String members = String.valueOf(kd.getMaxMembers());
        	return members;
        }
        if(identifier.equals("onlinemembers")) {
        	User ku = kdc.getOnlineUser(player.getName());
        	if(ku.getKingdom() == null) {
        		return ChatColor.LIGHT_PURPLE + "No Data";
        	}
        	Kingdom kd = ku.getKingdom();
        	int count = 0;
        	for(Player p : Bukkit.getOnlinePlayers()) {
        		if(kd.getMembers().containsKey(p.getUniqueId())) {
        			count++;
        		}
        	}
        	return String.valueOf(count);
        }
        if(identifier.equals("channel")) {
        	User ku = kdc.getOnlineUser(player.getName());
        	if(ku.getKingdom() == null) {
        		return ChatColor.LIGHT_PURPLE + "No Data";
        	}
        	return ChatColor.WHITE + "CIV-Chat";
        }
        if(identifier.equals("power")) {
        	User ku = kdc.getOnlineUser(player.getName());
        	if(ku.getKingdom() == null) {
        		return ChatColor.LIGHT_PURPLE + "No Data";
        	}
        	cm = new ConfigManager(plugin);
        	String kingdom_name = ku.getKingdom().getName();
        	if(cm.getConfig().get("power." + kingdom_name) == null) {
        		return ChatColor.GRAY + "No power";
        	}else {
        		int power = cm.getConfig().getInt("power." + kingdom_name);
        		if(power < 11) {
        			return ChatColor.RED + String.valueOf(power);
        		}else if(power > 14 && power < 25) {
        			return ChatColor.YELLOW + String.valueOf(power);
        		}else if(power > 25 && power < 45) {
        			return ChatColor.GREEN + String.valueOf(power);
        		}else if(power > 44) {
        			return ChatColor.GOLD + String.valueOf(power);
        		}else {
        			return ChatColor.GRAY + String.valueOf(power);
        		}
        		
        	}
        }
        if(identifier.equals("powerclean")) {
        	User ku = kdc.getOnlineUser(player.getName());
        	if(ku.getKingdom() == null) {
        		return ChatColor.LIGHT_PURPLE + "No Data";
        	}
        	cm = new ConfigManager(plugin);
        	String kingdom_name = ku.getKingdom().getName();
        	if(cm.getConfig().get("power." + kingdom_name) == null) {
        		return String.valueOf(0);
        	}else {
        		int power = cm.getConfig().getInt("power." + kingdom_name);
        		return String.valueOf(power);
        		
        	}
        }
        
 
        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%) 
        // was provided
        return null;
    }
}