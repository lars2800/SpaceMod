package net.lars.spacemod.itemgroup;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.lars.spacemod.block.ModBlocks;
import net.lars.spacemod.fluid.ModFluids;
import net.lars.spacemod.item.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static ItemGroup SPACEMOD_TAB;

    public static void registerModItemGroups(){

        SPACEMOD_TAB = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier("spacemod", "spacemod_tab"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemgroup.spacemod_tab"))
                        .icon(() -> new ItemStack(ModBlocks.ROCKET_ENGINE_BLOCK))
                        .entries((context, entries) -> {

                            entries.add(ModBlocks.ROCKET_ENGINE_BLOCK);
                            entries.add(ModFluids.MIXED_LIQUID_FUEL_BUCKET);
                            entries.add(ModBlocks.FUEL_INSERTER_BLOCK);
                            entries.add(ModItems.ROCKET_FLAME_SPAWNER);

                        })
                        .build()
        );

    }
}
