package team.creative.itemphysiclite.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.renderer.item.ItemStackRenderState;

@Mixin(ItemStackRenderState.class)
public interface ItemStackRenderStateAccessor {
    
    @Invoker
    public ItemStackRenderState.LayerRenderState callFirstLayer();
    
}
