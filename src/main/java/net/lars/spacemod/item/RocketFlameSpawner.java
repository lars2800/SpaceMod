package net.lars.spacemod.item;

import net.lars.spacemod.custom.rocketflame.RocketFlameManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.block.Blocks.AIR;

public class RocketFlameSpawner extends Item {
    public RocketFlameSpawner(Settings settings) {
        super(settings);
    }

    // Makes the item glow like an enchanted item
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        // Only run on client
        if ( !context.getWorld().isClient() ){ return ActionResult.SUCCESS; }

        // Find position and spawn flame
        BlockPos clickedBlockPos = context.getBlockPos();

        if ( context.getWorld().getBlockState(clickedBlockPos) != AIR.getDefaultState()){
            RocketFlameManager.createNewFlame( clickedBlockPos );
        }

        return ActionResult.SUCCESS;
    }
}
