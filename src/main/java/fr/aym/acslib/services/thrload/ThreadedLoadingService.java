package fr.aym.acslib.services.thrload;

import fr.aym.acslib.services.ACsService;

import javax.annotation.Nullable;

public interface ThreadedLoadingService extends ACsService
{
    default void addTask(ModLoadingSteps finishFor, String taskName, Runnable task) {
        addTask(finishFor, taskName, task, null);
    }
    void addTask(ModLoadingSteps finishFor, String taskName, Runnable task, @Nullable Runnable followingInThreadTask);

    boolean mcLoadingFinished();

    @Override
    default String getName() {
        return "ThrLoad";
    }
}