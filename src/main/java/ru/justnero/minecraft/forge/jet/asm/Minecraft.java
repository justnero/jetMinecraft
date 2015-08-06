package ru.justnero.minecraft.forge.jet.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import codechicken.lib.asm.CC_ClassWriter;
import ru.justnero.minecraft.forge.jet.Jet;

public class Minecraft extends ITransformer {

    @Override
    public byte[] transform(byte[] data, boolean isObfuscated) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, 0);
        
        for(MethodNode methodNode : classNode.methods) {
            if(methodNode.name.equals("<init>")) {
                if(!Jet.isServer()) {
                    transform_init(methodNode);
                }
            }
            if(methodNode.name.equals(isObfuscated ? "a" : "func_147115_a")   && methodNode.desc.equals("(Z)V")) {
                transform_147115_a(methodNode);
            }
            if(methodNode.name.equals(isObfuscated ? "al" : "func_147116_af")  && methodNode.desc.equals("()V")) {
                transform_147116_af(methodNode);
            }
            if(methodNode.name.equals(isObfuscated ? "am" : "func_147121_ag")  && methodNode.desc.equals("()V")) {
                transform_147121_ag(methodNode);
            }
        }

        ClassWriter writer = new CC_ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES,true);
        classNode.accept(writer);
        return writer.toByteArray();
    }
    
    private void transform_init(MethodNode method) {
        String searchClass = "com/mojang/authlib/yggdrasil/YggdrasilAuthenticationService";
        String replaceClass = "ru/justnero/minecraft/forge/jet/yggdrasil/JetAuthService";
        
        for(int i=0;i<method.instructions.size();i++) {
            AbstractInsnNode insn = method.instructions.get(i);
            if(insn.getOpcode() == Opcodes.NEW) {
                TypeInsnNode tmp = (TypeInsnNode) insn;
                if(tmp.desc.equals(searchClass)) {
                    tmp.desc = replaceClass;
                }
            } else if(insn.getOpcode() == Opcodes.INVOKESPECIAL || insn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                MethodInsnNode tmp = (MethodInsnNode) insn;
                if(tmp.owner.equals(searchClass)) {
                    tmp.owner = replaceClass;
                }
            }
        }
    }
    
    private void transform_147115_a(MethodNode method) {
        String invokeOwner = "ru/justnero/minecraft/forge/jet/protect/ProtectManager";
        String invokeName = "checkPermission";
        String invokeDesc = "(Z)Z";
        
        AbstractInsnNode targetNode = method.instructions.getFirst();
        
        InsnList toInject = new InsnList();
        toInject.add(new VarInsnNode(Opcodes.ILOAD,1));
        toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,invokeOwner,invokeName,invokeDesc,false));
        LabelNode ifne = new LabelNode();
        toInject.add(new JumpInsnNode(Opcodes.IFNE,ifne));
        toInject.add(new InsnNode(Opcodes.RETURN));
        toInject.add(ifne);
        toInject.add(new FrameNode(Opcodes.F_SAME,0,null,0,null));

        method.instructions.insertBefore(targetNode,toInject);
    }
    
    private void transform_147116_af(MethodNode method) {
        String invokeOwner = "ru/justnero/minecraft/forge/jet/protect/ProtectManager";
        String invokeName = "checkPermission";
        String invokeDesc = "(Z)Z";
        
        AbstractInsnNode targetNode = method.instructions.getFirst();
        
        InsnList toInject = new InsnList();
        toInject.add(new InsnNode(Opcodes.ICONST_1));
        toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,invokeOwner,invokeName,invokeDesc,false));
        LabelNode ifne = new LabelNode();
        toInject.add(new JumpInsnNode(Opcodes.IFNE,ifne));
        toInject.add(new InsnNode(Opcodes.RETURN));
        toInject.add(ifne);

        method.instructions.insertBefore(targetNode,toInject);
    }

    private void transform_147121_ag(MethodNode method) {
        String invokeOwner = "ru/justnero/minecraft/forge/jet/protect/ProtectManager";
        String invokeName = "checkPermission";
        String invokeDesc = "(Z)Z";
        
        AbstractInsnNode targetNode = method.instructions.getFirst();
        
        InsnList toInject = new InsnList();
        toInject.add(new InsnNode(Opcodes.ICONST_0));
        toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,invokeOwner,invokeName,invokeDesc,false));
        LabelNode ifne = new LabelNode();
        toInject.add(new JumpInsnNode(Opcodes.IFNE,ifne));
        toInject.add(new InsnNode(Opcodes.RETURN));
        toInject.add(ifne);

        method.instructions.insertBefore(targetNode,toInject);
    }
    
}
