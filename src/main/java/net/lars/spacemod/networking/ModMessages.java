package net.lars.spacemod.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.lars.spacemod.networking.packet.FluidStorageSyncS2C;
import net.minecraft.util.Identifier;

import static net.lars.spacemod.Spacemod.MOD_ID;

public class ModMessages {
    public static final Identifier FLUID_STORAGE_SYNC = new Identifier(MOD_ID, "fluid_storage_sync");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(FLUID_STORAGE_SYNC, FluidStorageSyncS2C::receive);
    }
}
