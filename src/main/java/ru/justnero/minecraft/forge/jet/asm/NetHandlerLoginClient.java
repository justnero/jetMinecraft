package ru.justnero.minecraft.forge.jet.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import codechicken.lib.asm.CC_ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;

public class NetHandlerLoginClient extends ITransformer {

    @Override
    public byte[] transform(byte[] data, boolean isObfuscated) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, 0);

        for(MethodNode methodNode : classNode.methods) {
            if(methodNode.name.equals(isObfuscated ? "c" : "func_147391_c")) {
                transform_147391_c(methodNode);
            }
        }

        ClassWriter writer = new CC_ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES,true);
        classNode.accept(writer);
        return writer.toByteArray();
    }
    
    private void transform_147391_c(MethodNode method) {
        String injectClass = "ru/justnero/minecraft/forge/jet/yggdrasil/JetAuthService";
            
        TypeInsnNode insn1 = null;
        MethodInsnNode insn2 = null;
        MethodInsnNode insn3 = null;

        for(int j=0;j<method.instructions.size();j++) {
            AbstractInsnNode insn = method.instructions.get(j);
            if(insn.getOpcode() == Opcodes.NEW) {
                insn1 = (TypeInsnNode) insn;
            } else if(insn2 == null && insn.getOpcode() == Opcodes.INVOKESPECIAL) {
                insn2 = (MethodInsnNode) insn;
            } else if(insn2 != null && insn3 == null && insn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                insn3 = (MethodInsnNode) insn;
            }
        }

        if(insn1 != null && insn2 != null && insn3 != null) {
            insn1.desc  = injectClass;
            insn2.owner = injectClass;
            insn3.owner = injectClass;
        } else {
            System.out.println("Target for AuthPatcher injection not found");
        }
    }
    
}
