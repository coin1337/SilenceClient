package me.alpha432.oyvey.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.command.commands.BindCommand;
import me.alpha432.oyvey.features.command.commands.ConfigCommand;
import me.alpha432.oyvey.features.command.commands.FriendCommand;
import me.alpha432.oyvey.features.command.commands.HelpCommand;
import me.alpha432.oyvey.features.command.commands.ModuleCommand;
import me.alpha432.oyvey.features.command.commands.PrefixCommand;
import me.alpha432.oyvey.features.command.commands.ReloadCommand;
import me.alpha432.oyvey.features.command.commands.ReloadSoundCommand;
import me.alpha432.oyvey.features.command.commands.UnloadCommand;

public class CommandManager extends Feature {
   private final ArrayList<Command> commands = new ArrayList();
   private String clientMessage = "<OyVey>";
   private String prefix = ".";

   public CommandManager() {
      super("Command");
      this.commands.add(new BindCommand());
      this.commands.add(new ModuleCommand());
      this.commands.add(new PrefixCommand());
      this.commands.add(new ConfigCommand());
      this.commands.add(new FriendCommand());
      this.commands.add(new HelpCommand());
      this.commands.add(new ReloadCommand());
      this.commands.add(new UnloadCommand());
      this.commands.add(new ReloadSoundCommand());
   }

   public static String[] removeElement(String[] input, int indexToDelete) {
      LinkedList<String> result = new LinkedList();

      for(int i = 0; i < input.length; ++i) {
         if (i != indexToDelete) {
            result.add(input[i]);
         }
      }

      return (String[])result.toArray(input);
   }

   private static String strip(String str, String key) {
      return str.startsWith(key) && str.endsWith(key) ? str.substring(key.length(), str.length() - key.length()) : str;
   }

   public void executeCommand(String command) {
      String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
      String name = parts[0].substring(1);
      String[] args = removeElement(parts, 0);

      for(int i = 0; i < args.length; ++i) {
         if (args[i] != null) {
            args[i] = strip(args[i], "\"");
         }
      }

      Iterator var7 = this.commands.iterator();

      Command c;
      do {
         if (!var7.hasNext()) {
            Command.sendMessage(ChatFormatting.GRAY + "Command not found, type 'help' for the commands list.");
            return;
         }

         c = (Command)var7.next();
      } while(!c.getName().equalsIgnoreCase(name));

      c.execute(parts);
   }

   public Command getCommandByName(String name) {
      Iterator var2 = this.commands.iterator();

      Command command;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         command = (Command)var2.next();
      } while(!command.getName().equals(name));

      return command;
   }

   public ArrayList<Command> getCommands() {
      return this.commands;
   }

   public String getClientMessage() {
      return this.clientMessage;
   }

   public void setClientMessage(String clientMessage) {
      this.clientMessage = clientMessage;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }
}
