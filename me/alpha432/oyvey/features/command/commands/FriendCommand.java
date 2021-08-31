package me.alpha432.oyvey.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Iterator;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.manager.FriendManager;

public class FriendCommand extends Command {
   public FriendCommand() {
      super("friend", new String[]{"<add/del/name/clear>", "<name>"});
   }

   public void execute(String[] commands) {
      String f;
      if (commands.length != 1) {
         byte var7;
         if (commands.length == 2) {
            f = commands[0];
            var7 = -1;
            switch(f.hashCode()) {
            case 108404047:
               if (f.equals("reset")) {
                  var7 = 0;
               }
            default:
               switch(var7) {
               case 0:
                  OyVey.friendManager.onLoad();
                  sendMessage("Friends got reset.");
                  return;
               default:
                  sendMessage(commands[0] + (OyVey.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
               }
            }
         } else {
            if (commands.length >= 2) {
               f = commands[0];
               var7 = -1;
               switch(f.hashCode()) {
               case 96417:
                  if (f.equals("add")) {
                     var7 = 0;
                  }
                  break;
               case 99339:
                  if (f.equals("del")) {
                     var7 = 1;
                  }
               }

               switch(var7) {
               case 0:
                  OyVey.friendManager.addFriend(commands[1]);
                  sendMessage(ChatFormatting.GREEN + commands[1] + " has been friended");
                  return;
               case 1:
                  OyVey.friendManager.removeFriend(commands[1]);
                  sendMessage(ChatFormatting.RED + commands[1] + " has been unfriended");
                  return;
               default:
                  sendMessage("Unknown Command, try friend add/del (name)");
               }
            }

         }
      } else {
         if (OyVey.friendManager.getFriends().isEmpty()) {
            sendMessage("Friend list empty D:.");
         } else {
            f = "Friends: ";
            Iterator var3 = OyVey.friendManager.getFriends().iterator();

            while(var3.hasNext()) {
               FriendManager.Friend friend = (FriendManager.Friend)var3.next();

               try {
                  f = f + friend.getUsername() + ", ";
               } catch (Exception var6) {
               }
            }

            sendMessage(f);
         }

      }
   }
}
