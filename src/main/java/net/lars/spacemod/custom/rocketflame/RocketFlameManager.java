package net.lars.spacemod.custom.rocketflame;

import net.lars.spacemod.shader.FlameShaderManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

public class RocketFlameManager {
    private static RocketFlame[] activeFlames = new RocketFlame[64];

    // Public functions
    public static RocketFlame createNewFlame(Vec3d position,Vec3d direction){

        // Create a new flame
        RocketFlame newFlame = new RocketFlame(position,direction);

        // Loop over all curent flames
        for (int i = 0; i < activeFlames.length; i++) {
            // If the current index is empty, put the new flame there
            if ( activeFlames[i] == null ){
                activeFlames[i] = newFlame;
                // Return the flame, also return it
                return newFlame;
            }
        }

        return null;
    }

    public static void destroyFlame(RocketFlame flame){
        flame.isActive = false;
        flame.shouldRender = false;
    }

    public static void tickFlames(MinecraftClient client){

        if ( isWorldLoaded(client) ){

            // Loop over all flames
            for (int i = 0; i < activeFlames.length; i++) {

                // Check if the current index has a flame
                RocketFlame flame = activeFlames[i];
                if ( flame != null ){
                    if ( flame.isActive ){

                        flame.tick();
                        if ( flame.shouldRender ){
                            renderFlame(flame,i);
                        }

                    }
                    else {
                        // Flame is inactive, destroy it
                        activeFlames[i] = null;
                    }
                }

            }

        }
        else{
            cleanUp();
        }

    }

    // Private functions
    private static boolean isWorldLoaded(MinecraftClient client){
        return ( client.world != null );
    }

    private static void cleanUp(){
        Arrays.fill(activeFlames, null);
        FlameShaderManager.cleanUp();
    }

    private static void renderFlame(RocketFlame rocketFlame,int uniformIndex){

        // Every flame needs a little roatation else they wont render
        if ( rocketFlame.direction.getY() == 0 ){
            rocketFlame.direction = rocketFlame.direction.add(0.0f,0.001f,0.0f);
        }

        FlameShaderManager.u_rocket_flame_buffer[uniformIndex * 6]     = (float)rocketFlame.position.getX();
        FlameShaderManager.u_rocket_flame_buffer[uniformIndex * 6 + 1] = (float)rocketFlame.position.getY();
        FlameShaderManager.u_rocket_flame_buffer[uniformIndex * 6 + 2] = (float)rocketFlame.position.getZ();

        FlameShaderManager.u_rocket_flame_buffer[uniformIndex * 6 + 3] = (float)rocketFlame.direction.getX();
        FlameShaderManager.u_rocket_flame_buffer[uniformIndex * 6 + 4] = (float)rocketFlame.direction.getY();
        FlameShaderManager.u_rocket_flame_buffer[uniformIndex * 6 + 5] = (float)rocketFlame.direction.getZ();
    }
}
