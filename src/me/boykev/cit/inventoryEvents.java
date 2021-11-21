package me.boykev.cit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;

public class inventoryEvents implements Listener {
	private Main instance;
	public ConfigManager cm;
	

	public inventoryEvents(Main main) {
		this.instance = main;
	}
	
	@EventHandler
	public void onInventory(InventoryClickEvent e) {
		String inv = e.getInventory().getName();
		cm = new ConfigManager(instance);
		if(inv.equalsIgnoreCase(ChatColor.RED + "GC Items")) {
			e.setCancelled(true);
		}
		Player p = (Player) e.getWhoClicked();
		if(e.getCurrentItem() == null) {
			return;
		}
		ItemStack item = e.getCurrentItem();
		if(item.getType() == Material.AIR) {
			return;
		}
		NBTItem nbti = new NBTItem(item);
		String id = nbti.getString("identifier");
		if (id == null) {
			return;
		}
		if(cm.getConfig().get("items." + id) == null) {
			return;
		}
		ItemStack i = cm.getConfig().getItemStack("items." + id);
		if(i == null) {
			return;
		}
		p.getInventory().addItem(i);
	}
	
}
