package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class TpsSync extends Module {
   private static TpsSync INSTANCE = new TpsSync();
   public Setting<Boolean> attack;
   public Setting<Boolean> mining;

   public TpsSync() {
      super("TpsSync", "Syncs your client with the TPS.", Module.Category.PLAYER, true, false, false);
      this.attack = this.register(new Setting("Attack", Boolean.FALSE));
      this.mining = this.register(new Setting("Mine", Boolean.TRUE));
      this.setInstance();
   }

   public static TpsSync getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new TpsSync();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }
}
