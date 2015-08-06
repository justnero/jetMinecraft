package ru.justnero.minecraft.forge.jet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipFile;

import sun.misc.URLClassPath;

import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.LogManager;

import ru.justnero.minecraft.forge.jet.client.JetServer;

public class Jet {
    
    public static final String version = "1.0";
    public static final Logger log = LogManager.getLogger("jet");
    
    private static File gameDir;
    private static File jarLocation;
    private static int gameID = 1;
    private static String title = "jetMinecraft";
    private static boolean isServer = true;

    public static final String auth() {
        return "auth.justnero.ru";
    }
    
    public static final String urlSite() {
        return "http://justnero.ru";
    }
    
    public static final String urlForum() {
        return "http://justnero.ru/forum";
    }
    
    public static final String urlVK() {
        return "http://vk.com/neutro";
    }
    
    public static final String urlDeveloper() {
        return "http://justnero.ru";
    }
    
    public static final String project() {
        return "jetMinecraft";
    }
    
    public static final String skins() {
        return "http://justnero.ru/public/skins/plain/%s.png";
    }
    
    public static final String cloaks() {
        return "http://justnero.ru/public/cloaks/plain/%s.png";
    }

    public static File directory() {
    	return gameDir == null ? gameDir = jarLocation().getParentFile().getParentFile() : gameDir;
    }

    public static File jarLocation() {
    	return jarLocation == null ? jarLocation = genJarLocation() : jarLocation;
    }

    private static File genJarLocation() {
        return new File(Jet.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    public static int gameID() {
    	return gameID;
    }

    public static void setGameID(int game) {
    	if(gameID != 0) {
            gameID = game;
    	}
    }

    public static String title() {
    	return title;
    }

    public static void setTitle(String name) {
    	title = name;
    }
    
    public static boolean isServer() {
        return isServer;
    }
    
    public static void setIsServer(boolean value) {
        isServer = value;
    }
    
    private static String hash(File f) {
        if(!f.isDirectory()) {
            return sha1(f);
        }
        StringBuilder sb = new StringBuilder(f.getName());
        if(f.listFiles() != null) {
            for(File t : f.listFiles()) {
                sb.append(hash(t));
            }
        }
        return sha1(sb.toString());
    }

    private static String sha1(File file) {
        StringBuilder hexString = new StringBuilder();
        MessageDigest md;
        FileInputStream fis = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md.digest();
            for(int i=0;i<mdbytes.length;i++) {
                String hex = Integer.toHexString(0xff & mdbytes[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            fis.close();
        } catch (Exception e) {
            try {
                fis.close();
            } catch (Exception ex) {}
            log.catching(e);
            return e.toString();
        }
        return hexString.toString();
    }

    private static String sha1(String str) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch(NoSuchAlgorithmException ex) {
            log.catching(ex);
            return ex.toString();
        }
        byte[] digest = md.digest(str.getBytes());
        StringBuilder hex = new StringBuilder(digest.length);
        for(int i=0;i<digest.length;i++) {
            hex.append(Integer.toString((digest[i]&0xff)+0x100,16).substring(1));
        }
        return hex.toString();
    }

    private static Socket socket() {
        try {
            return new Socket(InetAddress.getByName(auth()),15705);
        } catch(IOException ex) {
            log.catching(ex);
            return null;
        }
    }

    public static boolean checkServer(String userName, String server) throws IOException {
    	Socket socket = socket();
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(10);
        out.writeUTF(userName);
        out.writeUTF(server);
        out.flush();
        int ret_val = in.readInt();
        in.close();
        out.close();
        socket.close();
        return ret_val == 200;
    }

    public static String joinServer(String userName, String sessionID, String server, int serverID) throws IOException {
        Socket socket = socket();
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        String hash = hash(directory());
        out.writeInt(9);
        out.writeUTF(userName);
        out.writeUTF(sha1(sessionID+server));
        out.writeUTF(server);
        out.writeInt(serverID);
        out.writeUTF(hash);
        out.flush();
        String ret_val = in.readUTF();
        in.close();
        out.close();
        socket.close();
        return ret_val;
    }

    private static String authServer(String username, String sessionID, Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(7);
        out.writeUTF(username);
        out.writeUTF(sha1(sha1(sessionID+sha1("Get away from here!"))));
        out.flush();
        int code = in.readInt();
        String ret_val = code == 200 ? in.readUTF() : null;
        in.close();
        out.close();
        socket.close();
        return ret_val;
    }
    
    @SuppressWarnings("unchecked")
    public static String authServer(String username, String sessionID) {
        try {
            URL[] urls = new URL[]{new File(directory(),"/bin/minecraft.jar").toURI().toURL()};
            URLClassPath urlcp = new URLClassPath(urls,null);
            Field field = urlcp.getClass().getDeclaredField("path");
            field.setAccessible(true);
            ArrayList<URL> list = (ArrayList<URL>) field.get(urlcp);
            if(list.size() == urls.length) {
                for(Method m : FileInputStream.class.getMethods()) {
                    if(m.getName().equalsIgnoreCase("readFile")) {
                        return null;
                    }
                }
                URL url = ZipFile.class.getResource("/java/util/zip/ZipFile.class");
                Scanner sc = new Scanner(url.openStream());
                while(sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if(line.contains("java/io/BufferedInputStream") ||
                	   line.contains("replace")) {
                    	sc.close();
                    	return null;
                    }
                }
                sc.close();
                try {
                    // @TODO WinRegistry here
//                    String val1 = WinRegistry.readString(
//                        WinRegistry.HKEY_CURRENT_USER,
//                        new String(new byte[]{83,79,70,84,87,65,82,69,92,92,77,105,99,114,111,115,111,102,116,92,92,87,105,110,100,111,119,115,32,78,84,92,92,67,117,114,114,101,110,116,86,101,114,115,105,111,110,92,92,87,105,110,100,111,119,115}), // SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Windows
//                        new String(new byte[]{65,112,112,73,110,105,116,95,68,76,76,115})); // AppInit_DLLs
//                    String val2 = WinRegistry.readString(
//                        WinRegistry.HKEY_LOCAL_MACHINE,
//                        new String(new byte[]{83,79,70,84,87,65,82,69,92,92,77,105,99,114,111,115,111,102,116,92,92,87,105,110,100,111,119,115,32,78,84,92,92,67,117,114,114,101,110,116,86,101,114,115,105,111,110,92,92,87,105,110,100,111,119,115}), // SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Windows
//                        new String(new byte[]{65,112,112,73,110,105,116,95,68,76,76,115})); // AppInit_DLLs
//                    if(!((val1 == null || val1.equals(new String(new byte[]{110,111})) || val1.isEmpty()) &&
//                         (val2 == null || val2.equals(new String(new byte[]{110,111})) || val2.isEmpty()))) { // no
//                        return null;
//                    }						   
                } catch(Exception ex) {
                    log.catching(ex);
                    return null;
                }
                return authServer(username,sessionID,socket());
            }
        } catch(Exception ex) {
            log.catching(ex);
        }
        return null;
    }

    public static List<JetServer> listServer(int gameID) throws IOException {
        Socket socket = socket();
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(8);
        out.writeInt(gameID);
        out.flush();
        if(in.readInt() == 200) {
            int cnt = in.readInt();
            List<JetServer> list = new ArrayList<JetServer>(cnt);
            for(int i=0;i<cnt;i++) {
                list.add(new JetServer(in.readInt(),in.readUTF(),in.readUTF()));
            }
            return list;
        } else {
            throw new IOException();
        }
    }
    
}
