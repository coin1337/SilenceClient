package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.Util;

public class ReverseStep extends Module {
   private static ReverseStep INSTANCE = new ReverseStep();
   private final Setting<Boolean> twoBlocks;

   public ReverseStep() {
      super("ReverseStep", "ReverseStep.", Module.Category.MOVEMENT, true, false, false);
      this.twoBlocks = this.register(new Setting("2Blocks", Boolean.FALSE));
      this.setInstance();
   }

   public static ReverseStep getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ReverseStep();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public void onUpdate() {
      if (Util.mc.field_71439_g != null && Util.mc.field_71441_e != null && !Util.mc.field_71439_g.func_70090_H() && !Util.mc.field_71439_g.func_180799_ab() && Util.mc.field_71439_g.field_70122_E) {
         --Util.mc.field_71439_g.field_70181_x;
      }

   }
}
