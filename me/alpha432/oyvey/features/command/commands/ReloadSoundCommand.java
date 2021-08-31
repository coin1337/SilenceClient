package me.alpha432.oyvey.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.features.command.Command;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReloadSoundCommand extends Command {
   public ReloadSoundCommand() {
      super("sound", new String[0]);
   }

   public void execute(String[] commands) {
      try {
         SoundManager sndManager = (SoundManager)ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, mc.func_147118_V(), new String[]{"sndManager", "sndManager"});
         sndManager.func_148596_a();
         Command.sendMessage(ChatFormatting.GREEN + "Reloaded Sound System.");
      } catch (Exception var3) {
         System.out.println(ChatFormatting.RED + "Could not restart sound manager: " + var3.toString());
         var3.printStackTrace();
         Command.sendMessage(ChatFormatting.RED + "Couldnt Reload Sound System!");
      }

   }
}
