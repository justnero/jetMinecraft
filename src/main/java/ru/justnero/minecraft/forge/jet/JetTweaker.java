package ru.justnero.minecraft.forge.jet;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.launcher.FMLTweaker;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class JetTweaker implements ITweaker {

    private File dir;
    private List<String> allArgs;
    private FMLTweaker fml;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        System.setProperty("java.net.preferIPv4Stack","true");

        dir = Jet.directory();

        Map<String, String> launchArgs = modify(parse(args));
        Launch.blackboard.put("launchArgs",launchArgs);
        for(String key : launchArgs.keySet()) {
            allArgs.add(key);
            allArgs.add(launchArgs.get(key));
        }
        
        if(!Jet.isServer()) {
            patchFMLTweaker(dir);
        }
    }

    public Map<String,String> parse(List<String> args) {
        Map<String,String> list = new HashMap<String,String>();
        allArgs = Lists.newArrayList();
        String classifier = null;
        for(String arg : args) {
            if(arg.startsWith("-")) {
                if(classifier != null) {
                    classifier = list.put(classifier, "");
                } else if(arg.contains("=")) {
                    classifier = list.put(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=') + 1));
                } else {
                    classifier = arg;
                }
            } else {
                if(classifier != null) {
                    classifier = list.put(classifier, arg);
                } else {
                    allArgs.add(arg);
                }
            }
        }

        return list;
    }

    public Map<String,String> modify(Map<String,String> list) {
        if(allArgs.contains("--jetServer")) {
            return list;
        } else {
            Jet.setIsServer(false);
        }
        if(list.containsKey("--gameID")) {
            Jet.setGameID(Integer.valueOf(list.get("--gameID")));
            list.remove("--gameID");
        } else {
            throw new RuntimeException("GameID not set");
        }
        if(list.containsKey("--windowTitle")) {
            Jet.setTitle(list.get("--windowTitle"));
            list.remove("--windowTitle");
        } else {
            throw new RuntimeException("Title not set");
        }

    	String sessionID = Jet.authServer(list.get("--username"),list.get("--sessionID"));
    	if(sessionID == null) {
            throw new RuntimeException("Session is null");
    	}
    	System.out.println(sessionID);

        list.remove("--server");
        list.remove("--port");
    	list.remove("--gameID");
    	list.remove("--sessionID");
        list.remove("--proxyHost");
        list.remove("--proxyPort");
        list.remove("--proxyUser");
        list.remove("--proxyPass");

    	list.put("--version","jet1.7.10");
    	list.put("--gameDir",dir.getAbsolutePath());
    	list.put("--assetsDir",(new File(dir,"assets/")).getAbsolutePath());
    	list.put("--assetIndex","1.7.10");
    	list.put("--resourcePackDir",(new File(dir,"resourcepacks/")).getAbsolutePath());
    	list.put("--uuid",sessionID);
    	list.put("--accessToken",sessionID);

    	return list;
    }

    @SuppressWarnings("unchecked")
    private void patchFMLTweaker(File dir) {
        for(ITweaker tweak : (List<ITweaker>) Launch.blackboard.get("Tweaks")) {
            if(tweak instanceof FMLTweaker) {
                fml = (FMLTweaker) tweak;
                try {
                    Field gameDirField = FMLTweaker.class.getDeclaredField("gameDir");
                    gameDirField.setAccessible(true);
                    gameDirField.set(fml,dir);
                } catch (Exception ex) {
                    throw new RuntimeException("Error patching FMLTweaker",ex);
                }
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String[] getLaunchArguments() {
        List<String> list = (List<String>) Launch.blackboard.get("ArgumentList");
        list.clear();
    	return allArgs.toArray(new String[allArgs.size()]);
    }

    @Override
    public String getLaunchTarget() {
        return fml.getLaunchTarget();
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {}


}
