package me.boykev.cit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;

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
		
	public void onEnable() {
		Log.info("Staat aan");
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
		
	}
	
	public void onDisable() {
		System.out.println(ChatColor.RED + "Staat uit!");
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("nbt")) {
			if(!p.hasPermission("nbt.command")) {
				p.sendMessage(ChatColor.RED + "Sorry, je hebt niet de benodigde permissies");
				p.sendTitle(ChatColor.RED + "Error!", ChatColor.WHITE + "Je hebt niet de juiste perms!", 10, 60, 10);
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 1);
				return false;
			}
			if(args[0].equalsIgnoreCase("set")) {
				if(args.length < 3) {
					p.sendMessage(ChatColor.RED + "Oeps, het commando is niet juist gebruikt.");
					p.sendMessage(ChatColor.BLUE + "/nbt set [tagname] [tag]");
					return false;
				}
				ItemStack item = p.getInventory().getItemInMainHand();
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
				if(args.length < 2) {
					ItemStack i = p.getInventory().getItemInMainHand();
					NBTItem it = new NBTItem(i);
					for(String s : it.getKeys()) {
						String comp = it.getString(s);
						p.sendMessage(ChatColor.RED + comp + " : " + s);
						
					}
					return false;
					
				}
				ItemStack i = p.getInventory().getItemInMainHand();
				NBTItem it = new NBTItem(i);
				p.sendMessage(ChatColor.RED + it.getString(args[1]));
				return false;
			}
		}
		if(cmd.getName().equalsIgnoreCase("mtgive")) {
			
		}
		return false;
	}
	
}
