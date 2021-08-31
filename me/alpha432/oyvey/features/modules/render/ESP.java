package me.alpha432.oyvey.features.modules.render;

import java.awt.Color;
import java.util.Iterator;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class ESP extends Module {
   private static ESP INSTANCE = new ESP();
   private final Setting<Boolean> items = this.register(new Setting("Items", false));
   private final Setting<Boolean> xporbs = this.register(new Setting("XpOrbs", false));
   private final Setting<Boolean> xpbottles = this.register(new Setting("XpBottles", false));
   private final Setting<Boolean> pearl = this.register(new Setting("Pearls", false));
   private final Setting<Integer> red = this.register(new Setting("Red", 255, 0, 255));
   private final Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255));
   private final Setting<Integer> blue = this.register(new Setting("Blue", 255, 0, 255));
   private final Setting<Integer> boxAlpha = this.register(new Setting("BoxAlpha", 120, 0, 255));
   private final Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255));

   public ESP() {
      super("ESP", "Renders a nice ESP.", Module.Category.RENDER, false, false, false);
      this.setInstance();
   }

   public static ESP getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ESP();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public void onRender3D(Render3DEvent event) {
      AxisAlignedBB bb;
      Vec3d interp;
      int i;
      Iterator var5;
      Entity entity;
      if ((Boolean)this.items.getValue()) {
         i = 0;
         var5 = mc.field_71441_e.field_72996_f.iterator();

         while(var5.hasNext()) {
            entity = (Entity)var5.next();
            if (entity instanceof EntityItem && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
               interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
               bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
               GlStateManager.func_179094_E();
               GlStateManager.func_179147_l();
               GlStateManager.func_179097_i();
               GlStateManager.func_179120_a(770, 771, 0, 1);
               GlStateManager.func_179090_x();
               GlStateManager.func_179132_a(false);
               GL11.glEnable(2848);
               GL11.glHint(3154, 4354);
               GL11.glLineWidth(1.0F);
               RenderGlobal.func_189696_b(bb, (float)(Integer)this.red.getValue() / 255.0F, (float)(Integer)this.green.getValue() / 255.0F, (float)(Integer)this.blue.getValue() / 255.0F, (float)(Integer)this.boxAlpha.getValue() / 255.0F);
               GL11.glDisable(2848);
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179126_j();
               GlStateManager.func_179098_w();
               GlStateManager.func_179084_k();
               GlStateManager.func_179121_F();
               RenderUtil.drawBlockOutline(bb, new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), 1.0F);
               ++i;
               if (i >= 50) {
                  break;
               }
            }
         }
      }

      if ((Boolean)this.xporbs.getValue()) {
         i = 0;
         var5 = mc.field_71441_e.field_72996_f.iterator();

         while(var5.hasNext()) {
            entity = (Entity)var5.next();
            if (entity instanceof EntityXPOrb && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
               interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
               bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
               GlStateManager.func_179094_E();
               GlStateManager.func_179147_l();
               GlStateManager.func_179097_i();
               GlStateManager.func_179120_a(770, 771, 0, 1);
               GlStateManager.func_179090_x();
               GlStateManager.func_179132_a(false);
               GL11.glEnable(2848);
               GL11.glHint(3154, 4354);
               GL11.glLineWidth(1.0F);
               RenderGlobal.func_189696_b(bb, (float)(Integer)this.red.getValue() / 255.0F, (float)(Integer)this.green.getValue() / 255.0F, (float)(Integer)this.blue.getValue() / 255.0F, (float)(Integer)this.boxAlpha.getValue() / 255.0F);
               GL11.glDisable(2848);
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179126_j();
               GlStateManager.func_179098_w();
               GlStateManager.func_179084_k();
               GlStateManager.func_179121_F();
               RenderUtil.drawBlockOutline(bb, new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), 1.0F);
               ++i;
               if (i >= 50) {
                  break;
               }
            }
         }
      }

      if ((Boolean)this.pearl.getValue()) {
         i = 0;
         var5 = mc.field_71441_e.field_72996_f.iterator();

         while(var5.hasNext()) {
            entity = (Entity)var5.next();
            if (entity instanceof EntityEnderPearl && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
               interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
               bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
               GlStateManager.func_179094_E();
               GlStateManager.func_179147_l();
               GlStateManager.func_179097_i();
               GlStateManager.func_179120_a(770, 771, 0, 1);
               GlStateManager.func_179090_x();
               GlStateManager.func_179132_a(false);
               GL11.glEnable(2848);
               GL11.glHint(3154, 4354);
               GL11.glLineWidth(1.0F);
               RenderGlobal.func_189696_b(bb, (float)(Integer)this.red.getValue() / 255.0F, (float)(Integer)this.green.getValue() / 255.0F, (float)(Integer)this.blue.getValue() / 255.0F, (float)(Integer)this.boxAlpha.getValue() / 255.0F);
               GL11.glDisable(2848);
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179126_j();
               GlStateManager.func_179098_w();
               GlStateManager.func_179084_k();
               GlStateManager.func_179121_F();
               RenderUtil.drawBlockOutline(bb, new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), 1.0F);
               ++i;
               if (i >= 50) {
                  break;
               }
            }
         }
      }

      if ((Boolean)this.xpbottles.getValue()) {
         i = 0;
         var5 = mc.field_71441_e.field_72996_f.iterator();

         while(var5.hasNext()) {
            entity = (Entity)var5.next();
            if (entity instanceof EntityExpBottle && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
               interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
               bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
               GlStateManager.func_179094_E();
               GlStateManager.func_179147_l();
               GlStateManager.func_179097_i();
               GlStateManager.func_179120_a(770, 771, 0, 1);
               GlStateManager.func_179090_x();
               GlStateManager.func_179132_a(false);
               GL11.glEnable(2848);
               GL11.glHint(3154, 4354);
               GL11.glLineWidth(1.0F);
               RenderGlobal.func_189696_b(bb, (float)(Integer)this.red.getValue() / 255.0F, (float)(Integer)this.green.getValue() / 255.0F, (float)(Integer)this.blue.getValue() / 255.0F, (float)(Integer)this.boxAlpha.getValue() / 255.0F);
               GL11.glDisable(2848);
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179126_j();
               GlStateManager.func_179098_w();
               GlStateManager.func_179084_k();
               GlStateManager.func_179121_F();
               RenderUtil.drawBlockOutline(bb, new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), 1.0F);
               ++i;
               if (i >= 50) {
                  break;
               }
            }
         }
      }

   }
}
