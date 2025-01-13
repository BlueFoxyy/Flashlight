package me.foxyy.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;

public class BlockLoc {
    private World world;
    private int x;
    private int y;
    private int z;

    public BlockLoc(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockLoc(Location location) {
        this.world = location.getWorld();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    @Override
    public String toString() {
        return "BlockLoc{" +
                "world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BlockLoc blockLoc = (BlockLoc) o;
        return x == blockLoc.x && y == blockLoc.y && z == blockLoc.z && world.getName().equals(blockLoc.world.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    public Block getBlock() {
        return world.getBlockAt(x, y, z);
    }
}
