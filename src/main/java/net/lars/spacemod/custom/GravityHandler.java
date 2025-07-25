package net.lars.spacemod.custom;

import net.lars.spacemod.dimension.ModDimensions;
import net.minecraft.server.network.ServerPlayerEntity;

public class GravityHandler {
    public static void handleGravity(ServerPlayerEntity player){
        if ( isPlayerInSpaceDimension(player) ) {
            setSpaceGravity(player);
        }
        else {
            setEarthGravity(player);
        }
    }

    public static boolean isPlayerInSpaceDimension(ServerPlayerEntity player) {
        return player.getWorld().getRegistryKey().equals(ModDimensions.SPACE_DIMENSION_KEY);
    }

    public static void setEarthGravity(ServerPlayerEntity player){
        if ( player.isCreative() ){return;}

        player.getAbilities().allowFlying = false;
        player.getAbilities().flying = false;
        player.setNoGravity(false);
        player.sendAbilitiesUpdate();
    }

    public static void setSpaceGravity(ServerPlayerEntity player){
        if ( player.isCreative() ){return;}

        player.getAbilities().allowFlying = true;
        player.getAbilities().flying = true;
        player.setNoGravity(true);
        player.sendAbilitiesUpdate();
    }

}
