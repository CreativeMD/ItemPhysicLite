package team.creative.itemphysiclite.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import team.creative.itemphysiclite.ItemEntityExtender;
import team.creative.itemphysiclite.ItemPhysicLite;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityExtender {
    
    public boolean vanillaRendered;
    
    public ItemEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }
    
    @Override
    public boolean vanillaRendered() {
        return vanillaRendered;
    }
    
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (level().isClientSide())
            vanillaRendered = ItemPhysicLite.CONFIG.vanillaRendered.canPass(level(), ((ItemEntity) (Entity) this).getItem());
    }
    
}
