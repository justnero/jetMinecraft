package ru.justnero.minecraft.forge.jet.client;

public class JetServer {

    public String serverName;
    public String serverIP;
    public int serverID;

    public JetServer(String serverName, String serverIP) {
        this(-1,serverName,serverIP);
    }

    public JetServer(int server, String serverName, String serverIP) {
        this.serverID = server;
        this.serverName = serverName;
        this.serverIP = serverIP;
    }

}
