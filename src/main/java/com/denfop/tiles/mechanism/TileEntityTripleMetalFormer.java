package com.denfop.tiles.mechanism;

import java.util.EnumSet;
import java.util.Set;

import com.denfop.container.ContainerMultiMetalFormer;
import com.denfop.gui.GuiMultiMetalFormer;
import com.denfop.invslot.InvSlotProcessableMultiGeneric;
import com.denfop.tiles.base.TileEntityMultiMachine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.upgrade.UpgradableProperty;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class TileEntityTripleMetalFormer extends TileEntityMultiMachine
		implements INetworkClientTileEntityEventListener {
	private int mode;

	public TileEntityTripleMetalFormer() {
		super(EnumMultiMachine.TRIPLE_METAL_FORMER.usagePerTick,EnumMultiMachine.TRIPLE_METAL_FORMER.lenghtOperation, Recipes.metalformerExtruding,0);
		this.inputSlots = new InvSlotProcessableMultiGeneric(this, "input", sizeWorkingSlot, Recipes.metalformerExtruding);
	}

	@Override
	public EnumMultiMachine getMachine() {
		return EnumMultiMachine.TRIPLE_METAL_FORMER;
	}
	
	public ContainerBase<? extends TileEntityMultiMachine> getGuiContainer(EntityPlayer entityPlayer) {
		return new ContainerMultiMetalFormer(entityPlayer, this, this.sizeWorkingSlot);
	}

	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
		return new GuiMultiMetalFormer(new ContainerMultiMetalFormer(entityPlayer, this, sizeWorkingSlot));
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		setMode(nbttagcompound.getInteger("mode"));
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("mode", this.mode);
	}

	public String getInventoryName() {
		return StatCollector.translateToLocal("iu.MetalFormer2.name");
	}

	public float getWrenchDropRate() {
		return 0.85F;
	}

	public void onNetworkEvent(EntityPlayer player, int event) {
		if (event == 0) {
			cycleMode();
		}
	}

	public void onNetworkUpdate(String field) {
		super.onNetworkUpdate(field);
		if (field.equals("mode"))
			setMode(this.mode);
	}

	public int getMode() {
		return this.mode;
	}

	public void setMode(int mode) {
		InvSlotProcessableMultiGeneric slot = (InvSlotProcessableMultiGeneric) this.inputSlots;
		switch (mode) {
		case 0:
			slot.setRecipeManager(Recipes.metalformerExtruding);
			break;
		case 1:
			slot.setRecipeManager(Recipes.metalformerRolling);
			break;
		case 2:
			slot.setRecipeManager(Recipes.metalformerCutting);
			break;
		default:
			throw new RuntimeException("invalid mode: " + mode);
		}
		this.mode = mode;
	}

	private void cycleMode() {
		setMode((getMode() + 1) % 3);
	}

	public Set<UpgradableProperty> getUpgradableProperties() {
		return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer,
				UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
	}
}