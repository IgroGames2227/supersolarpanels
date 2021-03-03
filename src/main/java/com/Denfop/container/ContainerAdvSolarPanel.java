package com.Denfop.container;


import java.util.ArrayList;
import java.util.List;

import com.Denfop.tiles.base.TileEntitySolarPanel;
import com.Denfop.utils.ModUtils;

import ic2.core.IC2;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ContainerAdvSolarPanel extends Container
{
	private TileEntitySolarPanel tileentity;
	private int storage = 0;
	private int fuel = 0;
	private boolean sunIsUp;
	private boolean skyIsVisible;
	private int generating;
	private int storage2;
	private int solarType;
	private boolean rain;
	private int panelx;
	private int panely;
	private int panelz;

	public ContainerAdvSolarPanel(InventoryPlayer inventoryplayer, TileEntitySolarPanel tileentitysolarpanel)
	{
		this.tileentity = tileentitysolarpanel;

		for (int j = 0; j < 9; ++j)
		{
			
			this.addSlotToContainer(new Slot(this.tileentity, j, 17 + j * 18, 59));
		}
		
		for (int i = 0; i < 3; ++i)
		{
			for (int k = 0; k < 9; ++k)
			{
				this.addSlotToContainer(new Slot(inventoryplayer, k + i * 9 + 9, 17 + k * 18, 86 + i * 18));
			}
		}

		for (int j = 0; j < 9; ++j)
		{
			this.addSlotToContainer(new Slot(inventoryplayer, j, 17 + j * 18, 144));
		}

	
	
   
    
 
	}
	@Override
	public void addCraftingToCrafters(ICrafting icrafting)
	{
		super.addCraftingToCrafters(icrafting);

		icrafting.sendProgressBarUpdate(this, 0, this.tileentity.sunIsUp ? 1 : 0);
		icrafting.sendProgressBarUpdate(this, 1, this.tileentity.skyIsVisible ? 1 : 0);
		icrafting.sendProgressBarUpdate(this, 8, this.tileentity.rain ? 1 : 0);
		
	
		ModUtils.sendContainerInt(this, icrafting, 2, 5, this.tileentity.generating);
		ModUtils.sendContainerInt(this, icrafting, 3, 4, this.tileentity.storage);
		ModUtils.sendContainerInt(this, icrafting, 6, 7, this.tileentity.storage2);
		ModUtils.sendContainerInt(this, icrafting, 9, 10, this.tileentity.panelx);
		ModUtils.sendContainerInt(this, icrafting, 11, 12, this.tileentity.panely);
		ModUtils.sendContainerInt(this, icrafting, 13, 14, this.tileentity.panelz);
		
	}
	
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); ++i)
		{
			ICrafting icrafting = (ICrafting) this.crafters.get(i);
			
			
					if (this.sunIsUp != this.tileentity.sunIsUp)
				icrafting.sendProgressBarUpdate(this, 0, this.tileentity.sunIsUp ? 1 : 0);
					if (this.rain != this.tileentity.rain)
						icrafting.sendProgressBarUpdate(this, 8, this.tileentity.rain ? 1 : 0);
			if (this.skyIsVisible != this.tileentity.skyIsVisible)
				icrafting.sendProgressBarUpdate(this, 1, this.tileentity.skyIsVisible ? 1 : 0);
			if (this.generating != this.tileentity.generating)
				ModUtils.sendContainerInt(this, icrafting, 2, 5, this.tileentity.generating);
			if (this.storage != this.tileentity.storage)
				ModUtils.sendContainerInt(this, icrafting, 3, 4, this.tileentity.storage);
			if (this.storage2 != this.tileentity.storage2)
				ModUtils.sendContainerInt(this, icrafting, 6, 7, this.tileentity.storage2);
			if (this.panelx != this.tileentity.panelx)
				ModUtils.sendContainerInt(this, icrafting, 9, 10, this.tileentity.panelx);
			if (this.panely != this.tileentity.panely)
				ModUtils.sendContainerInt(this, icrafting, 11, 12, this.tileentity.panely);
			if (this.panelz != this.tileentity.panelz)
				ModUtils.sendContainerInt(this, icrafting, 13, 14, this.tileentity.panelz);
		
		}
		this.rain = this.tileentity.rain;
		this.sunIsUp = this.tileentity.sunIsUp;
		this.skyIsVisible = this.tileentity.skyIsVisible;
		this.generating = this.tileentity.generating;
		this.storage = this.tileentity.storage;
		this.storage2 = this.tileentity.storage2;
		this.panelx = this.tileentity.panelx;
		this.panely = this.tileentity.panely;
		this.panelz = this.tileentity.panelz;
		
	}

	@Override
	public void updateProgressBar(int id, int val)
	{
		
		if (id == 0)
			this.tileentity.sunIsUp = val != 0;
		if (id == 8)
			this.tileentity.rain = val != 0;
		if (id == 1)
			this.tileentity.skyIsVisible = val != 0;

			this.tileentity.generating = ModUtils.recieveContainerInt(2, 5, id, val, this.tileentity.generating);
		this.tileentity.storage = ModUtils.recieveContainerInt(3, 4, id, val, this.tileentity.storage);
		this.tileentity.storage2 = ModUtils.recieveContainerInt(6, 7, id, val, this.tileentity.storage2);
		
		this.tileentity.panelx = ModUtils.recieveContainerInt(9, 10, id, val, this.tileentity.panelx);
		this.tileentity.panely = ModUtils.recieveContainerInt(11, 12, id, val, this.tileentity.panely);
		this.tileentity.panelz = ModUtils.recieveContainerInt(13, 14, id, val, this.tileentity.panelz);
			}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber)
	{
		ItemStack stack = null;
		Slot slotObject = (Slot) this.inventorySlots.get(slotNumber);
		if (slotObject != null && slotObject.getHasStack())
		{
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			if (slotNumber >= 0 && slotNumber < 9)
			{
				if (!this.mergeItemStack(stackInSlot, 10, 45, true))
					return null;
			}
				
			else if (slotNumber >= 9 && slotNumber <= 38)
			{
				if (!this.mergeItemStack(stackInSlot, 0, 9, false) && !this.mergeItemStack(stackInSlot, 39, 45, false))
					return null;
			}
			else if (slotNumber >= 39 && slotNumber < 45)
			{
				if (!this.mergeItemStack(stackInSlot, 0, 9, false) && !this.mergeItemStack(stackInSlot, 9, 38, false))
					return null;
			}
			else if (!this.mergeItemStack(stackInSlot, 0, 45, false))
				return null;

			if (stackInSlot.stackSize == 0)
				slotObject.putStack(null);
			else
				slotObject.onSlotChanged();

			if (stack.stackSize == stackInSlot.stackSize)
				return null;

			slotObject.onPickupFromSlot(player, stackInSlot);
		}

		return stack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return this.tileentity.isUseableByPlayer(entityplayer);
	}
}
