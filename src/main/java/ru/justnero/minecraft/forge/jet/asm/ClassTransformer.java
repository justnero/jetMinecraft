package ru.justnero.minecraft.forge.jet.asm;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.launchwrapper.IClassTransformer;
import ru.justnero.minecraft.forge.jet.Jet;

public class ClassTransformer implements IClassTransformer {
    
    private static final Map<String,ITransformer> patch = new HashMap<String,ITransformer>();
    
    @Override
    public byte[] transform(String name,String transformedName,byte[] basicClass) {
        if(basicClass != null && patch.containsKey(transformedName)) {
            Jet.log.info("Patching {} with {}",transformedName,patch.get(transformedName).getClass().getName());
            return patch.get(transformedName).transform(basicClass,!name.equals(transformedName));
        }
        return basicClass;
    }
    
    public static void put(String className, ITransformer transformer) {
        patch.put(className,transformer);
    }

}
