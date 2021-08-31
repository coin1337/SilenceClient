package me.alpha432.oyvey.features.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.entity.player.EntityPlayer;

public class PopCounter extends Module {
   public static HashMap<String, Integer> TotemPopContainer = new HashMap();
   private static PopCounter INSTANCE = new PopCounter();

   public PopCounter() {
      super("PopCounter", "Counts other players totem pops.", Module.Category.PLAYER, true, false, false);
      this.setInstance();
   }

   public static PopCounter getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new PopCounter();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public void onEnable() {
      TotemPopContainer.clear();
   }

   public void onDeath(EntityPlayer player) {
      if (TotemPopContainer.containsKey(player.func_70005_c_())) {
         int l_Count = (Integer)TotemPopContainer.get(player.func_70005_c_());
         TotemPopContainer.remove(player.func_70005_c_());
         if (l_Count == 1) {
            Command.sendMessage(ChatFormatting.RED + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.RED + " Totem!");
         } else {
            Command.sendMessage(ChatFormatting.RED + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.RED + " Totems!");
         }
      }

   }

   public void onTotemPop(EntityPlayer player) {
      if (!fullNullCheck()) {
         if (!mc.field_71439_g.equals(player)) {
            int l_Count = 1;
            if (TotemPopContainer.containsKey(player.func_70005_c_())) {
               l_Count = (Integer)TotemPopContainer.get(player.func_70005_c_());
               HashMap var10000 = TotemPopContainer;
               String var10001 = player.func_70005_c_();
               ++l_Count;
               var10000.put(var10001, l_Count);
            } else {
               TotemPopContainer.put(player.func_70005_c_(), l_Count);
            }

            if (l_Count == 1) {
               Command.sendMessage(ChatFormatting.RED + player.func_70005_c_() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.RED + " Totem.");
            } else {
               Command.sendMessage(ChatFormatting.RED + player.func_70005_c_() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.RED + " Totems.");
            }

         }
      }
   }
}
