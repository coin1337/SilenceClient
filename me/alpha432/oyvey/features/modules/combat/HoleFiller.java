package me.alpha432.oyvey.features.modules.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.TestUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class HoleFiller extends Module {
   private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true));
   private static HoleFiller INSTANCE = new HoleFiller();
   private final Setting<Integer> range = this.register(new Setting("PlaceRange", 8, 0, 10));
   private final Setting<Integer> delay = this.register(new Setting("Delay", 50, 0, 250));
   private final Setting<Integer> blocksPerTick = this.register(new Setting("BlocksPerTick", 20, 8, 30));
   private final Timer offTimer = new Timer();
   private final Timer timer = new Timer();
   private final Map<BlockPos, Integer> retries = new HashMap();
   private final Timer retryTimer = new Timer();
   private int blocksThisTick = 0;
   private ArrayList<BlockPos> holes = new ArrayList();
   private int trie;

   public HoleFiller() {
      super("HoleFiller", "Fills holes around you.", Module.Category.COMBAT, true, false, true);
      this.setInstance();
   }

   public static HoleFiller getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new HoleFiller();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public void onEnable() {
      if (fullNullCheck()) {
         this.disable();
      }

      this.offTimer.reset();
      this.trie = 0;
   }

   public void onTick() {
      if (this.isOn()) {
         this.doHoleFill();
      }

   }

   public void onDisable() {
      this.retries.clear();
   }

   private void doHoleFill() {
      if (!this.check()) {
         this.holes = new ArrayList();
         Iterable<BlockPos> blocks = BlockPos.func_177980_a(mc.field_71439_g.func_180425_c().func_177982_a(-(Integer)this.range.getValue(), -(Integer)this.range.getValue(), -(Integer)this.range.getValue()), mc.field_71439_g.func_180425_c().func_177982_a((Integer)this.range.getValue(), (Integer)this.range.getValue(), (Integer)this.range.getValue()));
         Iterator var2 = blocks.iterator();

         while(true) {
            BlockPos pos;
            do {
               do {
                  if (!var2.hasNext()) {
                     this.holes.forEach(this::placeBlock);
                     this.toggle();
                     return;
                  }

                  pos = (BlockPos)var2.next();
               } while(mc.field_71441_e.func_180495_p(pos).func_185904_a().func_76230_c());
            } while(mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 1, 0)).func_185904_a().func_76230_c());

            boolean solidNeighbours = mc.field_71441_e.func_180495_p(pos.func_177982_a(1, 0, 0)).func_177230_c() == Blocks.field_150357_h | mc.field_71441_e.func_180495_p(pos.func_177982_a(1, 0, 0)).func_177230_c() == Blocks.field_150343_Z && mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 0, 1)).func_177230_c() == Blocks.field_150357_h | mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 0, 1)).func_177230_c() == Blocks.field_150343_Z && mc.field_71441_e.func_180495_p(pos.func_177982_a(-1, 0, 0)).func_177230_c() == Blocks.field_150357_h | mc.field_71441_e.func_180495_p(pos.func_177982_a(-1, 0, 0)).func_177230_c() == Blocks.field_150343_Z && mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 0, -1)).func_177230_c() == Blocks.field_150357_h | mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 0, -1)).func_177230_c() == Blocks.field_150343_Z && mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 0, 0)).func_185904_a() == Material.field_151579_a && mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 1, 0)).func_185904_a() == Material.field_151579_a && mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 2, 0)).func_185904_a() == Material.field_151579_a;
            if (solidNeighbours) {
               this.holes.add(pos);
            }
         }
      }
   }

   private void placeBlock(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.func_72839_b((Entity)null, new AxisAlignedBB(pos)).iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            if (this.blocksThisTick < (Integer)this.blocksPerTick.getValue()) {
               int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
               int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
               if (obbySlot == -1 && eChestSot == -1) {
                  this.toggle();
               }

               int originalSlot = mc.field_71439_g.field_71071_by.field_70461_c;
               mc.field_71439_g.field_71071_by.field_70461_c = obbySlot == -1 ? eChestSot : obbySlot;
               mc.field_71442_b.func_78765_e();
               TestUtil.placeBlock(pos);
               if (mc.field_71439_g.field_71071_by.field_70461_c != originalSlot) {
                  mc.field_71439_g.field_71071_by.field_70461_c = originalSlot;
                  mc.field_71442_b.func_78765_e();
               }

               this.timer.reset();
               ++this.blocksThisTick;
            }

            return;
         }

         entity = (Entity)var2.next();
      } while(!(entity instanceof EntityLivingBase));

   }

   private boolean check() {
      if (fullNullCheck()) {
         this.disable();
         return true;
      } else {
         this.blocksThisTick = 0;
         if (this.retryTimer.passedMs(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
         }

         return !this.timer.passedMs((long)(Integer)this.delay.getValue());
      }
   }
}
