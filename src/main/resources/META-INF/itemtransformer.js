function initializeCoreMod() {
	print("Init ItemPhysicLite coremods ...")
    return {
		'renderer': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.client.renderer.entity.ItemEntityRenderer',
				'methodName': 'm_7392_',
				'methodDesc': '(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V'
            },
            'transformer': function(method) {
				var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
				var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
				var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
				var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
				var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
				var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				
				var renderMethodname = asmapi.mapMethod("m_7392_");
				
				var start = method.instructions.getFirst();
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 2));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 3));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ILOAD, 6));
				
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/ItemEntityRenderer", asmapi.mapField("f_115019_"), "Lnet/minecraft/client/renderer/entity/ItemRenderer;"));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/ItemEntityRenderer", asmapi.mapField("f_115020_"), "Ljava/util/Random;"));
				
				method.instructions.insertBefore(start, asmapi.buildMethodCall("team/creative/itemphysiclite/ItemPhysicLite", "render", "(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/ItemRenderer;Ljava/util/Random;)Z", asmapi.MethodType.STATIC));
				
				method.instructions.insertBefore(start, new JumpInsnNode(Opcodes.IFEQ, start));
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 2));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 3));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ILOAD, 6));
				method.instructions.insertBefore(start, asmapi.buildMethodCall("net/minecraft/client/renderer/entity/EntityRenderer", renderMethodname, "(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", asmapi.MethodType.SPECIAL));
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new InsnNode(Opcodes.RETURN));
				
                return method;
            }
		}
    }
}
