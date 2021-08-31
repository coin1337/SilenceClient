package me.alpha432.oyvey.features.modules.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import org.apache.commons.io.IOUtils;

public class FakePlayer extends Module {
   private final String name = "NiggaHack.me";
   private EntityOtherPlayerMP _fakePlayer;

   public FakePlayer() {
      super("FakePlayer", "Spawns a FakePlayer for testing", Module.Category.PLAYER, false, false, false);
   }

   public static String getUuid(String name) {
      JsonParser parser = new JsonParser();
      String url = "https://api.mojang.com/users/profiles/minecraft/" + name;

      try {
         String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
         if (UUIDJson.isEmpty()) {
            return "invalid name";
         } else {
            JsonObject UUIDObject = (JsonObject)parser.parse(UUIDJson);
            return reformatUuid(UUIDObject.get("id").toString());
         }
      } catch (Exception var5) {
         var5.printStackTrace();
         return "error";
      }
   }

   private static String reformatUuid(String uuid) {
      String longUuid = "";
      longUuid = longUuid + uuid.substring(1, 9) + "-";
      longUuid = longUuid + uuid.substring(9, 13) + "-";
      longUuid = longUuid + uuid.substring(13, 17) + "-";
      longUuid = longUuid + uuid.substring(17, 21) + "-";
      longUuid = longUuid + uuid.substring(21, 33);
      return longUuid;
   }

   public void onEnable() {
      if (fullNullCheck()) {
         this.disable();
      } else {
         this._fakePlayer = null;
         if (mc.field_71439_g != null) {
            WorldClient var10003;
            UUID var10006;
            try {
               var10003 = mc.field_71441_e;
               this.getClass();
               var10006 = UUID.fromString(getUuid("NiggaHack.me"));
               this.getClass();
               this._fakePlayer = new EntityOtherPlayerMP(var10003, new GameProfile(var10006, "NiggaHack.me"));
            } catch (Exception var2) {
               var10003 = mc.field_71441_e;
               var10006 = UUID.fromString("70ee432d-0a96-4137-a2c0-37cc9df67f03");
               this.getClass();
               this._fakePlayer = new EntityOtherPlayerMP(var10003, new GameProfile(var10006, "NiggaHack.me"));
               Command.sendMessage("Failed to load uuid, setting another one.");
            }

            Object[] var10001 = new Object[1];
            this.getClass();
            var10001[0] = "NiggaHack.me";
            Command.sendMessage(String.format("%s has been spawned.", var10001));
            this._fakePlayer.func_82149_j(mc.field_71439_g);
            this._fakePlayer.field_70759_as = mc.field_71439_g.field_70759_as;
            mc.field_71441_e.func_73027_a(-100, this._fakePlayer);
         }

      }
   }

   public void onDisable() {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         super.onDisable();
         mc.field_71441_e.func_72900_e(this._fakePlayer);
      }

   }
}
