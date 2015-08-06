package ru.justnero.minecraft.forge.jet.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

import codechicken.lib.asm.CC_ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;

public class MinecraftServer extends ITransformer {

    @Override
    public byte[] transform(byte[] data, boolean isObfuscated) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, 0);

        for(FieldNode fieldNode : classNode.fields) {
            if(fieldNode.desc.equals("Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;")) {
                fieldNode.desc = "Lru/justnero/minecraft/forge/jet/yggdrasil/JetAuthService;";
            }
        }

        for(MethodNode methodNode : classNode.methods) {
            if(methodNode.name.equals("<init>")) {
                transform_init(methodNode);
            }
        }

        ClassWriter writer = new CC_ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES,true);
        classNode.accept(writer);
        return writer.toByteArray();
    }
    
    private void transform_init(MethodNode method) {
        String searchClass  =  "com/mojang/authlib/yggdrasil/YggdrasilAuthenticationService";
        String searchLClass = "Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;";
        String replaceClass =  "ru/justnero/minecraft/forge/jet/yggdrasil/JetAuthService";
        
        for(int j=0;j<method.instructions.size();j++) {
            AbstractInsnNode insn = method.instructions.get(j);
            if(insn.getOpcode() == Opcodes.NEW) {
                TypeInsnNode tmp = (TypeInsnNode) insn;
                if(tmp.desc.equals(searchClass)) {
                    tmp.desc = replaceClass;
                }
            }
            if(insn.getOpcode() == Opcodes.INVOKESPECIAL || insn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                MethodInsnNode tmp = (MethodInsnNode) insn;
                if(tmp.owner.equals(searchClass)) {
                    tmp.owner = replaceClass;
                }
            }
            if(insn.getOpcode() == Opcodes.PUTFIELD || insn.getOpcode() == Opcodes.GETFIELD) {
                FieldInsnNode tmp = (FieldInsnNode) insn;
                if(tmp.desc.equals(searchLClass)) {
                    tmp.desc = replaceClass;
                }
            }
        }
    }

}
