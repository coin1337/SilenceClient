package me.alpha432.oyvey.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class MotionUtil implements Util {
   public static boolean isMoving(EntityLivingBase entity) {
      return entity.field_191988_bg != 0.0F || entity.field_70702_br != 0.0F;
   }

   public static void setSpeed(EntityLivingBase entity, double speed) {
      double[] dir = forward(speed);
      entity.field_70159_w = dir[0];
      entity.field_70179_y = dir[1];
   }

   public static double getBaseMoveSpeed() {
      double baseSpeed = 0.2873D;
      if (mc.field_71439_g != null && mc.field_71439_g.func_70644_a(Potion.func_188412_a(1))) {
         int amplifier = mc.field_71439_g.func_70660_b(Potion.func_188412_a(1)).func_76458_c();
         baseSpeed *= 1.0D + 0.2D * (double)(amplifier + 1);
      }

      return baseSpeed;
   }

   public static double getSpeed(EntityLivingBase entity) {
      return Math.sqrt(entity.field_70159_w * entity.field_70159_w + entity.field_70179_y * entity.field_70179_y);
   }

   public static double[] forward(double speed) {
      float forward = mc.field_71439_g.field_71158_b.field_192832_b;
      float side = mc.field_71439_g.field_71158_b.field_78902_a;
      float yaw = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
      if (forward != 0.0F) {
         if (side > 0.0F) {
            yaw += (float)(forward > 0.0F ? -45 : 45);
         } else if (side < 0.0F) {
            yaw += (float)(forward > 0.0F ? 45 : -45);
         }

         side = 0.0F;
         if (forward > 0.0F) {
            forward = 1.0F;
         } else if (forward < 0.0F) {
            forward = -1.0F;
         }
      }

      double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
      double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      double posX = (double)forward * speed * cos + (double)side * speed * sin;
      double posZ = (double)forward * speed * sin - (double)side * speed * cos;
      return new double[]{posX, posZ};
   }
}
