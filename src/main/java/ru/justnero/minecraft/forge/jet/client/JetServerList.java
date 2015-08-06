package ru.justnero.minecraft.forge.jet.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;

import ru.justnero.minecraft.forge.jet.Jet;

public class JetServerList extends ServerList {

    private List<JetServerData> servers = new ArrayList<JetServerData>();

    public JetServerList(Minecraft minecraft) {
        super(minecraft);
    }

    @Override
    public void loadServerList() {
    	if(servers == null) {
            servers = new ArrayList<JetServerData>();
    	}
        try {
            servers.clear();
            servers.addAll(JetServerData.convert(Jet.listServer(Jet.gameID())));
        } catch(IOException ex) {
            Jet.log.error("Couldn\'t load server list",ex);
            Jet.log.catching(ex);
        }
    }

    @Override
    public void saveServerList() {}

    @Override
    public JetServerData getServerData(int index) {
        return servers.get(index);
    }

    @Override
    public void removeServerData(int par1) {}

    public void addServerData(JetServerData par1ServerData) {}

    @Override
    public int countServers() {
        return this.servers.size();
    }

    @Override
    public void swapServers(int par1, int par2) {}

    @Override
    public void func_147413_a(int p_147413_1_, ServerData p_147413_2_) {}

}
