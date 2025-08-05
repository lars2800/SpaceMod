package net.lars.spacemod;

import net.fabricmc.api.ModInitializer;
import net.lars.spacemod.block.ModBlocks;
import net.lars.spacemod.command.ModCommands;
import net.lars.spacemod.custom.GravityHandler;
import net.lars.spacemod.dimension.ModDimensions;
import net.lars.spacemod.entity.ModBlockEntities;
import net.lars.spacemod.fluid.ModFluids;
import net.lars.spacemod.item.ModItems;
import net.lars.spacemod.itemgroup.ModItemGroups;
import net.lars.spacemod.networking.ModMessages;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Spacemod implements ModInitializer {

    public static final String MOD_ID = "spacemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerModBlockEntities();
        ModDimensions.registerModDimensions();
        ModCommands.registerModCommands();
        ModMessages.registerS2CPackets();
        ModFluids.registerModFluids();
        ModDimensions.registerModDimensions();
        ModCommands.registerModCommands();
        ModItemGroups.registerModItemGroups();

        this.registerServerEvents();
    }

    public void registerServerEvents(){
        ServerTickEvents.END_SERVER_TICK.register(server -> {

            // Gravity handlers
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                GravityHandler.handleGravity(player);
            }

        });
    }


}
