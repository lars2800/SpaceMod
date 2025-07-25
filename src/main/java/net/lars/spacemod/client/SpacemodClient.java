package net.lars.spacemod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.lars.spacemod.custom.rocketflame.RocketFlameManager;
import net.lars.spacemod.fluid.ModFluids;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class SpacemodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        registerClientFluids();

        registerClientTick();
    }

    public void registerClientFluids(){
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_MIXED_LIQUID_FUEL, ModFluids.FLOWING_MIXED_LIQUID_FUEL,
                new SimpleFluidRenderHandler(
                        new Identifier("minecraft:block/water_still"),
                        new Identifier("minecraft:block/water_flow"),
                        0x01dffeff // 0x Alpha R G B ( each 2charchters )
                ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_MIXED_LIQUID_FUEL, ModFluids.FLOWING_MIXED_LIQUID_FUEL);
    }


    public void registerClientTick(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            RocketFlameManager.tickFlames(client);
        });
    }
}