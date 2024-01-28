package home.utils;

import java.awt.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.slf4j.Logger;

public final class Utils {

    // location of the log files is configured in file 'log4j2.xml'
    private static final Path LOG_FILE = Paths.get("logs", "app.log");

    public static void runInThread(String description, Runnable runnable) {
        var thread = new Thread(runnable);
        thread.setName(description);
        thread.setDaemon(true);
        thread.start();
    }

    public static <T> void logAndShowError(Logger log, Component parentComponent,
            String msg, String title, Throwable t) {
        String threadName = "[THREAD: " + Thread.currentThread().getName() + " ] ";

        log.error("Exception: " + threadName, t);

        var sb = new StringBuilder();
        sb.append(threadName).append("\n").append(msg)
                .append("\n\nDescription.\n").append(t.getMessage());

        if (Files.exists(LOG_FILE)) {
            sb.append("\n\nLog file:\n").append(LOG_FILE.toAbsolutePath().toString());
        }

        JOptionPane.showMessageDialog(parentComponent, sb.toString(),
                title, JOptionPane.ERROR_MESSAGE);
    }

    public static SQLException logAndCreateSqlException(String errorMsg, Logger log) {
        return logAndCreateSqlException(errorMsg, log, null);

    }

    public static SQLException logAndCreateSqlException(String errorMsg, Logger log, Exception e) {
        if (e == null) {
            log.error(errorMsg);
            return new SQLException(errorMsg);
        }

        log.error(errorMsg, e);
        return new SQLException(errorMsg, e);
    }

    public static IllegalStateException logAndCreateIllegalStateException(String errorMsg, Logger log, Exception e) {
        log.error(errorMsg, e);
        return new IllegalStateException(errorMsg, e);
    }

    private Utils() {
    }
}
