package Recorders;

import Sources.BaseSource;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractRecord {
    protected final static ExecutorService executor = Executors.newSingleThreadExecutor();
    protected final AtomicBoolean IS_RECORD = new AtomicBoolean(false);
    protected final double FRAME_RATE = 15;
    protected final BaseSource source;

    public AbstractRecord(BaseSource source) {
        this.source = source;
    }

    public void record(Path output) {
        IS_RECORD.set(true);
        start(output);
    }

    public abstract void start(Path outputFilename);

    public void stop() {
        IS_RECORD.set(false);
    }
}
