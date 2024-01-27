package home.utils;

public class Utils {

    public static void runInThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    private Utils() {
    }
}
