package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtilTwo;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.MotionUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class Speed extends Module {
   private static Speed INSTANCE = new Speed();
   public Setting<Speed.Mode> modes;
   public Setting<Double> speed;
   private final Setting<Double> yPortSpeed;
   public Setting<Boolean> motionyonoff;
   public Setting<Boolean> stepyport;
   private Timer timer;
   private float stepheight;
   public double startY;
   int waitCounter;
   int forward;

   public Speed() {
      super("Speed", "Speed.", Module.Category.MOVEMENT, true, false, false);
      this.modes = this.register(new Setting("Mode", Speed.Mode.STRAFE));
      this.speed = this.register(new Setting("Speed", 1.0D, 1.0D, 50.0D, (v) -> {
         return this.modes.getValue() == Speed.Mode.VANILA;
      }));
      this.yPortSpeed = this.register(new Setting("YPort Speed", 0.1D, 0.0D, 1.0D, (v) -> {
         return this.modes.getValue() == Speed.Mode.YPORT;
      }));
      this.motionyonoff = this.register(new Setting("NoJumpOff", true));
      this.stepyport = this.register(new Setting("OnStep", true));
      this.timer = new Timer();
      this.stepheight = 2.1F;
      this.startY = 0.0D;
      this.forward = 1;
      this.setInstance();
   }

   public static Speed getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Speed();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public String getDisplayInfo() {
      return this.modes.currentEnumName();
   }

   public void onDisable() {
      this.timer.reset();
      EntityUtilTwo.resetTimer();
   }

   public void onUpdate() {
      boolean boost = Math.abs(mc.field_71439_g.field_70759_as - mc.field_71439_g.field_70177_z) < 90.0F;
      if ((!mc.field_71439_g.func_70617_f_() || mc.field_71439_g.func_70090_H() || mc.field_71439_g.func_180799_ab()) && (Boolean)this.stepyport.getValue()) {
         Step.mc.field_71439_g.field_70138_W = this.stepheight;
      }

      if (this.modes.getValue() == Speed.Mode.STAFE) {
      }

      if (mc.field_71439_g.field_191988_bg != 0.0F && this.modes.getValue() == Speed.Mode.STRAFE) {
         if (!mc.field_71439_g.func_70051_ag()) {
            mc.field_71439_g.func_70031_b(true);
         }

         float yaw = mc.field_71439_g.field_70177_z;
         if (mc.field_71439_g.field_191988_bg > 0.0F) {
            if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F) {
               yaw += mc.field_71439_g.field_71158_b.field_78902_a > 0.0F ? -45.0F : 45.0F;
            }

            this.forward = 1;
            mc.field_71439_g.field_191988_bg = 1.0F;
            mc.field_71439_g.field_70702_br = 0.0F;
         } else if (mc.field_71439_g.field_191988_bg < 0.0F) {
            if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F) {
               yaw += mc.field_71439_g.field_71158_b.field_78902_a > 0.0F ? 45.0F : -45.0F;
            }

            this.forward = -1;
            mc.field_71439_g.field_191988_bg = -1.0F;
            mc.field_71439_g.field_70702_br = 0.0F;
         }

         if (mc.field_71439_g.field_70122_E) {
            mc.field_71439_g.func_70637_d(false);
            if (this.waitCounter < 1) {
               ++this.waitCounter;
               return;
            }

            this.waitCounter = 0;
            float f = (float)Math.toRadians((double)yaw);
            mc.field_71439_g.field_70181_x = 0.41D;
            EntityPlayerSP var10000 = mc.field_71439_g;
            var10000.field_70159_w -= (double)(MathHelper.func_76126_a(f) * 0.22F) * (double)this.forward;
            var10000 = mc.field_71439_g;
            var10000.field_70179_y += (double)(MathHelper.func_76134_b(f) * 0.23F) * (double)this.forward;
            if (mc.field_71474_y.field_74314_A.func_151468_f()) {
               mc.field_71439_g.field_70181_x = 0.41D;
               var10000 = mc.field_71439_g;
               var10000.field_70159_w -= (double)(MathHelper.func_76126_a(f) * 0.22F) * (double)this.forward;
               var10000 = mc.field_71439_g;
               var10000.field_70179_y += (double)(MathHelper.func_76134_b(f) * 0.22F) * (double)this.forward;
            }
         } else {
            if (this.waitCounter < 1) {
               ++this.waitCounter;
               return;
            }

            this.waitCounter = 0;
            double currentSpeed = Math.sqrt(mc.field_71439_g.field_70159_w * mc.field_71439_g.field_70159_w + mc.field_71439_g.field_70179_y * mc.field_71439_g.field_70179_y);
            double speed = boost ? 1.0064D : 1.001D;
            if (mc.field_71439_g.field_70181_x < 0.0D) {
               speed = 1.0D;
            }

            double direction = Math.toRadians((double)yaw);
            mc.field_71439_g.field_70159_w = -Math.sin(direction) * speed * currentSpeed * (double)this.forward;
            mc.field_71439_g.field_70179_y = Math.cos(direction) * speed * currentSpeed * (double)this.forward;
         }
      }

      if (mc.field_71439_g != null && mc.field_71441_e != null) {
         if (this.modes.getValue() == Speed.Mode.VANILA) {
            double[] calc = MathUtil.directionSpeed((Double)this.speed.getValue() / 10.0D);
            mc.field_71439_g.field_70159_w = calc[0];
            mc.field_71439_g.field_70179_y = calc[1];
         }

         if (mc.field_71439_g != null && mc.field_71441_e != null) {
            if (this.modes.getValue() == Speed.Mode.YPORT) {
               this.handleYPortSpeed();
            }

         } else {
            this.disable();
         }
      }
   }

   public void onToggle() {
      Step.mc.field_71439_g.field_70138_W = 0.6F;
      if (this.modes.getValue() == Speed.Mode.YPORT && (Boolean)this.motionyonoff.getValue()) {
         mc.field_71439_g.field_70181_x = -3.0D;
      }

   }

   private void handleYPortSpeed() {
      if (MotionUtil.isMoving(mc.field_71439_g) && (!mc.field_71439_g.func_70090_H() || !mc.field_71439_g.func_180799_ab()) && !mc.field_71439_g.field_70123_F) {
         if (mc.field_71439_g.field_70122_E) {
            EntityUtilTwo.setTimer(1.15F);
            mc.field_71439_g.func_70664_aZ();
            MotionUtil.setSpeed(mc.field_71439_g, MotionUtil.getBaseMoveSpeed() + (Double)this.yPortSpeed.getValue());
         } else {
            mc.field_71439_g.field_70181_x = -1.0D;
            EntityUtilTwo.resetTimer();
         }

      }
   }

   public static void strafe() {
      strafe(getSpeed());
   }

   public static void strafe(float f) {
      if (isMoving()) {
         double d = getDirection();
         mc.field_71439_g.field_70159_w = -Math.sin(d) * (double)f;
         mc.field_71439_g.field_70179_y = Math.cos(d) * (double)f;
      }
   }

   public static float getSpeed() {
      return (float)Math.sqrt(mc.field_71439_g.field_70159_w * mc.field_71439_g.field_70159_w + mc.field_71439_g.field_70179_y * mc.field_71439_g.field_70179_y);
   }

   public static double getDirection() {
      float f = mc.field_71439_g.field_70177_z;
      if (mc.field_71439_g.field_191988_bg < 0.0F) {
         f += 180.0F;
      }

      float f2 = 1.0F;
      if (mc.field_71439_g.field_191988_bg < 0.0F) {
         f2 = -0.5F;
      } else if (mc.field_71439_g.field_191988_bg > 0.0F) {
         f2 = 0.5F;
      }

      if (mc.field_71439_g.field_70702_br > 0.0F) {
         f -= 90.0F * f2;
      }

      if (mc.field_71439_g.field_70702_br < 0.0F) {
         f += 90.0F * f2;
      }

      return Math.toRadians((double)f);
   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent playerTickEvent) {
      if (this.modes.getValue() == Speed.Mode.VANILA) {
         strafe();
      }

   }

   public static boolean isMoving() {
      return mc.field_71439_g != null && (mc.field_71439_g.field_71158_b.field_192832_b != 0.0F || mc.field_71439_g.field_71158_b.field_78902_a != 0.0F);
   }

   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
   }

   public static enum Mode {
      STRAFE,
      YPORT,
      VANILA,
      STAFE;
   }
}
