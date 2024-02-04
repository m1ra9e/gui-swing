package home.utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ThreadUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadUtil.class);

    private static final ExecutorService EXECUTOR = Executors
            .newSingleThreadExecutor(new DaemonThreadFactory());

    static {
        Runtime.getRuntime().addShutdownHook(new ExecutorShutdownThread());
    }

    public static void runInThread(Runnable runnable) {
        EXECUTOR.execute(runnable);
    }

    @Deprecated(forRemoval = true)
    public static void runInThread(String description, Runnable runnable) {
        var thread = new Thread(runnable);
        thread.setName(description);
        thread.setDaemon(true);
        thread.start();
    }

    private ThreadUtil() {
    }

    private static final class DaemonThreadFactory implements ThreadFactory {

        private final ThreadFactory defaultTf = Executors.defaultThreadFactory();

        private DaemonThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable runnable) {
            var thread = defaultTf.newThread(runnable);
            thread.setDaemon(true);
            return thread;
        }
    }

    private static final class ExecutorShutdownThread extends Thread {

        private ExecutorShutdownThread() {
            super("-> executor shutdown thread");
        }

        @Override
        public void run() {
            Thread.currentThread().setName("-> shutdown_hook : shutdown executor");
            try {
                EXECUTOR.shutdown();
                if (EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOG.info("Executor service stopped successfully.");
                } else {
                    LOG.info("Executor did not terminate in the specified time.");
                    List<Runnable> droppedTasks = EXECUTOR.shutdownNow();
                    LOG.info("Executor was abruptly shut down. " + droppedTasks.size()
                            + " tasks will not be executed.");
                }
            } catch (InterruptedException e) {
                LogUtils.logAndShowError(LOG, null, "Interrupted the operation of stopping tasks.",
                        "Stopping tasks error", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
