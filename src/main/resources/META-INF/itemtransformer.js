function initializeCoreMod() {
	print("Init ItemPhysicLite coremods ...")
    return {
        'renderer': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.client.renderer.entity.ItemRenderer',
				'methodName': 'func_225623_a_',
				'methodDesc': '(Lnet/minecraft/entity/item/ItemEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V'
            },
            'transformer': function(method) {
				var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
				var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
				var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
				var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
				var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
				var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				
				var renderMethodname = asmapi.mapMethod("func_225623_a_");
				
				var start = method.instructions.getFirst();
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 2));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 3));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ILOAD, 6));
				
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/ItemRenderer", "itemRenderer", "Lnet/minecraft/client/renderer/ItemRenderer;"));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/ItemRenderer", "random", "Ljava/util/Random;"));
				
				method.instructions.insertBefore(start, asmapi.buildMethodCall("team/creative/itemphysiclite/ItemPhysicLite", "renderItem", "(Lnet/minecraft/entity/item/ItemEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/renderer/ItemRenderer;Ljava/util/Random;)Z", asmapi.MethodType.STATIC));
				
				method.instructions.insertBefore(start, new JumpInsnNode(Opcodes.IFEQ, start));
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 2));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 3));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ILOAD, 6));
				method.instructions.insertBefore(start, asmapi.buildMethodCall("net/minecraft/client/renderer/entity/EntityRenderer", renderMethodname, "(Lnet/minecraft/entity/Entity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", asmapi.MethodType.SPECIAL));
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new InsnNode(Opcodes.RETURN));
                return method;
            }
		}
    }
}
