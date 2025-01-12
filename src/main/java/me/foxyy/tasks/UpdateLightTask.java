package me.foxyy.tasks;

import me.foxyy.Flashlight;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class UpdateLightTask extends BukkitRunnable {

    static Set<Location> lightBlocks;

    private Vector rotateAround(Vector v, Vector k, double theta) {
        return v.multiply(Math.cos(theta))
                .add(k.crossProduct(v).multiply(Math.sin(theta)))
                .add(k.multiply(k.dot(v)).multiply(1 - Math.cos(theta)));
    }

    private Vector getPerpendicularVector(Vector v) {
        Vector ret = new Vector(v.getZ(), v.getZ(), -v.getX()-v.getY());
        if (ret.isZero())
            return new Vector(-v.getY()-v.getZ(), v.getX(), v.getX());
        return ret;
    }

    public void run() {
        final int maxLightLevel = 15;

        final int phiSamples = 36;
        final int thetaSamples = 36;

        final double targetPhi = Flashlight.getInstance().getMainConfig().getDouble("degree") * Math.PI / 180;
        final double targetDepth = Flashlight.getInstance().getMainConfig().getDouble("depth");

        Map<Location, Integer> currentLightBlocks = new HashMap<>();

        for (Player player : Flashlight.getInstance().getServer().getOnlinePlayers()) {
            final Vector lookVector = player.getEyeLocation().getDirection().normalize();
            final Vector perpendicularLookVector = getPerpendicularVector(lookVector);
            for (int thetaStep = 0; thetaStep < thetaSamples; thetaStep++) {
                final double theta = thetaStep * (2 * Math.PI / thetaSamples);
                for (int phiStep = 0; phiStep < phiSamples; phiStep++) {
                    final double phi = phiStep * targetPhi / phiSamples;
                    for (int depth = 0; depth < targetDepth; depth++) {
                        final int lightLevel = (int)(maxLightLevel * depth / targetDepth + 0.5);
                        final Vector ray = rotateAround(
                                rotateAround(lookVector, perpendicularLookVector, phi),
                                lookVector,
                                theta
                        );
                        final Location location = player.getLocation().add(ray);
                        if (player.getWorld().getBlockAt(location).getType() == Material.AIR) {
                            if (currentLightBlocks.get(location) != null) {
                                currentLightBlocks.compute(location, (k, currentLightLevel) -> Math.min(currentLightLevel + lightLevel, 15));
                            } else {
                                currentLightBlocks.put(location, lightLevel);
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        for (Location location : lightBlocks) {
            if (currentLightBlocks.get(location) == null) {
                if (Objects.requireNonNull(location.getWorld()).getBlockAt(location).getType() == Material.LIGHT) {
                    location.getWorld().setType(location, Material.AIR);
                }
            }
        }

        for (HashMap.Entry<Location, Integer> pair : currentLightBlocks.entrySet()) {
            Location location = pair.getKey();
            Block block = location.getBlock();
            int lightLevel = pair.getValue();
            block.setType(Material.LIGHT);
            final Levelled level = (Levelled) block.getBlockData();
            level.setLevel(lightLevel);
            block.setBlockData(level, true);
        }

        lightBlocks = currentLightBlocks.keySet();
    }
}
