package team.creative.itemphysiclite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.ICreativeLoader;
import team.creative.creativecore.client.ClientLoader;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;
import team.creative.itemphysiclite.mixin.EntityAccessor;
import team.creative.itemphysiclite.mixin.ItemStackRenderStateAccessor;
import team.creative.itemphysiclite.mixin.LayerRenderStateAccessor;

@Mod(value = ItemPhysicLite.MODID, dist = Dist.CLIENT)
public class ItemPhysicLite implements ClientLoader {
    
    public static final Logger LOGGER = LogManager.getLogger(ItemPhysicLite.MODID);
    public static final String MODID = "itemphysiclite";
    public static ItemPhysicLiteConfig CONFIG;
    public static long lastTickTime;
    private static final double RANDOM_Y_OFFSET_SCALE = 0.05 / (Math.PI * 2);
    
    public static boolean submit(ItemEntityRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera, RandomSource rand) {
        if (state.ageInTicks < 1)
            return false;
        
        pose.pushPose();
        
        rand.setSeed(state.seed);
        int j = getModelCount(state.count);
        boolean gui3d = ((ItemEntityRenderStateExtender) state).isBlock();
        var transform = ((LayerRenderStateAccessor) ((ItemStackRenderStateAccessor) state.item).callFirstLayer()).getTransform();
        
        pose.mulPose(com.mojang.math.Axis.XP.rotation((float) Math.PI / 2));
        pose.mulPose(com.mojang.math.Axis.ZP.rotation(((ItemEntityRenderStateExtender) state).getYRot()));
        
        var mc = Minecraft.getInstance();
        if (state.ageInTicks != 0 && (gui3d || mc.options != null)) {
            if (gui3d)
                pose.translate(0, -0.2, -0.08);
            else if (((ItemEntityRenderStateExtender) state).hasAdditionalOffset())
                pose.translate(0, 0.0, -0.14 - state.bobOffset * RANDOM_Y_OFFSET_SCALE);
            else
                pose.translate(0, 0, -0.04 - state.bobOffset * RANDOM_Y_OFFSET_SCALE);
            
            double height = transform.scale().y();
            if (gui3d)
                pose.translate(0, height, 0);
            pose.mulPose(com.mojang.math.Axis.YP.rotation(((ItemEntityRenderStateExtender) state).getXRot()));
            if (gui3d)
                pose.translate(0, -height, 0);
        }
        
        if (!gui3d) {
            float f7 = -0.0F * (j - 1) * 0.5F;
            float f8 = -0.0F * (j - 1) * 0.5F;
            float f9 = -0.09375F * (j - 1) * 0.5F;
            pose.translate(f7, f8, f9);
        }
        
        float f = transform.scale().x();
        float f1 = transform.scale().y();
        float f2 = transform.scale().z();
        
        for (int k = 0; k < j; ++k) {
            pose.pushPose();
            if (k > 0) {
                if (gui3d) {
                    float f11 = (rand.nextFloat() * 2.0F - 1.0F) * f;
                    float f13 = (rand.nextFloat() * 2.0F - 1.0F) * f1;
                    float f10 = (rand.nextFloat() * 2.0F - 1.0F) * f2;
                    pose.translate(f11, f13, f10);
                }
            }
            
            state.item.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            pose.popPose();
            if (!gui3d)
                pose.translate(0.0F * f, 0.0F * f1, 0.09375F * f2);
        }
        
        pose.popPose();
        return true;
    }
    
    public static int getModelCount(int count) {
        if (count > 48)
            return 5;
        if (count > 32)
            return 4;
        if (count > 16)
            return 3;
        if (count > 1)
            return 2;
        
        return 1;
    }
    
    public static Fluid calculateFluid(ItemEntity item) {
        return calculateFluid(item, false);
    }
    
    public static Fluid calculateFluid(ItemEntity item, boolean below) {
        if (item.level() == null)
            return null;
        
        double d0 = item.position().y;
        BlockPos pos = item.blockPosition();
        if (below)
            pos = pos.below();
        
        FluidState state = item.level().getFluidState(pos);
        Fluid fluid = state.getType();
        if (fluid == null || fluid.getTickDelay(item.level()) == 0) {
            return null;
        }
        
        if (below)
            return fluid;
        
        double filled = state.getHeight(item.level(), pos);
        
        if (d0 - pos.getY() - 0.2 <= filled)
            return fluid;
        return null;
    }
    
    public static Vec3 getStuckSpeedMultiplier(Entity entity) {
        return ((EntityAccessor) entity).getStuckSpeedMultiplier();
    }
    
    public static float getViscosity(Fluid fluid, Level level) {
        if (fluid == null)
            return 0;
        return CreativeCore.loader().getFluidViscosityMultiplier(fluid, level);
    }
    
    public ItemPhysicLite() {
        ICreativeLoader loader = CreativeCore.loader();
        loader.registerClient(this);
    }
    
    @Override
    public void onInitializeClient() {
        ICreativeLoader loader = CreativeCore.loader();
        loader.registerClientRenderGui(x -> lastTickTime = System.nanoTime());
        
        CreativeConfigRegistry.ROOT.registerValue(MODID, CONFIG = new ItemPhysicLiteConfig(), ConfigSynchronization.CLIENT, false);
    }
}
