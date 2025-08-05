package net.lars.spacemod.custom.rocketflame;

import net.minecraft.util.math.Vec3d;

public class RocketFlame {

    public Vec3d position;
    public Vec3d direction;
    public boolean isActive = true;
    public boolean shouldRender = true;

    public RocketFlame(Vec3d flamePosition,Vec3d flameDirection){
        this.position = flamePosition;
        this.direction = flameDirection;
    }

    public void tick(){
    }
}
