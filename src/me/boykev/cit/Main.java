package me.boykev.cit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
		System.out.println(ChatColor.GREEN + "Staat aan!");
		PluginManager pm = Bukkit.getServer().getPluginManager();
		if(pm.getPlugin("api").isEnabled()) {
			Log.info("API Is gevonden en actief");
		}else {
			Log.warn("API Niet gevonden, downloaden....");
			File pluginDirectory = new File("plugins/nbt-tag-api.jar");
			if(!pluginDirectory.exists()){
			try {
				downloadAPI("[url]https://www.curseforge.com/minecraft/bukkit-plugins/nbt-api/download/3347730/file[/url]", "plugins/nbt-tag-api.jar");
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
		if(cmd.getName().equalsIgnoreCase("testitem")) {
			if(args[0].equalsIgnoreCase("give")) {
				ItemStack item = new ItemStack(Material.WHEAT);
				
				NBTItem nbti = new NBTItem(item);
				nbti.setString("test", "test2");
				p.getInventory().addItem(nbti.getItem());
				return false;
			}
			if(args[0].equalsIgnoreCase("check")) {
				ItemStack i = p.getInventory().getItemInMainHand();
				NBTItem it = new NBTItem(i);
				p.sendMessage(ChatColor.RED + it.getString(args[1]));
				return false;
			}
		}
		if(cmd.getName().equalsIgnoreCase("setnbt")) {
			ItemStack item = p.getInventory().getItemInMainHand();
			String nbttag = args[0];
			String tag = args[1];
			
			NBTItem nbti = new NBTItem(item);
			nbti.setString(nbttag, tag);
			p.getInventory().remove(item);
			p.getInventory().addItem(nbti.getItem());
			p.sendMessage(ChatColor.GREEN + "NBT Tag toegepast!");
			return false;
		}
		return false;
	}
	
}
