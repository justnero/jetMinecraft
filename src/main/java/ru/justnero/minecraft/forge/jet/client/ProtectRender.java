package ru.justnero.minecraft.forge.jet.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.justnero.minecraft.forge.jet.protect.Point3D;
import ru.justnero.minecraft.forge.jet.protect.Protect;
import ru.justnero.minecraft.forge.jet.protect.ProtectManager;
import static java.lang.Math.*;

public class ProtectRender extends Render {

    private final Minecraft _mc;
    private final ProtectManager _pm;

    public ProtectRender(Minecraft mc) {
        _mc = mc;
        _pm = ProtectManager.instance();
    }

    @Override
    public void doRender(Entity entity,double d0,double d1,double d2,float f,float f1) {
        if(_pm.drawFrameActivated) {
            for(Protect rg : _pm.all().values()) {
                if(rg != null) {
                    Point3D min = rg.cubiod().getPoint(1);
                    Point3D max = rg.cubiod().getPoint(2);
                    renderLabel(
                            String.valueOf((char) 167) +
                            (rg.owner() ? "1" : rg.member() ? "2" : "4") +
                            rg.name(),
                            round(min.x() + abs(max.x() - min.x()) / 2.0D),
                            round(min.y() + abs(max.y() - min.y()) / 2.0D),
                            round(min.z() + abs(max.z() - min.z()) / 2.0D),
                            80
                    );
                    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                            -_mc.thePlayer.posX + min.x(),
                            -_mc.thePlayer.posY + min.y(),
                            -_mc.thePlayer.posZ + min.z(),
                            -_mc.thePlayer.posX + max.x() + 1.0D,
                            -_mc.thePlayer.posY + max.y() + 1.0D,
                            -_mc.thePlayer.posZ + max.z() + 1.0D
                    );
                    drawOutlinedBoundingBox(
                            aabb,
                            rg.owner() ? 1 : (rg.member() ? 2 : 4)
                    );
                }
            }
        }
    }

    private double getDistanceSq(double x,double y,double z,Entity entity) {
        return getDistanceSq(
                x,
                y,
                z,
                entity.posX,
                entity.posY,
                entity.posZ
        );
    }

    private double getDistanceSq(double x1,double y1,double z1,double x2,double y2,double z2) {
        double dX = x1 - x2;
        double dY = y1 - y2;
        double dZ = z1 - z2;
        return dX * dX + dY * dY + dZ * dZ;
    }

    protected void renderLabel(String text,double x,double y,double z,int distance) {
        double var10 = getDistanceSq(x,y,z,renderManager.livingPlayer);
        if(var10 <= distance * distance) {
            x = -_mc.thePlayer.posX + x;
            y = -_mc.thePlayer.posY + y;
            z = -_mc.thePlayer.posZ + z;
            FontRenderer fR = getFontRendererFromRenderManager();
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x,(float) y,(float) z);
            GL11.glNormal3f(0.0F,1.0F,0.0F);
            GL11.glRotatef(-renderManager.playerViewY,0.0F,1.0F,0.0F);
            GL11.glRotatef(_mc.gameSettings.thirdPersonView == 2 ? -renderManager.playerViewX : renderManager.playerViewX,1.0F,0.0F,0.0F);
            GL11.glScalef(-0.3F,-0.3F,0.3F);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            fR.drawString(text,-fR.getStringWidth(text) / 2,0,553648127);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            fR.drawString(text,-fR.getStringWidth(text) / 2,0,-1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F,1.0F,1.0F,1.0F);
            GL11.glPopMatrix();
        }
    }

    private void drawOutlinedBoundingBox(AxisAlignedBB aabb,int color) {
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(5.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        switch(color) {
            case 4:
                GL11.glColor4f(170.0F,0.0F,0.0F,1.0F);
                break;
            case 2:
                GL11.glColor4f(0.0F,170.0F,0.0F,1.0F);
                break;
            case 1:
            default:
                GL11.glColor4f(0.0F,0.0F,170.0F,1.0F);
                break;
        }
        Tessellator tesselator = Tessellator.instance;
        tesselator.startDrawing(3);
        tesselator.addVertex(aabb.minX,aabb.minY,aabb.minZ);
        tesselator.addVertex(aabb.maxX,aabb.minY,aabb.minZ);
        tesselator.addVertex(aabb.maxX,aabb.minY,aabb.maxZ);
        tesselator.addVertex(aabb.minX,aabb.minY,aabb.maxZ);
        tesselator.addVertex(aabb.minX,aabb.minY,aabb.minZ);
        tesselator.draw();
        tesselator.startDrawing(3);
        tesselator.addVertex(aabb.minX,aabb.maxY,aabb.minZ);
        tesselator.addVertex(aabb.maxX,aabb.maxY,aabb.minZ);
        tesselator.addVertex(aabb.maxX,aabb.maxY,aabb.maxZ);
        tesselator.addVertex(aabb.minX,aabb.maxY,aabb.maxZ);
        tesselator.addVertex(aabb.minX,aabb.maxY,aabb.minZ);
        tesselator.draw();
        tesselator.startDrawing(1);
        tesselator.addVertex(aabb.minX,aabb.minY,aabb.minZ);
        tesselator.addVertex(aabb.minX,aabb.maxY,aabb.minZ);
        tesselator.addVertex(aabb.maxX,aabb.minY,aabb.minZ);
        tesselator.addVertex(aabb.maxX,aabb.maxY,aabb.minZ);
        tesselator.addVertex(aabb.maxX,aabb.minY,aabb.maxZ);
        tesselator.addVertex(aabb.maxX,aabb.maxY,aabb.maxZ);
        tesselator.addVertex(aabb.minX,aabb.minY,aabb.maxZ);
        tesselator.addVertex(aabb.minX,aabb.maxY,aabb.maxZ);
        tesselator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
