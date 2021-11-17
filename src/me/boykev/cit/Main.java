package me.boykev.cit;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin{
		
	public void onEnable() {
		System.out.println(ChatColor.GREEN + "Staat aan!");
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
