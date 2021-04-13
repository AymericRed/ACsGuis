package fr.nico.sqript.structures;

import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptLine;

import java.util.List;

public interface IScriptLoop {
    void initLoop(IScript script, int tabLevel, ScriptCompileGroup compileGroup, List<ScriptLine> forContainer) throws Exception;
}
