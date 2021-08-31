package me.alpha432.oyvey.features.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class ExtraTab extends Module {
   private static ExtraTab INSTANCE = new ExtraTab();
   public Setting<Integer> size = this.register(new Setting("Size", 250, 1, 1000));

   public ExtraTab() {
      super("ExtraTab", "Extends Tab.", Module.Category.MISC, false, false, false);
      this.setInstance();
   }

   public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
      String name = networkPlayerInfoIn.func_178854_k() != null ? networkPlayerInfoIn.func_178854_k().func_150254_d() : ScorePlayerTeam.func_96667_a(networkPlayerInfoIn.func_178850_i(), networkPlayerInfoIn.func_178845_a().getName());
      return OyVey.friendManager.isFriend(name) ? ChatFormatting.AQUA + name : name;
   }

   public static ExtraTab getINSTANCE() {
      if (INSTANCE == null) {
         INSTANCE = new ExtraTab();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }
}
