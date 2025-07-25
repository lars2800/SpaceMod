package net.lars.spacemod.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

// Thank you KapenJoe !
// https://github.com/Tutorials-By-Kaupenjoe/Fabric-Tutorial-1.19/blob/26-fluidHandling/src/main/java/net/kaupenjoe/tutorialmod/util/FluidStack.java

public class FluidStack {
    public FluidVariant fluidVariant;
    public long amount;

    public FluidStack(FluidVariant variant, long amount) {
        this.fluidVariant = variant;
        this.amount = amount;
    }

    public FluidVariant getFluidVariant() {
        return fluidVariant;
    }

    public void setFluidVariant(FluidVariant fluidVariant) {
        this.fluidVariant = fluidVariant;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public static long convertDropletsToMb(long droplets) {
        return (droplets / 81);
    }

    public static long convertMbToDroplets(long mb) {
        return mb * 81;
    }
}