package me.alpha432.oyvey.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class HoleManager extends Feature {
   private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true));
   private final List<BlockPos> midSafety = new ArrayList();
   private List<BlockPos> holes = new ArrayList();

   public void update() {
      if (!fullNullCheck()) {
         this.holes = this.calcHoles();
      }

   }

   public List<BlockPos> getHoles() {
      return this.holes;
   }

   public List<BlockPos> getMidSafety() {
      return this.midSafety;
   }

   public List<BlockPos> getSortedHoles() {
      this.holes.sort(Comparator.comparingDouble((hole) -> {
         return mc.field_71439_g.func_174818_b(hole);
      }));
      return this.getHoles();
   }

   public List<BlockPos> calcHoles() {
      ArrayList<BlockPos> safeSpots = new ArrayList();
      this.midSafety.clear();
      List<BlockPos> positions = BlockUtil.getSphere(EntityUtil.getPlayerPos(mc.field_71439_g), 6.0F, 6, false, true, 0);
      Iterator var3 = positions.iterator();

      while(true) {
         BlockPos pos;
         do {
            do {
               do {
                  if (!var3.hasNext()) {
                     return safeSpots;
                  }

                  pos = (BlockPos)var3.next();
               } while(!mc.field_71441_e.func_180495_p(pos).func_177230_c().equals(Blocks.field_150350_a));
            } while(!mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 1, 0)).func_177230_c().equals(Blocks.field_150350_a));
         } while(!mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 2, 0)).func_177230_c().equals(Blocks.field_150350_a));

         boolean isSafe = true;
         boolean midSafe = true;
         BlockPos[] var7 = surroundOffset;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            BlockPos offset = var7[var9];
            Block block = mc.field_71441_e.func_180495_p(pos.func_177971_a(offset)).func_177230_c();
            if (BlockUtil.isBlockUnSolid(block)) {
               midSafe = false;
            }

            if (block != Blocks.field_150357_h && block != Blocks.field_150343_Z && block != Blocks.field_150477_bB && block != Blocks.field_150467_bQ) {
               isSafe = false;
            }
         }

         if (isSafe) {
            safeSpots.add(pos);
         }

         if (midSafe) {
            this.midSafety.add(pos);
         }
      }
   }

   public boolean isSafe(BlockPos pos) {
      boolean isSafe = true;
      BlockPos[] var3 = surroundOffset;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         BlockPos offset = var3[var5];
         Block block = mc.field_71441_e.func_180495_p(pos.func_177971_a(offset)).func_177230_c();
         if (block != Blocks.field_150357_h) {
            isSafe = false;
            break;
         }
      }

      return isSafe;
   }
}
