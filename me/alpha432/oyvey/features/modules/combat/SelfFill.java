package me.alpha432.oyvey.features.modules.combat;

import java.lang.reflect.Field;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.player.MCP;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.MappingUtil;
import me.alpha432.oyvey.util.Util;
import me.alpha432.oyvey.util.WorldUtil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;

public class SelfFill extends Module {
   private BlockPos playerPos;
   private final Setting<SelfFill.Modes> mode;
   private final Setting<Boolean> rotate;
   private final Setting<Float> offest;
   private final Setting<Boolean> test2;
   private final Setting<Boolean> futureBeta;
   private final Setting<Boolean> toggleRStep;
   private final Setting<Boolean> test;
   private final Setting<SelfFill.Settings> setting;
   BlockPos pos;

   public SelfFill() {
      super("Burrow", "SelfFills yourself in a hole.", Module.Category.COMBAT, true, false, true);
      this.mode = this.register(new Setting("Settings", SelfFill.Modes.Silent));
      this.rotate = this.register(new Setting("Rotate", false, (v) -> {
         return this.mode.getValue() == SelfFill.Modes.NoJump;
      }));
      this.offest = this.register(new Setting("offset", 1.6F, -20.0F, 20.0F, (v) -> {
         return this.mode.getValue() == SelfFill.Modes.NoJump;
      }));
      this.test2 = this.register(new Setting("Test", false, (v) -> {
         return this.mode.getValue() == SelfFill.Modes.Silent;
      }));
      this.futureBeta = this.register(new Setting("SilentJump", false, (v) -> {
         return this.mode.getValue() == SelfFill.Modes.Silent;
      }));
      this.toggleRStep = this.register(new Setting("ToggleRStep", false, (v) -> {
         return this.mode.getValue() == SelfFill.Modes.Silent;
      }));
      this.test = this.register(new Setting("Test", false, (v) -> {
         return this.mode.getValue() == SelfFill.Modes.Silent;
      }));
      this.setting = this.register(new Setting("Settings", SelfFill.Settings.Obsidian));
   }

   public void onEnable() {
      if (this.mode.getValue() == SelfFill.Modes.NoJump) {
         super.onEnable();
         BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
         if (mc.field_71441_e.func_180495_p(pos.func_177977_b()).func_177230_c() == Blocks.field_150343_Z) {
         }
      }

      if (this.mode.getValue() == SelfFill.Modes.Silent) {
         if ((Boolean)this.futureBeta.getValue()) {
            this.setTimer(50.0F);
         }

         if ((Boolean)this.toggleRStep.getValue()) {
            OyVey.moduleManager.getModuleByName("ReverseStep").disable();
         }

         this.playerPos = new BlockPos(Util.mc.field_71439_g.field_70165_t, Util.mc.field_71439_g.field_70163_u, Util.mc.field_71439_g.field_70161_v);
         if (this.setting.getValue() == SelfFill.Settings.Obsidian && Util.mc.field_71441_e.func_180495_p(this.playerPos).func_177230_c().equals(Blocks.field_150343_Z)) {
            this.disable();
            return;
         }
      }

      if (this.mode.getValue() == SelfFill.Modes.Silent) {
         if (this.setting.getValue() == SelfFill.Settings.EnderChest && Util.mc.field_71441_e.func_180495_p(this.playerPos).func_177230_c().equals(Blocks.field_150477_bB)) {
            this.disable();
            return;
         }

         Util.mc.field_71439_g.func_70664_aZ();
      }

      if (this.mode.getValue() == SelfFill.Modes.Server && this.setting.getValue() == SelfFill.Settings.Obsidian) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketChatMessage("/setblock ~ ~ ~ obsidian"));
         this.disable();
      } else if (this.mode.getValue() == SelfFill.Modes.Server && this.setting.getValue() == SelfFill.Settings.EnderChest) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketChatMessage("/setblock ~ ~ ~ ender_chest"));
         this.disable();
      }

   }

   public void onDisable() {
      if (this.mode.getValue() == SelfFill.Modes.Silent) {
         if ((Boolean)this.toggleRStep.getValue()) {
            OyVey.moduleManager.getModuleByName("ReverseStep").enable();
         }

         this.setTimer(1.0F);
      }

   }

   public void onUpdate() {
      if (this.mode.getValue() == SelfFill.Modes.Silent) {
         if (nullCheck()) {
            return;
         }

         if (Util.mc.field_71439_g.field_70163_u > (double)this.playerPos.func_177956_o() + 1.04D) {
            if ((Boolean)this.test2.getValue()) {
               mc.func_147114_u().func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 3.0D, mc.field_71439_g.field_70161_v, false));
            }

            if (this.setting.getValue() == SelfFill.Settings.Obsidian) {
               WorldUtil.placeBlock(this.playerPos, InventoryUtil.findHotbarBlock(Blocks.field_150343_Z));
            }

            if (this.setting.getValue() == SelfFill.Settings.EnderChest) {
               WorldUtil.placeBlock(this.playerPos, InventoryUtil.findHotbarBlock(Blocks.field_150477_bB));
            }

            if ((Boolean)this.test2.getValue()) {
               mc.func_147114_u().func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 3.0D, mc.field_71439_g.field_70161_v, true));
            }

            if (!(Boolean)this.test2.getValue()) {
               Util.mc.field_71439_g.func_70664_aZ();
            }

            this.disable();
         }
      }

      if (this.mode.getValue() == SelfFill.Modes.NoJump) {
         BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.41999998688698D, mc.field_71439_g.field_70161_v, true));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.7531999805211997D, mc.field_71439_g.field_70161_v, true));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.00133597911214D, mc.field_71439_g.field_70161_v, true));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.16610926093821D, mc.field_71439_g.field_70161_v, true));
         int oldslot = MCP.mc.field_71439_g.field_71071_by.field_70461_c;
         int blockender;
         if (this.setting.getValue() == SelfFill.Settings.Obsidian) {
            blockender = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            InventoryUtil.switchToHotbarSlot(blockender, false);
         }

         if (this.setting.getValue() == SelfFill.Settings.EnderChest) {
            blockender = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            InventoryUtil.switchToHotbarSlot(blockender, false);
         }

         BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), true, false);
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)(Float)this.offest.getValue(), mc.field_71439_g.field_70161_v, false));
         InventoryUtil.switchToHotbarSlot(oldslot, false);
         Command.sendMessage("Burrow - done");
         this.disable();
      }

   }

   public String getDisplayInfo() {
      if (this.mode.getValue() == SelfFill.Modes.Silent) {
         return "SemiFast";
      } else {
         return this.mode.getValue() == SelfFill.Modes.Server ? "SuolFill" : "Burrow";
      }
   }

   private void setTimer(float value) {
      try {
         Field timer = Minecraft.class.getDeclaredField(MappingUtil.timer);
         timer.setAccessible(true);
         Field tickLength = Timer.class.getDeclaredField(MappingUtil.tickLength);
         tickLength.setAccessible(true);
         tickLength.setFloat(timer.get(Util.mc), 50.0F / value);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public static enum Modes {
      Silent,
      Server,
      NoJump;
   }

   public static enum Settings {
      Obsidian,
      EnderChest;
   }
}
