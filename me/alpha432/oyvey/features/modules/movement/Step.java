package me.alpha432.oyvey.features.modules.movement;

import java.util.Iterator;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.math.AxisAlignedBB;

public class Step extends Module {
   public Setting<Integer> stepHeight;
   private double[] selectedPositions;
   private int packets;
   private static Step instance;
   private final Setting<Step.Modes> mode;

   public Step() {
      super("Step", "Allows you to step up blocks", Module.Category.MOVEMENT, true, false, false);
      this.mode = this.register(new Setting("Settings", Step.Modes.Vanila));
      this.stepHeight = this.register(new Setting("Height", 2, 1, 4, (v) -> {
         return this.mode.getValue() == Step.Modes.Vanila;
      }));
      instance = this;
   }

   public static Step getInstance() {
      if (instance == null) {
         instance = new Step();
      }

      return instance;
   }

   public void onToggle() {
      mc.field_71439_g.field_70138_W = 0.6F;
   }

   public void onUpdate() {
      if (mc.field_71439_g.field_70122_E && !mc.field_71439_g.func_70617_f_() && !mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_180799_ab() && !mc.field_71439_g.field_71158_b.field_78901_c && !mc.field_71439_g.field_70145_X) {
         if (mc.field_71439_g.field_191988_bg != 0.0F || mc.field_71439_g.field_70702_br != 0.0F) {
            double normal = this.getnormal();
            if (this.mode.getValue() == Step.Modes.Packet) {
               if (normal < 0.0D || normal > 2.0D) {
                  return;
               }

               if (normal == 2.0D) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.42D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.78D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.63D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.51D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.9D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.21D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.45D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.43D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.0D, mc.field_71439_g.field_70161_v);
               }

               if (normal == 1.5D) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.41999998688698D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.7531999805212D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.00133597911214D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.16610926093821D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.24918707874468D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.1707870772188D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0D, mc.field_71439_g.field_70161_v);
               }

               if (normal == 1.0D) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.41999998688698D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.7531999805212D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0D, mc.field_71439_g.field_70161_v);
               }
            }

            if (this.mode.getValue() == Step.Modes.Vanila) {
               mc.field_71439_g.field_70138_W = (float)(Integer)this.stepHeight.getValue();
            }

         }
      }
   }

   public double getnormal() {
      mc.field_71439_g.field_70138_W = 0.6F;
      double max_y = -1.0D;
      AxisAlignedBB grow = mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, 0.05D, 0.0D).func_186662_g(0.05D);
      if (!mc.field_71441_e.func_184144_a(mc.field_71439_g, grow.func_72317_d(0.0D, 2.0D, 0.0D)).isEmpty()) {
         return 100.0D;
      } else {
         Iterator var4 = mc.field_71441_e.func_184144_a(mc.field_71439_g, grow).iterator();

         while(var4.hasNext()) {
            AxisAlignedBB aabb = (AxisAlignedBB)var4.next();
            if (aabb.field_72337_e > max_y) {
               max_y = aabb.field_72337_e;
            }
         }

         return max_y - mc.field_71439_g.field_70163_u;
      }
   }

   public static enum Modes {
      Vanila,
      Packet;
   }
}
