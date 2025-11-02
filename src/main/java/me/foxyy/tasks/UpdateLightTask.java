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

    static Set<Material> transparentMaterialSet = new HashSet<>();
    static {
        transparentMaterialSet.add(Material.LIGHT);
        transparentMaterialSet.add(Material.AIR);
        transparentMaterialSet.add(Material.CAVE_AIR);
        transparentMaterialSet.add(Material.VOID_AIR);
    }

    static Map<BlockLoc, Material> lightBlocks = new HashMap<>();

    private static Vector getPerpendicularVector(Vector v) {
        Vector ret = new Vector(v.getZ(), v.getZ(), -v.getX()-v.getY());
        if (ret.isZero())
            return new Vector(-v.getY()-v.getZ(), v.getX(), v.getX()).normalize();
        return ret.normalize();
    }

    private static boolean isOccluding(Material material) {
        return material.isOccluding() && !material.equals(Material.CAVE_AIR) && !material.equals(Material.VOID_AIR);
    }

    private static boolean isLightable(Material material) {
        return transparentMaterialSet.contains(material);
    }

    public static void clear() {
        for (BlockLoc blockLoc : lightBlocks.keySet()) {
            if (blockLoc.getBlock().getType() == Material.LIGHT) {
                blockLoc.getBlock().setType(lightBlocks.get(blockLoc), false);
            }
        }
    }

    public void run() {
        final double configDegree = Math.ceil(Flashlight.getInstance().getMainConfig().getDouble("degree"));
        final int configDepth = Flashlight.getInstance().getMainConfig().getInt("depth");
        final int configBrightness = Flashlight.getInstance().getMainConfig().getInt("brightness");

        final int phiSamples = 40;
        final int thetaSamples = 36;

        final double maxPhi = configDegree * Math.PI / 180;
        final double minPhi = 5 * Math.PI / 180;

        Map<BlockLoc, Integer> currentLightBlocks = new HashMap<>(); // BlockLoc -> brightness map

        for (Player player : Flashlight.getInstance().getServer().getOnlinePlayers()) {
            if (!Flashlight.getInstance().flashlightState.get(player))
                continue;

            List<Block> blocks = player.getLastTwoTargetBlocks(transparentMaterialSet, configDepth);
            BlockLoc lookingBlockLoc = new BlockLoc(blocks.getFirst().getLocation());
//            Flashlight.getInstance().getLogger().info(lookingBlockLoc.getBlock().toString());

            final Vector u = player.getLocation().getDirection().normalize(); // player look vector
            final Vector v = getPerpendicularVector(u); // arbitrary perpendicular vector to player look vector
            final Vector w = u.clone().crossProduct(v).normalize();
            for (int thetaStep = 0; thetaStep < thetaSamples; thetaStep++) {
                final double theta = 2 * Math.PI * thetaStep / thetaSamples;
                for (int phiStep = 0; phiStep < phiSamples; phiStep++) {
                    final double phi = (maxPhi - minPhi) * phiStep / phiSamples + minPhi;
                    for (int depth = 0; depth <= configDepth; depth++) {
                        final Vector ray = w.clone().multiply(Math.sin(phi) * Math.cos(theta))
                                .add(v.clone().multiply(Math.sin(phi) * Math.sin(theta)))
                                .add(u.clone().multiply(Math.cos(phi))).normalize().multiply(depth);
                        final Location location = player.getEyeLocation().clone().add(ray);
                        if (isLightable(location.getBlock().getType())) {
                            BlockLoc blockLoc = new BlockLoc(location);
                            if (!blockLoc.equals(lookingBlockLoc))
                                currentLightBlocks.put(blockLoc, configBrightness);
                        } else if (isOccluding(location.getBlock().getType())) {
                            break;
                        }
                    }
                }
            }
        }

        for (BlockLoc blockLoc : currentLightBlocks.keySet()) {
            if (!currentLightBlocks.containsKey(blockLoc)) {
                lightBlocks.remove(blockLoc);
            }
        }

        for (BlockLoc blockLoc : lightBlocks.keySet()) {
            if (blockLoc.getBlock().getType() == Material.LIGHT) {
                blockLoc.getBlock().setType(lightBlocks.get(blockLoc), false);
            }
        }

        for (BlockLoc blockLoc : currentLightBlocks.keySet()) {
            if (lightBlocks.get(blockLoc) == null) {
                lightBlocks.put(blockLoc, blockLoc.getBlock().getType());
            }
        }

        for (HashMap.Entry<BlockLoc, Integer> pair : currentLightBlocks.entrySet()) {
            Block block = pair.getKey().getBlock();
            int lightLevel = pair.getValue();
            block.setType(Material.LIGHT, false);
            final Levelled level = (Levelled) block.getBlockData();
            level.setLevel(lightLevel);
            block.setBlockData(level, false);
        }
    }
}
