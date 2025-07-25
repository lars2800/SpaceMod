package net.lars.spacemod.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.lars.spacemod.Spacemod.MOD_ID;
import static net.minecraft.block.Blocks.STONE;

public class ModBlocks {
    public static final Block ROCKET_ENGINE_BLOCK = new RocketEngineBlock(
            FabricBlockSettings.copyOf(STONE)
                    .strength(3.0f)
                    .nonOpaque()
    );

    public static final Block FUEL_INSERTER_BLOCK = new FuelInserterBlock(
            FabricBlockSettings.copyOf(STONE)
                    .strength(3.0f)
                    .nonOpaque()
    );

    public static final Block TEST_MOTOR_BLOCK = new TestMotorBlock(
            FabricBlockSettings.copyOf(STONE)
                    .strength(3.0f)
                    .nonOpaque()
    );

    public static void registerModBlocks() {

        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "rocket_engine_block"), ROCKET_ENGINE_BLOCK);
        Registry.register(Registries.ITEM,  new Identifier(MOD_ID, "rocket_engine_block"), new BlockItem(ROCKET_ENGINE_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "fuel_inserter_block"), FUEL_INSERTER_BLOCK);
        Registry.register(Registries.ITEM,  new Identifier(MOD_ID, "fuel_inserter_block"), new BlockItem(FUEL_INSERTER_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "test_motor_block"), TEST_MOTOR_BLOCK);
        Registry.register(Registries.ITEM,  new Identifier(MOD_ID, "test_motor_block"), new BlockItem(TEST_MOTOR_BLOCK, new Item.Settings()));
    }
}
