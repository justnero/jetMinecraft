package ru.justnero.minecraft.forge.jet.client;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ProtectEntity extends Entity {
    public ProtectEntity(World world) {
        super(world);
        this.onUpdate();
    }

    @Override
    protected void entityInit() { }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1) {}

    @Override
    public void onUpdate() {
        Minecraft mc = FMLClientHandler.instance().getClient();
        setPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ);
    }
}
