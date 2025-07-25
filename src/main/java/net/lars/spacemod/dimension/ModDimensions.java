package net.lars.spacemod.dimension;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import static net.lars.spacemod.Spacemod.MOD_ID;

public class ModDimensions {
    public static final RegistryKey<World> SPACE_DIMENSION_KEY = RegistryKey.of(
            RegistryKeys.WORLD,
            new Identifier(MOD_ID, "space_dim")
    );

    public static final RegistryKey<DimensionType> SPACE_DIMENSION_TYPE_KEY = RegistryKey.of(
            RegistryKeys.DIMENSION_TYPE,
            new Identifier(MOD_ID, "space_dim")
    );

    public static void registerModDimensions(){

        DimensionRenderingRegistry.registerDimensionEffects(
                new Identifier(MOD_ID, "space_dim"),
                new SpaceDimensionEffects()
        );

    }
}