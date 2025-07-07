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

import java.util.function.Consumer;

public class ConsumerArrayConsumerGenerator extends ClassLoader {
	public ConsumerArrayConsumer generate(int maxUnroll) {
		if (maxUnroll < 1) {
			throw new IllegalArgumentException("Max unroll must be greater than zero.");
		}

		ClassNode classNode = new ClassNode();
		classNode.name = "dev/boredhuman/ConsumerArrayConsumerImpl";
		classNode.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
		classNode.superName = Type.getInternalName(Object.class);
		classNode.interfaces.add(Type.getInternalName(ConsumerArrayConsumer.class));
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
		acceptMethod.access = Opcodes.ACC_PUBLIC;
		acceptMethod.desc = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Consumer[].class), Type.getType(Object.class));

		InsnList acceptInstructions = acceptMethod.instructions;

		acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // load array
		acceptInstructions.add(new InsnNode(Opcodes.ARRAYLENGTH)); // get array length
		// stack: length

		LabelNode[] cases = new LabelNode[maxUnroll];
		for (int i = 0; i < cases.length; i++) {
			cases[i] = new LabelNode();
		}

		LabelNode defaultLabel = new LabelNode();

		// switch on array length
		TableSwitchInsnNode switchNode = new TableSwitchInsnNode(1, maxUnroll, defaultLabel, cases);

		acceptInstructions.add(switchNode);

		LabelNode exitLabel = new LabelNode();

		// generate the cases
		for (int i = 0; i < maxUnroll; i++) {
			acceptInstructions.add(cases[i]);

			for (int j = 0, len = i + 1; j < len; j++) {
				acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				// stack: array
				acceptInstructions.add(new IntInsnNode(Opcodes.BIPUSH, j));
				// stack: array, index
				acceptInstructions.add(new InsnNode(Opcodes.AALOAD));
				// stack: consumer
				acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
				// stack: consumer, obj
				acceptInstructions.add(new MethodInsnNode(
					Opcodes.INVOKEINTERFACE,
					Type.getInternalName(Consumer.class),
					"accept",
					Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class)),
					true
				));
				// stack: empty
			}

			acceptInstructions.add(new JumpInsnNode(Opcodes.GOTO, exitLabel));
		}

		// create default case
		acceptInstructions.add(defaultLabel);

		acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		acceptInstructions.add(new InsnNode(Opcodes.ARRAYLENGTH));
		acceptInstructions.add(new VarInsnNode(Opcodes.ISTORE, 3));
		// store length into local

		// our counter for the loop
		acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		acceptInstructions.add(new InsnNode(Opcodes.ICONST_0));

		LabelNode conditionCheck = new LabelNode();
		acceptInstructions.add(conditionCheck);

		// dup so our counter doesn't get consumed by the condition check
		acceptInstructions.add(new InsnNode(Opcodes.DUP));
		// Stack: array, i, i

		// fetch array length
		acceptInstructions.add(new VarInsnNode(Opcodes.ILOAD, 3));

		// Stack: array, i, i, length

		LabelNode cleanup = new LabelNode();

		// if i == array.length exit
		acceptInstructions.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, cleanup));

		// Stack: array, i
		acceptInstructions.add(new InsnNode(Opcodes.DUP2));

		// Stack: array, i, array, i
		acceptInstructions.add(new InsnNode(Opcodes.AALOAD));
		acceptInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
		acceptInstructions.add(new MethodInsnNode(
			Opcodes.INVOKEINTERFACE,
			Type.getInternalName(Consumer.class),
			"accept",
			Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class)),
			true
		));

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

			return (ConsumerArrayConsumer) klass.getConstructor().newInstance();
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}

	private Class<?> defineClass(byte[] classBytes) {
		return this.defineClass(null, classBytes, 0, classBytes.length);
	}
}
