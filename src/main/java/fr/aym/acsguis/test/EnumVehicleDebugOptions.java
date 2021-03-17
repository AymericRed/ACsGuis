package fr.aym.acsguis.test;

import java.util.function.Function;

public enum EnumVehicleDebugOptions
{
    NONE(d->0), //0000 0000
    CENTER_OF_MASS(d -> d|1), // - ---- ---1
    HULL_BOX(d -> d|2), // - ---- --1-
    SEATS(d -> d|4), // - ---- -1--
    WHEELS(d -> d|8), // - ---- 1---
    PLAYER_COLLISIONS(d -> d|16), // - ---1 ----
    LATE_NETWORK(d -> d|32), // - --1- ----
    TRAILER_ATTACH_POINTS(d -> d|64), // - -1-- ----
    CAMERA_RAYCAST(d -> d|128), // - 1--- ----
    DOOR_ATTACH_POINTS(d -> d|256),// 1 ---- ----
    COLLISION_DEBUG(d -> d|512);

    private final Function<Integer, Integer> debugFlagFunc;

    EnumVehicleDebugOptions(Function<Integer, Integer> debugFlagFunc)
    {
        this.debugFlagFunc = debugFlagFunc;
    }

    public int applyDebugMode(int oldDebugMode)
    {
        return debugFlagFunc.apply(oldDebugMode);
    }
    public int removeDebugMode(int oldDebugMode)
    {
        return (255-applyDebugMode(0))&oldDebugMode;
    }

    public boolean isActive(int debugMode)
    {
        return this==NONE || (debugMode&applyDebugMode(0))>0;
    }
}
