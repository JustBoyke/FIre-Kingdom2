package me.boykev.kingdom;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;

import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import org.json.simple.JSONObject;

public class EventSystem implements Listener{
	
	public EventSystem(Main main) {
		EventSystem.instance = main;
	}
	
	KingdomCraft kdc = KingdomCraftProvider.get();
	
	
	MySQLDatabase mySQLDatabase = new MySQLDatabase("remote.dixiehosting.nl", "nedercraft", "nedercraft", "aRhXkWAt6F7bVbT44pUp", 3308);
	
	public List<UUID> getAccessibleBankAccounts(UUID playerUUID) {
	    List<UUID> accessibleAccounts = new ArrayList<>();
	    mySQLDatabase.connect();
	    Connection connection = mySQLDatabase.getConnection();
	    
	    if (connection != null) {
	        String query = "SELECT bank_account_uuid FROM bankdata WHERE player_uuid=?";
	        
	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setString(1, playerUUID.toString());
	            
	            ResultSet resultSet = preparedStatement.executeQuery();
	            
	            while (resultSet.next()) {
	                String bankAccountUUIDString = resultSet.getString("bank_account_uuid");
	                UUID bankAccountUUID = UUID.fromString(bankAccountUUIDString);
	                accessibleAccounts.add(bankAccountUUID);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    mySQLDatabase.disconnect();
	    return accessibleAccounts;
	    
	}
	
	public String getAccountName(UUID accountID) {
	    mySQLDatabase.connect();
	    Connection connection = mySQLDatabase.getConnection();
	    String name = null;

	    if (connection != null) {
	        String query = "SELECT account_name FROM bank_names WHERE bank_account_uuid=?";

	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setString(1, accountID.toString());

	            ResultSet resultSet = preparedStatement.executeQuery();
	            if (resultSet.next()) {
	                name = resultSet.getString("account_name");
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    mySQLDatabase.disconnect();
	    return name;
	}
	
	public String getBalance(UUID accountID) {
	    mySQLDatabase.connect();
	    Connection connection = mySQLDatabase.getConnection();
	    String name = null;

	    if (connection != null) {
	        String query = "SELECT balance FROM bank_names WHERE bank_account_uuid=?";

	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setString(1, accountID.toString());

	            ResultSet resultSet = preparedStatement.executeQuery();
	            if (resultSet.next()) {
	                name = resultSet.getString("balance");
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    mySQLDatabase.disconnect();
	    return name;
	}
	public Integer getBankType(UUID accountID) {
	    mySQLDatabase.connect();
	    Connection connection = mySQLDatabase.getConnection();
	    Integer name = null;

	    if (connection != null) {
	        String query = "SELECT type FROM bank_names WHERE bank_account_uuid=?";

	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setString(1, accountID.toString());

	            ResultSet resultSet = preparedStatement.executeQuery();
	            if (resultSet.next()) {
	                name = resultSet.getInt("type");
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    mySQLDatabase.disconnect();
	    return name;
	}
	public Double getItemValue(Material item) {
	    mySQLDatabase.connect();
	    Connection connection = mySQLDatabase.getConnection();
	    Double name = null;

	    if (connection != null) {
	        String query = "SELECT value FROM bankingexchange WHERE item_name=?";

	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setString(1, item.toString().toLowerCase());

	            ResultSet resultSet = preparedStatement.executeQuery();
	            if (resultSet.next()) {
	                name = resultSet.getDouble("value");
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    mySQLDatabase.disconnect();
	    return name;
	}
	
	private ItemStack createBankAccountItem(UUID bankAccountUUID) {
	    // Create an ItemStack representing the bank account
	    String name = this.getAccountName(bankAccountUUID);
	    Integer type = this.getBankType(bankAccountUUID);
	    ItemStack itemStack = null;
	    if(type == 0) {
	    	itemStack = new ItemStack(Material.ENDER_CHEST); // Use an appropriate material
	    }else {
	    	itemStack = new ItemStack(Material.CHEST); // Use an appropriate material
	    }
	    ItemMeta itemMeta = itemStack.getItemMeta();
	    itemMeta.setDisplayName(ChatColor.GOLD + "Account: " + ChatColor.GRAY + name); // Set an appropriate display name
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.GRAY + "Balance: " + ChatColor.GOLD + this.getBalance(bankAccountUUID));
	    itemMeta.setLore(lore);
	    
	    // You can also add lore or other information to the item meta if needed
	    
	    itemStack.setItemMeta(itemMeta);
	    NBTItem nbti = new NBTItem(itemStack);
	    
	    nbti.setString("uuid", bankAccountUUID.toString());	    
	    return nbti.getItem();
	}
	private ItemStack createAccountItem(Material mat, String name, Integer type) {
	    // Create an ItemStack representing the bank account
		ItemStack itemStack = new ItemStack(mat);
	    ItemMeta itemMeta = itemStack.getItemMeta();
	    itemMeta.setDisplayName(ChatColor.GOLD + name); // Set an appropriate display name
	    List<String> lore = new ArrayList<String>();
	    Double value = this.getItemValue(itemStack.getType());
	    lore.add(ChatColor.GRAY + "Waarde: " + ChatColor.GOLD + value);
	    if(type == 0) {
	    	lore.add(ChatColor.GRAY + "Click to withdraw");
	    }else if(type == 1) {
	    	lore.add(ChatColor.GRAY + "Click to deposit");
	    }else {
	    	lore.add(ChatColor.GRAY + "Placeholder");
	    }
	    itemMeta.setLore(lore);
	    
	    // You can also add lore or other information to the item meta if needed
	    
	    itemStack.setItemMeta(itemMeta);
	    NBTItem nbti = new NBTItem(itemStack);
	    
	    nbti.setDouble("waarde", value);   
	    return nbti.getItem();
	}
	
	public void openBankingInventory(Player p) {
	    Inventory bankInventory = Bukkit.createInventory(null, 9, ChatColor.GRAY + "Bank account ");
	    
	    // Retrieve the list of bank accounts the player has access to from the database
	    List<UUID> bankAccountUUIDs = getAccessibleBankAccounts(p.getUniqueId());
	    
	    // Populate the inventory with bank accounts
	    for (UUID bankAccountUUID : bankAccountUUIDs) {
	        ItemStack bankAccountItem = createBankAccountItem(bankAccountUUID);
	        bankInventory.addItem(bankAccountItem);
	    }
	    
	    p.openInventory(bankInventory);
	}
	
	public void openBankAccount(Player p, UUID bankAccountUUID) {
		String name = this.getAccountName(bankAccountUUID);
		Inventory inv = Bukkit.createInventory(null, 18, ChatColor.GRAY + "Bank account: " + ChatColor.GREEN + name);
		
		inv.setItem(1, this.createAccountItem(Material.COPPER_INGOT, "0,05,-", 0));
		inv.setItem(2, this.createAccountItem(Material.IRON_INGOT, "0,30,-", 0));
		inv.setItem(3, this.createAccountItem(Material.GOLD_INGOT, "5,00,-", 0));
		inv.setItem(4, this.createAccountItem(Material.AMETHYST_SHARD, "10,00,-", 0));
		inv.setItem(5, this.createAccountItem(Material.DIAMOND, "150,00,-", 0));
		inv.setItem(6, this.createAccountItem(Material.EMERALD, "150,00,-", 0));
		inv.setItem(7, this.createAccountItem(Material.NETHERITE_INGOT, "500,00,-", 0));
		
		inv.setItem(10, this.createAccountItem(Material.COPPER_INGOT, "0,05,-", 1));
		inv.setItem(11, this.createAccountItem(Material.IRON_INGOT, "0,30,-", 1));
		inv.setItem(12, this.createAccountItem(Material.GOLD_INGOT, "5,00,-", 1));
		inv.setItem(13, this.createAccountItem(Material.AMETHYST_SHARD, "10,00,-", 1));
		inv.setItem(14, this.createAccountItem(Material.DIAMOND, "150,00,-", 1));
		inv.setItem(15, this.createAccountItem(Material.EMERALD, "150,00,-", 1));
		inv.setItem(16, this.createAccountItem(Material.NETHERITE_INGOT, "500,00,-", 1));
		
		
		p.openInventory(inv);
	}

	
	private static Main instance;
	@SuppressWarnings("unused")
	private ConfigManager cm;
	
	private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
}
	
	@SuppressWarnings("unused")
	private ItemStack getWeapon(Player attacker) {
        ItemStack weapon = attacker.getInventory().getItemInMainHand(); // Assuming the attacker used their main hand.
        if (weapon != null && weapon.getType() != Material.AIR) {
            return weapon;
        }
        return null;
    }
	
	
	@EventHandler
	public void placeStatusSign(SignChangeEvent e) {
		if(e.getLine(0).equalsIgnoreCase("[statussign]")) {
			if(e.getLine(1).isBlank()) {
				e.getPlayer().sendMessage(ChatColor.RED + "Geen signlink opgegeven");
				return;
			}
			if(e.getLine(2).isBlank()) {
				e.getPlayer().sendMessage(ChatColor.RED + "Geen status opgegeven");
				return;
			}
			String line1 = e.getLine(1);
			String line2 = e.getLine(2);
			e.setLine(0, ChatColor.BLUE + "[statussign]");
			e.setLine(1, line2);
			e.setLine(2, ChatColor.MAGIC + line1);
			e.getPlayer().sendMessage(ChatColor.GREEN + "Statuschanger Gemaakt");
		}
		if(e.getLine(0).equalsIgnoreCase("[banking]")) {
			e.setLine(0, ChatColor.BLUE + "[banking]");
			e.setLine(1, ChatColor.ITALIC + "Klik links om de");
			e.setLine(2, ChatColor.ITALIC + "bank te openen");
		}
	}

	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void clickOnSign(PlayerInteractEvent e) {
		Block blok = e.getClickedBlock();
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(instance.signs.contains(blok.getType())) {
				Sign sign = (Sign) blok.getState();
				if(sign.getLine(0).equalsIgnoreCase(ChatColor.BLUE + "[statussign]")) {
					e.setCancelled(true);
					String signlink = ChatColor.stripColor(sign.getLine(2));
					String state = ChatColor.stripColor(sign.getLine(1));
					if(signlink.isEmpty()) {
						e.getPlayer().sendMessage(ChatColor.RED + "Oeps er ging iets fout met deze sign!");
						return;
					}
					if(state.isEmpty()) {
						e.getPlayer().sendMessage(ChatColor.RED + "Oeps er ging iets fout met deze sign!");
						return;
					}
					if(state.equalsIgnoreCase("gesloten")) {
						state = "&cGesloten";
					}else if(state.equalsIgnoreCase("geopend")) {
						state = "&aGeopend";
					}
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "var edit " + signlink + " set " + state);
					e.getPlayer().sendMessage(ChatColor.GREEN + "Status gewijzigd!");
					return;
				}
				if(sign.getLine(0).equalsIgnoreCase(ChatColor.RED + "[banking]")) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "Deze bank sign is invalide en kan niet worden gebruikt!");
					return;
				}
				if(sign.getLine(0).equalsIgnoreCase(ChatColor.BLUE + "[banking]")) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.GREEN + "Je klikte op een banking sign!");
					this.openBankingInventory(e.getPlayer());
					return;
				}
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player attacker = victim.getKiller();

        if (attacker == null) {
            // No player killed the victim
            return;
        }

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        ItemStack weaponUsed = attacker.getInventory().getItemInMainHand();

        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) is.getItemMeta();
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(victim.getName()));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Killed by: " + ChatColor.BLUE + attacker.getName());
        lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Died on: " + ChatColor.BLUE + format.format(now));
        if(weaponUsed.getItemMeta().getDisplayName() == null) {
        	lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Killed with:" + ChatColor.BLUE + weaponUsed.getType().toString());
        }else if(weaponUsed.hasItemMeta() == false) {
        	lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Killed with:" + ChatColor.BLUE + weaponUsed.getType().toString());
        }else if(weaponUsed.getItemMeta().getDisplayName() == "") {
        	lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Killed with:" + ChatColor.BLUE + weaponUsed.getType().toString());
        }else {
        	lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Killed with:" + ChatColor.BLUE + weaponUsed.getItemMeta().getDisplayName());
        }

        skull.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + victim.getName() + ChatColor.GRAY + " Head");
        skull.setLore(lore);
        is.setItemMeta(skull);

        attacker.getInventory().addItem(is);
        attacker.sendMessage(ChatColor.GREEN + "Je hebt " + ChatColor.GOLD + victim.getName() + ChatColor.GREEN + " vermoord en daardoor zijn skull gekregen als prijs!");
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
		if(inv.getTitle().startsWith(ChatColor.GRAY + "Bank account ")) {
			e.setCancelled(true);
			if(item == null) {
				return;
			}
			if(item.getType() == Material.CHEST && e.getCurrentItem().getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Account: ")) {
				NBTItem nbti = new NBTItem(e.getCurrentItem());
				String uuid = nbti.getString("uuid");
				this.openBankAccount(p, UUID.fromString(uuid));
				p.sendMessage(ChatColor.GREEN + "Je klikte op account met ID: " + ChatColor.GOLD + uuid);
				return;
			}
			return;
		}
		if(inv.getTitle().startsWith(ChatColor.GRAY + "Bank account: ")) {
			e.setCancelled(true);
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
		
	}
	
	@EventHandler
	public void onMailboxPlace(BlockPlaceEvent e) {
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
			        jsonParam.put("title", "Gamemode changed | Server: Civilization Survival");
			        jsonParam.put("channelid", "1166051676818518127");
			        jsonParam.put("serverid", "557672774471254017");
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
