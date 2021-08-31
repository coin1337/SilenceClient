package me.alpha432.oyvey.features.modules.player;

import java.awt.Color;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.BlockEvent;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speedmine extends Module {
   private static Speedmine INSTANCE = new Speedmine();
   private final Timer timer = new Timer();
   public Setting<Speedmine.Mode> mode;
   public Setting<Float> damage;
   public Setting<Boolean> webSwitch;
   public Setting<Boolean> doubleBreak;
   public Setting<Boolean> render;
   public Setting<Boolean> box;
   private final Setting<Integer> boxAlpha;
   public Setting<Boolean> outline;
   private final Setting<Float> lineWidth;
   public BlockPos currentPos;
   public IBlockState currentBlockState;

   public Speedmine() {
      super("Speedmine", "Speeds up mining.", Module.Category.PLAYER, true, false, false);
      this.mode = this.register(new Setting("Mode", Speedmine.Mode.PACKET));
      this.damage = this.register(new Setting("Damage", 0.7F, 0.0F, 1.0F, (v) -> {
         return this.mode.getValue() == Speedmine.Mode.DAMAGE;
      }));
      this.webSwitch = this.register(new Setting("WebSwitch", false));
      this.doubleBreak = this.register(new Setting("DoubleBreak", false));
      this.render = this.register(new Setting("Render", false));
      this.box = this.register(new Setting("Box", false, (v) -> {
         return (Boolean)this.render.getValue();
      }));
      this.boxAlpha = this.register(new Setting("BoxAlpha", 85, 0, 255, (v) -> {
         return (Boolean)this.box.getValue() && (Boolean)this.render.getValue();
      }));
      this.outline = this.register(new Setting("Outline", true, (v) -> {
         return (Boolean)this.render.getValue();
      }));
      this.lineWidth = this.register(new Setting("Width", 1.0F, 0.1F, 5.0F, (v) -> {
         return (Boolean)this.outline.getValue() && (Boolean)this.render.getValue();
      }));
      this.setInstance();
   }

   public static Speedmine getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Speedmine();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public void onTick() {
      if (this.currentPos != null) {
         if (mc.field_71441_e.func_180495_p(this.currentPos).equals(this.currentBlockState) && mc.field_71441_e.func_180495_p(this.currentPos).func_177230_c() != Blocks.field_150350_a) {
            if ((Boolean)this.webSwitch.getValue() && this.currentBlockState.func_177230_c() == Blocks.field_150321_G && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemPickaxe) {
               InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
            }
         } else {
            this.currentPos = null;
            this.currentBlockState = null;
         }
      }

   }

   public void onUpdate() {
      if (!fullNullCheck()) {
         mc.field_71442_b.field_78781_i = 0;
      }
   }

   public void onRender3D(Render3DEvent event) {
      if ((Boolean)this.render.getValue() && this.currentPos != null && this.currentBlockState.func_177230_c() == Blocks.field_150343_Z) {
         Color color = new Color(this.timer.passedMs((long)((int)(2000.0F * OyVey.serverManager.getTpsFactor()))) ? 0 : 255, this.timer.passedMs((long)((int)(2000.0F * OyVey.serverManager.getTpsFactor()))) ? 255 : 0, 0, 255);
         RenderUtil.drawBoxESP(this.currentPos, color, false, color, (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), false);
      }

   }

   @SubscribeEvent
   public void onBlockEvent(BlockEvent event) {
      if (!fullNullCheck()) {
         if (event.getStage() == 3 && mc.field_71442_b.field_78770_f > 0.1F) {
            mc.field_71442_b.field_78778_j = true;
         }

         if (event.getStage() == 4) {
            if (BlockUtil.canBreak(event.pos)) {
               mc.field_71442_b.field_78778_j = false;
               switch((Speedmine.Mode)this.mode.getValue()) {
               case PACKET:
                  if (this.currentPos == null) {
                     this.currentPos = event.pos;
                     this.currentBlockState = mc.field_71441_e.func_180495_p(this.currentPos);
                     this.timer.reset();
                  }

                  mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, event.pos, event.facing));
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                  event.setCanceled(true);
                  break;
               case DAMAGE:
                  if (mc.field_71442_b.field_78770_f >= (Float)this.damage.getValue()) {
                     mc.field_71442_b.field_78770_f = 1.0F;
                  }
                  break;
               case INSTANT:
                  mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, event.pos, event.facing));
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                  mc.field_71442_b.func_187103_a(event.pos);
                  mc.field_71441_e.func_175698_g(event.pos);
               }
            }

            BlockPos above;
            if ((Boolean)this.doubleBreak.getValue() && BlockUtil.canBreak(above = event.pos.func_177982_a(0, 1, 0)) && mc.field_71439_g.func_70011_f((double)above.func_177958_n(), (double)above.func_177956_o(), (double)above.func_177952_p()) <= 5.0D) {
               mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, above, event.facing));
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, above, event.facing));
               mc.field_71442_b.func_187103_a(above);
               mc.field_71441_e.func_175698_g(above);
            }
         }

      }
   }

   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   public static enum Mode {
      PACKET,
      DAMAGE,
      INSTANT;
   }
}
