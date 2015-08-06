package ru.justnero.minecraft.forge.jet.protect;

public class Point3D {
    
    private final double pointX;
    private final double pointY;
    private final double pointZ;

    public Point3D() {
        pointX = 0;
        pointY = 0;
        pointZ = 0;
    }

    public Point3D(double pX, double pY, double pZ) {
        pointX = pX;
        pointY = pY;
        pointZ = pZ;
    }

    public double x() {
        return pointX;
    }

    public double y() {
        return pointY;
    }

    public double z() {
        return pointZ;
    }
}