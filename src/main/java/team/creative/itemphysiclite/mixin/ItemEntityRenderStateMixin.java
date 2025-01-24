package team.creative.itemphysiclite.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.item.ItemEntity;
import team.creative.itemphysiclite.ClientPhysic;
import team.creative.itemphysiclite.ItemEntityRenderStateExtender;
import team.creative.itemphysiclite.ItemPhysicLite;

@Mixin(ItemEntityRenderState.class)
public class ItemEntityRenderStateMixin implements ItemEntityRenderStateExtender {
    
    @Unique
    public float rotX;
    @Unique
    public float rotY;
    @Unique
    public boolean skipRendering;
    @Unique
    public boolean additionalOffset;
    
    @Override
    public float getXRot() {
        return rotX;
    }
    
    @Override
    public float getYRot() {
        return rotY;
    }
    
    @Override
    public boolean hasAdditionalOffset() {
        return additionalOffset;
    }
    
    @Override
    public void extractPhysic(ItemEntity entity) {
        ClientPhysic.calculateRotation(entity, (ItemEntityRenderState) (Object) this);
        additionalOffset = ItemPhysicLite.CONFIG.blockRequireOffset.is(entity.level().getBlockState(entity.blockPosition())) || ItemPhysicLite.CONFIG.blockBelowRequireOffset.is(
            entity.level().getBlockState(entity.blockPosition().below()));
        rotX = entity.getXRot();
        rotY = entity.getYRot();
    }
    
}
