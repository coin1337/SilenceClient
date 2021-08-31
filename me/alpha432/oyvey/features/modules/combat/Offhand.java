package me.alpha432.oyvey.features.modules.combat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.ProcessRightClickBlockEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class Offhand extends Module {
   private static Offhand instance;
   private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue();
   private final Timer timer = new Timer();
   private final Timer secondTimer = new Timer();
   public Setting<Boolean> crystal = this.register(new Setting("Crystal", true));
   public Setting<Float> crystalHealth = this.register(new Setting("CrystalHP", 13.0F, 0.1F, 36.0F));
   public Setting<Float> crystalHoleHealth = this.register(new Setting("CrystalHoleHP", 3.5F, 0.1F, 36.0F));
   public Setting<Boolean> gapple = this.register(new Setting("Gapple", true));
   public Setting<Boolean> armorCheck = this.register(new Setting("ArmorCheck", true));
   public Setting<Integer> actions = this.register(new Setting("Packets", 4, 1, 4));
   public Offhand.Mode2 currentMode;
   public int totems;
   public int crystals;
   public int gapples;
   public int lastTotemSlot;
   public int lastGappleSlot;
   public int lastCrystalSlot;
   public int lastObbySlot;
   public int lastWebSlot;
   public boolean holdingCrystal;
   public boolean holdingTotem;
   public boolean holdingGapple;
   public boolean didSwitchThisTick;
   private boolean second;
   private boolean switchedForHealthReason;

   public Offhand() {
      super("Offhand", "Allows you to switch up your Offhand.", Module.Category.COMBAT, true, false, false);
      this.currentMode = Offhand.Mode2.TOTEMS;
      this.totems = 0;
      this.crystals = 0;
      this.gapples = 0;
      this.lastTotemSlot = -1;
      this.lastGappleSlot = -1;
      this.lastCrystalSlot = -1;
      this.lastObbySlot = -1;
      this.lastWebSlot = -1;
      this.holdingCrystal = false;
      this.holdingTotem = false;
      this.holdingGapple = false;
      this.didSwitchThisTick = false;
      this.second = false;
      this.switchedForHealthReason = false;
      instance = this;
   }

   public static Offhand getInstance() {
      if (instance == null) {
         instance = new Offhand();
      }

      return instance;
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(ProcessRightClickBlockEvent event) {
      if (event.hand == EnumHand.MAIN_HAND && event.stack.func_77973_b() == Items.field_185158_cP && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71476_x != null && event.pos == mc.field_71476_x.func_178782_a()) {
         event.setCanceled(true);
         mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
         mc.field_71442_b.func_187101_a(mc.field_71439_g, mc.field_71441_e, EnumHand.OFF_HAND);
      }

   }

   public void onUpdate() {
      if (this.timer.passedMs(50L)) {
         if (mc.field_71439_g != null && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP && Mouse.isButtonDown(1)) {
            mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
            mc.field_71474_y.field_74313_G.field_74513_e = Mouse.isButtonDown(1);
         }
      } else if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP) {
         mc.field_71474_y.field_74313_G.field_74513_e = false;
      }

      if (!nullCheck()) {
         this.doOffhand();
         if (this.secondTimer.passedMs(50L) && this.second) {
            this.second = false;
            this.timer.reset();
         }

      }
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (!fullNullCheck() && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP && mc.field_71474_y.field_74313_G.func_151470_d()) {
         if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if (packet2.func_187022_c() == EnumHand.MAIN_HAND) {
               if (this.timer.passedMs(50L)) {
                  mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
               }

               event.setCanceled(true);
            }
         } else if (event.getPacket() instanceof CPacketPlayerTryUseItem && ((CPacketPlayerTryUseItem)event.getPacket()).func_187028_a() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
            event.setCanceled(true);
         }
      }

   }

   public String getDisplayInfo() {
      if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
         return "Crystals";
      } else if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY) {
         return "Totems";
      } else {
         return mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao ? "Gapples" : null;
      }
   }

   public void doOffhand() {
      this.didSwitchThisTick = false;
      this.holdingCrystal = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
      this.holdingTotem = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY;
      this.holdingGapple = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao;
      this.totems = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
         return itemStack.func_77973_b() == Items.field_190929_cY;
      }).mapToInt(ItemStack::func_190916_E).sum();
      if (this.holdingTotem) {
         this.totems += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter((itemStack) -> {
            return itemStack.func_77973_b() == Items.field_190929_cY;
         }).mapToInt(ItemStack::func_190916_E).sum();
      }

      this.crystals = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
         return itemStack.func_77973_b() == Items.field_185158_cP;
      }).mapToInt(ItemStack::func_190916_E).sum();
      if (this.holdingCrystal) {
         this.crystals += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter((itemStack) -> {
            return itemStack.func_77973_b() == Items.field_185158_cP;
         }).mapToInt(ItemStack::func_190916_E).sum();
      }

      this.gapples = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
         return itemStack.func_77973_b() == Items.field_151153_ao;
      }).mapToInt(ItemStack::func_190916_E).sum();
      if (this.holdingGapple) {
         this.gapples += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter((itemStack) -> {
            return itemStack.func_77973_b() == Items.field_151153_ao;
         }).mapToInt(ItemStack::func_190916_E).sum();
      }

      this.doSwitch();
   }

   public void doSwitch() {
      this.currentMode = Offhand.Mode2.TOTEMS;
      if ((Boolean)this.gapple.getValue() && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword && mc.field_71474_y.field_74313_G.func_151470_d()) {
         this.currentMode = Offhand.Mode2.GAPPLES;
      } else if (this.currentMode != Offhand.Mode2.CRYSTALS && (Boolean)this.crystal.getValue() && (EntityUtil.isSafe(mc.field_71439_g) && EntityUtil.getHealth(mc.field_71439_g, true) > (Float)this.crystalHoleHealth.getValue() || EntityUtil.getHealth(mc.field_71439_g, true) > (Float)this.crystalHealth.getValue())) {
         this.currentMode = Offhand.Mode2.CRYSTALS;
      }

      if (this.currentMode == Offhand.Mode2.CRYSTALS && this.crystals == 0) {
         this.setMode(Offhand.Mode2.TOTEMS);
      }

      if (this.currentMode == Offhand.Mode2.CRYSTALS && (!EntityUtil.isSafe(mc.field_71439_g) && EntityUtil.getHealth(mc.field_71439_g, true) <= (Float)this.crystalHealth.getValue() || EntityUtil.getHealth(mc.field_71439_g, true) <= (Float)this.crystalHoleHealth.getValue())) {
         if (this.currentMode == Offhand.Mode2.CRYSTALS) {
            this.switchedForHealthReason = true;
         }

         this.setMode(Offhand.Mode2.TOTEMS);
      }

      if (this.switchedForHealthReason && (EntityUtil.isSafe(mc.field_71439_g) && EntityUtil.getHealth(mc.field_71439_g, true) > (Float)this.crystalHoleHealth.getValue() || EntityUtil.getHealth(mc.field_71439_g, true) > (Float)this.crystalHealth.getValue())) {
         this.setMode(Offhand.Mode2.CRYSTALS);
         this.switchedForHealthReason = false;
      }

      if (this.currentMode == Offhand.Mode2.CRYSTALS && (Boolean)this.armorCheck.getValue() && (mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b() == Items.field_190931_a || mc.field_71439_g.func_184582_a(EntityEquipmentSlot.HEAD).func_77973_b() == Items.field_190931_a || mc.field_71439_g.func_184582_a(EntityEquipmentSlot.LEGS).func_77973_b() == Items.field_190931_a || mc.field_71439_g.func_184582_a(EntityEquipmentSlot.FEET).func_77973_b() == Items.field_190931_a)) {
         this.setMode(Offhand.Mode2.TOTEMS);
      }

      if (!(mc.field_71462_r instanceof GuiContainer) || mc.field_71462_r instanceof GuiInventory) {
         Item currentOffhandItem = mc.field_71439_g.func_184592_cb().func_77973_b();
         int i;
         switch(this.currentMode) {
         case TOTEMS:
            if (this.totems > 0 && !this.holdingTotem) {
               this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.field_190929_cY, false);
               i = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
               this.putItemInOffhand(this.lastTotemSlot, i);
            }
            break;
         case GAPPLES:
            if (this.gapples > 0 && !this.holdingGapple) {
               this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.field_151153_ao, false);
               i = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
               this.putItemInOffhand(this.lastGappleSlot, i);
            }
            break;
         default:
            if (this.crystals > 0 && !this.holdingCrystal) {
               this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.field_185158_cP, false);
               i = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
               this.putItemInOffhand(this.lastCrystalSlot, i);
            }
         }

         for(i = 0; i < (Integer)this.actions.getValue(); ++i) {
            InventoryUtil.Task task = (InventoryUtil.Task)this.taskList.poll();
            if (task != null) {
               task.run();
               if (task.isSwitching()) {
                  this.didSwitchThisTick = true;
               }
            }
         }

      }
   }

   private int getLastSlot(Item item, int slotIn) {
      if (item == Items.field_185158_cP) {
         return this.lastCrystalSlot;
      } else if (item == Items.field_151153_ao) {
         return this.lastGappleSlot;
      } else if (item == Items.field_190929_cY) {
         return this.lastTotemSlot;
      } else if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
         return this.lastObbySlot;
      } else if (InventoryUtil.isBlock(item, BlockWeb.class)) {
         return this.lastWebSlot;
      } else {
         return item == Items.field_190931_a ? -1 : slotIn;
      }
   }

   private void putItemInOffhand(int slotIn, int slotOut) {
      if (slotIn != -1 && this.taskList.isEmpty()) {
         this.taskList.add(new InventoryUtil.Task(slotIn));
         this.taskList.add(new InventoryUtil.Task(45));
         this.taskList.add(new InventoryUtil.Task(slotOut));
         this.taskList.add(new InventoryUtil.Task());
      }

   }

   public void setMode(Offhand.Mode2 mode) {
      this.currentMode = this.currentMode == mode ? Offhand.Mode2.TOTEMS : mode;
   }

   public static enum Mode2 {
      TOTEMS,
      GAPPLES,
      CRYSTALS;
   }
}
