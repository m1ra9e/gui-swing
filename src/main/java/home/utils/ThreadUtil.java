/*******************************************************************************
 * Copyright 2021-2024 Lenar Shamsutdinov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
        // For work "Thread.setDefaultUncaughtExceptionHandler(handler)" (in
        // Main.setUncaughtExceptionProcessing()) must be used
        // "EXECUTOR.execute()" but not "EXECUTOR.submit()".
        //
        // Because exceptions thrown from tasks make it to the uncaught exception
        // handler only for tasks submitted with execute; for tasks submitted with
        // submit, any thrown exception, checked or not, is considered to be part of the
        // task’s return status. If a task submitted with submit terminates with an
        // exception, it is rethrown by Future.get, wrapped in an ExecutionException.
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
