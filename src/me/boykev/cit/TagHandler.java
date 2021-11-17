package me.boykev.cit;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class TagHandler {
	
	private static NBTTagCompound getTag(ItemStack item) {
		
		net.minecraft.server.v1_12_R1.ItemStack itemNms = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag;
		if (itemNms.hasTag()) tag = itemNms.getTag();
		else tag = new NBTTagCompound();
		
		return tag;
	}
	
	
	private static ItemStack setTag(ItemStack item, NBTTagCompound tag) {
		
		net.minecraft.server.v1_12_R1.ItemStack itemNms = CraftItemStack.asNMSCopy(item);
		itemNms.setTag(tag);
		
		return CraftItemStack.asBukkitCopy(itemNms);
	}
	
	public static ItemStack addString(ItemStack item, String name, String value) {
		
		NBTTagCompound tag = TagHandler.getTag(item);
		tag.setString(name, value);
		
		return TagHandler.setTag(item, tag);
	}
	
	public static boolean hasString(ItemStack item, String name) {
		NBTTagCompound tag = TagHandler.getTag(item);
		return tag.hasKey(name);
	}
	
	public static String getString(ItemStack item, String name) {
		NBTTagCompound tag = TagHandler.getTag(item);
		return tag.getString(name);
	}
	
}
