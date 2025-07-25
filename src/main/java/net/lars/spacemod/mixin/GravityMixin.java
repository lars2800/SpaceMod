package net.lars.spacemod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.lars.spacemod.Spacemod.MOD_ID;

@Mixin(Entity.class)
public abstract class GravityMixin {
    @Shadow
    public abstract World getWorld();

    @Inject(method = "tick", at = @At("HEAD"))
    private void disableGravityInCustomDimension(CallbackInfo ci) {
        Entity self = (Entity)(Object)this;

        if (getWorld().getRegistryKey().getValue().equals(new Identifier(MOD_ID, "space_dim"))) {

            if (!self.isPlayer()){
                self.setNoGravity(true); // Disable gravity
            }

        } else {
            self.setNoGravity(false); // Default behavior
        }
    }
}
