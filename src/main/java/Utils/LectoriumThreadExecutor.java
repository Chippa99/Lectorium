package Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LectoriumThreadExecutor {
    private static ExecutorService executor;
    public static ExecutorService getExecutor() {
        if (executor == null) {
            executor = Executors.newFixedThreadPool(2);
        }
        return executor;
    }
}
