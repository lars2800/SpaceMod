package net.lars.spacemod.entity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.lars.spacemod.fluid.custom.CustomFluidStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.lars.spacemod.fluid.ModFluids.STILL_MIXED_LIQUID_FUEL;
import static net.lars.spacemod.Spacemod.LOGGER;

public class FuelInserterBlockEntity extends BlockEntity {

    public final CustomFluidStorage liquidHydrogenFluidStorage = new CustomFluidStorage( FluidConstants.BUCKET * 1, Fluids.LAVA, this, 0);
    public final CustomFluidStorage liquidOxygenFluidStorage   = new CustomFluidStorage( FluidConstants.BUCKET * 1, Fluids.WATER, this, 1);
    public final CustomFluidStorage liquidMixtureFluidStorage  = new CustomFluidStorage( FluidConstants.BUCKET * 30, STILL_MIXED_LIQUID_FUEL, this, 2);

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        liquidOxygenFluidStorage.writeNbt(nbt);
        liquidHydrogenFluidStorage.writeNbt(nbt);
        liquidMixtureFluidStorage.writeNbt(nbt);
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        liquidOxygenFluidStorage.readNbt(nbt);
        liquidHydrogenFluidStorage.readNbt(nbt);
        liquidMixtureFluidStorage.readNbt(nbt);
    }

    //
    // Other
    //

    public FuelInserterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUEL_INSERTER, pos, state);
    }

    private void insertInto(CustomFluidStorage storage,long amountToInsert,FluidVariant variant){
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(
                    variant,
                    amountToInsert,
                    transaction
            );

            if (inserted == amountToInsert) {
                transaction.commit(); // All good
            } else {
                transaction.abort(); // Not enough space
            }
        }
    }

    private void extractOutOf(CustomFluidStorage storage,long amountToExtract,FluidVariant variant){
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = storage.extract(
                    variant,
                    amountToExtract,
                    transaction
            );

            if (extracted == amountToExtract) {
                transaction.commit();
            } else {
                transaction.abort();
            }

        }
    }

    private void processRecipe() {
        long liquidOxygenPerTick = FluidConstants.BUCKET * 1;
        long liquidHydrogenPerTick = liquidOxygenPerTick * 1;
        long liquidMixturePerTick = liquidOxygenPerTick * 20;

        boolean enoughOxygen   = liquidOxygenFluidStorage.getAmount() >= liquidOxygenPerTick;
        boolean enoughHydrogen = liquidHydrogenFluidStorage.getAmount() >= liquidHydrogenPerTick;

        // Check if there's enough room in the output tank
        long availableOutputSpace = liquidMixtureFluidStorage.getCapacity() - liquidMixtureFluidStorage.getAmount();
        boolean enoughSpaceForMixture = availableOutputSpace >= liquidMixturePerTick;

        if (enoughOxygen && enoughHydrogen && enoughSpaceForMixture) {
            insertInto(liquidMixtureFluidStorage, liquidMixturePerTick, FluidVariant.of(STILL_MIXED_LIQUID_FUEL));
            extractOutOf(liquidOxygenFluidStorage, liquidOxygenPerTick, FluidVariant.of(Fluids.WATER));
            extractOutOf(liquidHydrogenFluidStorage, liquidHydrogenPerTick, FluidVariant.of(Fluids.LAVA));
        }

        markDirty();
    }


    public static void tick(World world, BlockPos pos, BlockState state, FuelInserterBlockEntity be) {
        be.processRecipe();

    }


}
