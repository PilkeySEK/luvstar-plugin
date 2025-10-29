package me.pilkeysek;

import org.bukkit.Location;

public class Util {
    public static String locToIntString(Location loc) {
        return Integer.toString((int) loc.getX()) + " " + Integer.toString((int) loc.getY()) + " " + Integer.toString((int) loc.getZ());
    }
}
