package team.creative.itemphysiclite;

import net.minecraft.world.entity.item.ItemEntity;

public interface ItemEntityRenderStateExtender {
    
    public boolean isBlock();
    
    public float getXRot();
    
    public float getYRot();
    
    public boolean hasAdditionalOffset();
    
    public void extractPhysic(ItemEntity item);
}
