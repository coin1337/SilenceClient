package me.alpha432.oyvey.features.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.gui.components.Component;
import me.alpha432.oyvey.features.gui.components.items.Item;
import me.alpha432.oyvey.features.gui.components.items.buttons.ModuleButton;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class OyVeyGui extends GuiScreen {
   private static OyVeyGui oyveyGui;
   private static OyVeyGui INSTANCE = new OyVeyGui();
   private final ArrayList<Component> components = new ArrayList();

   public OyVeyGui() {
      this.setInstance();
      this.load();
   }

   public static OyVeyGui getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new OyVeyGui();
      }

      return INSTANCE;
   }

   public static OyVeyGui getClickGui() {
      return getInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   private void load() {
      int x = -84;
      Iterator var2 = OyVey.moduleManager.getCategories().iterator();

      while(var2.hasNext()) {
         final Module.Category category = (Module.Category)var2.next();
         ArrayList var10000 = this.components;
         String var10004 = category.getName();
         x += 90;
         var10000.add(new Component(var10004, x, 4, true) {
            public void setupItems() {
               counter1 = new int[]{1};
               OyVey.moduleManager.getModulesByCategory(category).forEach((module) -> {
                  if (!module.hidden) {
                     this.addButton(new ModuleButton(module));
                  }

               });
            }
         });
      }

      this.components.forEach((components) -> {
         components.getItems().sort(Comparator.comparing(Feature::getName));
      });
   }

   public void updateModule(Module module) {
      Iterator var2 = this.components.iterator();

      while(var2.hasNext()) {
         Component component = (Component)var2.next();
         Iterator var4 = component.getItems().iterator();

         while(var4.hasNext()) {
            Item item = (Item)var4.next();
            if (item instanceof ModuleButton) {
               ModuleButton button = (ModuleButton)item;
               Module mod = button.getModule();
               if (module != null && module.equals(mod)) {
                  button.initSettings();
               }
            }
         }
      }

   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      this.checkMouseWheel();
      this.func_146276_q_();
      this.components.forEach((components) -> {
         components.drawScreen(mouseX, mouseY, partialTicks);
      });
   }

   public void func_73864_a(int mouseX, int mouseY, int clickedButton) {
      this.components.forEach((components) -> {
         components.mouseClicked(mouseX, mouseY, clickedButton);
      });
   }

   public void func_146286_b(int mouseX, int mouseY, int releaseButton) {
      this.components.forEach((components) -> {
         components.mouseReleased(mouseX, mouseY, releaseButton);
      });
   }

   public boolean func_73868_f() {
      return false;
   }

   public final ArrayList<Component> getComponents() {
      return this.components;
   }

   public void checkMouseWheel() {
      int dWheel = Mouse.getDWheel();
      if (dWheel < 0) {
         this.components.forEach((component) -> {
            component.setY(component.getY() - 10);
         });
      } else if (dWheel > 0) {
         this.components.forEach((component) -> {
            component.setY(component.getY() + 10);
         });
      }

   }

   public int getTextOffset() {
      return -6;
   }

   public Component getComponentByName(String name) {
      Iterator var2 = this.components.iterator();

      Component component;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         component = (Component)var2.next();
      } while(!component.getName().equalsIgnoreCase(name));

      return component;
   }

   public void func_73869_a(char typedChar, int keyCode) throws IOException {
      super.func_73869_a(typedChar, keyCode);
      this.components.forEach((component) -> {
         component.onKeyTyped(typedChar, keyCode);
      });
   }
}
