package net.lars.spacemod.fluid;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.lars.spacemod.Spacemod.MOD_ID;

public class ModFluids {
    public static FlowableFluid STILL_MIXED_LIQUID_FUEL;
    public static FlowableFluid FLOWING_MIXED_LIQUID_FUEL;
    public static Block MIXED_LIQUID_FUEL_BLOCK;
    public static Item MIXED_LIQUID_FUEL_BUCKET;

    public static void registerModFluids(){
        STILL_MIXED_LIQUID_FUEL = Registry.register(Registries.FLUID,
                new Identifier(MOD_ID, "mixed_liquid_fuel"), new MixedLiquedFuelFluid.Still());
        FLOWING_MIXED_LIQUID_FUEL = Registry.register(Registries.FLUID,
                new Identifier(MOD_ID, "flowing_mixed_liquid_fuel"), new MixedLiquedFuelFluid.Flowing());

        MIXED_LIQUID_FUEL_BLOCK = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "mixed_liquid_fuel_block"),
                new FluidBlock(ModFluids.STILL_MIXED_LIQUID_FUEL, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        MIXED_LIQUID_FUEL_BUCKET = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "mixed_liquid_fuel_bucket"),
                new BucketItem(ModFluids.STILL_MIXED_LIQUID_FUEL, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
    }
}
