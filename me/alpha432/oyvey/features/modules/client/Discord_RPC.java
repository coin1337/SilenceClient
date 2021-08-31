package me.alpha432.oyvey.features.modules.client;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.alpha432.oyvey.features.modules.Module;

public class Discord_RPC extends Module {
   private static String discordID = "821045682428969040";
   private static DiscordRichPresence discordRichPresence = new DiscordRichPresence();
   private static DiscordRPC discordRPC;

   public Discord_RPC() {
      super("RPC", "DiscordRPC", Module.Category.CLIENT, true, false, false);
   }

   public void onToggle() {
      startRPC();
   }

   public static void startRPC() {
      DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
      eventHandlers.disconnected = (var1, var2) -> {
         System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2);
      };
      discordRPC.Discord_Initialize(discordID, eventHandlers, true, (String)null);
      discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
      discordRichPresence.details = "v0.0.2";
      discordRichPresence.largeImageKey = "silenceclient";
      discordRichPresence.largeImageText = "";
      discordRichPresence.state = "";
      discordRPC.Discord_UpdatePresence(discordRichPresence);
   }

   public static void stopRPC() {
      discordRPC.Discord_Shutdown();
      discordRPC.Discord_ClearPresence();
   }

   static {
      discordRPC = DiscordRPC.INSTANCE;
   }
}
