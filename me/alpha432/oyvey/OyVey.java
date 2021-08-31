package me.alpha432.oyvey;

import me.alpha432.oyvey.manager.ColorManager;
import me.alpha432.oyvey.manager.CommandManager;
import me.alpha432.oyvey.manager.ConfigManager;
import me.alpha432.oyvey.manager.EventManager;
import me.alpha432.oyvey.manager.FileManager;
import me.alpha432.oyvey.manager.FriendManager;
import me.alpha432.oyvey.manager.HoleManager;
import me.alpha432.oyvey.manager.InventoryManager;
import me.alpha432.oyvey.manager.ModuleManager;
import me.alpha432.oyvey.manager.PacketManager;
import me.alpha432.oyvey.manager.PositionManager;
import me.alpha432.oyvey.manager.PotionManager;
import me.alpha432.oyvey.manager.ReloadManager;
import me.alpha432.oyvey.manager.RotationManager;
import me.alpha432.oyvey.manager.ServerManager;
import me.alpha432.oyvey.manager.SpeedManager;
import me.alpha432.oyvey.manager.TextManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(
   modid = "silence",
   name = "Silence",
   version = "0.0.2"
)
public class OyVey {
   public static final String MODID = "silence";
   public static final String MODNAME = "Silence";
   public static final String MODVER = "0.0.2";
   public static final Logger LOGGER = LogManager.getLogger("Silence");
   public static CommandManager commandManager;
   public static FriendManager friendManager;
   public static ModuleManager moduleManager;
   public static PacketManager packetManager;
   public static ColorManager colorManager;
   public static HoleManager holeManager;
   public static InventoryManager inventoryManager;
   public static PotionManager potionManager;
   public static RotationManager rotationManager;
   public static PositionManager positionManager;
   public static SpeedManager speedManager;
   public static ReloadManager reloadManager;
   public static FileManager fileManager;
   public static ConfigManager configManager;
   public static ServerManager serverManager;
   public static EventManager eventManager;
   public static TextManager textManager;
   @Instance
   public static OyVey INSTANCE;
   private static boolean unloaded = false;

   public static void load() {
      LOGGER.info("\n\nLoading Silence");
      unloaded = false;
      if (reloadManager != null) {
         reloadManager.unload();
         reloadManager = null;
      }

      textManager = new TextManager();
      commandManager = new CommandManager();
      friendManager = new FriendManager();
      moduleManager = new ModuleManager();
      rotationManager = new RotationManager();
      packetManager = new PacketManager();
      eventManager = new EventManager();
      speedManager = new SpeedManager();
      potionManager = new PotionManager();
      inventoryManager = new InventoryManager();
      serverManager = new ServerManager();
      fileManager = new FileManager();
      colorManager = new ColorManager();
      positionManager = new PositionManager();
      configManager = new ConfigManager();
      holeManager = new HoleManager();
      LOGGER.info("Managers loaded.");
      moduleManager.init();
      LOGGER.info("Modules loaded.");
      configManager.init();
      eventManager.init();
      LOGGER.info("EventManager loaded.");
      textManager.init(true);
      moduleManager.onLoad();
      LOGGER.info("Silence successfully loaded!\n");
   }

   public static void unload(boolean unload) {
      LOGGER.info("\n\nUnloading Silence");
      if (unload) {
         reloadManager = new ReloadManager();
         reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
      }

      onUnload();
      eventManager = null;
      friendManager = null;
      speedManager = null;
      holeManager = null;
      positionManager = null;
      rotationManager = null;
      configManager = null;
      commandManager = null;
      colorManager = null;
      serverManager = null;
      fileManager = null;
      potionManager = null;
      inventoryManager = null;
      moduleManager = null;
      textManager = null;
      LOGGER.info("Sielnce unloaded!\n");
   }

   public static void reload() {
      unload(false);
      load();
   }

   public static void onUnload() {
      if (!unloaded) {
         eventManager.onUnload();
         moduleManager.onUnload();
         configManager.saveConfig(configManager.config.replaceFirst("silence/", ""));
         moduleManager.onUnloadPost();
         unloaded = true;
      }

   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      LOGGER.info("I am gona gas you kike - Alpha432");
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      Display.setTitle("Silence v0.0.2");
      load();
   }
}
