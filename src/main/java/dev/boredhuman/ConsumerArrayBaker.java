package dev.boredhuman;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.function.Consumer;

/**
 * This unrolls the primitive array and calls each consumer in the given array sequentially
 */
public class ConsumerArrayBaker extends ClassLoader {
	public Consumer bake(Consumer[] tasks, BakeTypes bakeType, VariableStorage variableStorage) {
		ClassNode classNode = new ClassNode();
		classNode.name = "dev/boredhuman/ConsumerBakedArray";
		classNode.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
		classNode.superName = Type.getInternalName(Object.class);
		classNode.interfaces.add(Type.getInternalName(Consumer.class));
		classNode.version = 52;

		Type consumerArray = Type.getType(Consumer[].class);

		FieldNode field;
		if (variableStorage == VariableStorage.VIRTUAL) {
			field = new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "tasks", consumerArray.getDescriptor(), null, null);
		} else if (variableStorage == VariableStorage.STATIC) {
			field = new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, "tasks", consumerArray.getDescriptor(), null, null);
		} else {
			throw new RuntimeException("Unsupported variable storage " + variableStorage);
		}

		classNode.fields.add(field);

		MethodNode constructor = new MethodNode();
		constructor.name = "<init>";
		constructor.access = Opcodes.ACC_PUBLIC;
		constructor.desc = Type.getMethodType(Type.VOID_TYPE, consumerArray).getDescriptor();

		constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		constructor.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V"));
		constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

		if ((field.access & Opcodes.ACC_STATIC) != 0) {
			constructor.instructions.add(new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, "tasks", consumerArray.getDescriptor()));
		} else {
			constructor.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, classNode.name, "tasks", consumerArray.getDescriptor()));
		}

		constructor.instructions.add(new InsnNode(Opcodes.RETURN));

		classNode.methods.add(constructor);

		MethodNode acceptMethod = new MethodNode();
		acceptMethod.name = "accept";
		acceptMethod.desc = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class));
		acceptMethod.access = Opcodes.ACC_PUBLIC;

		InsnList acceptInstructions = acceptMethod.instructions;

		acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		if ((field.access & Opcodes.ACC_STATIC) != 0) {
			acceptInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, "tasks", consumerArray.getDescriptor()));
		} else {
			acceptInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, "tasks", consumerArray.getDescriptor()));
		}

		if (bakeType == BakeTypes.DUP) {
			for (int i = 0, len = tasks.length; i < len; i++) {
				if (i != len - 1) {
					acceptInstructions.add(new InsnNode(Opcodes.DUP));
				}

				acceptInstructions.add(new IntInsnNode(Opcodes.BIPUSH, i));
				acceptInstructions.add(new InsnNode(Opcodes.AALOAD));
				acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				acceptInstructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Type.getInternalName(Consumer.class), "accept", acceptMethod.desc, true));
			}
		} else if (bakeType == BakeTypes.LOCAL) {
			acceptInstructions.add(new VarInsnNode(Opcodes.ASTORE, 2));

			for (int i = 0, len = tasks.length; i < len; i++) {
				acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
				acceptInstructions.add(new IntInsnNode(Opcodes.BIPUSH, i));
				acceptInstructions.add(new InsnNode(Opcodes.AALOAD));
				acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				acceptInstructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Type.getInternalName(Consumer.class), "accept", acceptMethod.desc, true));
			}
		} else if (bakeType == BakeTypes.DUP_UPFRONT) {
			int size = 1;
			for (int i = tasks.length - 1; i >= 0;) {
				// need to make sure there are atleast 2 that we can dup
				if (i > 1 && size > 1) {
					acceptInstructions.add(new InsnNode(Opcodes.DUP2));
					i -= 2;
					size += 2;
				} else {
					acceptInstructions.add(new InsnNode(Opcodes.DUP));
					i -= 1;
					size += 1;
				}
			}

			for (int i = 0, len = tasks.length; i < len; i++) {
				acceptInstructions.add(new IntInsnNode(Opcodes.BIPUSH, i));
				acceptInstructions.add(new InsnNode(Opcodes.AALOAD));
				acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				acceptInstructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Type.getInternalName(Consumer.class), "accept", acceptMethod.desc, true));
			}
		} else {
			throw new RuntimeException(String.format("Unsupported bake type %s\n", bakeType));
		}

		acceptInstructions.add(new InsnNode(Opcodes.RETURN));

		classNode.methods.add(acceptMethod);

		try {
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(classWriter);

			Class<?> klass = this.defineClass(classWriter.toByteArray());

			return (Consumer) klass.getConstructor(Consumer[].class).newInstance((Object) tasks);
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}

	private Class<?> defineClass(byte[] classBytes) {
		return this.defineClass(null, classBytes, 0, classBytes.length);
	}
}
