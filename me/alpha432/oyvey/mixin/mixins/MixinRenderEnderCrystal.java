package me.alpha432.oyvey.mixin.mixins;

import javax.annotation.Nullable;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.modules.render.Wireframe;
import me.alpha432.oyvey.util.ColorUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({RenderEnderCrystal.class})
public class MixinRenderEnderCrystal extends Render<EntityEnderCrystal> {
   @Shadow
   private static final ResourceLocation field_110787_a = new ResourceLocation("textures/entity/endercrystal/endercrystal.png");
   @Shadow
   private final ModelBase field_76995_b = new ModelEnderCrystal(0.0F, true);
   @Shadow
   private final ModelBase field_188316_g = new ModelEnderCrystal(0.0F, false);

   protected MixinRenderEnderCrystal(RenderManager renderManager) {
      super(renderManager);
   }

   @Overwrite
   public void func_76986_a(EntityEnderCrystal entity, double x, double y, double z, float entityYaw, float partialTicks) {
      float f = (float)entity.field_70261_a + partialTicks;
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)x, (float)y, (float)z);
      this.func_110776_a(field_110787_a);
      float f1 = MathHelper.func_76126_a(f * 0.2F) / 2.0F + 0.5F;
      f1 += f1 * f1;
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(entity));
      }

      float green;
      float blue;
      if (Wireframe.getINSTANCE().isOn() && (Boolean)Wireframe.getINSTANCE().crystals.getValue()) {
         float red = (float)(Integer)ClickGui.getInstance().red.getValue() / 255.0F;
         green = (float)(Integer)ClickGui.getInstance().green.getValue() / 255.0F;
         blue = (float)(Integer)ClickGui.getInstance().blue.getValue() / 255.0F;
         if (((Wireframe.RenderMode)Wireframe.getINSTANCE().cMode.getValue()).equals(Wireframe.RenderMode.WIREFRAME) && (Boolean)Wireframe.getINSTANCE().crystalModel.getValue()) {
            this.field_188316_g.func_78088_a(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
         }

         GlStateManager.func_179094_E();
         GL11.glPushAttrib(1048575);
         if (((Wireframe.RenderMode)Wireframe.getINSTANCE().cMode.getValue()).equals(Wireframe.RenderMode.WIREFRAME)) {
            GL11.glPolygonMode(1032, 6913);
         }

         GL11.glDisable(3553);
         GL11.glDisable(2896);
         if (((Wireframe.RenderMode)Wireframe.getINSTANCE().cMode.getValue()).equals(Wireframe.RenderMode.WIREFRAME)) {
            GL11.glEnable(2848);
         }

         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glDisable(2929);
         GL11.glDepthMask(false);
         GL11.glColor4f((Boolean)ClickGui.getInstance().rainbow.getValue() ? (float)ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRed() / 255.0F : red, (Boolean)ClickGui.getInstance().rainbow.getValue() ? (float)ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getGreen() / 255.0F : green, (Boolean)ClickGui.getInstance().rainbow.getValue() ? (float)ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getBlue() / 255.0F : blue, (Float)Wireframe.getINSTANCE().cAlpha.getValue() / 255.0F);
         if (((Wireframe.RenderMode)Wireframe.getINSTANCE().cMode.getValue()).equals(Wireframe.RenderMode.WIREFRAME)) {
            GL11.glLineWidth((Float)Wireframe.getINSTANCE().crystalLineWidth.getValue());
         }

         this.field_188316_g.func_78088_a(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
         GL11.glDisable(2896);
         GL11.glEnable(2929);
         GL11.glDepthMask(true);
         GL11.glColor4f((Boolean)ClickGui.getInstance().rainbow.getValue() ? (float)ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRed() / 255.0F : red, (Boolean)ClickGui.getInstance().rainbow.getValue() ? (float)ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getGreen() / 255.0F : green, (Boolean)ClickGui.getInstance().rainbow.getValue() ? (float)ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getBlue() / 255.0F : blue, (Float)Wireframe.getINSTANCE().cAlpha.getValue() / 255.0F);
         if (((Wireframe.RenderMode)Wireframe.getINSTANCE().cMode.getValue()).equals(Wireframe.RenderMode.WIREFRAME)) {
            GL11.glLineWidth((Float)Wireframe.getINSTANCE().crystalLineWidth.getValue());
         }

         this.field_188316_g.func_78088_a(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
         GlStateManager.func_179126_j();
         GlStateManager.func_179099_b();
         GlStateManager.func_179121_F();
      } else {
         this.field_188316_g.func_78088_a(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
      }

      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

      GlStateManager.func_179121_F();
      BlockPos blockpos = entity.func_184518_j();
      if (blockpos != null) {
         this.func_110776_a(RenderDragon.field_110843_g);
         green = (float)blockpos.func_177958_n() + 0.5F;
         blue = (float)blockpos.func_177956_o() + 0.5F;
         float f4 = (float)blockpos.func_177952_p() + 0.5F;
         double d0 = (double)green - entity.field_70165_t;
         double d1 = (double)blue - entity.field_70163_u;
         double d2 = (double)f4 - entity.field_70161_v;
         RenderDragon.func_188325_a(x + d0, y - 0.3D + (double)(f1 * 0.4F) + d1, z + d2, partialTicks, (double)green, (double)blue, (double)f4, entity.field_70261_a, entity.field_70165_t, entity.field_70163_u, entity.field_70161_v);
      }

      super.func_76986_a(entity, x, y, z, entityYaw, partialTicks);
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityEnderCrystal entityEnderCrystal) {
      return null;
   }
}
