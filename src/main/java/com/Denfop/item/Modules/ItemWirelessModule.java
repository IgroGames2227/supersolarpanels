package com.Denfop.item.Modules;

import java.util.List;

import com.Denfop.SuperSolarPanels;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class ItemWirelessModule extends Item {
	
	public ItemWirelessModule() {
		 setCreativeTab(SuperSolarPanels.tabssp);
		this.setUnlocalizedName("WirelessModule");
		this.setTextureName("wirelessvajra:wirelessmodule");
		this.setMaxStackSize(64);
		
		
		
		
	}
	   public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
	        NBTTagCompound nbttagcompound = SuperSolarPanels.getOrCreateNbtData(itemStack);
	        info.add(StatCollector.translateToLocal("ssp.Name") + ": " +nbttagcompound.getString("Name"));
	        info.add(StatCollector.translateToLocal("ssp.World") + ": " +nbttagcompound.getInteger("World"));
	        info.add(StatCollector.translateToLocal("ssp.tier") + ": " +nbttagcompound.getInteger("tier"));
	        info.add(StatCollector.translateToLocal("ssp.Xcoord") + ": " +nbttagcompound.getInteger("Xcoord"));
	        info.add(StatCollector.translateToLocal("ssp.Ycoord") + ": " +nbttagcompound.getInteger("Ycoord"));
	        info.add(StatCollector.translateToLocal("ssp.Zcoord") + ": " +nbttagcompound.getInteger("Zcoord"));
	       }
	
	
	

}