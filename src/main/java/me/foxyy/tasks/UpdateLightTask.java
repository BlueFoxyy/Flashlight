package me.foxyy.tasks;

import me.foxyy.Flashlight;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class UpdateLightTask extends BukkitRunnable {

    static Set<Location> lightBlocks = new HashSet<>();

    private static Vector getPerpendicularVector(Vector v) {
        Vector ret = new Vector(v.getZ(), v.getZ(), -v.getX()-v.getY());
        if (ret.isZero())
            return new Vector(-v.getY()-v.getZ(), v.getX(), v.getX()).normalize();
        return ret.normalize();
    }

    public static void clear() {
        for (Location location : lightBlocks) {
            if (location.getBlock().getType() == Material.LIGHT) {
                location.getBlock().setType(Material.AIR, false);
            }
        }
    }

    public void run() {
        final int maxLightLevel = 15;

        final double configDegree = Math.ceil(Flashlight.getInstance().getMainConfig().getDouble("degree"));
        final int configDepth = Flashlight.getInstance().getMainConfig().getInt("depth");
        final int configBrightness = Flashlight.getInstance().getMainConfig().getInt("brightness");

        final int phiSamples = 10;
        final int thetaSamples = 36;

        final double targetPhi = configDegree * Math.PI / 180;
        final int targetDepth = configDepth;

        Map<Location, Integer> currentLightBlocks = new HashMap<>();
        Set<Location> lookingLocations = new HashSet<>();

        for (Player player : Flashlight.getInstance().getServer().getOnlinePlayers()) {
            if (!Flashlight.getInstance().flashlightState.get(player))
                continue;

            final Vector u = player.getLocation().getDirection().normalize(); // player look vector
            final Vector v = getPerpendicularVector(u); // arbitrary perpendicular vector to player look vector
            final Vector w = u.clone().crossProduct(v).normalize();
            for (int thetaStep = 0; thetaStep < thetaSamples; thetaStep++) {
                final double theta = 2 * Math.PI * thetaStep / thetaSamples;
                for (int phiStep = 0; phiStep < phiSamples; phiStep++) {
                    final double phi = targetPhi * phiStep / phiSamples;
                    for (int depth = 0; depth <= targetDepth; depth++) {
                        final Vector ray = w.clone().multiply(Math.sin(phi) * Math.cos(theta))
                                .add(v.clone().multiply(Math.sin(phi) * Math.sin(theta)))
                                .add(u.clone().multiply(Math.cos(phi))).normalize().multiply(depth);
                        final Location location = player.getEyeLocation().clone().add(ray);
                        if (location.getBlock().getType() == Material.LIGHT
                        ||  location.getBlock().getType() == Material.AIR) {
                            currentLightBlocks.put(location, configBrightness);
                        } else if (!location.getBlock().getType().isTransparent()) {
                            break;
                        }
                    }
                }
            }

            // bugfix: cant lit fire where a light block is present
            List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, targetDepth);
            if (lastTwoTargetBlocks.size() == 2 && lastTwoTargetBlocks.get(1).getType().isOccluding()) {
                Block adjacentBlock = lastTwoTargetBlocks.getFirst();
                lookingLocations.add(adjacentBlock.getLocation());
            }
        }

        for (Location location : lookingLocations) {
            currentLightBlocks.remove(location);
        }
        lightBlocks.removeAll(currentLightBlocks.keySet());
        for (Location location : lightBlocks) {
            if (location.getBlock().getType() == Material.LIGHT) {
                location.getBlock().setType(Material.AIR, false);
            }
        }

        for (HashMap.Entry<Location, Integer> pair : currentLightBlocks.entrySet()) {
            Location location = pair.getKey();
            Block block = location.getBlock();
            int lightLevel = pair.getValue();
            block.setType(Material.LIGHT, false);
            final Levelled level = (Levelled) block.getBlockData();
            level.setLevel(lightLevel);
            block.setBlockData(level, false);
        }

        lightBlocks = currentLightBlocks.keySet();
    }
}
