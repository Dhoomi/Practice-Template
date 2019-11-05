package io.dhoom.util;

import org.bukkit.*;

public class LocationSerializer
{
    public static String serializeLocation(final Location location) {
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
    }
    
    public static Location deserializeLocation(final String serializedLocation) {
        final String[] split = serializedLocation.split(":");
        return new Location(Bukkit.getServer().getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }
}
