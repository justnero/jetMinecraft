package ru.justnero.minecraft.forge.jet.protect;

public class Protect {
    
    private final String  name;
    private final Cuboid  cuboid;
    private final int     priority;
    private final boolean build;
    private final boolean use;
    private final boolean owner;
    private final boolean member;

    public Protect(String name, Cuboid cuboid, int priority, boolean build, boolean use, boolean owner, boolean member) {
        this.name     = name;
        this.cuboid   = cuboid;
        this.priority = priority;
        this.build    = build;
        this.use      = use;
        this.owner    = owner;
        this.member   = member;
    }
    
    public boolean contains(Point3D point) {
        return cuboid.contains(point);
    }

    public String name() {
        return name;
    }

    public Cuboid cubiod() {
        return cuboid;
    }

    public int priority() {
        return priority;
    }

    public boolean build() {
        return build;
    }

    public boolean use() {
        return use;
    }

    public boolean owner() {
        return owner;
    }

    public boolean member() {
        return member;
    }
    
}
