package me.alpha432.oyvey.util;

import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WorldUtil implements MinecraftInstance {
   public static void placeBlock(BlockPos pos) {
      EnumFacing[] var1 = EnumFacing.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EnumFacing enumFacing = var1[var3];
         if (!MinecraftInstance.mc.field_71441_e.func_180495_p(pos.func_177972_a(enumFacing)).func_177230_c().equals(Blocks.field_150350_a) && !isIntercepted(pos)) {
            Vec3d vec = new Vec3d((double)pos.func_177958_n() + 0.5D + (double)enumFacing.func_82601_c() * 0.5D, (double)pos.func_177956_o() + 0.5D + (double)enumFacing.func_96559_d() * 0.5D, (double)pos.func_177952_p() + 0.5D + (double)enumFacing.func_82599_e() * 0.5D);
            float[] old = new float[]{MinecraftInstance.mc.field_71439_g.field_70177_z, MinecraftInstance.mc.field_71439_g.field_70125_A};
            MinecraftInstance.mc.field_71439_g.field_71174_a.func_147297_a(new Rotation((float)Math.toDegrees(Math.atan2(vec.field_72449_c - MinecraftInstance.mc.field_71439_g.field_70161_v, vec.field_72450_a - MinecraftInstance.mc.field_71439_g.field_70165_t)) - 90.0F, (float)(-Math.toDegrees(Math.atan2(vec.field_72448_b - (MinecraftInstance.mc.field_71439_g.field_70163_u + (double)MinecraftInstance.mc.field_71439_g.func_70047_e()), Math.sqrt((vec.field_72450_a - MinecraftInstance.mc.field_71439_g.field_70165_t) * (vec.field_72450_a - MinecraftInstance.mc.field_71439_g.field_70165_t) + (vec.field_72449_c - MinecraftInstance.mc.field_71439_g.field_70161_v) * (vec.field_72449_c - MinecraftInstance.mc.field_71439_g.field_70161_v))))), MinecraftInstance.mc.field_71439_g.field_70122_E));
            MinecraftInstance.mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(MinecraftInstance.mc.field_71439_g, Action.START_SNEAKING));
            MinecraftInstance.mc.field_71442_b.func_187099_a(MinecraftInstance.mc.field_71439_g, MinecraftInstance.mc.field_71441_e, pos.func_177972_a(enumFacing), enumFacing.func_176734_d(), new Vec3d(pos), EnumHand.MAIN_HAND);
            MinecraftInstance.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            MinecraftInstance.mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(MinecraftInstance.mc.field_71439_g, Action.STOP_SNEAKING));
            MinecraftInstance.mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(old[0], old[1], MinecraftInstance.mc.field_71439_g.field_70122_E));
            return;
         }
      }

   }

   public static void createFakePlayer(@Nullable String name, boolean copyInventory, boolean copyAngles, boolean health, boolean player, int entityID) {
      EntityOtherPlayerMP entity = player ? new EntityOtherPlayerMP(mc.field_71441_e, mc.func_110432_I().func_148256_e()) : new EntityOtherPlayerMP(mc.field_71441_e, new GameProfile(UUID.fromString("70ee432d-0a96-4137-a2c0-37cc9df67f03"), name));
      entity.func_82149_j(mc.field_71439_g);
      if (copyInventory) {
         entity.field_71071_by.func_70455_b(mc.field_71439_g.field_71071_by);
      }

      if (copyAngles) {
         entity.field_70177_z = mc.field_71439_g.field_70177_z;
         entity.field_70759_as = mc.field_71439_g.field_70759_as;
      }

      if (health) {
         entity.func_70606_j(mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj());
      }

      mc.field_71441_e.func_73027_a(entityID, entity);
   }

   public static void placeBlock(BlockPos pos, int slot) {
      if (slot != -1) {
         int prev = MinecraftInstance.mc.field_71439_g.field_71071_by.field_70461_c;
         MinecraftInstance.mc.field_71439_g.field_71071_by.field_70461_c = slot;
         placeBlock(pos);
         MinecraftInstance.mc.field_71439_g.field_71071_by.field_70461_c = prev;
      }
   }

   public static boolean isIntercepted(BlockPos pos) {
      Iterator var1 = MinecraftInstance.mc.field_71441_e.field_72996_f.iterator();

      Entity entity;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         entity = (Entity)var1.next();
      } while(!(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }

   public static BlockPos GetLocalPlayerPosFloored() {
      return new BlockPos(Math.floor(MinecraftInstance.mc.field_71439_g.field_70165_t), Math.floor(MinecraftInstance.mc.field_71439_g.field_70163_u), Math.floor(MinecraftInstance.mc.field_71439_g.field_70161_v));
   }

   public static boolean canBreak(BlockPos pos) {
      return MinecraftInstance.mc.field_71441_e.func_180495_p(pos).func_177230_c().func_176195_g(MinecraftInstance.mc.field_71441_e.func_180495_p(pos), MinecraftInstance.mc.field_71441_e, pos) != -1.0F;
   }
}
