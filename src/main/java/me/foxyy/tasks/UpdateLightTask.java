package me.foxyy.tasks;

import me.foxyy.Flashlight;
import me.foxyy.utils.BlockLoc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class UpdateLightTask extends BukkitRunnable {

    static Set<BlockLoc> lightBlocks = new HashSet<>();

    private static Vector getPerpendicularVector(Vector v) {
        Vector ret = new Vector(v.getZ(), v.getZ(), -v.getX()-v.getY());
        if (ret.isZero())
            return new Vector(-v.getY()-v.getZ(), v.getX(), v.getX()).normalize();
        return ret.normalize();
    }

    public static void clear() {
        for (BlockLoc blockLoc : lightBlocks) {
            if (blockLoc.getBlock().getType() == Material.LIGHT) {
                blockLoc.getBlock().setType(Material.AIR, false);
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


        final double maxPhi = configDegree * Math.PI / 180;
        final double minPhi = 5 * Math.PI / 180;
        final int targetDepth = configDepth;

        Map<BlockLoc, Integer> currentLightBlocks = new HashMap<>();
        Set<Material> transparentMaterialSet = new HashSet<>();
        transparentMaterialSet.add(Material.LIGHT);
        transparentMaterialSet.add(Material.AIR);

        for (Player player : Flashlight.getInstance().getServer().getOnlinePlayers()) {
            if (!Flashlight.getInstance().flashlightState.get(player))
                continue;

            List<Block> blocks = player.getLastTwoTargetBlocks(transparentMaterialSet, targetDepth);
            BlockLoc lookingBlockLoc = new BlockLoc(blocks.getFirst().getLocation());
//            Flashlight.getInstance().getLogger().info(lookingBlockLoc.getBlock().toString());

            final Vector u = player.getLocation().getDirection().normalize(); // player look vector
            final Vector v = getPerpendicularVector(u); // arbitrary perpendicular vector to player look vector
            final Vector w = u.clone().crossProduct(v).normalize();
            for (int thetaStep = 0; thetaStep < thetaSamples; thetaStep++) {
                final double theta = 2 * Math.PI * thetaStep / thetaSamples;
                for (int phiStep = 0; phiStep < phiSamples; phiStep++) {
                    final double phi = (maxPhi - minPhi) * phiStep / phiSamples + minPhi;
                    for (int depth = 0; depth <= targetDepth; depth++) {
                        final Vector ray = w.clone().multiply(Math.sin(phi) * Math.cos(theta))
                                .add(v.clone().multiply(Math.sin(phi) * Math.sin(theta)))
                                .add(u.clone().multiply(Math.cos(phi))).normalize().multiply(depth);
                        final Location location = player.getEyeLocation().clone().add(ray);
                        if (location.getBlock().getType() == Material.LIGHT
                        ||  location.getBlock().getType() == Material.AIR) {
                            BlockLoc blockLoc = new BlockLoc(location);
                            if (!blockLoc.equals(lookingBlockLoc))
                                currentLightBlocks.put(blockLoc, configBrightness);
                        } else if (!location.getBlock().getType().isTransparent()) {
                            break;
                        }
                    }
                }
            }
        }

        lightBlocks.removeAll(currentLightBlocks.keySet());
        for (BlockLoc blockLoc : lightBlocks) {
            if (blockLoc.getBlock().getType() == Material.LIGHT) {
                blockLoc.getBlock().setType(Material.AIR, false);
            }
        }

//        Flashlight.getInstance().getLogger().info("Current: " + currentLightBlocks.toString());
//        Flashlight.getInstance().getLogger().info("Remove:  " + lightBlocks.toString());

        for (HashMap.Entry<BlockLoc, Integer> pair : currentLightBlocks.entrySet()) {
            Block block = pair.getKey().getBlock();
            int lightLevel = pair.getValue();
            block.setType(Material.LIGHT, false);
            final Levelled level = (Levelled) block.getBlockData();
            level.setLevel(lightLevel);
            block.setBlockData(level, false);
        }

        lightBlocks = currentLightBlocks.keySet();
    }
}
