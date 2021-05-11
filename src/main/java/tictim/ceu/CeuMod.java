package tictim.ceu;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tictim.ceu.config.CeuConfig;
import tictim.ceu.contents.CeuMetaTileEntities;
import tictim.ceu.contents.CeuResources;
import tictim.ceu.message.MessageConfigSync;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Mod(modid = CeuMod.MODID, name = CeuMod.NAME, version = CeuMod.VERSION, dependencies = "required-after:gregtech;", guiFactory = "tictim.ceu.CeuConfigGuiFactory")
@EventBusSubscriber(modid = CeuMod.MODID)
public class CeuMod{
	public static final String MODID = "ceu";
	public static final String NAME = "Ceu";
	public static final String VERSION = "1.0.5.3-SNAPSHOT";

	public static final Logger LOGGER = LogManager.getLogger(NAME);

	public static final SimpleNetworkWrapper NET = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	private static Configuration cfg;

	static List<IConfigElement> collectProperties(){
		return Collections.singletonList(new ConfigElement(cfg.getCategory("general")));
	}

	@EventHandler
	public void construct(FMLConstructionEvent event){
		CeuResources.init();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		cfg = new Configuration(new File(event.getModConfigurationDirectory(), "ceu.cfg"));
		CeuConfig.createConfigInstance(cfg);
		CeuMetaTileEntities.register();
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		CeuMetaTileEntities.addRecipes();
		NET.registerMessage(MessageConfigSync.Handler.INSTANCE, MessageConfigSync.class, 0, Side.CLIENT);
	}

	@EventHandler
	public static void onServerStarting(FMLServerStartingEvent event){
		CeuConfig.setSyncedConfig(null);
	}

	@SubscribeEvent
	public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
		if(event.player instanceof EntityPlayerMP) CeuMod.NET.sendTo(new MessageConfigSync(), (EntityPlayerMP)event.player);
	}

	@SubscribeEvent
	public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event){
		if(event.getModID().equals(CeuMod.MODID)){
			CeuConfig.createConfigInstance(cfg);
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			if(server!=null) for(EntityPlayerMP p : server.getPlayerList().getPlayers()) CeuMod.NET.sendTo(new MessageConfigSync(), p);
		}
	}
}
