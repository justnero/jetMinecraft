package ru.justnero.minecraft.forge.jet.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import ru.justnero.minecraft.forge.jet.Jet;

import ru.justnero.minecraft.forge.jet.protect.ProtectManager;

public class HandshakeMessage extends Message {

    public long freezeTime;
    public long avaitingProtects;

    public HandshakeMessage() {}

    public HandshakeMessage(long freezeTime,long avaitingProtects) {
        this.freezeTime       = freezeTime;
        this.avaitingProtects = avaitingProtects;
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        freezeTime       = bytes.readLong();
        avaitingProtects = bytes.readLong();
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeLong(freezeTime);
        bytes.writeLong(avaitingProtects);
    }

    public static class Handler implements IMessageHandler<HandshakeMessage,IMessage> {
        @Override
        public IMessage onMessage(HandshakeMessage message,MessageContext ctx) {
            Jet.log.debug("Handshake message");
            ProtectManager pm = ProtectManager.instance();
            pm.freezeTime       = message.freezeTime;
            pm.avaitingProtects = message.avaitingProtects;
            pm.freeze(message.freezeTime);
            return null;
        }
    }

}
