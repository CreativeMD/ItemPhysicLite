package team.creative.itemphysiclite;

import java.lang.reflect.Field;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

@Mod(value = ItemPhysicLite.MODID)
public class ItemPhysicLite {
    
    public static final Logger LOGGER = LogManager.getLogger(ItemPhysicLite.MODID);
    
    public static final String MODID = "itemphysiclite";
    
    public ItemPhysicLite() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }
    
    private static Minecraft mc = Minecraft.getInstance();
    
    private void doClientStuff(final FMLClientSetupEvent event) {
        ModLoadingContext.get()
                .registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public static long lastTickTime;
    
    @SubscribeEvent
    @OnlyIn(value = Dist.CLIENT)
    public void renderTick(RenderTickEvent event) {
        if (event.phase == Phase.END)
            lastTickTime = System.nanoTime();
    }
    
    public static boolean render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight, ItemRenderer itemRenderer, Random rand) {
        if (entity.getAge() == 0)
            return false;
        
        pose.pushPose();
        ItemStack itemstack = entity.getItem();
        rand.setSeed(itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue());
        BakedModel bakedmodel = itemRenderer.getModel(itemstack, entity.level, (LivingEntity) null, entity.getId());
        boolean flag = bakedmodel.isGui3d();
        int j = getModelCount(itemstack);
        
        float rotateBy = (System.nanoTime() - lastTickTime) / 200000000F;
        if (mc.isPaused())
            rotateBy = 0;
        
        Vec3 motionMultiplier = getStuckSpeedMultiplier(entity);
        if (motionMultiplier != null && motionMultiplier.lengthSqr() > 0)
            rotateBy *= motionMultiplier.x * 0.2;
        
        pose.mulPose(Vector3f.XP.rotation((float) Math.PI / 2));
        pose.mulPose(Vector3f.ZP.rotation(entity.getYRot()));
        
        boolean applyEffects = entity.getAge() != 0 && (flag || mc.options != null);
        
        //Handle Rotations
        if (applyEffects) {
            if (flag) {
                if (!entity.isOnGround()) {
                    rotateBy *= 2;
                    Fluid fluid = getFluid(entity);
                    if (fluid == null)
                        fluid = getFluid(entity, true);
                    if (fluid != null)
                        rotateBy /= fluid.getAttributes().getDensity() / 1000 * 10;
                    
                    entity.setXRot(entity.getXRot() + rotateBy);
                }
            } else if (entity != null && !Double.isNaN(entity.getX()) && !Double.isNaN(entity.getY()) && !Double.isNaN(entity.getZ()) && entity.level != null) {
                if (entity.isOnGround()) {
                    if (!flag)
                        entity.setXRot(0);
                } else {
                    rotateBy *= 2;
                    Fluid fluid = getFluid(entity);
                    if (fluid != null)
                        rotateBy /= fluid.getAttributes().getDensity() / 1000 * 10;
                    
                    entity.setXRot(entity.getXRot() + rotateBy);
                }
            }
            
            if (flag)
                pose.translate(0, -0.2, -0.08);
            else if (entity.level.getBlockState(entity.blockPosition()).getBlock() == Blocks.SNOW || entity.level.getBlockState(entity.blockPosition().below())
                    .getBlock() == Blocks.SOUL_SAND)
                pose.translate(0, 0.0, -0.14);
            else
                pose.translate(0, 0, -0.04);
            
            double height = 0.2;
            if (flag)
                pose.translate(0, height, 0);
            pose.mulPose(Vector3f.YP.rotation(entity.getXRot()));
            if (flag)
                pose.translate(0, -height, 0);
        }
        
        if (!flag) {
            float f7 = -0.0F * (j - 1) * 0.5F;
            float f8 = -0.0F * (j - 1) * 0.5F;
            float f9 = -0.09375F * (j - 1) * 0.5F;
            pose.translate(f7, f8, f9);
        }
        
        for (int k = 0; k < j; ++k) {
            pose.pushPose();
            if (k > 0) {
                if (flag) {
                    float f11 = (rand.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f13 = (rand.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f10 = (rand.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    pose.translate(f11, f13, f10);
                }
            }
            
            itemRenderer.render(itemstack, ItemTransforms.TransformType.GROUND, false, pose, buffer, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);
            pose.popPose();
            if (!flag)
                pose.translate(0.0, 0.0, 0.09375F); // pose.translate(0.0, 0.0, 0.05375F);
                
        }
        
        pose.popPose();
        return true;
    }
    
    public static Fluid getFluid(ItemEntity item) {
        return getFluid(item, false);
    }
    
    public static Fluid getFluid(ItemEntity item, boolean below) {
        if (item.level == null)
            return null;
        
        double d0 = item.position().y;
        BlockPos pos = item.blockPosition();
        if (below)
            pos = pos.below();
        
        FluidState state = item.level.getFluidState(pos);
        Fluid fluid = state.getType();
        
        if (fluid == null || fluid.getAttributes().getDensity() == 0)
            return null;
        
        if (below)
            return fluid;
        
        double filled = state.getHeight(item.level, pos);
        
        if (d0 - pos.getY() - 0.2 <= filled)
            return fluid;
        return null;
    }
    
    public static int getModelCount(ItemStack stack) {
        
        if (stack.getCount() > 48)
            return 5;
        if (stack.getCount() > 32)
            return 4;
        if (stack.getCount() > 16)
            return 3;
        if (stack.getCount() > 1)
            return 2;
        
        return 1;
    }
    
    private static Field stuckSpeedMultiplierField = null;
    
    public static Vec3 getStuckSpeedMultiplier(Entity entity) {
        if (stuckSpeedMultiplierField == null)
            stuckSpeedMultiplierField = ObfuscationReflectionHelper.findField(Entity.class, "f_19865_");
        try {
            return (Vec3) stuckSpeedMultiplierField.get(entity);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return null;
        }
    }
}
