package me.alpha432.oyvey.features.modules.misc;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ToolTips extends Module {
   private static final ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
   private static ToolTips INSTANCE = new ToolTips();
   public Map<EntityPlayer, ItemStack> spiedPlayers = new ConcurrentHashMap();
   public Map<EntityPlayer, Timer> playerTimers = new ConcurrentHashMap();
   private int textRadarY = 0;

   public ToolTips() {
      super("ShulkerViewer", "Several tweaks for tooltips.", Module.Category.MISC, true, false, false);
      this.setInstance();
   }

   public static ToolTips getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ToolTips();
      }

      return INSTANCE;
   }

   public static void displayInv(ItemStack stack, String name) {
      try {
         Item item = stack.func_77973_b();
         TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
         ItemShulkerBox shulker = (ItemShulkerBox)item;
         entityBox.field_145854_h = shulker.func_179223_d();
         entityBox.func_145834_a(mc.field_71441_e);
         ItemStackHelper.func_191283_b(stack.func_77978_p().func_74775_l("BlockEntityTag"), entityBox.field_190596_f);
         entityBox.func_145839_a(stack.func_77978_p().func_74775_l("BlockEntityTag"));
         entityBox.func_190575_a(name == null ? stack.func_82833_r() : name);
         (new Thread(() -> {
            try {
               Thread.sleep(200L);
            } catch (InterruptedException var2) {
            }

            mc.field_71439_g.func_71007_a(entityBox);
         })).start();
      } catch (Exception var5) {
      }

   }

   private void setInstance() {
      INSTANCE = this;
   }

   public void onUpdate() {
      if (!fullNullCheck()) {
         Iterator var1 = mc.field_71441_e.field_73010_i.iterator();

         while(var1.hasNext()) {
            EntityPlayer player = (EntityPlayer)var1.next();
            if (player != null && player.func_184614_ca().func_77973_b() instanceof ItemShulkerBox && mc.field_71439_g != player) {
               ItemStack stack = player.func_184614_ca();
               this.spiedPlayers.put(player, stack);
            }
         }

      }
   }

   public void onRender2D(Render2DEvent event) {
      if (!fullNullCheck()) {
         int x = -3;
         int y = 124;
         this.textRadarY = 0;
         Iterator var4 = mc.field_71441_e.field_73010_i.iterator();

         while(true) {
            EntityPlayer player;
            Timer playerTimer;
            do {
               do {
                  if (!var4.hasNext()) {
                     return;
                  }

                  player = (EntityPlayer)var4.next();
               } while(this.spiedPlayers.get(player) == null);

               if (player.func_184614_ca() != null && player.func_184614_ca().func_77973_b() instanceof ItemShulkerBox) {
                  if (player.func_184614_ca().func_77973_b() instanceof ItemShulkerBox && (playerTimer = (Timer)this.playerTimers.get(player)) != null) {
                     playerTimer.reset();
                     this.playerTimers.put(player, playerTimer);
                  }
                  break;
               }

               playerTimer = (Timer)this.playerTimers.get(player);
               if (playerTimer == null) {
                  Timer timer = new Timer();
                  timer.reset();
                  this.playerTimers.put(player, timer);
                  break;
               }
            } while(playerTimer.passedS(3.0D));

            ItemStack stack = (ItemStack)this.spiedPlayers.get(player);
            this.renderShulkerToolTip(stack, x, y, player.func_70005_c_());
            y += 78;
            this.textRadarY = y - 10 - 114 + 2;
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void makeTooltip(ItemTooltipEvent event) {
   }

   public void renderShulkerToolTip(ItemStack stack, int x, int y, String name) {
      NBTTagCompound tagCompound = stack.func_77978_p();
      NBTTagCompound blockEntityTag;
      if (tagCompound != null && tagCompound.func_150297_b("BlockEntityTag", 10) && (blockEntityTag = tagCompound.func_74775_l("BlockEntityTag")).func_150297_b("Items", 9)) {
         GlStateManager.func_179098_w();
         GlStateManager.func_179140_f();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         mc.func_110434_K().func_110577_a(SHULKER_GUI_TEXTURE);
         RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
         RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 57, 500);
         RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
         GlStateManager.func_179097_i();
         Color color = new Color((Integer)ClickGui.getInstance().red.getValue(), (Integer)ClickGui.getInstance().green.getValue(), (Integer)ClickGui.getInstance().blue.getValue(), 200);
         this.renderer.drawStringWithShadow(name == null ? stack.func_82833_r() : name, (float)(x + 8), (float)(y + 6), ColorUtil.toRGBA(color));
         GlStateManager.func_179126_j();
         RenderHelper.func_74520_c();
         GlStateManager.func_179091_B();
         GlStateManager.func_179142_g();
         GlStateManager.func_179145_e();
         NonNullList nonnulllist = NonNullList.func_191197_a(27, ItemStack.field_190927_a);
         ItemStackHelper.func_191283_b(blockEntityTag, nonnulllist);

         for(int i = 0; i < nonnulllist.size(); ++i) {
            int iX = x + i % 9 * 18 + 8;
            int iY = y + i / 9 * 18 + 18;
            ItemStack itemStack = (ItemStack)nonnulllist.get(i);
            mc.func_175597_ag().field_178112_h.field_77023_b = 501.0F;
            RenderUtil.itemRender.func_180450_b(itemStack, iX, iY);
            RenderUtil.itemRender.func_180453_a(mc.field_71466_p, itemStack, iX, iY, (String)null);
            mc.func_175597_ag().field_178112_h.field_77023_b = 0.0F;
         }

         GlStateManager.func_179140_f();
         GlStateManager.func_179084_k();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      }

   }
}
