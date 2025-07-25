package net.lars.spacemod.fluid.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.lars.spacemod.networking.ModMessages;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class CustomFluidStorage extends SingleVariantStorage<FluidVariant> {
    private final long capacity;
    private final FlowableFluid fluidType;
    private final BlockEntity blockEntity;
    private int uniqueFluidStorageId; // This number is to identify a fluid storage apart from others on the same block entity it needs to be different from others on the same blockentity but the same across reloads ( no random )

    public CustomFluidStorage(long capacity, FlowableFluid fluidType, BlockEntity blockEntity, int id) {
        this.capacity = capacity;
        this.fluidType = fluidType;
        this.blockEntity = blockEntity;

        // This id is necessary for when using multiple Storages
        this.uniqueFluidStorageId = id;
    }

    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return this.capacity;
    }

    @Override
    protected void onFinalCommit() {
        this.blockEntity.markDirty();
        if(!this.blockEntity.getWorld().isClient()) {
            sendFluidPacket();
        }
    }

    @Override
    protected boolean canInsert(FluidVariant variant) {
        return variant.isOf( this.fluidType );
    }

    public void handleReceiveFluidPacket(int fluidStorageId,long fluidLevel){
        if ( fluidStorageId == this.uniqueFluidStorageId ){
            this.amount = fluidLevel;
        }
    }

    private void sendFluidPacket(){
        PacketByteBuf data = PacketByteBufs.create();

        data.writeBlockPos( this.blockEntity.getPos() );
        data.writeInt( this.uniqueFluidStorageId );
        data.writeLong( this.amount );

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) this.blockEntity.getWorld(), this.blockEntity.getPos())) {
            ServerPlayNetworking.send(player, ModMessages.FLUID_STORAGE_SYNC, data);
        }
    }

    public void writeNbt(NbtCompound nbt){
        nbt.put("fuelInserterBlock.fluidStorageVariant" + this.uniqueFluidStorageId, this.variant.toNbt());
        nbt.putLong("fuelInserterBlock.fluidStorageAmount" + this.uniqueFluidStorageId, this.amount);
    }

    public void readNbt(NbtCompound nbt){
        this.variant = FluidVariant.fromNbt(nbt.getCompound("fuelInserterBlock.fluidStorageVariant" + this.uniqueFluidStorageId));
        this.amount = nbt.getLong("fuelInserterBlock.fluidStorageAmount" + this.uniqueFluidStorageId);
    }
}