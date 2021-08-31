package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Mouse;

public class MCP extends Module {
   private boolean clicked = false;

   public MCP() {
      super("MCP", "Throws a pearl", Module.Category.PLAYER, false, false, false);
   }

   public void onEnable() {
      if (fullNullCheck()) {
         this.disable();
      }

   }

   public void onTick() {
      if (Mouse.isButtonDown(2)) {
         if (!this.clicked) {
            this.throwPearl();
         }

         this.clicked = true;
      } else {
         this.clicked = false;
      }

   }

   private void throwPearl() {
      int pearlSlot = InventoryUtil.findHotbarBlock(ItemEnderPearl.class);
      boolean offhand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151079_bi;
      if (pearlSlot != -1 || offhand) {
         int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
         if (!offhand) {
            InventoryUtil.switchToHotbarSlot(pearlSlot, false);
         }

         mc.field_71442_b.func_187101_a(mc.field_71439_g, mc.field_71441_e, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
         if (!offhand) {
            InventoryUtil.switchToHotbarSlot(oldslot, false);
         }
      }

   }
}
