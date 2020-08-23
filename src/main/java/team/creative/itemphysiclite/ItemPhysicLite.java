package team.creative.itemphysiclite;

import java.lang.reflect.Field;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(value = ItemPhysicLite.MODID)
@EventBusSubscriber
public class ItemPhysicLite {
	
	public static final Logger LOGGER = LogManager.getLogger(ItemPhysicLite.MODID);
	
	public static final String MODID = "itemphysiclite";
	
	public ItemPhysicLite() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
	}
	
	private static Minecraft mc = Minecraft.getInstance();
	
	private void doClientStuff(final FMLClientSetupEvent event) {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		
	}
	
	public static long lastTickTime;
	
	@SubscribeEvent
	@OnlyIn(value = Dist.CLIENT)
	public void renderTick(RenderTickEvent event) {
		if (event.phase == Phase.END)
			lastTickTime = System.nanoTime();
	}
	
	public static boolean renderItem(ItemEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, net.minecraft.client.renderer.ItemRenderer itemRenderer, Random random) {
		if (entityIn.getAge() == 0)
			return false;
		
		matrixStackIn.push();
		ItemStack itemstack = entityIn.getItem();
		int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
		random.setSeed(i);
		IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(itemstack, entityIn.world, (LivingEntity) null);
		boolean flag = ibakedmodel.isGui3d();
		int j = getModelCount(itemstack);
		
		float rotateBy = (System.nanoTime() - lastTickTime) / 800000000000000F;
		if (mc.isGamePaused())
			rotateBy = 0;
		
		Vector3d motionMultiplier = getMotionMultiplier(entityIn);
		if (motionMultiplier != null && motionMultiplier.lengthSquared() > 0)
			rotateBy *= motionMultiplier.x * 0.2;
		
		matrixStackIn.rotate(Vector3f.XP.rotation((float) Math.PI / 2));
		matrixStackIn.rotate(Vector3f.ZP.rotation(entityIn.rotationYaw));
		
		boolean applyEffects = entityIn.getAge() != 0 && (flag || mc.getRenderManager().options != null);
		
		//Handle Rotations
		if (applyEffects) {
			if (flag) {
				if (!entityIn.func_233570_aj_()) {
					rotateBy *= 2;
					Fluid fluid = getFluid(entityIn);
					if (fluid == null)
						fluid = getFluid(entityIn, true);
					if (fluid != null)
						rotateBy /= fluid.getAttributes().getDensity() / 1000 * 10;
					
					entityIn.rotationPitch += rotateBy;
				}
			} else if (entityIn != null && !Double.isNaN(entityIn.getPosX()) && !Double.isNaN(entityIn.getPosY()) && !Double.isNaN(entityIn.getPosZ()) && entityIn.world != null) {
				if (entityIn.func_233570_aj_()) {
					if (!flag)
						entityIn.rotationPitch = 0;
				} else {
					rotateBy *= 2;
					Fluid fluid = getFluid(entityIn);
					if (fluid != null)
						rotateBy /= fluid.getAttributes().getDensity() / 1000 * 10;
					
					entityIn.rotationPitch += rotateBy;
				}
			}
			
			if (flag)
				matrixStackIn.translate(0, -0.2, -0.08);
			else if (entityIn.world.getBlockState(entityIn.func_233580_cy_()).getBlock() == Blocks.SNOW)
				matrixStackIn.translate(0, 0.0, -0.14);
			else
				matrixStackIn.translate(0, 0, -0.04);
			
			double height = 0.2;
			if (flag)
				matrixStackIn.translate(0, height, 0);
			matrixStackIn.rotate(Vector3f.YP.rotation(entityIn.rotationPitch));
			if (flag)
				matrixStackIn.translate(0, -height, 0);
		}
		
		if (!flag) {
			float f7 = -0.0F * (j - 1) * 0.5F;
			float f8 = -0.0F * (j - 1) * 0.5F;
			float f9 = -0.09375F * (j - 1) * 0.5F;
			matrixStackIn.translate(f7, f8, f9);
		}
		
		for (int k = 0; k < j; ++k) {
			matrixStackIn.push();
			if (k > 0) {
				if (flag) {
					float f11 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f13 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f10 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					matrixStackIn.translate(f11, f13, f10);
				}
			}
			
			itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
			matrixStackIn.pop();
			if (!flag)
				matrixStackIn.translate(0.0, 0.0, 0.05375F);
		}
		
		matrixStackIn.pop();
		return true;
	}
	
	public static Fluid getFluid(ItemEntity item) {
		return getFluid(item, false);
	}
	
	public static Fluid getFluid(ItemEntity item, boolean below) {
		if (item.world == null)
			return null;
		
		double d0 = item.getPosY();
		BlockPos pos = item.func_233580_cy_();
		if (below)
			pos = pos.down();
		
		FluidState state = item.world.getFluidState(pos);
		Fluid fluid = state.getFluid();
		
		if (fluid == null || fluid.getFluid().getAttributes().getDensity() == 0)
			return null;
		
		if (below)
			return fluid;
		
		double filled = state.getHeight();
		
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
	
	private static Field motionMultiplierField = null;
	
	public static Vector3d getMotionMultiplier(Entity entity) {
		if (motionMultiplierField == null)
			motionMultiplierField = ObfuscationReflectionHelper.findField(Entity.class, "field_213328_B");
		try {
			return (Vector3d) motionMultiplierField.get(entity);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
}
