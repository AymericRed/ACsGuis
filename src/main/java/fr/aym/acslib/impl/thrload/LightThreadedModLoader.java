package fr.aym.acslib.impl.thrload;

import fr.aym.acslib.ACsPlatform;
import fr.aym.acslib.services.ACsRegisteredService;
import fr.aym.acslib.services.thrload.ModLoadingSteps;
import fr.aym.acslib.services.thrload.ThreadedLoadingService;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.event.*;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@ACsRegisteredService(name = "ThrLoad", version = "1.0.0")
public class LightThreadedModLoader implements ThreadedLoadingService
{
    private ExecutorService POOL;
    private Map<ThreadedLoadingTask, Future<?>> tasks;
    private Queue<Runnable> inThreadTasks;
    private ModLoadingSteps step = ModLoadingSteps.NOT_INIT;
    private final AtomicLong economised = new AtomicLong();

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public void initService() {
        POOL = Executors.newFixedThreadPool(1, new DefaultThreadFactory("ACsThreadedLoader"));
        tasks = new ConcurrentHashMap<>();
        inThreadTasks = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void onFMLStateEvent(FMLStateEvent event) {
        if(event instanceof FMLPreInitializationEvent)
            step(ModLoadingSteps.PRE_INIT);
        else if(event instanceof FMLInitializationEvent)
            step(ModLoadingSteps.INIT);
        else if(event instanceof FMLPostInitializationEvent)
            step(ModLoadingSteps.POST_INIT);
        else if(event instanceof FMLLoadCompleteEvent)
            step(ModLoadingSteps.FINISH_LOAD);
    }

    @Override
    public void addTask(ModLoadingSteps finishFor, String taskName, Runnable task, @Nullable Runnable followingInThreadTask) {
        if(POOL.isShutdown())
        {
            ACsPlatform.serviceInfo(this, "Pool is shutdown, running task now "+taskName);
            task.run();
            if (followingInThreadTask != null) {
                followingInThreadTask.run();
            }
        }
        else if(finishFor.getIndex() <= step.getIndex())
        {
            ACsPlatform.serviceWarn(this, "Got a past task, running it now "+taskName);
            task.run();
            if (followingInThreadTask != null) {
                followingInThreadTask.run();
            }
        }
        else {
            ThreadedLoadingTask taskt = new ThreadedLoadingTask(task, finishFor, followingInThreadTask, taskName, this);
            tasks.put(taskt, POOL.submit(taskt));
        }
    }

    @Override
    public void step(ModLoadingSteps step) {
        ACsPlatform.serviceDebug(this, "Transition: "+step);
        for(Map.Entry<ThreadedLoadingTask, Future<?>> task : tasks.entrySet()) {
            if(task.getKey().shouldEndNow(step) && !task.getValue().isDone()) {
                long time = System.currentTimeMillis();
                ACsPlatform.serviceDebug(this, "Waiting on "+task.getKey().toString());
                ProgressManager.ProgressBar bar = ProgressManager.push(getName() + " : "+task.getKey().getName(), 1);
                try {
                    bar.step(task.getKey().getName());
                    task.getValue().get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException("Threaded loading task error", e);
                }
                finally {
                    ProgressManager.pop(bar);
                }
                ACsPlatform.serviceDebug(this, "Waited " + (System.currentTimeMillis()-time) + " ms");
            }
        }
        this.step = step;

        long time = System.currentTimeMillis();
        if(!inThreadTasks.isEmpty()) {
            //DynamXMain.log.debug("Run in thread tasks");
            ProgressManager.ProgressBar bar = ProgressManager.push("Load " + ACsPlatform.NAME + " resources", inThreadTasks.size());
            int i = 0;
            while (!inThreadTasks.isEmpty()) {
                i++;
                bar.step("Task "+i);
                inThreadTasks.poll().run();
            }
            ACsPlatform.serviceDebug(this, "TT Took " + (System.currentTimeMillis() - time) + " ms");
        }

        if(step == ModLoadingSteps.FINISH_LOAD)
            ACsPlatform.serviceInfo(this, "Le lancement multithreadé a économisé "+economised.get()+" ms");
    }

    protected void onEnd(ThreadedLoadingTask task, Runnable followingInThreadTask, long tookTime) {
        economised.addAndGet(tookTime);
        if(followingInThreadTask != null) {
            if(POOL.isShutdown())
            {
                ACsPlatform.serviceWarn(this, "Received following task too late, do it now !");
                followingInThreadTask.run();
            }
            /*else if(step == ModLoadingSteps.FULLY_LOADED)
            {
                DynamXMain.log.info("Received following task after mc fully loaded, do it now !");
                followingInThreadTask.run();
            }*/
            else
                inThreadTasks.offer(followingInThreadTask);
        }
        tasks.remove(task);
        ACsPlatform.serviceDebug(this, "Finished "+task.toString()+" in "+tookTime+" ms during "+step);
    }

    /**
     * @return True if LoadCompleteEvent has been fired by fml
     */
    @Override
    public boolean mcLoadingFinished() {
        return step == ModLoadingSteps.FINISH_LOAD;
    }

    private class DefaultThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final String namePrefix;

        public DefaultThreadFactory(String prefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = prefix;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix,
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            t.setUncaughtExceptionHandler((th, e) -> ACsPlatform.serviceError(LightThreadedModLoader.this, "Error in "+t.getName(), e));
            return t;
        }
    }
}
