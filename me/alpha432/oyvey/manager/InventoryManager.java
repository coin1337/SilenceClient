package me.alpha432.oyvey.manager;

import me.alpha432.oyvey.util.Util;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryManager implements Util {
   public int currentPlayerItem;
   private int recoverySlot = -1;

   public void update() {
      if (this.recoverySlot != -1) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(this.recoverySlot == 8 ? 7 : this.recoverySlot + 1));
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(this.recoverySlot));
         mc.field_71439_g.field_71071_by.field_70461_c = this.recoverySlot;
         int i = mc.field_71439_g.field_71071_by.field_70461_c;
         if (i != this.currentPlayerItem) {
            this.currentPlayerItem = i;
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(this.currentPlayerItem));
         }

         this.recoverySlot = -1;
      }

   }

   public void recoverSilent(int slot) {
      this.recoverySlot = slot;
   }
}
