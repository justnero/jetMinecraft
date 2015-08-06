package ru.justnero.minecraft.forge.jet.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.multiplayer.ServerData;

public class JetServerData extends ServerData {

    private final int serverID;

    public JetServerData(String serverName, String serverIP) {
        this(-1,serverName,serverIP);
    }

    public JetServerData(int server, String serverName, String serverIP) {
        super(serverName,serverIP);
        serverID = server;
    }

    public int serverID() {
        return serverID;
    }

    public boolean isHidingAddress() {
        return true;
    }

    public static List<JetServerData> convert(List<JetServer> servers) {
    	List<JetServerData> list = new ArrayList<JetServerData>(servers.size());
    	for(JetServer server : servers) {
            list.add(new JetServerData(server.serverID,server.serverName,server.serverIP));
    	}
    	return list;
    }

}
