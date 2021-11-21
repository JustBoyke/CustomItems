package me.boykev.cit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin{
	
	public void downloadAPI(String link, String directory) throws IOException {
		URL url = new URL(link);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(directory);
		
		byte[] b = new byte[2048];
		int length;
		
		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
			}
		is.close();
		os.close();
	}
	public ConfigManager cm;
	public String Status;
	public licenseInfo lic;
		
	public void onEnable() {
		Log.info("Staat aan");
		PluginDescriptionFile pdf = this.getDescription();
		PluginManager pm = Bukkit.getServer().getPluginManager();
		if(pm.isPluginEnabled("NBTAPI")) {
			Log.info("API Is gevonden en actief");
		}else {
			Log.warn("API Niet gevonden, downloaden....");
			File pluginDirectory = new File("plugins/item-nbt-api-plugin.jar");
			if(!pluginDirectory.exists()){
			try {
				downloadAPI("[url]https://www.curseforge.com/minecraft/bukkit-plugins/nbt-api/download/3347730/file[/url]", "plugins/item-nbt-api-plugin.jar");
			} catch (IOException e) {
				System.out.println("Download failed! :(");
				pm.disablePlugin(this);
				} 
			}
		}
		cm = new ConfigManager(this);
		cm.LoadDefaults();
		if(cm.getConfig().getString("key").equals("-")) {
			lic = new licenseInfo(this);
			String plname = pdf.getName();
			lic.createLicense();
			System.out.println(ChatColor.GREEN + plname + " plugin heeft automatisch een licentie aangemaakt!");
		}
		if(!cm.getConfig().getString("key").equals("-")) {
	    	String plname = pdf.getName();
	    	lic = new licenseInfo(this);
	    	if(lic.getLicense().equalsIgnoreCase("Valid")) {
	    		System.out.println(ChatColor.GREEN + plname + " plugin opgestart!");
	    		pm.registerEvents(new inventoryEvents(this), this);
	    	}
	    	if(lic.getLicense().equalsIgnoreCase("Abuse")) {
	    		Bukkit.broadcastMessage(ChatColor.YELLOW + "Deze server abused de " + plname + "!");
	    		System.out.println(ChatColor.RED + plname + " plugin niet opgestart wegens abuse van de TOS");
	    		lic.licenseAbuse();
	    	}
	    	if(lic.getLicense().equalsIgnoreCase("Edit")) {
	    		Bukkit.broadcastMessage(ChatColor.YELLOW + "Deze server abused de " + plname + " plugin door edits te maken!");
	    		System.out.println(ChatColor.RED + plname + " plugin niet opgestart wegens abuse van de TOS");
	    		lic.licenseAbuse();
	    	}
	    	if(lic.getLicense().equalsIgnoreCase("Failed")) {
	    		System.out.println(ChatColor.RED + plname + " plugin niet opgestart door een fout in het licentiesysteem");
	    		lic.licenseError();
	    	}
		}
		
		
		
	}
	
	public void onDisable() {
		System.out.println(ChatColor.RED + "Staat uit!");
	}
	
	public Integer getAmount(String count) {
		if(count == null) {
			return Integer.valueOf(1);
		}
		return Integer.valueOf(count);
	}
	
	public Inventory test = Bukkit.createInventory(null, 54, ChatColor.RED + "GC Items");
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		cm = new ConfigManager(this);
		lic = new licenseInfo(this);
		if(cmd.getName().equalsIgnoreCase("nbt")) {
			if(!p.hasPermission("customitems.nbt")) {
				p.sendMessage(ChatColor.RED + "Sorry, je hebt niet de benodigde permissies");
				p.sendTitle(ChatColor.RED + "Error!", ChatColor.WHITE + "Je hebt niet de juiste perms!", 10, 60, 10);
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 1);
				return false;
			}
			if(lic.dataInfo(p) == false) {
				return false;
			}
			if(args.length < 1) {
				p.sendMessage(ChatColor.BLUE + "/nbt check - Laat alle tags op een items in je hand zien.");
				p.sendMessage(ChatColor.BLUE + "/nbt set - voegt een custom tag toe aan een item.");
				return false;
			}
			if(args[0].equalsIgnoreCase("set")) {
				if(!p.hasPermission("customitems.nbt.set")) {
					p.sendMessage(ChatColor.RED + "Sorry, je hebt niet de benodigde permissies");
					p.sendTitle(ChatColor.RED + "Error!", ChatColor.WHITE + "Je hebt niet de juiste perms!", 10, 60, 10);
					p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 1);
					return false;
				}
				if(args.length < 3) {
					p.sendMessage(ChatColor.RED + "Oeps, het commando is niet juist gebruikt.");
					p.sendMessage(ChatColor.BLUE + "/nbt set [tagname] [tag]");
					return false;
				}
				ItemStack item = p.getInventory().getItemInMainHand();
				if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
					p.sendMessage(ChatColor.RED + "Je hebt geen item in je hand.");
					return false;
				}
				String nbttag = args[1];
				String tag = args[2];
				
				NBTItem nbti = new NBTItem(item);
				nbti.setString(nbttag, tag);
				p.getInventory().remove(item);
				p.getInventory().addItem(nbti.getItem());
				p.sendMessage(ChatColor.GREEN + "NBT Tag toegepast!");
				return false;
			}
			if(args[0].equalsIgnoreCase("check")) {
				if(!p.hasPermission("customitems.nbt.check")) {
					p.sendMessage(ChatColor.RED + "Sorry, je hebt niet de benodigde permissies");
					p.sendTitle(ChatColor.RED + "Error!", ChatColor.WHITE + "Je hebt niet de juiste perms!", 10, 60, 10);
					p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 1);
					return false;
				}
				if(args.length < 2) {
					ItemStack i = p.getInventory().getItemInMainHand();
					if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
						p.sendMessage(ChatColor.RED + "Je hebt geen item in je hand.");
						return false;
					}
					NBTItem it = new NBTItem(i);
					for(String s : it.getKeys()) {
						String comp = it.getString(s);
						p.sendMessage(ChatColor.RED + s + " : " + comp);
					}
					return false;
				}
				ItemStack i = p.getInventory().getItemInMainHand();
				if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
					p.sendMessage(ChatColor.RED + "Je hebt geen item in je hand.");
					return false;
				}
				NBTItem it = new NBTItem(i);
				p.sendMessage(ChatColor.RED + it.getString(args[1]));
				return false;
			}
		}
		if(cmd.getName().equalsIgnoreCase("gcitems")) {
			if(!p.hasPermission("customitems.gcitems")) {
				p.sendMessage(ChatColor.RED + "Sorry, je hebt niet de benodigde permissies");
				p.sendTitle(ChatColor.RED + "Error!", ChatColor.WHITE + "Je hebt niet de juiste perms!", 10, 60, 10);
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 1);
				return false;
			}
			if(lic.dataInfo(p) == false) {
				return false;
			}
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("help")) {
					p.sendMessage(ChatColor.BLUE + "/gcitems - opent het menu met custom items.");
					p.sendMessage(ChatColor.BLUE + "/gcitems add - voegt het item in je inventory toe aan de lijst.");
					p.sendMessage(ChatColor.BLUE + "/gcitems help - daar kijk je nu naar.");
					p.sendMessage(ChatColor.BLUE + "/gcitems remove - Verwijderd het item in je inventory uit de database.");
				}
				if(args[0].equalsIgnoreCase("add")) {
					if(!p.hasPermission("customitems.gcitems.add")) {
						p.sendMessage(ChatColor.RED + "Sorry, je hebt niet de benodigde permissies");
						p.sendTitle(ChatColor.RED + "Error!", ChatColor.WHITE + "Je hebt niet de juiste perms!", 10, 60, 10);
						p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 1);
						return false;
					}
					ItemStack i = p.getInventory().getItemInMainHand();
					if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
						p.sendMessage(ChatColor.RED + "Je hebt geen item in je hand.");
						return false;
					}
					UUID id = UUID.randomUUID();
					String stringid = id.toString();
					String nbttag = "identifier";
					String tag = stringid;
					NBTItem nbti = new NBTItem(i);
					nbti.setString(nbttag, tag);
					p.getInventory().remove(i);
					p.getInventory().addItem(nbti.getItem());
					ItemStack i2 = p.getInventory().getItemInMainHand();
					if(cm.getConfig().get("items." + stringid) != null) {
						Random random = new Random();
						Integer rand = random.nextInt(50+(80000));
						cm.editConfig().set("items." + stringid + String.valueOf(rand), i2);
						cm.save();
						p.sendMessage(ChatColor.GREEN + "Het item is opgeslagen!");
						return false;
					}
					cm.editConfig().set("items." + stringid, i2);
					cm.save();
					p.sendMessage(ChatColor.GREEN + "Het item is opgeslagen!");
				}
				if(args[0].equalsIgnoreCase("remove")) {
					if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
						p.sendMessage(ChatColor.RED + "Je hebt geen item in je hand.");
						return false;
					}
					ItemStack i = p.getInventory().getItemInMainHand();
					NBTItem nbti = new NBTItem(i);
					if(nbti.getString("identifier") == null) {
						p.sendMessage(ChatColor.RED + "Dit items staat niet in de database of mist de identifier. GEEN IDT");
						return false;
					}
					String id = nbti.getString("identifier");
					if(cm.getConfig().get("items." + id) == null) {
						p.sendMessage(ChatColor.RED + "Dit items staat niet in de database of mist de identifier.");
						p.sendMessage(id);
						return false;
					}
					cm.editConfig().set("items." + id, null);
					cm.save();
					p.sendMessage(ChatColor.RED + "Item Verwijderd uit de database!");
					return false;
				}
				return false;
			}
			test.clear();
			int i = 0;
			if(cm.getConfig().getConfigurationSection("items") == null) {
				p.sendMessage(ChatColor.RED + "Er staan nog geen items in de lijst!");
				return false;
			}
			for(String s : cm.getConfig().getConfigurationSection("items").getKeys(true)){
				if(i < 54) {
					ItemStack item = cm.getConfig().getItemStack("items." + s);
					test.setItem(i,item);
					i++;
				}
			}
			p.openInventory(test);
		}
		if(cmd.getName().equalsIgnoreCase("mtgive")) {
			if(!p.hasPermission("nbt.mtgive")) {
				p.sendMessage(ChatColor.RED + "Sorry, je hebt niet de benodigde permissies");
				p.sendTitle(ChatColor.RED + "Error!", ChatColor.WHITE + "Je hebt niet de juiste perms!", 10, 60, 10);
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 1);
				return false;
			}
			if(lic.dataInfo(p) == false) {
				return false;
			}
			if(args.length < 2) {
				p.sendMessage(ChatColor.RED + "Oeps, het commando is niet juist gebruikt.");
				p.sendMessage(ChatColor.BLUE + "/mtgive [item] [mtcustom tag] [aantal] (customname)");
				return false;
			}
			Material m = Material.matchMaterial(args[0]);
			
			if(m == null) {
				p.sendMessage(ChatColor.RED + "Het item is niet gevonden.");
				return false;
			}
			if(args.length < 3) {
				ItemStack i = new ItemStack(m, 1);
				NBTItem it = new NBTItem(i);
				it.setString("mtcustom", args[1]);
				p.getInventory().addItem(it.getItem());
			}else {
				ItemStack i = new ItemStack(m, getAmount(args[2]));
				if(args.length > 3) {
					ItemMeta im = i.getItemMeta();
					im.setDisplayName(ChatColor.translateAlternateColorCodes('&', args[3]));
					i.setItemMeta(im);
				}
				NBTItem it = new NBTItem(i);
				it.setString("mtcustom", args[1]);
				p.getInventory().addItem(it.getItem());
			}
		}
		if(cmd.getName().equalsIgnoreCase("customitems")) {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("pling")) {
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HARP, 100F, 0.3F);
					return false;
				}
			}
			PluginDescriptionFile pdf = this.getDescription();
			
	        TextComponent component = new TextComponent(TextComponent.fromLegacyText(ChatColor.GREEN + "Developer: Boyke (boykev)"));
	        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitch.tv/gewoonboyke"));
	        
	        TextComponent version = new TextComponent(TextComponent.fromLegacyText(ChatColor.GREEN + "Versie: " + pdf.getVersion()));
	        version.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/customitems pling"));
	       
			
			p.sendMessage(ChatColor.GRAY + "---- [ Custom Items ] ----");
			p.spigot().sendMessage(version);
			p.spigot().sendMessage(component);
			p.sendMessage(ChatColor.GREEN + "Website: https://boykevanvugt.nl");
			if(lic.getLicense().equalsIgnoreCase("Valid")) {
				TextComponent license = new TextComponent(TextComponent.fromLegacyText(ChatColor.GREEN + "Licentie: Valid!"));
		        license.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Deze licentie is actief en geverrifieerd").create()));
		        p.spigot().sendMessage(license);
			}else {
				TextComponent license = new TextComponent(TextComponent.fromLegacyText(ChatColor.RED + "Licentie: Valid!"));
		        license.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Deze licentie is geblokkeerd of niet actief!").create()));
		        p.spigot().sendMessage(license);
		        p.playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 100F,1.0F);
			}
			p.sendMessage(ChatColor.GRAY + "----------------------");
		}
		return false;
	}
	
}
