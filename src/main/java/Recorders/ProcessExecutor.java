package Recorders;

import Sources.Settings;
import Utils.LectoriumThreadExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ProcessExecutor {
    private Process process;
    private Settings setupSettings;
    private static final Logger log = LoggerFactory.getLogger(ProcessExecutor.class);
    public enum RECORD_TYPE {FILE, STREAM}
    private final RECORD_TYPE recordType;

    public ProcessExecutor(Settings setupSettings, RECORD_TYPE recordType) {
        this.setupSettings = setupSettings;
        this.recordType = recordType;
    }

    public void start() {
        LectoriumThreadExecutor.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command(setupSettings.getSetupSettings());
                    processBuilder.inheritIO();
                    log.info("Stream start");
                    process = processBuilder.start();
                    if (!process.waitFor(15, TimeUnit.SECONDS)) {
                        stop();
                    }
                    int exitCode = process.exitValue();
                    if (!(exitCode == 0 || exitCode == 1))
                        throw new IllegalStateException("Process " + setupSettings.getSetupSettings()[0] + "stopped with exitCode - " + exitCode);
                    log.info("Stream stop");
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        });
    }

    public void stop() {
        process.destroy();
        try {
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroyForcibly();
            }
        } catch (InterruptedException e) {
            log.error("Error during waiting destroy process");
        }
    }

    public Settings getSetupSettings() {
        return setupSettings;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProcessExecutor && ((ProcessExecutor)obj).recordType == recordType;
            //    && ((ProcessExecutor)obj).setupSettings.getClass().isAssignableFrom(setupSettings.getClass());
    }

    @Override
    public int hashCode() {
        return ProcessExecutor.class.getName().hashCode();
    }
}
