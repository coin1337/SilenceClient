package me.alpha432.oyvey.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ClientEvent;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.TextUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {
   private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
   private static final ItemStack totem;
   private static HUD INSTANCE;
   private final Setting<Boolean> grayNess = this.register(new Setting("Gray", true));
   private final Setting<Boolean> renderingUp = this.register(new Setting("RenderingUp", false, "Orientation of the HUD-Elements."));
   private final Setting<Boolean> waterMark = this.register(new Setting("Watermark", false, "displays watermark"));
   private final Setting<Boolean> arrayList = this.register(new Setting("ActiveModules", false, "Lists the active modules."));
   private final Setting<Boolean> coords = this.register(new Setting("Coords", false, "Your current coordinates"));
   private final Setting<Boolean> direction = this.register(new Setting("Direction", false, "The Direction you are facing."));
   private final Setting<Boolean> armor = this.register(new Setting("Armor", false, "ArmorHUD"));
   private final Setting<Boolean> totems = this.register(new Setting("Totems", false, "TotemHUD"));
   private final Setting<Boolean> greeter = this.register(new Setting("Welcomer", false, "The time"));
   private final Setting<Boolean> speed = this.register(new Setting("Speed", false, "Your Speed"));
   private final Setting<Boolean> potions = this.register(new Setting("Potions", false, "Your Speed"));
   private final Setting<Boolean> ping = this.register(new Setting("Ping", false, "Your response time to the server."));
   private final Setting<Boolean> tps = this.register(new Setting("TPS", false, "Ticks per second of the server."));
   private final Setting<Boolean> fps = this.register(new Setting("FPS", false, "Your frames per second."));
   private final Setting<Boolean> lag = this.register(new Setting("LagNotifier", false, "The time"));
   private final Timer timer = new Timer();
   private final Map<String, Integer> players = new HashMap();
   public Setting<String> command = this.register(new Setting("Command", "Silence"));
   public Setting<TextUtil.Color> bracketColor;
   public Setting<TextUtil.Color> commandColor;
   public Setting<String> commandBracket;
   public Setting<String> commandBracket2;
   public Setting<Boolean> notifyToggles;
   public Setting<Boolean> magenDavid;
   public Setting<Integer> animationHorizontalTime;
   public Setting<Integer> animationVerticalTime;
   public Setting<HUD.RenderingMode> renderingMode;
   public Setting<Integer> waterMarkY;
   public Setting<Boolean> time;
   public Setting<Integer> lagTime;
   private int color;
   private boolean shouldIncrement;
   private int hitMarkerTimer;

   public HUD() {
      super("HUD", "HUD Elements rendered on your screen", Module.Category.CLIENT, true, false, false);
      this.bracketColor = this.register(new Setting("BracketColor", TextUtil.Color.LIGHT_PURPLE));
      this.commandColor = this.register(new Setting("NameColor", TextUtil.Color.WHITE));
      this.commandBracket = this.register(new Setting("Bracket", ""));
      this.commandBracket2 = this.register(new Setting("Bracket2", " >>"));
      this.notifyToggles = this.register(new Setting("ChatNotify", true, "notifys in chat"));
      this.magenDavid = this.register(new Setting("MagenDavid", true, "draws magen david"));
      this.animationHorizontalTime = this.register(new Setting("AnimationHTime", 500, 1, 1000, (v) -> {
         return (Boolean)this.arrayList.getValue();
      }));
      this.animationVerticalTime = this.register(new Setting("AnimationVTime", 50, 1, 500, (v) -> {
         return (Boolean)this.arrayList.getValue();
      }));
      this.renderingMode = this.register(new Setting("Ordering", HUD.RenderingMode.ABC));
      this.waterMarkY = this.register(new Setting("WatermarkPosY", 2, 0, 20, (v) -> {
         return (Boolean)this.waterMark.getValue();
      }));
      this.time = this.register(new Setting("Time", false, "The time"));
      this.lagTime = this.register(new Setting("LagTime", 1000, 0, 2000));
      this.setInstance();
   }

   public static HUD getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new HUD();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public void onUpdate() {
      if (this.shouldIncrement) {
         ++this.hitMarkerTimer;
      }

      if (this.hitMarkerTimer == 10) {
         this.hitMarkerTimer = 0;
         this.shouldIncrement = false;
      }

   }

   public void onRender2D(Render2DEvent event) {
      if (!fullNullCheck()) {
         int width = this.renderer.scaledWidth;
         int height = this.renderer.scaledHeight;
         this.color = ColorUtil.toRGBA((Integer)ClickGui.getInstance().red.getValue(), (Integer)ClickGui.getInstance().green.getValue(), (Integer)ClickGui.getInstance().blue.getValue());
         int posX;
         int posY;
         int var10002;
         if ((Boolean)this.waterMark.getValue()) {
            String string = (String)this.command.getPlannedValue() + " v0.0.2";
            if ((Boolean)ClickGui.getInstance().rainbow.getValue()) {
               if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                  this.renderer.drawString(string, 2.0F, (float)(Integer)this.waterMarkY.getValue(), ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
               } else {
                  int[] arrayOfInt = new int[]{1};
                  char[] stringToCharArray = string.toCharArray();
                  float f = 0.0F;
                  char[] var8 = stringToCharArray;
                  posX = stringToCharArray.length;

                  for(posY = 0; posY < posX; ++posY) {
                     char c = var8[posY];
                     this.renderer.drawString(String.valueOf(c), 2.0F + f, (float)(Integer)this.waterMarkY.getValue(), ColorUtil.rainbow(arrayOfInt[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                     f += (float)this.renderer.getStringWidth(String.valueOf(c));
                     var10002 = arrayOfInt[0]++;
                  }
               }
            } else {
               this.renderer.drawString(string, 2.0F, (float)(Integer)this.waterMarkY.getValue(), this.color, true);
            }
         }

         int[] counter1 = new int[]{1};
         int j = mc.field_71462_r instanceof GuiChat && !(Boolean)this.renderingUp.getValue() ? 14 : 0;
         String fpsText;
         if ((Boolean)this.arrayList.getValue()) {
            int k;
            String str;
            Module module;
            if ((Boolean)this.renderingUp.getValue()) {
               if (this.renderingMode.getValue() == HUD.RenderingMode.ABC) {
                  for(k = 0; k < OyVey.moduleManager.sortedModulesABC.size(); ++k) {
                     str = (String)OyVey.moduleManager.sortedModulesABC.get(k);
                     this.renderer.drawString(str, (float)(width - 2 - this.renderer.getStringWidth(str)), (float)(2 + j * 10), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                     ++j;
                     var10002 = counter1[0]++;
                  }
               } else {
                  for(k = 0; k < OyVey.moduleManager.sortedModules.size(); ++k) {
                     module = (Module)OyVey.moduleManager.sortedModules.get(k);
                     fpsText = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? "" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                     this.renderer.drawString(fpsText, (float)(width - 2 - this.renderer.getStringWidth(fpsText)), (float)(2 + j * 10), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                     ++j;
                     var10002 = counter1[0]++;
                  }
               }
            } else if (this.renderingMode.getValue() == HUD.RenderingMode.ABC) {
               for(k = 0; k < OyVey.moduleManager.sortedModulesABC.size(); ++k) {
                  str = (String)OyVey.moduleManager.sortedModulesABC.get(k);
                  j += 10;
                  this.renderer.drawString(str, (float)(width - 2 - this.renderer.getStringWidth(str)), (float)(height - j), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                  var10002 = counter1[0]++;
               }
            } else {
               for(k = 0; k < OyVey.moduleManager.sortedModules.size(); ++k) {
                  module = (Module)OyVey.moduleManager.sortedModules.get(k);
                  fpsText = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                  j += 10;
                  this.renderer.drawString(fpsText, (float)(width - 2 - this.renderer.getStringWidth(fpsText)), (float)(height - j), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                  var10002 = counter1[0]++;
               }
            }
         }

         String grayString = (Boolean)this.grayNess.getValue() ? String.valueOf(ChatFormatting.GRAY) : "";
         int i = mc.field_71462_r instanceof GuiChat && (Boolean)this.renderingUp.getValue() ? 13 : ((Boolean)this.renderingUp.getValue() ? -2 : 0);
         ArrayList effects;
         Iterator var38;
         String str1;
         PotionEffect potionEffect;
         String str;
         if ((Boolean)this.renderingUp.getValue()) {
            if ((Boolean)this.potions.getValue()) {
               effects = new ArrayList(Minecraft.func_71410_x().field_71439_g.func_70651_bq());
               var38 = effects.iterator();

               while(var38.hasNext()) {
                  potionEffect = (PotionEffect)var38.next();
                  str = OyVey.potionManager.getColoredPotionString(potionEffect);
                  i += 10;
                  this.renderer.drawString(str, (float)(width - this.renderer.getStringWidth(str) - 2), (float)(height - 2 - i), potionEffect.func_188419_a().func_76401_j(), true);
               }
            }

            if ((Boolean)this.speed.getValue()) {
               fpsText = grayString + "Speed " + ChatFormatting.WHITE + OyVey.speedManager.getSpeedKpH() + " km/h";
               i += 10;
               this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(height - 2 - i), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
               var10002 = counter1[0]++;
            }

            if ((Boolean)this.time.getValue()) {
               fpsText = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
               i += 10;
               this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(height - 2 - i), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
               var10002 = counter1[0]++;
            }

            if ((Boolean)this.tps.getValue()) {
               fpsText = grayString + "TPS " + ChatFormatting.WHITE + OyVey.serverManager.getTPS();
               i += 10;
               this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(height - 2 - i), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
               var10002 = counter1[0]++;
            }

            fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.field_71470_ab;
            str1 = grayString + "Ping " + ChatFormatting.WHITE + OyVey.serverManager.getPing();
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
               if ((Boolean)this.ping.getValue()) {
                  i += 10;
                  this.renderer.drawString(str1, (float)(width - this.renderer.getStringWidth(str1) - 2), (float)(height - 2 - i), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                  var10002 = counter1[0]++;
               }

               if ((Boolean)this.fps.getValue()) {
                  i += 10;
                  this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(height - 2 - i), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                  var10002 = counter1[0]++;
               }
            } else {
               if ((Boolean)this.fps.getValue()) {
                  i += 10;
                  this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(height - 2 - i), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                  var10002 = counter1[0]++;
               }

               if ((Boolean)this.ping.getValue()) {
                  i += 10;
                  this.renderer.drawString(str1, (float)(width - this.renderer.getStringWidth(str1) - 2), (float)(height - 2 - i), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                  var10002 = counter1[0]++;
               }
            }
         } else {
            if ((Boolean)this.potions.getValue()) {
               effects = new ArrayList(Minecraft.func_71410_x().field_71439_g.func_70651_bq());
               var38 = effects.iterator();

               while(var38.hasNext()) {
                  potionEffect = (PotionEffect)var38.next();
                  str = OyVey.potionManager.getColoredPotionString(potionEffect);
                  this.renderer.drawString(str, (float)(width - this.renderer.getStringWidth(str) - 2), (float)(2 + i++ * 10), potionEffect.func_188419_a().func_76401_j(), true);
               }
            }

            if ((Boolean)this.speed.getValue()) {
               fpsText = grayString + "Speed " + ChatFormatting.WHITE + OyVey.speedManager.getSpeedKpH() + " km/h";
               this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(2 + i++ * 10), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
               var10002 = counter1[0]++;
            }

            if ((Boolean)this.time.getValue()) {
               fpsText = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
               this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(2 + i++ * 10), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
               var10002 = counter1[0]++;
            }

            if ((Boolean)this.tps.getValue()) {
               fpsText = grayString + "TPS " + ChatFormatting.WHITE + OyVey.serverManager.getTPS();
               this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(2 + i++ * 10), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
               var10002 = counter1[0]++;
            }

            fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.field_71470_ab;
            str1 = grayString + "Ping " + ChatFormatting.WHITE + OyVey.serverManager.getPing();
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
               if ((Boolean)this.ping.getValue()) {
                  this.renderer.drawString(str1, (float)(width - this.renderer.getStringWidth(str1) - 2), (float)(2 + i++ * 10), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                  var10002 = counter1[0]++;
               }

               if ((Boolean)this.fps.getValue()) {
                  this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(2 + i++ * 10), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                  var10002 = counter1[0]++;
               }
            } else {
               if ((Boolean)this.fps.getValue()) {
                  this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(2 + i++ * 10), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                  var10002 = counter1[0]++;
               }

               if ((Boolean)this.ping.getValue()) {
                  this.renderer.drawString(str1, (float)(width - this.renderer.getStringWidth(str1) - 2), (float)(2 + i++ * 10), (Boolean)ClickGui.getInstance().rainbow.getValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                  var10002 = counter1[0]++;
               }
            }
         }

         boolean inHell = mc.field_71441_e.func_180494_b(mc.field_71439_g.func_180425_c()).func_185359_l().equals("Hell");
         posX = (int)mc.field_71439_g.field_70165_t;
         posY = (int)mc.field_71439_g.field_70163_u;
         int posZ = (int)mc.field_71439_g.field_70161_v;
         float nether = !inHell ? 0.125F : 8.0F;
         int hposX = (int)(mc.field_71439_g.field_70165_t * (double)nether);
         int hposZ = (int)(mc.field_71439_g.field_70161_v * (double)nether);
         i = mc.field_71462_r instanceof GuiChat ? 14 : 0;
         String coordinates = ChatFormatting.WHITE + "XYZ " + ChatFormatting.RESET + (inHell ? posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]" + ChatFormatting.RESET : posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]");
         String direction = (Boolean)this.direction.getValue() ? OyVey.rotationManager.getDirection4D(false) : "";
         String coords = (Boolean)this.coords.getValue() ? coordinates : "";
         i += 10;
         if ((Boolean)ClickGui.getInstance().rainbow.getValue()) {
            String rainbowCoords = (Boolean)this.coords.getValue() ? "XYZ " + (inHell ? posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]" : posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]") : "";
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
               this.renderer.drawString(direction, 2.0F, (float)(height - i - 11), ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
               this.renderer.drawString(rainbowCoords, 2.0F, (float)(height - i), ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            } else {
               int[] counter2 = new int[]{1};
               char[] stringToCharArray = direction.toCharArray();
               float s = 0.0F;
               char[] var22 = stringToCharArray;
               int var23 = stringToCharArray.length;

               for(int var24 = 0; var24 < var23; ++var24) {
                  char c = var22[var24];
                  this.renderer.drawString(String.valueOf(c), 2.0F + s, (float)(height - i - 11), ColorUtil.rainbow(counter2[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                  s += (float)this.renderer.getStringWidth(String.valueOf(c));
                  var10002 = counter2[0]++;
               }

               int[] counter3 = new int[]{1};
               char[] stringToCharArray2 = rainbowCoords.toCharArray();
               float u = 0.0F;
               char[] var47 = stringToCharArray2;
               int var26 = stringToCharArray2.length;

               for(int var27 = 0; var27 < var26; ++var27) {
                  char c = var47[var27];
                  this.renderer.drawString(String.valueOf(c), 2.0F + u, (float)(height - i), ColorUtil.rainbow(counter3[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                  u += (float)this.renderer.getStringWidth(String.valueOf(c));
                  var10002 = counter3[0]++;
               }
            }
         } else {
            this.renderer.drawString(direction, 2.0F, (float)(height - i - 11), this.color, true);
            this.renderer.drawString(coords, 2.0F, (float)(height - i), this.color, true);
         }

         if ((Boolean)this.armor.getValue()) {
            this.renderArmorHUD(true);
         }

         if ((Boolean)this.totems.getValue()) {
            this.renderTotemHUD();
         }

         if ((Boolean)this.greeter.getValue()) {
            this.renderGreeter();
         }

         if ((Boolean)this.lag.getValue()) {
            this.renderLag();
         }

      }
   }

   public Map<String, Integer> getTextRadarPlayers() {
      return EntityUtil.getTextRadarPlayers();
   }

   public void renderGreeter() {
      int width = this.renderer.scaledWidth;
      String text = "";
      if ((Boolean)this.greeter.getValue()) {
         text = text + MathUtil.getTimeOfDay() + mc.field_71439_g.getDisplayNameString();
      }

      if ((Boolean)ClickGui.getInstance().rainbow.getValue()) {
         if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
            this.renderer.drawString(text, (float)width / 2.0F - (float)this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
         } else {
            int[] counter1 = new int[]{1};
            char[] stringToCharArray = text.toCharArray();
            float i = 0.0F;
            char[] var6 = stringToCharArray;
            int var7 = stringToCharArray.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               char c = var6[var8];
               this.renderer.drawString(String.valueOf(c), (float)width / 2.0F - (float)this.renderer.getStringWidth(text) / 2.0F + 2.0F + i, 2.0F, ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
               i += (float)this.renderer.getStringWidth(String.valueOf(c));
               int var10002 = counter1[0]++;
            }
         }
      } else {
         this.renderer.drawString(text, (float)width / 2.0F - (float)this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, this.color, true);
      }

   }

   public void renderLag() {
      int width = this.renderer.scaledWidth;
      if (OyVey.serverManager.isServerNotResponding()) {
         String text = ChatFormatting.RED + "Server not responding " + MathUtil.round((float)OyVey.serverManager.serverRespondingTime() / 1000.0F, 1) + "s.";
         this.renderer.drawString(text, (float)width / 2.0F - (float)this.renderer.getStringWidth(text) / 2.0F + 2.0F, 20.0F, this.color, true);
      }

   }

   public void renderTotemHUD() {
      int width = this.renderer.scaledWidth;
      int height = this.renderer.scaledHeight;
      int totems = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
         return itemStack.func_77973_b() == Items.field_190929_cY;
      }).mapToInt(ItemStack::func_190916_E).sum();
      if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY) {
         totems += mc.field_71439_g.func_184592_cb().func_190916_E();
      }

      if (totems > 0) {
         GlStateManager.func_179098_w();
         int i = width / 2;
         int iteration = false;
         int y = height - 55 - (mc.field_71439_g.func_70090_H() && mc.field_71442_b.func_78763_f() ? 10 : 0);
         int x = i - 189 + 180 + 2;
         GlStateManager.func_179126_j();
         RenderUtil.itemRender.field_77023_b = 200.0F;
         RenderUtil.itemRender.func_180450_b(totem, x, y);
         RenderUtil.itemRender.func_180453_a(mc.field_71466_p, totem, x, y, "");
         RenderUtil.itemRender.field_77023_b = 0.0F;
         GlStateManager.func_179098_w();
         GlStateManager.func_179140_f();
         GlStateManager.func_179097_i();
         this.renderer.drawStringWithShadow(totems + "", (float)(x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (float)(y + 9), 16777215);
         GlStateManager.func_179126_j();
         GlStateManager.func_179140_f();
      }

   }

   public void renderArmorHUD(boolean percent) {
      int width = this.renderer.scaledWidth;
      int height = this.renderer.scaledHeight;
      GlStateManager.func_179098_w();
      int i = width / 2;
      int iteration = 0;
      int y = height - 55 - (mc.field_71439_g.func_70090_H() && mc.field_71442_b.func_78763_f() ? 10 : 0);
      Iterator var7 = mc.field_71439_g.field_71071_by.field_70460_b.iterator();

      while(var7.hasNext()) {
         ItemStack is = (ItemStack)var7.next();
         ++iteration;
         if (!is.func_190926_b()) {
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.func_179126_j();
            RenderUtil.itemRender.field_77023_b = 200.0F;
            RenderUtil.itemRender.func_180450_b(is, x, y);
            RenderUtil.itemRender.func_180453_a(mc.field_71466_p, is, x, y, "");
            RenderUtil.itemRender.field_77023_b = 0.0F;
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            String s = is.func_190916_E() > 1 ? is.func_190916_E() + "" : "";
            this.renderer.drawStringWithShadow(s, (float)(x + 19 - 2 - this.renderer.getStringWidth(s)), (float)(y + 9), 16777215);
            if (percent) {
               float green = (float)((is.func_77958_k() - is.func_77952_i()) / is.func_77958_k());
               float red = 1.0F - green;
               int dmg = 100 - (int)(red * 100.0F);
               this.renderer.drawStringWithShadow(dmg + "", (float)(x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (float)(y - 11), ColorUtil.toRGBA((int)(red * 255.0F), (int)(green * 255.0F), 0));
            }
         }
      }

      GlStateManager.func_179126_j();
      GlStateManager.func_179140_f();
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(AttackEntityEvent event) {
      this.shouldIncrement = true;
   }

   public void onLoad() {
      OyVey.commandManager.setClientMessage(this.getCommandMessage());
   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (event.getStage() == 2 && this.equals(event.getSetting().getFeature())) {
         OyVey.commandManager.setClientMessage(this.getCommandMessage());
      }

   }

   public String getCommandMessage() {
      return TextUtil.coloredString((String)this.commandBracket.getPlannedValue(), (TextUtil.Color)this.bracketColor.getPlannedValue()) + TextUtil.coloredString((String)this.command.getPlannedValue(), (TextUtil.Color)this.commandColor.getPlannedValue()) + TextUtil.coloredString((String)this.commandBracket2.getPlannedValue(), (TextUtil.Color)this.bracketColor.getPlannedValue());
   }

   public void drawTextRadar(int yOffset) {
      if (!this.players.isEmpty()) {
         int y = this.renderer.getFontHeight() + 7 + yOffset;

         int textheight;
         for(Iterator var3 = this.players.entrySet().iterator(); var3.hasNext(); y += textheight) {
            Entry<String, Integer> player = (Entry)var3.next();
            String text = (String)player.getKey() + " ";
            textheight = this.renderer.getFontHeight() + 1;
            this.renderer.drawString(text, 2.0F, (float)y, this.color, true);
         }
      }

   }

   static {
      totem = new ItemStack(Items.field_190929_cY);
      INSTANCE = new HUD();
   }

   public static enum RenderingMode {
      Length,
      ABC;
   }
}
