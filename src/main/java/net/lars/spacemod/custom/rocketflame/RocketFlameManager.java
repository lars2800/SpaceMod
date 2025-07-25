package net.lars.spacemod.custom.rocketflame;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class RocketFlameManager {
    public static RocketFlame[] activeFlames = new RocketFlame[64];

    public static void createNewFlame(BlockPos position){
        RocketFlame newFlame = new RocketFlame(position);

        for (int i = 0; i < activeFlames.length; i++) {
            if ( activeFlames[i] == null ){
                activeFlames[i] = newFlame;
                break;
            }
        }
    }

    public static void tickFlames(MinecraftClient client){

        for (RocketFlame activeFlame : activeFlames) {
            if (activeFlame != null) {
                activeFlame.tick(client);
            }
        }
    }
}
