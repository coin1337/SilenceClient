package me.alpha432.oyvey.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

public class FriendManager extends Feature {
   private List<FriendManager.Friend> friends = new ArrayList();

   public FriendManager() {
      super("Friends");
   }

   public boolean isFriend(String name) {
      this.cleanFriends();
      return this.friends.stream().anyMatch((friend) -> {
         return friend.username.equalsIgnoreCase(name);
      });
   }

   public boolean isFriend(EntityPlayer player) {
      return this.isFriend(player.func_70005_c_());
   }

   public void addFriend(String name) {
      FriendManager.Friend friend = this.getFriendByName(name);
      if (friend != null) {
         this.friends.add(friend);
      }

      this.cleanFriends();
   }

   public void removeFriend(String name) {
      this.cleanFriends();
      Iterator var2 = this.friends.iterator();

      while(var2.hasNext()) {
         FriendManager.Friend friend = (FriendManager.Friend)var2.next();
         if (friend.getUsername().equalsIgnoreCase(name)) {
            this.friends.remove(friend);
            break;
         }
      }

   }

   public void onLoad() {
      this.friends = new ArrayList();
      this.clearSettings();
   }

   public void saveFriends() {
      this.clearSettings();
      this.cleanFriends();
      Iterator var1 = this.friends.iterator();

      while(var1.hasNext()) {
         FriendManager.Friend friend = (FriendManager.Friend)var1.next();
         this.register(new Setting(friend.getUuid().toString(), friend.getUsername()));
      }

   }

   public void cleanFriends() {
      this.friends.stream().filter(Objects::nonNull).filter((friend) -> {
         return friend.getUsername() != null;
      });
   }

   public List<FriendManager.Friend> getFriends() {
      this.cleanFriends();
      return this.friends;
   }

   public FriendManager.Friend getFriendByName(String input) {
      UUID uuid = PlayerUtil.getUUIDFromName(input);
      if (uuid != null) {
         FriendManager.Friend friend = new FriendManager.Friend(input, uuid);
         return friend;
      } else {
         return null;
      }
   }

   public void addFriend(FriendManager.Friend friend) {
      this.friends.add(friend);
   }

   public static class Friend {
      private final String username;
      private final UUID uuid;

      public Friend(String username, UUID uuid) {
         this.username = username;
         this.uuid = uuid;
      }

      public String getUsername() {
         return this.username;
      }

      public UUID getUuid() {
         return this.uuid;
      }
   }
}
