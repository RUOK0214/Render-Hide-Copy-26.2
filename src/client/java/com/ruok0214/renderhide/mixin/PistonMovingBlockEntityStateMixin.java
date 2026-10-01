package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.MovingPistonStates;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
abstract class PistonMovingBlockEntityStateMixin {
    // Chunk packet data can load the moved state AFTER setBlockEntity installed
    // a default instance. Publish again once its actual state is available.
    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void renderhide$publishLoadedState(ValueInput input, CallbackInfo ci) {
        PistonMovingBlockEntity piston = (PistonMovingBlockEntity) (Object) this;
        MovingPistonStates.put(piston.getLevel(), piston);
    }
}
