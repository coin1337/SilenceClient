package me.alpha432.oyvey.features.modules.combat;

import java.util.Objects;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Criticals extends Module {
   private final Setting<Integer> packets = this.register(new Setting("Packets", 2, 1, 4, "Amount of packets you want to send."));
   private final Timer timer = new Timer();
   private final boolean resetTimer = false;

   public Criticals() {
      super("Criticals", "Scores criticals for you", Module.Category.COMBAT, true, false, false);
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      CPacketUseEntity packet;
      if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).func_149565_c() == Action.ATTACK) {
         this.getClass();
         if (!this.timer.passedMs(0L)) {
            return;
         }

         if (mc.field_71439_g.field_70122_E && !mc.field_71474_y.field_74314_A.func_151470_d() && packet.func_149564_a(mc.field_71441_e) instanceof EntityLivingBase && !mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_180799_ab()) {
            switch((Integer)this.packets.getValue()) {
            case 1:
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.10000000149011612D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
               break;
            case 2:
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.0625101D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.1E-5D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
               break;
            case 3:
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.0625101D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.0125D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
               break;
            case 4:
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.1625D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 4.0E-6D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0E-6D, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayer());
               mc.field_71439_g.func_71009_b((Entity)Objects.requireNonNull(packet.func_149564_a(mc.field_71441_e)));
            }

            this.timer.reset();
         }
      }

   }

   public String getDisplayInfo() {
      return "Packet";
   }
}
