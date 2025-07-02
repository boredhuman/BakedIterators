package dev.boredhuman;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * This unrolls the primitive array and calls each runnable in the given array sequentially
 */
public class ArrayBaker extends ClassLoader {
	public Runnable bake(Runnable[] tasks) {
		try {
			ClassNode classNode = new ClassNode();
			classNode.name = "dev/boredhuman/BakedArray";
			classNode.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
			classNode.superName = Type.getInternalName(Object.class);
			classNode.interfaces.add(Type.getInternalName(Runnable.class));
			classNode.version = 52;

			Type runnableArray = Type.getType(Runnable[].class);

			FieldNode field = new FieldNode(Opcodes.ACC_PUBLIC, "tasks", runnableArray.getDescriptor(), null, null);

			classNode.fields.add(field);

			MethodNode constructor = new MethodNode();
			constructor.name = "<init>";
			constructor.access = Opcodes.ACC_PUBLIC;
			constructor.desc = Type.getMethodType(Type.VOID_TYPE, runnableArray).getDescriptor();

			constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			constructor.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V"));
			constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
			constructor.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, classNode.name, "tasks", runnableArray.getDescriptor()));
			constructor.instructions.add(new InsnNode(Opcodes.RETURN));

			classNode.methods.add(constructor);

			MethodNode run = new MethodNode();
			run.name = "run";
			run.desc = "()V";
			run.access = Opcodes.ACC_PUBLIC;

			run.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			run.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, "tasks", runnableArray.getDescriptor()));

			for (int i = 0, len = tasks.length; i < len; i++) {
				if (i != len - 1) {
					run.instructions.add(new InsnNode(Opcodes.DUP));
				}

				run.instructions.add(new IntInsnNode(Opcodes.BIPUSH, i));
				run.instructions.add(new InsnNode(Opcodes.AALOAD));
				run.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Type.getInternalName(Runnable.class), "run", "()V", true));
			}

			run.instructions.add(new InsnNode(Opcodes.RETURN));

			classNode.methods.add(run);

			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(classWriter);

			Class<?> klass = this.defineClass(classWriter.toByteArray());

			return (Runnable) klass.getConstructor(Runnable[].class).newInstance((Object) tasks);
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}

	private Class<?> defineClass(byte[] classBytes) {
		return this.defineClass(null, classBytes, 0, classBytes.length);
	}
}
