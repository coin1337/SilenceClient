package me.alpha432.oyvey.mixin.mixins;

import com.mojang.authlib.GameProfile;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.player.TpsSync;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityPlayer.class})
public abstract class MixinEntityPlayer extends EntityLivingBase {
   public MixinEntityPlayer(World worldIn, GameProfile gameProfileIn) {
      super(worldIn);
   }

   @Inject(
      method = {"getCooldownPeriod"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getCooldownPeriodHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (TpsSync.getInstance().isOn() && (Boolean)TpsSync.getInstance().attack.getValue()) {
         callbackInfoReturnable.setReturnValue((float)(1.0D / ((EntityPlayer)EntityPlayer.class.cast(this)).func_110148_a(SharedMonsterAttributes.field_188790_f).func_111125_b() * 20.0D * (double)OyVey.serverManager.getTpsFactor()));
      }

   }
}
