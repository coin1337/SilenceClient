package me.alpha432.oyvey.mixin.mixins;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import me.alpha432.oyvey.features.modules.misc.NoHitBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({EntityRenderer.class})
public class MixinEntityRenderer {
   @Redirect(
      method = {"getMouseOver"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"
)
   )
   public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
      return (List)(!NoHitBox.getINSTANCE().isOn() || (!(Minecraft.func_71410_x().field_71439_g.func_184614_ca().func_77973_b() instanceof ItemPickaxe) || !(Boolean)NoHitBox.getINSTANCE().pickaxe.getValue()) && (Minecraft.func_71410_x().field_71439_g.func_184614_ca().func_77973_b() != Items.field_185158_cP || !(Boolean)NoHitBox.getINSTANCE().crystal.getValue()) && (Minecraft.func_71410_x().field_71439_g.func_184614_ca().func_77973_b() != Items.field_151153_ao || !(Boolean)NoHitBox.getINSTANCE().gapple.getValue()) && Minecraft.func_71410_x().field_71439_g.func_184614_ca().func_77973_b() != Items.field_151033_d && Minecraft.func_71410_x().field_71439_g.func_184614_ca().func_77973_b() != Items.field_151142_bV ? worldClient.func_175674_a(entityIn, boundingBox, predicate) : new ArrayList());
   }
}
