package me.alpha432.oyvey.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Iterator;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.command.Command;

public class HelpCommand extends Command {
   public HelpCommand() {
      super("help");
   }

   public void execute(String[] commands) {
      sendMessage("Commands: ");
      Iterator var2 = OyVey.commandManager.getCommands().iterator();

      while(var2.hasNext()) {
         Command command = (Command)var2.next();
         sendMessage(ChatFormatting.GRAY + OyVey.commandManager.getPrefix() + command.getName());
      }

   }
}
