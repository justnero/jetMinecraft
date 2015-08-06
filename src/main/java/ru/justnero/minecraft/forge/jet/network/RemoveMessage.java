package ru.justnero.minecraft.forge.jet.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import ru.justnero.minecraft.forge.jet.Jet;

import ru.justnero.minecraft.forge.jet.protect.ProtectManager;

public class RemoveMessage extends Message {

    private ArrayList<String> list;

    public RemoveMessage() {}

    public RemoveMessage(ArrayList<String> protects) {
        this.list = new ArrayList<String>();
        this.list.addAll(protects);
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        list = new ArrayList<String>();
        long count = bytes.readLong();
        for(long i=0;i<count;i++) {
            int len = bytes.readInt();
            char charArray[] = new char[len];
            for(int j=0;j<len;j++) {
                charArray[j] = bytes.readChar();
            }
            String pName = String.valueOf(charArray);
            list.add(pName);
        }
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeLong(list.size());
        for(String name : list) {
            bytes.writeInt(name.length());
            for(char c : name.toCharArray()) {
                bytes.writeChar(c);
            }
        }
    }

    public static class Handler implements IMessageHandler<RemoveMessage,IMessage> {
        @Override
        public IMessage onMessage(RemoveMessage message,MessageContext ctx) {
            Jet.log.debug("Remove message size: {}",message.list.size());
            ProtectManager.instance().remove(message.list);
            return null;
        }
    }

}
