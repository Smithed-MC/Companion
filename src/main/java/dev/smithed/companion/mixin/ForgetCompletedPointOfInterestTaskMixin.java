package dev.smithed.companion.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.ForgetCompletedPointOfInterestTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ForgetCompletedPointOfInterestTask.class)
public abstract class ForgetCompletedPointOfInterestTaskMixin {
    @Shadow @Final private MemoryModuleType<GlobalPos> memoryModule;

    @Inject(method = "shouldRun", at = @At("HEAD"), cancellable = true)
    public void rejectNamedBarrel(ServerWorld world, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        // excuse me if this looks like ass it makes it more readable i find
        // plus it all gets crunched on runtime

        GlobalPos globalPos = entity.getBrain().getOptionalMemory(this.memoryModule).get();

        // Check for barrel
        if (world.getBlockState(globalPos.getPos()) == Blocks.BARREL.getDefaultState()) {
            // check if barrel contains barrel block entity
            if (world.getBlockEntity(globalPos.getPos()) instanceof BarrelBlockEntity) {
                // check for name :)
                if (((BarrelBlockEntity) Objects.requireNonNull(world.getBlockEntity(globalPos.getPos()))).getName().getString().startsWith("§f")) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}