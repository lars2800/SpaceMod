package net.lars.spacemod.custom.rocketflame;

import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RocketFlame {

    public Vec3d position;

    public RocketFlame(BlockPos flamePosition){
        this.position = new Vec3d(
                flamePosition.getX(),
                flamePosition.getY() + 3.0,
                flamePosition.getZ()
        );
    }

    public void tick(MinecraftClient client){
        World world = client.player.getWorld();
        if ( world != null ){
            world.addParticle(ParticleTypes.FLAME,position.x, position.y, position.z, 0.0, -0.1, 0.0 );
        }
    }
}
