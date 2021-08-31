package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Wireframe extends Module {
   private static Wireframe INSTANCE = new Wireframe();
   public final Setting<Float> alpha = this.register(new Setting("PAlpha", 255.0F, 0.1F, 255.0F));
   public final Setting<Float> cAlpha = this.register(new Setting("CAlpha", 255.0F, 0.1F, 255.0F));
   public final Setting<Float> lineWidth = this.register(new Setting("PLineWidth", 1.0F, 0.1F, 3.0F));
   public final Setting<Float> crystalLineWidth = this.register(new Setting("CLineWidth", 1.0F, 0.1F, 3.0F));
   public Setting<Wireframe.RenderMode> mode;
   public Setting<Wireframe.RenderMode> cMode;
   public Setting<Boolean> players;
   public Setting<Boolean> playerModel;
   public Setting<Boolean> crystals;
   public Setting<Boolean> crystalModel;

   public Wireframe() {
      super("Wireframe", "Draws a wireframe esp around other players.", Module.Category.RENDER, false, false, false);
      this.mode = this.register(new Setting("PMode", Wireframe.RenderMode.SOLID));
      this.cMode = this.register(new Setting("CMode", Wireframe.RenderMode.SOLID));
      this.players = this.register(new Setting("Players", Boolean.FALSE));
      this.playerModel = this.register(new Setting("PlayerModel", Boolean.FALSE));
      this.crystals = this.register(new Setting("Crystals", Boolean.FALSE));
      this.crystalModel = this.register(new Setting("CrystalModel", Boolean.FALSE));
      this.setInstance();
   }

   public static Wireframe getINSTANCE() {
      if (INSTANCE == null) {
         INSTANCE = new Wireframe();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @SubscribeEvent
   public void onRenderPlayerEvent(Pre event) {
      event.getEntityPlayer().field_70737_aN = 0;
   }

   public static enum RenderMode {
      SOLID,
      WIREFRAME;
   }
}
