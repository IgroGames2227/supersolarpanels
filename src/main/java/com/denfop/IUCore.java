
package com.denfop;

import aroma1997.uncomplication.enet.EnergyNetGlobal;
import com.denfop.audio.AudioManager;
import com.denfop.block.base.BlocksItems;
import com.denfop.events.EventUpdate;
import com.denfop.events.TickHandlerIU;
import com.denfop.integration.crafttweaker.CTCore;
import com.denfop.integration.forestry.FIntegration;
import com.denfop.item.modules.EnumQuarryModules;
import com.denfop.item.modules.EnumSpawnerModules;
import com.denfop.item.upgrade.ItemUpgradePanelKit;
import com.denfop.network.NetworkManager;
import com.denfop.proxy.CommonProxy;
import com.denfop.register.Register;
import com.denfop.register.RegisterOreDict;
import com.denfop.tab.IUTab;
import com.denfop.tiles.mechanism.EnumUpgradesMultiMachine;
import com.denfop.tiles.overtimepanel.EnumType;
import com.denfop.utils.KeyboardIU;
import com.denfop.utils.Keys;
import com.denfop.world.GenOre;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import ic2.api.energy.EnergyNet;
import ic2.core.util.SideGateway;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.MOD_VERSION, dependencies = Constants.DEPENDENCES, acceptedMinecraftVersions = Constants.acceptedMinecraftVersions, certificateFingerprint = "denfop-certificate")
public class IUCore {

	public static final IUTab tabssp;
	@Mod.Instance("industrialupgrade")
	public static IUCore instance;
	@SidedProxy(clientSide = "com.denfop.proxy.ClientProxy", serverSide = "com.denfop.proxy.CommonProxy")
	public static CommonProxy proxy;
	public static final IUTab tabssp2;
	public static final IUTab tabssp3;
	public static final IUTab tabssp4;
	public static final IUTab tabssp1;
	@SidedProxy(
			clientSide = "com.denfop.utils.KeyboardClient",
			serverSide = "com.denfop.utils.KeyboardIU"
	)
	public static KeyboardIU keyboard;

	public static SideGateway<NetworkManager> network;
	public static final List<ItemStack> list = new ArrayList<>();
	@SidedProxy(clientSide = "com.denfop.audio.AudioManagerClient", serverSide = "com.denfop.audio.AudioManager")
	public static AudioManager audioManager;
	public static IUAchievements achievements;
	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		Config.config(event);
		IUItem.register_mineral();
		if (Loader.isModLoaded("AdvancedSolarPanel"))
			CommonProxy.throwInitException(new LoaderException(
					"IndustrialUpgrade is incompatible with Advanced Solar Panels.Please delete Advanced solar Panels"));

		BlocksItems.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

		Register.register();
		Register.registertiles();
		proxy.integration();
		MinecraftForge.EVENT_BUS.register(new TickHandlerIU());
		FMLCommonHandler.instance().bus().register(new TickHandlerIU());
		MinecraftForge.EVENT_BUS.register(this);
		RegisterOreDict.oredict();



