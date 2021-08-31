package me.alpha432.oyvey.util;

import java.awt.Color;
import me.alpha432.oyvey.features.modules.client.ClickGui;

public class ColorUtil {
   public static int toARGB(int r, int g, int b, int a) {
      return (new Color(r, g, b, a)).getRGB();
   }

   public static int toRGBA(int r, int g, int b) {
      return toRGBA(r, g, b, 255);
   }

   public static int toRGBA(int r, int g, int b, int a) {
      return (r << 16) + (g << 8) + b + (a << 24);
   }

   public static int toRGBA(float r, float g, float b, float a) {
      return toRGBA((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), (int)(a * 255.0F));
   }

   public static Color rainbow(int delay) {
      double rainbowState = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0D);
      return Color.getHSBColor((float)((rainbowState %= 360.0D) / 360.0D), (Float)ClickGui.getInstance().rainbowSaturation.getValue() / 255.0F, (Float)ClickGui.getInstance().rainbowBrightness.getValue() / 255.0F);
   }

   public static int toRGBA(float[] colors) {
      if (colors.length != 4) {
         throw new IllegalArgumentException("colors[] must have a length of 4!");
      } else {
         return toRGBA(colors[0], colors[1], colors[2], colors[3]);
      }
   }

   public static int toRGBA(double[] colors) {
      if (colors.length != 4) {
         throw new IllegalArgumentException("colors[] must have a length of 4!");
      } else {
         return toRGBA((float)colors[0], (float)colors[1], (float)colors[2], (float)colors[3]);
      }
   }

   public static int toRGBA(Color color) {
      return toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
   }
}
