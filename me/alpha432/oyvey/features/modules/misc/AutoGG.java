package me.alpha432.oyvey.features.modules.misc;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoGG extends Module {
   private static AutoGG INSTANCE = new AutoGG();
   public Setting<String> custom = this.register(new Setting("Custom", "Nigga-Hack.me"));
   public Setting<String> test = this.register(new Setting("Test", "null"));
   private ConcurrentHashMap<String, Integer> targetedPlayers = null;

   public AutoGG() {
      super("AutoGG", "Sends msg after you kill someone", Module.Category.MISC, true, false, false);
      this.setInstance();
   }

   public static AutoGG getINSTANCE() {
      if (INSTANCE == null) {
         INSTANCE = new AutoGG();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public void onEnable() {
      this.targetedPlayers = new ConcurrentHashMap();
   }

   public void onDisable() {
      this.targetedPlayers = null;
   }

   public void onUpdate() {
      if (!nullCheck()) {
         if (this.targetedPlayers == null) {
            this.targetedPlayers = new ConcurrentHashMap();
         }

         Iterator var1 = mc.field_71441_e.func_72910_y().iterator();

         while(var1.hasNext()) {
            Entity entity = (Entity)var1.next();
            String name2;
            EntityPlayer player;
            if (entity instanceof EntityPlayer && !((player = (EntityPlayer)entity).func_110143_aJ() > 0.0F) && this.shouldAnnounce(name2 = player.func_70005_c_())) {
               this.doAnnounce(name2);
               break;
            }
         }

         this.targetedPlayers.forEach((name, timeout) -> {
            if (timeout <= 0) {
               this.targetedPlayers.remove(name);
            } else {
               this.targetedPlayers.put(name, timeout - 1);
            }

         });
      }
   }

   @SubscribeEvent
   public void onLeavingDeathEvent(LivingDeathEvent event) {
      if (mc.field_71439_g != null) {
         if (this.targetedPlayers == null) {
            this.targetedPlayers = new ConcurrentHashMap();
         }

         EntityLivingBase entity;
         if ((entity = event.getEntityLiving()) != null) {
            if (entity instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)entity;
               if (!(player.func_110143_aJ() > 0.0F)) {
                  String name = player.func_70005_c_();
                  if (this.shouldAnnounce(name)) {
                     this.doAnnounce(name);
                  }

               }
            }
         }
      }
   }

   private boolean shouldAnnounce(String name) {
      return this.targetedPlayers.containsKey(name);
   }

   private void doAnnounce(String name) {
      this.targetedPlayers.remove(name);
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketChatMessage((String)this.custom.getValue()));
      int u = 0;

      for(int i = 0; i < 10; ++i) {
         ++u;
      }

      if (!((String)this.test.getValue()).equalsIgnoreCase("null")) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketChatMessage((String)this.test.getValue()));
      }

   }

   public void addTargetedPlayer(String name) {
      if (!Objects.equals(name, mc.field_71439_g.func_70005_c_())) {
         if (this.targetedPlayers == null) {
            this.targetedPlayers = new ConcurrentHashMap();
         }

         this.targetedPlayers.put(name, 20);
      }
   }
}
