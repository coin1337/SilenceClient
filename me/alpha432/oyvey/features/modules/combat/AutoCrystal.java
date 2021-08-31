package me.alpha432.oyvey.features.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.modules.misc.AutoGG;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoCrystal extends Module {
   private final Timer placeTimer = new Timer();
   private final Timer breakTimer = new Timer();
   private final Timer preditTimer = new Timer();
   private final Timer manualTimer = new Timer();
   private final Setting<Integer> attackFactor = this.register(new Setting("PredictDelay", 0, 0, 200));
   private final Setting<Integer> red = this.register(new Setting("Red", 0, 0, 255));
   private final Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255));
   private final Setting<Integer> blue = this.register(new Setting("Blue", 0, 0, 255));
   private final Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255));
   private final Setting<Integer> boxAlpha = this.register(new Setting("BoxAlpha", 125, 0, 255));
   private final Setting<Float> lineWidth = this.register(new Setting("LineWidth", 1.0F, 0.1F, 5.0F));
   public Setting<Boolean> place = this.register(new Setting("Place", true));
   public Setting<Float> placeDelay = this.register(new Setting("PlaceDelay", 4.0F, 0.0F, 300.0F));
   public Setting<Float> placeRange = this.register(new Setting("PlaceRange", 4.0F, 0.1F, 7.0F));
   public Setting<Boolean> explode = this.register(new Setting("Break", true));
   public Setting<Boolean> packetBreak = this.register(new Setting("PacketBreak", true));
   public Setting<Boolean> predicts = this.register(new Setting("Predict", true));
   public Setting<Boolean> rotate = this.register(new Setting("Rotate", true));
   public Setting<Float> breakDelay = this.register(new Setting("BreakDelay", 4.0F, 0.0F, 300.0F));
   public Setting<Float> breakRange = this.register(new Setting("BreakRange", 4.0F, 0.1F, 7.0F));
   public Setting<Float> breakWallRange = this.register(new Setting("BreakWallRange", 4.0F, 0.1F, 7.0F));
   public Setting<Boolean> opPlace = this.register(new Setting("1.13 Place", true));
   public Setting<Boolean> suicide = this.register(new Setting("AntiSuicide", true));
   public Setting<Boolean> autoswitch = this.register(new Setting("AutoSwitch", true));
   public Setting<Boolean> ignoreUseAmount = this.register(new Setting("IgnoreUseAmount", true));
   public Setting<Integer> wasteAmount = this.register(new Setting("UseAmount", 4, 1, 5));
   public Setting<Boolean> facePlaceSword = this.register(new Setting("FacePlaceSword", true));
   public Setting<Float> targetRange = this.register(new Setting("TargetRange", 4.0F, 1.0F, 12.0F));
   public Setting<Float> minDamage = this.register(new Setting("MinDamage", 4.0F, 0.1F, 20.0F));
   public Setting<Float> facePlace = this.register(new Setting("FacePlaceHP", 4.0F, 0.0F, 36.0F));
   public Setting<Float> breakMaxSelfDamage = this.register(new Setting("BreakMaxSelf", 4.0F, 0.1F, 12.0F));
   public Setting<Float> breakMinDmg = this.register(new Setting("BreakMinDmg", 4.0F, 0.1F, 7.0F));
   public Setting<Float> minArmor = this.register(new Setting("MinArmor", 4.0F, 0.1F, 80.0F));
   public Setting<AutoCrystal.SwingMode> swingMode;
   public Setting<Boolean> render;
   public Setting<Boolean> renderDmg;
   public Setting<Boolean> box;
   public Setting<Boolean> outline;
   private final Setting<Integer> cRed;
   private final Setting<Integer> cGreen;
   private final Setting<Integer> cBlue;
   private final Setting<Integer> cAlpha;
   EntityEnderCrystal crystal;
   private EntityLivingBase target;
   private BlockPos pos;
   private int hotBarSlot;
   private boolean armor;
   private boolean armorTarget;
   private int crystalCount;
   private int predictWait;
   private int predictPackets;
   private boolean packetCalc;
   private float yaw;
   private EntityLivingBase realTarget;
   private int predict;
   private float pitch;
   private boolean rotating;

   public AutoCrystal() {
      super("AutoCrystal", "NiggaHack ac best ac", Module.Category.COMBAT, true, false, false);
      this.swingMode = this.register(new Setting("Swing", AutoCrystal.SwingMode.MainHand));
      this.render = this.register(new Setting("Render", true));
      this.renderDmg = this.register(new Setting("RenderDmg", true));
      this.box = this.register(new Setting("Box", true));
      this.outline = this.register(new Setting("Outline", true));
      this.cRed = this.register(new Setting("OL-Red", 0, 0, 255, (v) -> {
         return (Boolean)this.outline.getValue();
      }));
      this.cGreen = this.register(new Setting("OL-Green", 0, 0, 255, (v) -> {
         return (Boolean)this.outline.getValue();
      }));
      this.cBlue = this.register(new Setting("OL-Blue", 255, 0, 255, (v) -> {
         return (Boolean)this.outline.getValue();
      }));
      this.cAlpha = this.register(new Setting("OL-Alpha", 255, 0, 255, (v) -> {
         return (Boolean)this.outline.getValue();
      }));
      this.yaw = 0.0F;
      this.pitch = 0.0F;
      this.rotating = false;
   }

   public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
      ArrayList<BlockPos> circleblocks = new ArrayList();
      int cx = loc.func_177958_n();
      int cy = loc.func_177956_o();
      int cz = loc.func_177952_p();

      for(int x = cx - (int)r; (float)x <= (float)cx + r; ++x) {
         for(int z = cz - (int)r; (float)z <= (float)cz + r; ++z) {
            int y = sphere ? cy - (int)r : cy;

            while(true) {
               float f = sphere ? (float)cy + r : (float)(cy + h);
               if (!((float)y < f)) {
                  break;
               }

               double dist = (double)((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));
               if (dist < (double)(r * r) && (!hollow || !(dist < (double)((r - 1.0F) * (r - 1.0F))))) {
                  BlockPos l = new BlockPos(x, y + plus_y, z);
                  circleblocks.add(l);
               }

               ++y;
            }
         }
      }

      return circleblocks;
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getStage() == 0 && (Boolean)this.rotate.getValue() && this.rotating && event.getPacket() instanceof CPacketPlayer) {
         CPacketPlayer packet = (CPacketPlayer)event.getPacket();
         packet.field_149476_e = this.yaw;
         packet.field_149473_f = this.pitch;
         this.rotating = false;
      }

   }

   private void rotateTo(Entity entity) {
      if ((Boolean)this.rotate.getValue()) {
         float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), entity.func_174791_d());
         this.yaw = angle[0];
         this.pitch = angle[1];
         this.rotating = true;
      }

   }

   private void rotateToPos(BlockPos pos) {
      if ((Boolean)this.rotate.getValue()) {
         float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5F), (double)((float)pos.func_177956_o() - 0.5F), (double)((float)pos.func_177952_p() + 0.5F)));
         this.yaw = angle[0];
         this.pitch = angle[1];
         this.rotating = true;
      }

   }

   public void onEnable() {
      this.placeTimer.reset();
      this.breakTimer.reset();
      this.predictWait = 0;
      this.hotBarSlot = -1;
      this.pos = null;
      this.crystal = null;
      this.predict = 0;
      this.predictPackets = 1;
      this.target = null;
      this.packetCalc = false;
      this.realTarget = null;
      this.armor = false;
      this.armorTarget = false;
   }

   public void onDisable() {
      this.rotating = false;
   }

   public void onTick() {
      this.onCrystal();
   }

   public String getDisplayInfo() {
      return this.realTarget != null ? this.realTarget.func_70005_c_() : null;
   }

   public void onCrystal() {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         this.realTarget = null;
         this.manualBreaker();
         this.crystalCount = 0;
         if (!(Boolean)this.ignoreUseAmount.getValue()) {
            Iterator var1 = mc.field_71441_e.field_72996_f.iterator();

            while(var1.hasNext()) {
               Entity crystal = (Entity)var1.next();
               if (crystal instanceof EntityEnderCrystal && this.IsValidCrystal(crystal)) {
                  boolean count = false;
                  double damage = (double)this.calculateDamage((double)this.target.func_180425_c().func_177958_n() + 0.5D, (double)this.target.func_180425_c().func_177956_o() + 1.0D, (double)this.target.func_180425_c().func_177952_p() + 0.5D, this.target);
                  if (damage >= (double)(Float)this.minDamage.getValue()) {
                     count = true;
                  }

                  if (count) {
                     ++this.crystalCount;
                  }
               }
            }
         }

         this.hotBarSlot = -1;
         int crystalLimit;
         if (mc.field_71439_g.func_184592_cb().func_77973_b() != Items.field_185158_cP) {
            int crystalSlot = mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP ? mc.field_71439_g.field_71071_by.field_70461_c : -1;
            if (crystalSlot == -1) {
               for(crystalLimit = 0; crystalLimit < 9; ++crystalLimit) {
                  if (mc.field_71439_g.field_71071_by.func_70301_a(crystalLimit).func_77973_b() == Items.field_185158_cP) {
                     crystalSlot = crystalLimit;
                     this.hotBarSlot = crystalLimit;
                     break;
                  }
               }
            }

            if (crystalSlot == -1) {
               this.pos = null;
               this.target = null;
               return;
            }
         }

         if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() != Items.field_185158_cP) {
            this.pos = null;
            this.target = null;
         } else {
            if (this.target == null) {
               this.target = this.getTarget();
            }

            if (this.target == null) {
               this.crystal = null;
            } else {
               if (this.target.func_70032_d(mc.field_71439_g) > 12.0F) {
                  this.crystal = null;
                  this.target = null;
               }

               this.crystal = (EntityEnderCrystal)mc.field_71441_e.field_72996_f.stream().filter(this::IsValidCrystal).map((p_Entity) -> {
                  return (EntityEnderCrystal)p_Entity;
               }).min(Comparator.comparing((p_Entity) -> {
                  return this.target.func_70032_d(p_Entity);
               })).orElse((Object)null);
               if (this.crystal != null && (Boolean)this.explode.getValue() && this.breakTimer.passedMs(((Float)this.breakDelay.getValue()).longValue())) {
                  this.breakTimer.reset();
                  if ((Boolean)this.packetBreak.getValue()) {
                     this.rotateTo(this.crystal);
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(this.crystal));
                  } else {
                     this.rotateTo(this.crystal);
                     mc.field_71442_b.func_78764_a(mc.field_71439_g, this.crystal);
                  }

                  if (this.swingMode.getValue() == AutoCrystal.SwingMode.MainHand) {
                     mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                  } else if (this.swingMode.getValue() == AutoCrystal.SwingMode.OffHand) {
                     mc.field_71439_g.func_184609_a(EnumHand.OFF_HAND);
                  }
               }

               if (this.placeTimer.passedMs(((Float)this.placeDelay.getValue()).longValue()) && (Boolean)this.place.getValue()) {
                  this.placeTimer.reset();
                  double damage = 0.5D;
                  Iterator var20 = this.placePostions((Float)this.placeRange.getValue()).iterator();

                  while(true) {
                     double selfDmg;
                     double targetDmg;
                     BlockPos blockPos;
                     do {
                        while(true) {
                           do {
                              do {
                                 do {
                                    do {
                                       do {
                                          do {
                                             if (!var20.hasNext()) {
                                                if (damage == 0.5D) {
                                                   this.pos = null;
                                                   this.target = null;
                                                   this.realTarget = null;
                                                   return;
                                                }

                                                this.realTarget = this.target;
                                                if (AutoGG.getINSTANCE().isOn()) {
                                                   AutoGG autoGG = (AutoGG)OyVey.moduleManager.getModuleByName("AutoGG");
                                                   autoGG.addTargetedPlayer(this.target.func_70005_c_());
                                                }

                                                if (this.hotBarSlot != -1 && (Boolean)this.autoswitch.getValue() && !mc.field_71439_g.func_70644_a(MobEffects.field_76437_t)) {
                                                   mc.field_71439_g.field_71071_by.field_70461_c = this.hotBarSlot;
                                                }

                                                if (!(Boolean)this.ignoreUseAmount.getValue()) {
                                                   crystalLimit = (Integer)this.wasteAmount.getValue();
                                                   if (this.crystalCount >= crystalLimit) {
                                                      return;
                                                   }

                                                   if (damage < (double)(Float)this.minDamage.getValue()) {
                                                      crystalLimit = 1;
                                                   }

                                                   if (this.crystalCount < crystalLimit && this.pos != null) {
                                                      this.rotateToPos(this.pos);
                                                      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.pos, EnumFacing.UP, mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                                                   }
                                                } else if (this.pos != null) {
                                                   this.rotateToPos(this.pos);
                                                   mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.pos, EnumFacing.UP, mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                                                }

                                                return;
                                             }

                                             blockPos = (BlockPos)var20.next();
                                          } while(blockPos == null);
                                       } while(this.target == null);
                                    } while(!mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(blockPos)).isEmpty());
                                 } while(this.target.func_70011_f((double)blockPos.func_177958_n(), (double)blockPos.func_177956_o(), (double)blockPos.func_177952_p()) > (double)(Float)this.targetRange.getValue());
                              } while(this.target.field_70128_L);
                           } while(this.target.func_110143_aJ() + this.target.func_110139_bj() <= 0.0F);

                           targetDmg = (double)this.calculateDamage((double)blockPos.func_177958_n() + 0.5D, (double)blockPos.func_177956_o() + 1.0D, (double)blockPos.func_177952_p() + 0.5D, this.target);
                           this.armor = false;
                           Iterator var11 = this.target.func_184193_aE().iterator();

                           while(var11.hasNext()) {
                              ItemStack is = (ItemStack)var11.next();
                              float green = ((float)is.func_77958_k() - (float)is.func_77952_i()) / (float)is.func_77958_k();
                              float red = 1.0F - green;
                              int dmg = 100 - (int)(red * 100.0F);
                              if ((float)dmg <= (Float)this.minArmor.getValue()) {
                                 this.armor = true;
                              }
                           }

                           if (!(targetDmg < (double)(Float)this.minDamage.getValue())) {
                              break;
                           }

                           if ((Boolean)this.facePlaceSword.getValue()) {
                              if (!(this.target.func_110139_bj() + this.target.func_110143_aJ() > (Float)this.facePlace.getValue())) {
                                 break;
                              }
                           } else if (!(mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword) && !(this.target.func_110139_bj() + this.target.func_110143_aJ() > (Float)this.facePlace.getValue())) {
                              break;
                           }

                           if ((Boolean)this.facePlaceSword.getValue()) {
                              if (!this.armor) {
                                 continue;
                              }
                              break;
                           } else if (!(mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword) && this.armor) {
                              break;
                           }
                        }
                     } while((selfDmg = (double)this.calculateDamage((double)blockPos.func_177958_n() + 0.5D, (double)blockPos.func_177956_o() + 1.0D, (double)blockPos.func_177952_p() + 0.5D, mc.field_71439_g)) + ((Boolean)this.suicide.getValue() ? 2.0D : 0.5D) >= (double)(mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj()) && selfDmg >= targetDmg && targetDmg < (double)(this.target.func_110143_aJ() + this.target.func_110139_bj()));

                     if (damage < targetDmg) {
                        this.pos = blockPos;
                        damage = targetDmg;
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST,
      receiveCanceled = true
   )
   public void onPacketReceive(PacketEvent.Receive event) {
      SPacketSpawnObject packet;
      if (event.getPacket() instanceof SPacketSpawnObject && (packet = (SPacketSpawnObject)event.getPacket()).func_148993_l() == 51 && (Boolean)this.predicts.getValue() && this.preditTimer.passedMs(((Integer)this.attackFactor.getValue()).longValue()) && (Boolean)this.predicts.getValue() && (Boolean)this.explode.getValue() && (Boolean)this.packetBreak.getValue() && this.target != null) {
         if (!this.isPredicting(packet)) {
            return;
         }

         CPacketUseEntity predict = new CPacketUseEntity();
         predict.field_149567_a = packet.func_149001_c();
         predict.field_149566_b = Action.ATTACK;
         mc.field_71439_g.field_71174_a.func_147297_a(predict);
      }

   }

   public void onRender3D(Render3DEvent event) {
      if (this.pos != null && (Boolean)this.render.getValue() && this.target != null) {
         RenderUtil.drawBoxESP(this.pos, (Boolean)ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()) : new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), (Boolean)this.outline.getValue(), (Boolean)ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()) : new Color((Integer)this.cRed.getValue(), (Integer)this.cGreen.getValue(), (Integer)this.cBlue.getValue(), (Integer)this.cAlpha.getValue()), (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), true);
         if ((Boolean)this.renderDmg.getValue()) {
            double renderDamage = (double)this.calculateDamage((double)this.pos.func_177958_n() + 0.5D, (double)this.pos.func_177956_o() + 1.0D, (double)this.pos.func_177952_p() + 0.5D, this.target);
            RenderUtil.drawText(this.pos, (Math.floor(renderDamage) == renderDamage ? (int)renderDamage : String.format("%.1f", renderDamage)) + "");
         }
      }

   }

   private boolean isPredicting(SPacketSpawnObject packet) {
      BlockPos packPos = new BlockPos(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e());
      if (mc.field_71439_g.func_70011_f(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e()) > (double)(Float)this.breakRange.getValue()) {
         return false;
      } else if (!this.canSeePos(packPos) && mc.field_71439_g.func_70011_f(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e()) > (double)(Float)this.breakWallRange.getValue()) {
         return false;
      } else {
         double targetDmg = (double)this.calculateDamage(packet.func_186880_c() + 0.5D, packet.func_186882_d() + 1.0D, packet.func_186881_e() + 0.5D, this.target);
         if (EntityUtil.isInHole(mc.field_71439_g) && targetDmg >= 1.0D) {
            return true;
         } else {
            double selfDmg = (double)this.calculateDamage(packet.func_186880_c() + 0.5D, packet.func_186882_d() + 1.0D, packet.func_186881_e() + 0.5D, mc.field_71439_g);
            double d = (Boolean)this.suicide.getValue() ? 2.0D : 0.5D;
            if (selfDmg + d < (double)(mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj()) && targetDmg >= (double)(this.target.func_110139_bj() + this.target.func_110143_aJ())) {
               return true;
            } else {
               this.armorTarget = false;
               Iterator var9 = this.target.func_184193_aE().iterator();

               while(var9.hasNext()) {
                  ItemStack is = (ItemStack)var9.next();
                  float green = ((float)is.func_77958_k() - (float)is.func_77952_i()) / (float)is.func_77958_k();
                  float red = 1.0F - green;
                  int dmg = 100 - (int)(red * 100.0F);
                  if ((float)dmg <= (Float)this.minArmor.getValue()) {
                     this.armorTarget = true;
                  }
               }

               if (targetDmg >= (double)(Float)this.breakMinDmg.getValue() && selfDmg <= (double)(Float)this.breakMaxSelfDamage.getValue()) {
                  return true;
               } else {
                  return EntityUtil.isInHole(this.target) && this.target.func_110143_aJ() + this.target.func_110139_bj() <= (Float)this.facePlace.getValue();
               }
            }
         }
      }
   }

   private boolean IsValidCrystal(Entity p_Entity) {
      if (p_Entity == null) {
         return false;
      } else if (!(p_Entity instanceof EntityEnderCrystal)) {
         return false;
      } else if (this.target == null) {
         return false;
      } else if (p_Entity.func_70032_d(mc.field_71439_g) > (Float)this.breakRange.getValue()) {
         return false;
      } else if (!mc.field_71439_g.func_70685_l(p_Entity) && p_Entity.func_70032_d(mc.field_71439_g) > (Float)this.breakWallRange.getValue()) {
         return false;
      } else if (!this.target.field_70128_L && !(this.target.func_110143_aJ() + this.target.func_110139_bj() <= 0.0F)) {
         double targetDmg = (double)this.calculateDamage((double)p_Entity.func_180425_c().func_177958_n() + 0.5D, (double)p_Entity.func_180425_c().func_177956_o() + 1.0D, (double)p_Entity.func_180425_c().func_177952_p() + 0.5D, this.target);
         if (EntityUtil.isInHole(mc.field_71439_g) && targetDmg >= 1.0D) {
            return true;
         } else {
            double selfDmg = (double)this.calculateDamage((double)p_Entity.func_180425_c().func_177958_n() + 0.5D, (double)p_Entity.func_180425_c().func_177956_o() + 1.0D, (double)p_Entity.func_180425_c().func_177952_p() + 0.5D, mc.field_71439_g);
            double d = (Boolean)this.suicide.getValue() ? 2.0D : 0.5D;
            if (selfDmg + d < (double)(mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj()) && targetDmg >= (double)(this.target.func_110139_bj() + this.target.func_110143_aJ())) {
               return true;
            } else {
               this.armorTarget = false;
               Iterator var8 = this.target.func_184193_aE().iterator();

               while(var8.hasNext()) {
                  ItemStack is = (ItemStack)var8.next();
                  float green = ((float)is.func_77958_k() - (float)is.func_77952_i()) / (float)is.func_77958_k();
                  float red = 1.0F - green;
                  int dmg = 100 - (int)(red * 100.0F);
                  if ((float)dmg <= (Float)this.minArmor.getValue()) {
                     this.armorTarget = true;
                  }
               }

               if (targetDmg >= (double)(Float)this.breakMinDmg.getValue() && selfDmg <= (double)(Float)this.breakMaxSelfDamage.getValue()) {
                  return true;
               } else {
                  return EntityUtil.isInHole(this.target) && this.target.func_110143_aJ() + this.target.func_110139_bj() <= (Float)this.facePlace.getValue();
               }
            }
         }
      } else {
         return false;
      }
   }

   EntityPlayer getTarget() {
      EntityPlayer closestPlayer = null;
      Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

      while(true) {
         EntityPlayer entity;
         do {
            do {
               do {
                  do {
                     do {
                        do {
                           do {
                              if (!var2.hasNext()) {
                                 return closestPlayer;
                              }

                              entity = (EntityPlayer)var2.next();
                           } while(mc.field_71439_g == null);
                        } while(mc.field_71439_g.field_70128_L);
                     } while(entity.field_70128_L);
                  } while(entity == mc.field_71439_g);
               } while(OyVey.friendManager.isFriend(entity.func_70005_c_()));
            } while(entity.func_70032_d(mc.field_71439_g) > 12.0F);

            this.armorTarget = false;
            Iterator var4 = entity.func_184193_aE().iterator();

            while(var4.hasNext()) {
               ItemStack is = (ItemStack)var4.next();
               float green = ((float)is.func_77958_k() - (float)is.func_77952_i()) / (float)is.func_77958_k();
               float red = 1.0F - green;
               int dmg = 100 - (int)(red * 100.0F);
               if ((float)dmg <= (Float)this.minArmor.getValue()) {
                  this.armorTarget = true;
               }
            }
         } while(EntityUtil.isInHole(entity) && entity.func_110139_bj() + entity.func_110143_aJ() > (Float)this.facePlace.getValue() && !this.armorTarget && (Float)this.minDamage.getValue() > 2.2F);

         if (closestPlayer == null) {
            closestPlayer = entity;
         } else if (closestPlayer.func_70032_d(mc.field_71439_g) > entity.func_70032_d(mc.field_71439_g)) {
            closestPlayer = entity;
         }
      }
   }

   private void manualBreaker() {
      RayTraceResult result;
      if (this.manualTimer.passedMs(200L) && mc.field_71474_y.field_74313_G.func_151470_d() && mc.field_71439_g.func_184592_cb().func_77973_b() != Items.field_151153_ao && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151153_ao && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151031_f && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151062_by && (result = mc.field_71476_x) != null) {
         if (result.field_72313_a.equals(Type.ENTITY)) {
            Entity entity = result.field_72308_g;
            if (entity instanceof EntityEnderCrystal) {
               if ((Boolean)this.packetBreak.getValue()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(entity));
               } else {
                  mc.field_71442_b.func_78764_a(mc.field_71439_g, entity);
               }

               this.manualTimer.reset();
            }
         } else if (result.field_72313_a.equals(Type.BLOCK)) {
            BlockPos mousePos = new BlockPos((double)mc.field_71476_x.func_178782_a().func_177958_n(), (double)mc.field_71476_x.func_178782_a().func_177956_o() + 1.0D, (double)mc.field_71476_x.func_178782_a().func_177952_p());
            Iterator var3 = mc.field_71441_e.func_72839_b((Entity)null, new AxisAlignedBB(mousePos)).iterator();

            while(var3.hasNext()) {
               Entity target = (Entity)var3.next();
               if (target instanceof EntityEnderCrystal) {
                  if ((Boolean)this.packetBreak.getValue()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(target));
                  } else {
                     mc.field_71442_b.func_78764_a(mc.field_71439_g, target);
                  }

                  this.manualTimer.reset();
               }
            }
         }
      }

   }

   private boolean canSeePos(BlockPos pos) {
      return mc.field_71441_e.func_147447_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p()), false, true, false) == null;
   }

   private NonNullList<BlockPos> placePostions(float placeRange) {
      NonNullList positions = NonNullList.func_191196_a();
      positions.addAll((Collection)getSphere(new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v)), placeRange, (int)placeRange, false, true, 0).stream().filter((pos) -> {
         return this.canPlaceCrystal(pos, true);
      }).collect(Collectors.toList()));
      return positions;
   }

   private boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck) {
      BlockPos boost = blockPos.func_177982_a(0, 1, 0);
      BlockPos boost2 = blockPos.func_177982_a(0, 2, 0);

      try {
         Iterator var5;
         Entity entity;
         if (!(Boolean)this.opPlace.getValue()) {
            if (mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150357_h && mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150343_Z) {
               return false;
            }

            if (mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150350_a || mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_150350_a) {
               return false;
            }

            if (!specialEntityCheck) {
               return mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
            }

            var5 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).iterator();

            while(var5.hasNext()) {
               entity = (Entity)var5.next();
               if (!(entity instanceof EntityEnderCrystal)) {
                  return false;
               }
            }

            var5 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost2)).iterator();

            while(var5.hasNext()) {
               entity = (Entity)var5.next();
               if (!(entity instanceof EntityEnderCrystal)) {
                  return false;
               }
            }
         } else {
            if (mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150357_h && mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150343_Z) {
               return false;
            }

            if (mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150350_a) {
               return false;
            }

            if (!specialEntityCheck) {
               return mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).isEmpty();
            }

            var5 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).iterator();

            while(var5.hasNext()) {
               entity = (Entity)var5.next();
               if (!(entity instanceof EntityEnderCrystal)) {
                  return false;
               }
            }
         }

         return true;
      } catch (Exception var7) {
         return false;
      }
   }

   private float calculateDamage(double posX, double posY, double posZ, Entity entity) {
      float doubleExplosionSize = 12.0F;
      double distancedsize = entity.func_70011_f(posX, posY, posZ) / 12.0D;
      Vec3d vec3d = new Vec3d(posX, posY, posZ);
      double blockDensity = 0.0D;

      try {
         blockDensity = (double)entity.field_70170_p.func_72842_a(vec3d, entity.func_174813_aQ());
      } catch (Exception var19) {
      }

      double v = (1.0D - distancedsize) * blockDensity;
      float damage = (float)((int)((v * v + v) / 2.0D * 7.0D * 12.0D + 1.0D));
      double finald = 1.0D;
      if (entity instanceof EntityLivingBase) {
         finald = (double)this.getBlastReduction((EntityLivingBase)entity, this.getDamageMultiplied(damage), new Explosion(mc.field_71441_e, (Entity)null, posX, posY, posZ, 6.0F, false, true));
      }

      return (float)finald;
   }

   private float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
      float damage;
      if (entity instanceof EntityPlayer) {
         EntityPlayer ep = (EntityPlayer)entity;
         DamageSource ds = DamageSource.func_94539_a(explosion);
         damage = CombatRules.func_189427_a(damageI, (float)ep.func_70658_aO(), (float)ep.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
         int k = 0;

         try {
            k = EnchantmentHelper.func_77508_a(ep.func_184193_aE(), ds);
         } catch (Exception var9) {
         }

         float f = MathHelper.func_76131_a((float)k, 0.0F, 20.0F);
         damage *= 1.0F - f / 25.0F;
         if (entity.func_70644_a(MobEffects.field_76429_m)) {
            damage -= damage / 4.0F;
         }

         damage = Math.max(damage, 0.0F);
         return damage;
      } else {
         damage = CombatRules.func_189427_a(damageI, (float)entity.func_70658_aO(), (float)entity.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
         return damage;
      }
   }

   private float getDamageMultiplied(float damage) {
      int diff = mc.field_71441_e.func_175659_aa().func_151525_a();
      return damage * (diff == 0 ? 0.0F : (diff == 2 ? 1.0F : (diff == 1 ? 0.5F : 1.5F)));
   }

   public static enum SwingMode {
      MainHand,
      OffHand,
      None;
   }
}
