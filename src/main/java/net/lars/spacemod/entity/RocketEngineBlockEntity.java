package net.lars.spacemod.entity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.lars.spacemod.fluid.custom.CustomFluidStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.lars.spacemod.fluid.ModFluids.STILL_MIXED_LIQUID_FUEL;

public class RocketEngineBlockEntity extends BlockEntity {

    //
    // Fluid handling
    //
    public final CustomFluidStorage liquidMixtureFluidStorage  = new CustomFluidStorage( FluidConstants.BUCKET * 3, STILL_MIXED_LIQUID_FUEL, this, 0);

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        liquidMixtureFluidStorage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        liquidMixtureFluidStorage.readNbt(nbt);
    }

    //
    // Other
    //

    public RocketEngineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROCKET_ENGINE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, RocketEngineBlockEntity be) {
    }
}
