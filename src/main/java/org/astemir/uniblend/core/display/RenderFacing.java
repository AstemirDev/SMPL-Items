package org.astemir.uniblend.core.display;

import net.minecraft.world.entity.Display;

public enum RenderFacing {
    FIXED(Display.BillboardConstraints.FIXED),
    HORIZONTAL(Display.BillboardConstraints.HORIZONTAL),
    VERTICAL(Display.BillboardConstraints.VERTICAL),
    CENTERED(Display.BillboardConstraints.CENTER);

    private Display.BillboardConstraints constraints;
    RenderFacing(Display.BillboardConstraints constraints) {
        this.constraints = constraints;
    }

    public Display.BillboardConstraints getConstraints() {
        return constraints;
    }
}
