package net.lars.spacemod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.lars.spacemod.Spacemod.MOD_ID;

public class ModItems {
    public static final Item ROCKET_FLAME_SPAWNER = new RocketFlameSpawner(new Item.Settings().maxCount(1));

    public static void registerModItems() {
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "rocket_flame_spawner"), ROCKET_FLAME_SPAWNER);
    }
}
