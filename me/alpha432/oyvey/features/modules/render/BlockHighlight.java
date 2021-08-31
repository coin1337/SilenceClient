package me.alpha432.oyvey.features.modules.render;

import java.awt.Color;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;

public class BlockHighlight extends Module {
   private final Setting<Float> lineWidth = this.register(new Setting("LineWidth", 1.0F, 0.1F, 5.0F));
   private final Setting<Integer> cAlpha = this.register(new Setting("Alpha", 255, 0, 255));

   public BlockHighlight() {
      super("BlockHighlight", "Highlights the block u look at.", Module.Category.RENDER, false, false, false);
   }

   public void onRender3D(Render3DEvent event) {
      RayTraceResult ray = mc.field_71476_x;
      if (ray != null && ray.field_72313_a == Type.BLOCK) {
         BlockPos blockpos = ray.func_178782_a();
         RenderUtil.drawBlockOutline(blockpos, (Boolean)ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()) : new Color((Integer)ClickGui.getInstance().red.getValue(), (Integer)ClickGui.getInstance().green.getValue(), (Integer)ClickGui.getInstance().blue.getValue(), (Integer)this.cAlpha.getValue()), (Float)this.lineWidth.getValue(), false);
      }

   }
}
