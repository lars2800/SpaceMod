package net.lars.spacemod.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.lars.spacemod.block.FuelInserterBlock;
import net.lars.spacemod.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.lars.spacemod.Spacemod.MOD_ID;

public class ModBlockEntities {

    public static BlockEntityType<RocketEngineBlockEntity> ROCKET_ENGINE;
    public static BlockEntityType<FuelInserterBlockEntity> FUEL_INSERTER;
    public static BlockEntityType<TestMotorBlockEntity> TEST_MOTOR;


    public static void registerModBlockEntities(){

        ROCKET_ENGINE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(MOD_ID, "rocket_engine"),
                FabricBlockEntityTypeBuilder.create(RocketEngineBlockEntity::new, ModBlocks.ROCKET_ENGINE_BLOCK).build(null)
        );

        FUEL_INSERTER = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(MOD_ID, "fuel_inserter"),
                FabricBlockEntityTypeBuilder.create(FuelInserterBlockEntity::new, ModBlocks.FUEL_INSERTER_BLOCK).build(null)
        );

        TEST_MOTOR = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(MOD_ID, "test_motor"),
                FabricBlockEntityTypeBuilder.create(TestMotorBlockEntity::new, ModBlocks.TEST_MOTOR_BLOCK).build(null)
        );

        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.liquidMixtureFluidStorage, ROCKET_ENGINE);
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, side) -> {
            if (!(blockEntity instanceof FuelInserterBlockEntity)) return null;
            FuelInserterBlockEntity be = (FuelInserterBlockEntity) blockEntity;

            if (side == null) return null;

            return switch (side) {
                case DOWN -> be.liquidMixtureFluidStorage;
                case WEST  -> be.liquidHydrogenFluidStorage;
                case EAST  -> be.liquidOxygenFluidStorage;
                default    -> null;
            };
        }, ModBlockEntities.FUEL_INSERTER);
    }
}
