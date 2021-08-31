package me.alpha432.oyvey.util;

import java.util.ArrayList;
import java.util.List;
import me.alpha432.oyvey.OyVey;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityUtilTwo {
   private static final Minecraft mc = Minecraft.func_71410_x();

   public static Block isColliding(double posX, double posY, double posZ) {
      Block block = null;
      if (mc.field_71439_g != null) {
         AxisAlignedBB bb = mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(posX, posY, posZ) : mc.field_71439_g.func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(posX, posY, posZ);
         int y = (int)bb.field_72338_b;

         for(int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d) + 1; ++x) {
            for(int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f) + 1; ++z) {
               block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
            }
         }
      }

      return block;
   }

   public static Vec3d[] getOffsets(int y, boolean floor, boolean face) {
      List<Vec3d> offsets = getOffsetList(y, floor, face);
      Vec3d[] array = new Vec3d[offsets.size()];
      return (Vec3d[])offsets.toArray(array);
   }

   public static List<Vec3d> getOffsetList(int y, boolean floor, boolean face) {
      List<Vec3d> offsets = new ArrayList();
      if (face) {
         offsets.add(new Vec3d(-1.0D, (double)y, 0.0D));
         offsets.add(new Vec3d(1.0D, (double)y, 0.0D));
         offsets.add(new Vec3d(0.0D, (double)y, -1.0D));
         offsets.add(new Vec3d(0.0D, (double)y, 1.0D));
      } else {
         offsets.add(new Vec3d(-1.0D, (double)y, 0.0D));
      }

      if (floor) {
         offsets.add(new Vec3d(0.0D, (double)(y - 1), 0.0D));
      }

      return offsets;
   }

   public static boolean isInLiquid() {
      if (mc.field_71439_g == null) {
         return false;
      } else if (mc.field_71439_g.field_70143_R >= 3.0F) {
         return false;
      } else {
         boolean inLiquid = false;
         AxisAlignedBB bb = mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().func_174813_aQ() : mc.field_71439_g.func_174813_aQ();
         int y = (int)bb.field_72338_b;

         for(int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d) + 1; ++x) {
            for(int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f) + 1; ++z) {
               Block block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
               if (!(block instanceof BlockAir)) {
                  if (!(block instanceof BlockLiquid)) {
                     return false;
                  }

                  inLiquid = true;
               }
            }
         }

         return inLiquid;
      }
   }

   public static void setTimer(float speed) {
      Minecraft.func_71410_x().field_71428_T.field_194149_e = 50.0F / speed;
   }

   public static void resetTimer() {
      Minecraft.func_71410_x().field_71428_T.field_194149_e = 50.0F;
   }

   public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
      return getInterpolatedAmount(entity, ticks, ticks, ticks);
   }

   public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
      return (new Vec3d(entity.field_70142_S, entity.field_70137_T, entity.field_70136_U)).func_178787_e(getInterpolatedAmount(entity, (double)ticks));
   }

   public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
      return new Vec3d((entity.field_70165_t - entity.field_70142_S) * x, (entity.field_70163_u - entity.field_70137_T) * y, (entity.field_70161_v - entity.field_70136_U) * z);
   }

   public static float clamp(float val, float min, float max) {
      if (val <= min) {
         val = min;
      }

      if (val >= max) {
         val = max;
      }

      return val;
   }

   public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
      List<BlockPos> circleblocks = new ArrayList();
      int cx = loc.func_177958_n();
      int cy = loc.func_177956_o();
      int cz = loc.func_177952_p();

      for(int x = cx - (int)r; (float)x <= (float)cx + r; ++x) {
         for(int z = cz - (int)r; (float)z <= (float)cz + r; ++z) {
            for(int y = sphere ? cy - (int)r : cy; (float)y < (sphere ? (float)cy + r : (float)(cy + h)); ++y) {
               double dist = (double)((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));
               if (dist < (double)(r * r) && (!hollow || !(dist < (double)((r - 1.0F) * (r - 1.0F))))) {
                  BlockPos l = new BlockPos(x, y + plus_y, z);
                  circleblocks.add(l);
               }
            }
         }
      }

      return circleblocks;
   }

   public static List<BlockPos> getSquare(BlockPos pos1, BlockPos pos2) {
      List<BlockPos> squareBlocks = new ArrayList();
      int x1 = pos1.func_177958_n();
      int y1 = pos1.func_177956_o();
      int z1 = pos1.func_177952_p();
      int x2 = pos2.func_177958_n();
      int y2 = pos2.func_177956_o();
      int z2 = pos2.func_177952_p();

      for(int x = Math.min(x1, x2); x <= Math.max(x1, x2); ++x) {
         for(int z = Math.min(z1, z2); z <= Math.max(z1, z2); ++z) {
            for(int y = Math.min(y1, y2); y <= Math.max(y1, y2); ++y) {
               squareBlocks.add(new BlockPos(x, y, z));
            }
         }
      }

      return squareBlocks;
   }

   public static double[] calculateLookAt(double px, double py, double pz, Entity me) {
      double dirx = me.field_70165_t - px;
      double diry = me.field_70163_u - py;
      double dirz = me.field_70161_v - pz;
      double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
      dirx /= len;
      diry /= len;
      dirz /= len;
      double pitch = Math.asin(diry);
      double yaw = Math.atan2(dirz, dirx);
      pitch = pitch * 180.0D / 3.141592653589793D;
      yaw = yaw * 180.0D / 3.141592653589793D;
      yaw += 90.0D;
      return new double[]{yaw, pitch};
   }

   public static boolean basicChecksEntity(Entity pl) {
      return pl.func_70005_c_().equals(mc.field_71439_g.func_70005_c_()) || OyVey.friendManager.isFriend(pl.func_70005_c_()) || pl.field_70128_L;
   }

   public static BlockPos getPosition(Entity pl) {
      return new BlockPos(Math.floor(pl.field_70165_t), Math.floor(pl.field_70163_u), Math.floor(pl.field_70161_v));
   }

   public static List<BlockPos> getBlocksIn(Entity pl) {
      List<BlockPos> blocks = new ArrayList();
      AxisAlignedBB bb = pl.func_174813_aQ();

      for(double x = Math.floor(bb.field_72340_a); x < Math.ceil(bb.field_72336_d); ++x) {
         for(double y = Math.floor(bb.field_72338_b); y < Math.ceil(bb.field_72337_e); ++y) {
            for(double z = Math.floor(bb.field_72339_c); z < Math.ceil(bb.field_72334_f); ++z) {
               blocks.add(new BlockPos(x, y, z));
            }
         }
      }

      return blocks;
   }
}
