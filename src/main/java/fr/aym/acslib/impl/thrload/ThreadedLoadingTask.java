package fr.aym.acslib.impl.thrload;

import fr.aym.acslib.ACsPlatform;
import fr.aym.acslib.services.thrload.ModLoadingSteps;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.common.LoaderException;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ThreadedLoadingTask implements Runnable
{
    private final Runnable task;
    private final ModLoadingSteps endBefore;
    private final int id;
    private final String name;
    private final Runnable followingInThreadTask;
    private final LightThreadedModLoader executor;

    private static int lastId;

    public ThreadedLoadingTask(Runnable task, ModLoadingSteps endBefore, Runnable followingInThreadTask, String taskName, LightThreadedModLoader executor) {
        this.task = task;
        this.endBefore = endBefore;
        this.followingInThreadTask = followingInThreadTask;
        this.executor = executor;
        this.name = taskName;
        id = lastId;
        lastId++;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        try {
            task.run();
            executor.onEnd(this, followingInThreadTask, (System.currentTimeMillis()-time));
        } catch (Exception e) {
            ACsPlatform.serviceFatal(ACsPlatform.provideService("ThrLoad"), "Error in task "+name, e);
            //TODO ERROR SERVCICE DynamXErrorTracker.addError(ErrorType.INIT, "LoadingTasks", "ThreadTask "+name, e.toString(), DynamXErrorTracker.ErrorLevel.FATAL);
            executor.onEnd(this, () -> {
                if(e instanceof CustomModLoadingErrorDisplayException)
                    throw e;
                throw new ThreadedLoadingException(name, e);
            }, (System.currentTimeMillis()-time));
        }
    }

    public boolean shouldEndNow(ModLoadingSteps step) {
        return endBefore.getIndex() <= step.getIndex();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ThreadedLoadingTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static class ThreadedLoadingException extends LoaderException
    {
        public ThreadedLoadingException(String taskName, Exception e) {
            super("Exception in task "+taskName, e);
            setStackTrace(new StackTraceElement[0]);
        }

        @Override
        public String toString() {
            return getLocalizedMessage();
        }

        @Override
        public void printStackTrace(final PrintWriter s)
        {
            super.printStackTrace(s);
            printCustomMessage(new WrappedPrintStream()
            {
                @Override
                public void println(String line)
                {
                    s.println(line);
                }
            });
        }
        @Override
        public void printStackTrace(final PrintStream s)
        {
            super.printStackTrace(s);
            printCustomMessage(new WrappedPrintStream()
            {
                @Override
                public void println(String line)
                {
                    s.println(line);
                }
            });
        }

        protected void printCustomMessage(WrappedPrintStream stream) {}
    }
}
