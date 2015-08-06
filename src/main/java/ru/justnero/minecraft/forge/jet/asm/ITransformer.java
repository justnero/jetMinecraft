package ru.justnero.minecraft.forge.jet.asm;

public abstract class ITransformer {
    
    public abstract byte[] transform(byte[] data, boolean isObfuscated);

}
