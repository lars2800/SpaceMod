package net.lars.spacemod.block;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.lars.spacemod.entity.ModBlockEntities;
import net.lars.spacemod.entity.TestMotorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class TestMotorBlock extends DirectionalKineticBlock implements IBE<TestMotorBlockEntity> {

    public TestMotorBlock(Settings properties) {
        super(properties);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction preferred = this.getPreferredFacing(context);
        return (context.getPlayer() == null ) && preferred != null ? (BlockState)this.getDefaultState().with(FACING, preferred) : super.getPlacementState(context);
    }

    @Override
    public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
        return face == state.get(FACING);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.get(FACING)).getAxis();
    }

    public boolean hideStressImpact() {
        return true;
    }

    @Override
    public Class<TestMotorBlockEntity> getBlockEntityClass() {
        return TestMotorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TestMotorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.TEST_MOTOR;
    }
}
