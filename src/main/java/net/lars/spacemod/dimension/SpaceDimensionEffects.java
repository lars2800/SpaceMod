package net.lars.spacemod.dimension;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;

public class SpaceDimensionEffects extends DimensionEffects {

    public SpaceDimensionEffects(){
        super(0, false, SkyType.NONE, false, false);
    }

    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        return Vec3d.ZERO;
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return false;
    }
}
