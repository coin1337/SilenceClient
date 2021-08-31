package me.alpha432.oyvey.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ClientEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui extends Module {
   private static ClickGui INSTANCE = new ClickGui();
   public Setting<String> prefix = this.register(new Setting("Prefix", "."));
   public Setting<Boolean> customFov = this.register(new Setting("CustomFov", false));
   public Setting<Float> fov = this.register(new Setting("Fov", 150.0F, -180.0F, 180.0F));
   public Setting<Integer> red = this.register(new Setting("Red", 0, 0, 255));
   public Setting<Integer> green = this.register(new Setting("Green", 0, 0, 255));
   public Setting<Integer> blue = this.register(new Setting("Blue", 255, 0, 255));
   public Setting<Integer> hoverAlpha = this.register(new Setting("Alpha", 180, 0, 255));
   public Setting<Integer> topRed = this.register(new Setting("SecondRed", 0, 0, 255));
   public Setting<Integer> topGreen = this.register(new Setting("SecondGreen", 0, 0, 255));
   public Setting<Integer> topBlue = this.register(new Setting("SecondBlue", 150, 0, 255));
   public Setting<Integer> alpha = this.register(new Setting("HoverAlpha", 240, 0, 255));
   public Setting<Boolean> rainbow = this.register(new Setting("Rainbow", false));
   public Setting<ClickGui.rainbowMode> rainbowModeHud;
   public Setting<ClickGui.rainbowModeArray> rainbowModeA;
   public Setting<Integer> rainbowHue;
   public Setting<Float> rainbowBrightness;
   public Setting<Float> rainbowSaturation;
   private OyVeyGui click;

   public ClickGui() {
      super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
      this.rainbowModeHud = this.register(new Setting("HRainbowMode", ClickGui.rainbowMode.Static, (v) -> {
         return (Boolean)this.rainbow.getValue();
      }));
      this.rainbowModeA = this.register(new Setting("ARainbowMode", ClickGui.rainbowModeArray.Static, (v) -> {
         return (Boolean)this.rainbow.getValue();
      }));
      this.rainbowHue = this.register(new Setting("Delay", 240, 0, 600, (v) -> {
         return (Boolean)this.rainbow.getValue();
      }));
      this.rainbowBrightness = this.register(new Setting("Brightness ", 150.0F, 1.0F, 255.0F, (v) -> {
         return (Boolean)this.rainbow.getValue();
      }));
      this.rainbowSaturation = this.register(new Setting("Saturation", 150.0F, 1.0F, 255.0F, (v) -> {
         return (Boolean)this.rainbow.getValue();
      }));
      this.setInstance();
   }

   public static ClickGui getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ClickGui();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public void onUpdate() {
      if ((Boolean)this.customFov.getValue()) {
         mc.field_71474_y.func_74304_a(Options.FOV, (Float)this.fov.getValue());
      }

   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
         if (event.getSetting().equals(this.prefix)) {
            OyVey.commandManager.setPrefix((String)this.prefix.getPlannedValue());
            Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + OyVey.commandManager.getPrefix());
         }

         OyVey.colorManager.setColor((Integer)this.red.getPlannedValue(), (Integer)this.green.getPlannedValue(), (Integer)this.blue.getPlannedValue(), (Integer)this.hoverAlpha.getPlannedValue());
      }

   }

   public void onEnable() {
      mc.func_147108_a(OyVeyGui.getClickGui());
   }

   public void onLoad() {
      OyVey.colorManager.setColor((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.hoverAlpha.getValue());
      OyVey.commandManager.setPrefix((String)this.prefix.getValue());
   }

   public void onTick() {
      if (!(mc.field_71462_r instanceof OyVeyGui)) {
         this.disable();
      }

   }

   public static enum rainbowMode {
      Static,
      Sideway;
   }

   public static enum rainbowModeArray {
      Static,
      Up;
   }
}
