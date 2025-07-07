package dev.boredhuman;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * This class will generate a runnable consumer whose implementation is a switch block, where each case
 * iterates over the primitive array in an unrolled fashion, if the primitive array's length is greater
 * than that supported by the implementation it will fall back to just a basic loop over the primitive
 * array
 */
public class RunnableConsumerGenerator extends ClassLoader {

	public RunnableConsumer generate(int maxUnroll) {
		if (maxUnroll < 1) {
			throw new IllegalArgumentException("Max unroll must be greater than zero.");
		}

		ClassNode classNode = new ClassNode();
		classNode.name = "dev/boredhuman/SwitchingBakedArray";
		classNode.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
		classNode.superName = Type.getInternalName(Object.class);
		classNode.interfaces.add(Type.getInternalName(RunnableConsumer.class));
		classNode.version = 52;

		MethodNode constructor = new MethodNode();
		constructor.name = "<init>";
		constructor.access = Opcodes.ACC_PUBLIC;
		constructor.desc = "()V";

		constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		constructor.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V"));
		constructor.instructions.add(new InsnNode(Opcodes.RETURN));

		classNode.methods.add(constructor);

		MethodNode acceptMethod = new MethodNode();
		acceptMethod.name = "accept";
		acceptMethod.desc = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Runnable[].class));
		acceptMethod.access = Opcodes.ACC_PUBLIC;

		InsnList acceptInstructions = acceptMethod.instructions;

		// get the length field from runnable array
		acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		acceptInstructions.add(new InsnNode(Opcodes.ARRAYLENGTH));
		// store the length of the array in the second local
		acceptInstructions.add(new VarInsnNode(Opcodes.ISTORE, 2));
		acceptInstructions.add(new VarInsnNode(Opcodes.ILOAD, 2));

		LabelNode defaultLabel = new LabelNode();
		LabelNode[] switchLabels = new LabelNode[maxUnroll];
		for (int i = 0; i < maxUnroll; i++) {
			switchLabels[i] = new LabelNode();
		}

		acceptInstructions.add(new TableSwitchInsnNode(1, maxUnroll, defaultLabel, switchLabels));

		LabelNode exitLabel = new LabelNode();

		// create switch blocks
		for (int i = 0; i < maxUnroll; i++) {
			acceptInstructions.add(switchLabels[i]);

			// populate switch block
			for (int j = 0, len = i + 1; j < len; j++) {
				acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				acceptInstructions.add(new IntInsnNode(Opcodes.BIPUSH, j));
				acceptInstructions.add(new InsnNode(Opcodes.AALOAD));
				acceptInstructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Type.getInternalName(Runnable.class), "run", "()V", true));
			}
			// jump to return
			acceptInstructions.add(new JumpInsnNode(Opcodes.GOTO, exitLabel));
		}

		// create default case
		acceptInstructions.add(defaultLabel);

		// our counter for the loop
		acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		acceptInstructions.add(new InsnNode(Opcodes.ICONST_0));

		LabelNode conditionCheck = new LabelNode();
		acceptInstructions.add(conditionCheck);

		// dup so our counter doesn't get consumed by the condition check
		acceptInstructions.add(new InsnNode(Opcodes.DUP));
		// Stack: array, i, i

		// fetch array length
		acceptInstructions.add(new VarInsnNode(Opcodes.ILOAD, 2));

		// Stack: array, i, i, length

		LabelNode cleanup = new LabelNode();

		// if i == array.length exit
		acceptInstructions.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, cleanup));

		// Stack: array, i
		acceptInstructions.add(new InsnNode(Opcodes.DUP2));

		// Stack: array, i, array, i
		acceptInstructions.add(new InsnNode(Opcodes.AALOAD));
		acceptInstructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Type.getInternalName(Runnable.class), "run", "()V", true));

		// bump counter
		acceptInstructions.add(new InsnNode(Opcodes.ICONST_1));
		acceptInstructions.add(new InsnNode(Opcodes.IADD));

		// Stack: array, i
		acceptInstructions.add(new JumpInsnNode(Opcodes.GOTO, conditionCheck));

		acceptInstructions.add(cleanup);
		acceptInstructions.add(new InsnNode(Opcodes.POP2));

		// Stack: Empty

		acceptInstructions.add(exitLabel);
		acceptInstructions.add(new InsnNode(Opcodes.RETURN));

		classNode.methods.add(acceptMethod);

		try {
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(classWriter);

			Class<?> klass = this.defineClass(classWriter.toByteArray());

			return (RunnableConsumer) klass.getConstructor().newInstance();
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}

	private Class<?> defineClass(byte[] classBytes) {
		return this.defineClass(null, classBytes, 0, classBytes.length);
	}
}
