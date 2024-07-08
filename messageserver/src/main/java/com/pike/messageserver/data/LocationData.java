package com.pike.messageserver.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationData {
    /**
     * Name of the world
     */
    public String world;

    /**
     * X coordinate
     */
    public double x;

    /**
     * Y coordinate
     */
    public double y;

    /**
     * Z coordinate
     */
    public double z;

    /**
     * Yaw
     */
    public double yaw;

    /**
     * Pitch
     */
    public double pitch;

    @Override
    public String toString() {
        return "LocationData{" + "world='" + world + '\'' + ", x=" + x + ", y=" + y + ", z=" + z + ", yaw=" + yaw + ", pitch=" + pitch + '}';
    }

    /**
     * Creates a LocationData object from a given Location object.
     *
     * @param  location    the Location object to create LocationData from
     * @return             the LocationData object created from the Location object
     */
    public static LocationData fromLocation(Location location) {
        LocationData locationData = new LocationData();
        locationData.world = location.getWorld().getName();
        locationData.x = location.getX();
        locationData.y = location.getY();
        locationData.z = location.getZ();
        locationData.yaw = location.getYaw();
        locationData.pitch = location.getPitch();
        return locationData;
    }

    /**
     * Creates a Location object from the world name, x, y, and z coordinates.
     *
     * @return          the Location object created
     */
    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
}
