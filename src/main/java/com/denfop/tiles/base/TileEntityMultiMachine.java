package com.denfop.tiles.base;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import com.denfop.Config;
import com.denfop.IUCore;
import com.denfop.api.inv.IInvSlotProcessableMulti;
import com.denfop.audio.AudioSource;
import com.denfop.container.*;
import com.denfop.gui.*;
import com.denfop.invslot.InvSlotProcessableMultiSmelting;
import com.denfop.tiles.mechanism.EnumMultiMachine;
import com.denfop.utils.ModUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

public abstract class TileEntityMultiMachine extends TileEntityElectricMachine implements IHasGui, INetworkTileEntityEventListener, IUpgradableBlock, IEnergyHandler, IEnergyReceiver {

	public final int min;
	public final int max;
	public final boolean random;
	public final int type;
	public IMachineRecipeManager recipe;
	public  int module ;
	public  boolean quickly;

	public boolean modulesize = false;
	public final short[] progress;
	protected final double[] guiProgress;
	public  int energy2;
	public final int maxEnergy2;
	public final int defaultTier, defaultEnergyStorage;
	public final int defaultOperationsPerTick;
	public final  int defaultEnergyConsume;
public int expstorage = 0;
	public int operationLength;
	public int operationsPerTick;
	public final int sizeWorkingSlot;
	public  int energyConsume;

	public AudioSource audioSource;

	/**
	 * ��������� ������ ��� ��������� � ������ ������� � ������� ������
	 */
	public IInvSlotProcessableMulti inputSlots;

	/**
	 * ��������� �������� ������
	 */
	public final InvSlotOutput outputSlots;
	public boolean rf;
	/**
	 * ��������� ��������� ���������
	 */
	public final InvSlotUpgrade upgradeSlot;
	public final int expmaxstorage;