		proxy.load();
		proxy.initCore();
		GenOre.init();
		Keys.instance = IUCore.keyboard;
		IUCore.audioManager.initialize();
		achievements = new IUAchievements();
		}


	public static final List<ItemStack> get_ore = new ArrayList<>();
	public static final List<ItemStack> get_ingot = new ArrayList<>();

	@SubscribeEvent
	public void getore(OreDictionary.OreRegisterEvent event) {
		String oreClass = event.Name;
		if (oreClass.startsWith("ore")) {
			if(oreClass.equals("oreChargedCertusQuartz"))
				return;
			if(oreClass.equals("oreCertusQuartz"))
				return;
			if (get_ore == null) {

				assert false;
				get_ore.addAll(OreDictionary.getOres(oreClass));
			} else if (!get_ore.contains(OreDictionary.getOres(oreClass).get(0))) {
				get_ore.addAll(OreDictionary.getOres(oreClass));

			}

		}
		if (Loader.isModLoaded("Thaumcraft")) {
			if (!get_ore.contains(OreDictionary.getOres("oreCinnabar").get(0))) {
				get_ore.add(OreDictionary.getOres("oreInfusedAir").get(0));
				get_ore.add(OreDictionary.getOres("oreInfusedEarth").get(0));
				get_ore.add(OreDictionary.getOres("oreInfusedEntropy").get(0));
				get_ore.add(OreDictionary.getOres("oreInfusedFire").get(0));
				get_ore.add(OreDictionary.getOres("oreInfusedOrder").get(0));
				get_ore.add(OreDictionary.getOres("oreInfusedWater").get(0));
				get_ore.add(OreDictionary.getOres("oreAmber").get(0));
				get_ore.add(OreDictionary.getOres("oreCinnabar").get(0));
			}

		}
		if (oreClass.startsWith("ingot")) {
			String temp = oreClass.substring(5);
			String tempore = "ore" + temp;
			if (get_ingot == null) {
				if (OreDictionary.getOres(tempore).size() >= 1)
					get_ingot.add(OreDictionary.getOres(oreClass).get(0));

			} else {
				if (OreDictionary.getOres(tempore).size() >= 1) {
					if (!get_ingot.contains(OreDictionary.getOres(oreClass).get(0))) {
						get_ingot.add(OreDictionary.getOres(oreClass).get(0));

					}
				}
			}
		}
		if (oreClass.startsWith("gem")) {
			String temp = oreClass.substring(3);
			String tempore = "ore" + temp;
			if (get_ingot == null) {
				if (OreDictionary.getOres(tempore).size() >= 1)
					get_ingot.add(OreDictionary.getOres(oreClass).get(0));

			} else {
				if (OreDictionary.getOres(tempore).size() >= 1) {
					if (!get_ingot.contains(OreDictionary.getOres(oreClass).get(0))) {
						get_ingot.add(OreDictionary.getOres(oreClass).get(0));

					}
				}
			}
		}
		if (Loader.isModLoaded("Thaumcraft")) {
			if (!get_ingot.contains(OreDictionary.getOres("clusterCinnabar").get(0))) {
				get_ingot.add(OreDictionary.getOres("shardAir").get(0));
				get_ingot.add(OreDictionary.getOres("shardEarth").get(0));
				get_ingot.add(OreDictionary.getOres("shardEntropy").get(0));
				get_ingot.add(OreDictionary.getOres("shardFire").get(0));
				get_ingot.add(OreDictionary.getOres("shardOrder").get(0));
				get_ingot.add(OreDictionary.getOres("shardWater").get(0));
				get_ingot.add(OreDictionary.getOres("gemAmber").get(0));
				get_ingot.add(OreDictionary.getOres("clusterCinnabar").get(0));
			}
		}
		if (oreClass.startsWith("shard")) {
			String temp = oreClass.substring(5);
			String tempore = "ore" + temp;
			if (get_ingot == null) {
				if (OreDictionary.getOres(tempore).size() >= 1)
					get_ingot.add(OreDictionary.getOres(oreClass).get(0));

			} else {
				if (OreDictionary.getOres(tempore).size() >= 1) {
					if (!get_ingot.contains(OreDictionary.getOres(oreClass).get(0))) {
						get_ingot.add(OreDictionary.getOres(oreClass).get(0));

					}
				}
			}
		}
		if (oreClass.startsWith("ore")) {
			if(oreClass.equals("oreChargedCertusQuartz"))
				return;
			if(oreClass.equals("oreCertusQuartz"))
				return;
			String temp = oreClass.substring(3);

			if (OreDictionary.getOres("gem" + temp) == null || OreDictionary.getOres("gem" + temp).size() < 1) {

				list.add(OreDictionary.getOres(oreClass).get(0));

			} else {
				if (!list.contains(OreDictionary.getOres("gem" + temp).get(0))) {
					list.add(OreDictionary.getOres("gem" + temp).get(0));
				}
			}

		}
		if (oreClass.startsWith("ore"))
			if (Loader.isModLoaded("Thaumcraft")) {
				if (!list.contains(OreDictionary.getOres("clusterCinnabar").get(0))) {
					list.add(OreDictionary.getOres("shardAir").get(0));
					list.add(OreDictionary.getOres("shardEarth").get(0));
					list.add(OreDictionary.getOres("shardEntropy").get(0));
					list.add(OreDictionary.getOres("shardFire").get(0));
					list.add(OreDictionary.getOres("shardOrder").get(0));
					list.add(OreDictionary.getOres("shardWater").get(0));
					list.add(OreDictionary.getOres("gemAmber").get(0));
					list.add(OreDictionary.getOres("clusterCinnabar").get(0));
				}
			}
		if (oreClass.startsWith("gem")) {
			String temp = oreClass.substring(3);
			String orename = "ore" + temp;

			list.removeAll(OreDictionary.getOres(orename));
			list.add(OreDictionary.getOres(oreClass).get(0));

		}
	}

	@Mod.EventHandler
	public void load(final FMLInitializationEvent event) {
		if (Config.newsystem)
			initENet();
		if (Loader.isModLoaded("Waila"))
			FMLInterModComms.sendMessage("Waila", "register",
					"com.denfop.integration.waila.WailaHandler.callbackRegister");
		if (Loader.isModLoaded("modtweaker2"))
			CTCore.register();
		if(!Config.disableUpdateCheck)
		FMLCommonHandler.instance().bus().register(new EventUpdate());

	}

	public static void initENet() {
		EnergyNet.instance = EnergyNetGlobal.initialize();
	}



	@EventHandler
	public void onMissingMappings(FMLMissingMappingsEvent event) {
		BlocksItems.onMissingMappings(event);
	}

	@Mod.EventHandler
	public void Init(final FMLInitializationEvent event) {
		proxy.registerEvents();	proxy.registerRecipe();
		ItemUpgradePanelKit.EnumSolarPanelsKit.registerkit();
		EnumQuarryModules.register();
		EnumType.register();
		EnumUpgradesMultiMachine.register();
		EnumSpawnerModules.register();
	}

	@Mod.EventHandler
	public void afterModsLoaded(final FMLPostInitializationEvent event) {
		proxy.registerRenderers();
		if(Loader.isModLoaded("Forestry"))
			FIntegration.init();
	}

	static {
		tabssp = new IUTab(0,"sspblocks");
		tabssp1 = new IUTab(1,"sspmodules");
		tabssp2 = new IUTab(2,"ssptools");
		tabssp3 = new IUTab(3,"sspitems");
		tabssp4 = new IUTab(4,"sspores");
		IUCore.instance = new IUCore();
	}

	public static boolean isSimulating() {
		return !FMLCommonHandler.instance().getEffectiveSide().isClient();
	}
	@SubscribeEvent
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		if (proxy.isSimulating()) {
			keyboard.removePlayerReferences(event.player);
		}

	}
	static {
		IUCore.network = new SideGateway<>("com.denfop.network.NetworkManager", "com.denfop.network.NetworkManagerClient");
	}
}