package me.alpha432.oyvey.features.modules.misc;

import java.util.Objects;
import me.alpha432.oyvey.event.events.DeathEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.HUD;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.TextUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Tracker extends Module {
   private static Tracker instance;
   private EntityPlayer trackedPlayer;
   private int usedExp = 0;
   private int usedStacks = 0;

   public Tracker() {
      super("Duel", "Tracks players in 1v1s.", Module.Category.MISC, true, false, false);
      instance = this;
   }

   public static Tracker getInstance() {
      if (instance == null) {
         instance = new Tracker();
      }

      return instance;
   }

   public void onUpdate() {
      if (this.trackedPlayer == null) {
         this.trackedPlayer = EntityUtil.getClosestEnemy(1000.0D);
      } else if (this.usedStacks != this.usedExp / 64) {
         this.usedStacks = this.usedExp / 64;
         Command.sendMessage(TextUtil.coloredString(this.trackedPlayer.func_70005_c_() + " has used " + this.usedStacks + " stacks of XP!", (TextUtil.Color)HUD.getInstance().commandColor.getValue()));
      }

   }

   public void onSpawnEntity(Entity entity) {
      if (entity instanceof EntityExpBottle && Objects.equals(mc.field_71441_e.func_72890_a(entity, 3.0D), this.trackedPlayer)) {
         ++this.usedExp;
      }

   }

   public void onDisable() {
      this.trackedPlayer = null;
      this.usedExp = 0;
      this.usedStacks = 0;
   }

   @SubscribeEvent
   public void onDeath(DeathEvent event) {
      if (event.player.equals(this.trackedPlayer)) {
         this.usedExp = 0;
         this.usedStacks = 0;
      }

   }

   public String getDisplayInfo() {
      return this.trackedPlayer != null ? this.trackedPlayer.func_70005_c_() : null;
   }
}