	/**
	 * ����������� �������� ������ �������� TileEntity � ��������� ��������� ������
	 * ������ ������� 1
	 */
	public TileEntityMultiMachine(int energyconsume,int OperationsPerTick,IMachineRecipeManager recipe,int type) {
		this(1,energyconsume,OperationsPerTick,recipe,0,0,false,type);
	}
	public TileEntityMultiMachine(int energyconsume,int OperationsPerTick,IMachineRecipeManager recipe,int min,int max,boolean random,int type) {
		this(1,energyconsume,OperationsPerTick,recipe,min,max,random,type);
	}
	/**
	 * ����������� �������� ������ �������� TileEntity � ��������� ������ ����������
	 * ������ ������ �������
	 */
	public TileEntityMultiMachine(int aDefaultTier, int energyconsume, int OperationsPerTick,IMachineRecipeManager recipe,int min,int max,boolean random,int type) {
		super(energyconsume * OperationsPerTick, 1, 1);
		this.sizeWorkingSlot = getMachine().sizeWorkingSlot;
		this.progress = new short[sizeWorkingSlot];
		this.guiProgress = new double[sizeWorkingSlot];
		this.defaultEnergyConsume =this.energyConsume =  energyconsume;
		this.defaultOperationsPerTick = this.operationLength = OperationsPerTick;
		this.defaultTier = aDefaultTier;
		this.defaultEnergyStorage = energyconsume * OperationsPerTick;
		this.outputSlots = new InvSlotOutput(this, "output", 1, sizeWorkingSlot);
		this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 4, 4);
		this.expmaxstorage = Config.expstorage;
		this.maxEnergy2 = energyconsume * OperationsPerTick * 4;
		this.rf = false;
		this.quickly = false;
		this.module = 0;
		this.recipe = recipe;
		this.min=min;
		this.max=max;
		this.random=random;
		this.type=type;
	}
	
	public abstract EnumMultiMachine getMachine();

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		for (int i = 0; i < sizeWorkingSlot; i++) {
			this.progress[i] = nbttagcompound.getShort("progress" + i);
		}
		if(nbttagcompound.getInteger("expstorage") >0)
		this.expstorage = nbttagcompound.getInteger("expstorage");
		this.energy2 = nbttagcompound.getInteger("energy2");
		this.rf = nbttagcompound.getBoolean("rf");
		this.quickly = nbttagcompound.getBoolean("quickly");
		this.modulesize = nbttagcompound.getBoolean("modulesize");
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		for (int i = 0; i < sizeWorkingSlot; i++) {
			nbttagcompound.setShort("progress" + i, progress[i]);
		}
		if(this.expstorage >0)
		nbttagcompound.setInteger("expstorage", this.expstorage);
		nbttagcompound.setInteger("energy2", this.energy2);
		nbttagcompound.setBoolean("rf", this.rf);
		nbttagcompound.setBoolean("quickly", this.quickly);
		nbttagcompound.setBoolean("modulesize", this.modulesize);


	}

	/**
	 * �������� ������� �������� ��� ����������� � GUI
	 *
	 */
	public double getProgress(int slotId) {
		return this.guiProgress[slotId];
	}

	public void onLoaded() {
		super.onLoaded();
		if (IC2.platform.isSimulating())
			setOverclockRates();
	}

	public void onUnloaded() {
		super.onUnloaded();
		if (IC2.platform.isRendering() && this.audioSource != null) {
			IUCore.audioManager.removeSources(this);
			this.audioSource = null;
		}
	}
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
	}

	public int getEnergyStored(ForgeDirection from) {
		return this.energy2;
	}

	public int getMaxEnergyStored(ForgeDirection from) {
		return this.maxEnergy2;
	}
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
   if(this.rf) {
	   if (this.energy2 >= this.maxEnergy2)
		   return 0;
	   if (this.energy2 + maxReceive > this.maxEnergy2) {
		   int energyReceived = this.maxEnergy2 - this.energy2;
		   if (!simulate) {
			   this.energy2 = this.maxEnergy2;
		   }
		   return energyReceived;
	   }
	   if (!simulate) {

		   this.energy2 += maxReceive;
	   }
	   return maxReceive;
   }
		return 0;

	}
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
	return  0;
	}
		public void markDirty() {
		super.markDirty();
		if (IC2.platform.isSimulating())
			setOverclockRates();
	}
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		ItemStack ret = super.getWrenchDrop(entityPlayer);

		NBTTagCompound nbttagcompound = ModUtils.nbt(ret);
		nbttagcompound.setBoolean("rf",this.rf);
		return ret;
	}
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		if(amount == 0D)
			return 0;
		if (this.energy >= this.maxEnergy)
			return amount;
		if(this.energy+amount >= this.maxEnergy) {
			double p = this.maxEnergy - this.energy;
			this.energy +=(p);
			return amount-(p);
		}else {
			this.energy+= amount;
		}
		return 0.0D;
	}
	protected void updateEntityServer() {
		super.updateEntityServer();
		
		boolean needsInvUpdate = false;
		boolean isActive = false;
        int quickly = 1;

		for (int i = 0; i < sizeWorkingSlot; i++) {
			RecipeOutput output = getOutput(i);
			if(this.quickly)
				quickly = 100;
			int size = 1;
			if(this.inputSlots.get1(i) != null)
			if(this.modulesize) {
				for (int j = 0; ; j++) {
					ItemStack stack = new ItemStack(this.inputSlots.get1(i).getItem(), j, this.inputSlots.get1(i).getItemDamage());
					if(recipe != null ) {
						if (recipe.getOutputFor(stack, false) != null) {
							size = j;
							break;
						}
					}else if(this.inputSlots instanceof InvSlotProcessableMultiSmelting){
							size = 1;
							break;

					}
				}
				size = (int) Math.floor((float) this.inputSlots.get1(i).stackSize / size);
				int size1 = 0;

			for(int ii =0; ii <sizeWorkingSlot; ii++)
				if(this.outputSlots.get(ii) != null) {
					size1 += (64 - this.outputSlots.get(ii).stackSize);
				}else{
					size1 += 64;
				}
			if(output != null)
			size1 = size1 / output.items.get(0).stackSize;
			size = Math.min(size1,size);
			}
			if (output != null && (this.energy >= this.energyConsume*quickly*size || this.energy2 >= this.energyConsume*4*quickly*size)) {
				isActive = true;
				if (this.progress[i] == 0)
					initiate(0);

				this.progress[i]++;
				this.guiProgress[i] = (double) this.progress[i] / this.operationLength;
				 if(this.energy >= this.energyConsume*quickly*size ) {
					 this.energy -= this.energyConsume*quickly*size;
				 }else{
					 this.energy2 -= this.energyConsume*4*quickly*size;
				 }
				if (this.progress[i] >= this.operationLength || this.quickly) {
					this.guiProgress[i] = 0;
					this.progress[i] = 0;
					if(this.expstorage < this.expmaxstorage) {
					Random rand = new Random();
					
				int	exp = rand.nextInt(3) + 1;
				this.expstorage =  this.expstorage + exp;
				if(this.expstorage >= this.expmaxstorage) {
					expstorage = this.expmaxstorage;
				}
					}
					operate(i, output,size);
					needsInvUpdate = true;
					initiate(2);
				}

			} else {
				if (this.progress[i] != 0 && getActive())
					initiate(1);
				if (output == null)
					this.progress[i] = 0;
			}
		}
		
		if (getActive() != isActive) {
			setActive(isActive);
		}

		 for (int i = 0; i < this.upgradeSlot.size(); i++) {
		      ItemStack stack = this.upgradeSlot.get(i);
		      if (stack != null && stack.getItem() instanceof IUpgradeItem)
		        if (((IUpgradeItem)stack.getItem()).onTick(stack, this))
		          needsInvUpdate = true;  
		    } 

		if (needsInvUpdate)
			super.markDirty();

	}

	private void initiate(int soundEvent) {
		IC2.network.get().initiateTileEntityEvent(this, soundEvent, true);
	}
	public final float getChargeLevel1() {
		return Math.min((float)this.energy2/(float)this.maxEnergy2,1);
	}
	public void setOverclockRates() {
		 this.upgradeSlot.onChanged();
		
		    double stackOpLen = (this.defaultOperationsPerTick + this.upgradeSlot.extraProcessTime) * 64.0D * this.upgradeSlot.processTimeMultiplier;
		    this.operationsPerTick = (int)Math.min(Math.ceil(64.0D / stackOpLen), 2.147483647E9D);
		    this.operationLength = (int)Math.round(stackOpLen * this.operationsPerTick / 64.0D);
		    this.energyConsume = applyModifier(this.defaultEnergyConsume, this.upgradeSlot.extraEnergyDemand, this.upgradeSlot.energyDemandMultiplier);
		    setTier(applyModifier(this.defaultTier, this.upgradeSlot.extraTier, 1.0D));
		    this.maxEnergy = applyModifier(this.defaultEnergyStorage, this.upgradeSlot.extraEnergyStorage + this.operationLength * this.energyConsume, this.upgradeSlot.energyStorageMultiplier);

		    if (this.operationLength < 1)
		      this.operationLength = 1; 
		    	}

	/**
	 * ������������ ��������� ��� ������, ��� ������ �������� ������
	 *  @param slotId ����� ���� ���������� �����
	 * @param output ����� ������
	 */
	public void operate(int slotId, RecipeOutput output, int size) {
		for (int i = 0; i < this.operationsPerTick; i++) {

			operateOnce(slotId, output.items,size);

			output = getOutput(slotId);
			if (output == null)
				break;
		}
	}

	/**
	 * ��������� �������� �� ������ ��������
	 *  @param slotId        ����� ���� ���������� �����
	 * @param processResult ������ ��������� ����������
	 */
	public void operateOnce(int slotId, List<ItemStack> processResult, int size) {

		for(int i =0; i < size;i++) {
			if(!random) {
				this.inputSlots.consume(slotId);
				this.outputSlots.add(processResult);
			}else{
				Random rand = new Random();
				if(rand.nextInt(max+1) <= min){
					this.inputSlots.consume(slotId);
					this.outputSlots.add(processResult);
				}
			}
		   }
	}

	/**
	 * ��������� ����� ���� � ������ �������� ���� ������ �������
	 * 
	 * @param slotId ����� ���� �����������
	 * @return object
	 */
	public RecipeOutput getOutput(int slotId) {
		if (this.inputSlots.isEmpty(slotId))
			return null;
		RecipeOutput output = this.inputSlots.process(slotId);
		if (output == null)
			return null;
		if (this.outputSlots.canAdd(output.items))
			return output;

		return null;
	}

	public abstract String getInventoryName();

	public ContainerBase<? extends TileEntityMultiMachine> getGuiContainer(EntityPlayer entityPlayer) {
		return new ContainerMultiMachine(entityPlayer, this, this.sizeWorkingSlot);
	}

	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
		if(type == 0)
		return new GuiMultiMachine(new ContainerMultiMachine(entityPlayer, this, sizeWorkingSlot));
		if(type == 1)
			return new GuiMultiMachine1(new ContainerMultiMachine(entityPlayer, this, sizeWorkingSlot));
		if(type == 2)
			return new GuiMultiMachine2(new ContainerMultiMachine(entityPlayer, this, sizeWorkingSlot));
		if(type == 3)
			return new GuiMultiMachine3(new ContainerMultiMachine(entityPlayer, this, sizeWorkingSlot));
		return null;
	}

	/**
	 * �������� ����� ������/������ �������
	 *
	 */
	public String getStartSoundFile() {
		return null;
	}

	/**
	 * �������� ����� ������ ������� (��������: ���-�� ������ �� �������� �����
	 * ��������, � ������������ �������)
	 *
	 */
	public String getInterruptSoundFile() {
		return null;
	}

	public void onNetworkEvent(int event) {
		if (this.audioSource == null && getStartSoundFile() != null)
			this.audioSource = IUCore.audioManager.createSource(this, getStartSoundFile());
		switch (event) {
		case 0:
			if (this.audioSource != null)
				this.audioSource.play();
			break;
		case 1:
			if (this.audioSource != null) {
				this.audioSource.stop();
				if (getInterruptSoundFile() != null)
					IUCore.audioManager.playOnce(this, getInterruptSoundFile());
			}
			break;
		case 2:
			if (this.audioSource != null)
				this.audioSource.stop();
			break;
		}
	}

	public static int applyModifier(int base, int extra, double multiplier) {
		double ret = Math.round((base + extra) * multiplier);
		return (ret > 2.147483647E9D) ? Integer.MAX_VALUE : (int) ret;
	}

	public int getMode() {
		return 0;
	}
	
	/**
	 * ����� �������
	 */
	public double getEnergy() {
		return this.energy;
	}

	/**
	 * ������������ �������
	 */
	public boolean useEnergy(double amount) {
		if (this.energy >= amount) {
			this.energy -= amount;
			return true;
		}
		return false;
	}

	public void onGuiClosed(EntityPlayer entityPlayer) {
	}
}