package home.utils;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class Utils {

    public static void runInThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static <T> void logAndShowError(Logger log, Component parentComponent,
            String msg, String title, Exception e) {
        log.error("Exception: ", e);
        JOptionPane.showMessageDialog(parentComponent,
                msg + "\n\nDescription.\n" + e.getLocalizedMessage(),
                title, JOptionPane.ERROR_MESSAGE);
    }

    public static IOException getNewException(Exception e, String msg) {
        var ex = new IOException(msg);
        ex.addSuppressed(e);
        return ex;
    }

    private Utils() {
    }
}
