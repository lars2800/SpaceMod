package net.lars.spacemod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.lars.spacemod.dimension.ModDimensions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class ModCommands {
    public static void registerModCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerSimpleCommand(dispatcher, "tpSpace", ModCommands::teleportToSpaceCallback);
        });
    }

    public static void registerSimpleCommand(CommandDispatcher<ServerCommandSource> dispatcher, String name, Callback callback) {
        dispatcher.register(CommandManager.literal(name)
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> callback.run(context.getSource(), context)));
    }

    public static int teleportToSpaceCallback(ServerCommandSource source, CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by a player."));
            return 0;
        }

        MinecraftServer server = player.getServer();
        if (server == null) {
            source.sendError(Text.literal("Server instance not found."));
            return 0;
        }

        ServerWorld targetWorld = server.getWorld(ModDimensions.SPACE_DIMENSION_KEY);
        if (targetWorld == null) {
            source.sendError(Text.literal("Target dimension not found or not loaded."));
            return 0;
        }

        try {
            player.teleport(targetWorld, player.getX(), 512, player.getZ(), player.getYaw(), player.getPitch());
        } catch (Exception e) {
            source.sendError(Text.literal("Teleportation failed: " + e.getMessage()));
            return 0;
        }

        return Command.SINGLE_SUCCESS;
    }

}

// Put this either inside the same file or in its own file
@FunctionalInterface
interface Callback {
    int run(ServerCommandSource source, CommandContext<ServerCommandSource> context);
}
