package ru.justnero.minecraft.forge.jet.protect;

import static java.lang.Math.min;
import static java.lang.Math.max;

public class Cuboid {
    
    private final Point3D minPoint;
    private final Point3D maxPoint;
    
    public Cuboid(int x1, int y1, int z1, int x2, int y2, int z2) {
        minPoint = new Point3D(min(x1,x2),min(y1,y2),min(z1,z2));
        maxPoint = new Point3D(max(x1,x2),max(y1,y2),max(z1,z2));
    }
    
    public Cuboid(Point3D p1, Point3D p2) {
        minPoint = p1;
        maxPoint = p2;
    }
    
    public Point3D getPoint(int i) {
        switch(i) {
            case 1:
                return minPoint;
            case 2:
                return maxPoint;
            default:
                return null;
        }
    }
    
    public boolean contains(Point3D point) {
        return (point.x() >= minPoint.x() && point.x() <= maxPoint.x()) &&
               (point.y() >= minPoint.y() && point.y() <= maxPoint.y()) &&
               (point.z() >= minPoint.z() && point.z() <= maxPoint.z());
    }
    
}
