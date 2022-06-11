package team.creative.itemphysiclite.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("stuckSpeedMultiplier")
    Vec3 getStuckSpeedMultiplier();
}
