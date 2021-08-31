package me.alpha432.oyvey.features.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class UnlimitedSlider extends Button {
   public Setting setting;

   public UnlimitedSlider(Setting setting) {
      super(setting.getName());
      this.setting = setting;
      this.width = 15;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4F, this.y + (float)this.height - 0.5F, !this.isHovering(mouseX, mouseY) ? OyVey.colorManager.getColorWithAlpha((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()) : OyVey.colorManager.getColorWithAlpha((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()));
      OyVey.textManager.drawStringWithShadow(" - " + this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue() + ChatFormatting.WHITE + " +", this.x + 2.3F, this.y - 1.7F - (float)OyVeyGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.isHovering(mouseX, mouseY)) {
         mc.func_147118_V().func_147682_a(PositionedSoundRecord.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
         if (this.isRight(mouseX)) {
            if (this.setting.getValue() instanceof Double) {
               this.setting.setValue((Double)this.setting.getValue() + 1.0D);
            } else if (this.setting.getValue() instanceof Float) {
               this.setting.setValue((Float)this.setting.getValue() + 1.0F);
            } else if (this.setting.getValue() instanceof Integer) {
               this.setting.setValue((Integer)this.setting.getValue() + 1);
            }
         } else if (this.setting.getValue() instanceof Double) {
            this.setting.setValue((Double)this.setting.getValue() - 1.0D);
         } else if (this.setting.getValue() instanceof Float) {
            this.setting.setValue((Float)this.setting.getValue() - 1.0F);
         } else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue((Integer)this.setting.getValue() - 1);
         }
      }

   }

   public void update() {
      this.setHidden(!this.setting.isVisible());
   }

   public int getHeight() {
      return 14;
   }

   public void toggle() {
   }

   public boolean getState() {
      return true;
   }

   public boolean isRight(int x) {
      return (float)x > this.x + ((float)this.width + 7.4F) / 2.0F;
   }
}
