package net.lars.spacemod.entity;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestMotorBlockEntity extends GeneratingKineticBlockEntity {

    private float targetStress = 1024f; // desired stress generation
    private float speed = 64f;          // rotational speed

    public TestMotorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TEST_MOTOR, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();
        updateSpeedFromStress(); // just sets internal state
        updateGeneratedRotation(); // triggers onSpeedChanged()
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        // Safe to call now, network is available
        notifyStressCapacityChange(calculateAddedStressCapacity());
    }

    public void setTargetStress(float stress) {
        this.targetStress = stress;
        updateSpeedFromStress();
        notifyStressCapacityChange(calculateAddedStressCapacity());
        updateGeneratedRotation();
    }

    private void updateSpeedFromStress() {
        // Stress = Speed * Capacity  →  Capacity = Stress / Speed
        if (speed == 0) speed = 1; // avoid div-by-zero
    }

    @Override
    public float getGeneratedSpeed() {
        return speed;
    }

    @Override
    public float calculateAddedStressCapacity() {
        return targetStress / speed;
    }

    @Override
    public boolean isSource() {
        return true;
    }

    public static void tick(World world, BlockPos pos, BlockState state, TestMotorBlockEntity be) {
        // Optional: dynamic control logic here
    }
}