package net.lars.spacemod.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.lars.spacemod.fluid.custom.CustomFluidStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class FluidStorageSyncS2C {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos position = buf.readBlockPos();
        int fluidStorageId = buf.readInt();
        long fluidLevel = buf.readLong();

        client.execute(() -> {
            if (client.world == null) return;

            BlockEntity blockEntity = client.world.getBlockEntity(position);
            if (blockEntity == null) return;

            // Loop over all fields in the blockentity
            // If a variable is a custom fluid storage call it's on receive function
            for (var field : blockEntity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(blockEntity);
                    if (value instanceof CustomFluidStorage storage) {
                        storage.handleReceiveFluidPacket(fluidStorageId, fluidLevel);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
