package fr.aym.acslib.services.thrload;

public class ModLoadingSteps
{
    public static final ModLoadingSteps NOT_INIT = new ModLoadingSteps(0),
            PRE_INIT = new ModLoadingSteps(10000),
            BLOCK_REGISTRY = new ModLoadingSteps(20000),
            ITEM_REGISTRY = new ModLoadingSteps(25000),
            INIT = new ModLoadingSteps(30000),
            POST_INIT = new ModLoadingSteps(40000),
            FINISH_LOAD = new ModLoadingSteps(50000),
            NEVER = new ModLoadingSteps(100000);

    private final int index;

    public ModLoadingSteps(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
