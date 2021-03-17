package fr.aym.acsguis.test;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum EnumTerrainDebugOptions
{
    NONE(d->0, null, false, false), //0000 0000
    //ENTITY_BOXES(d -> d|1, new HashMap<>(), true), // - ---- ---1
    NETWORK_DATA(d -> d|2), // - ---- --1-
    CHUNK_BOXES(d -> (d|4)&455, new HashMap<>(), true), // - --00 01--
    BLOCK_BOXES(d -> (d|8)&459, new HashMap<>() , true), // - --00 10--
    CLIENT_CHUNK_BOXES(d -> (d|16)&467, new HashMap<>()), // - --01 00--
    CLIENT_BLOCK_BOXES(d -> (d|32)&483, new HashMap<>()), // - --10 00--
    PROFILING(d -> (d|64), null, true, false),// - -1-- ----
    SLOPE_BOXES(d -> (d|128)&255, new HashMap<>() , true), // 0 1--- ----
    CLIENT_SLOPE_BOXES(d -> (d|256)&511, new HashMap<>()); // 1 0--- ----

    private final Function<Integer, Integer> debugFlagFunc;
    public Map<BlockPos, float[]> dataIn;
    public final boolean needsServerRq, showOnDebugOptions;

    EnumTerrainDebugOptions(Function<Integer, Integer> debugFlagFunc)
    {
        this(debugFlagFunc, null);
    }
    EnumTerrainDebugOptions(Function<Integer, Integer> debugFlagFunc, Map<BlockPos, float[]> dataIn)
    {
        this(debugFlagFunc, dataIn, false);
    }
    EnumTerrainDebugOptions(Function<Integer, Integer> debugFlagFunc, Map<BlockPos, float[]> dataIn, boolean needsServerRq)
    {
        this(debugFlagFunc, dataIn, needsServerRq, true);
    }
    EnumTerrainDebugOptions(Function<Integer, Integer> debugFlagFunc, Map<BlockPos, float[]> dataIn, boolean needsServerRq, boolean showOnDebugOptions)
    {
        this.debugFlagFunc = debugFlagFunc;
        this.dataIn = dataIn;
        this.needsServerRq = needsServerRq;
        this.showOnDebugOptions = showOnDebugOptions;
    }

    public int applyDebugMode(int oldDebugMode)
    {
        return debugFlagFunc.apply(oldDebugMode);
    }
    public int removeDebugMode(int oldDebugMode)
    {
        return (511-applyDebugMode(0))&oldDebugMode;
    }

    public boolean isActive(int debugMode)
    {
        return this==NONE || (debugMode&applyDebugMode(0))>0;
    }
}
