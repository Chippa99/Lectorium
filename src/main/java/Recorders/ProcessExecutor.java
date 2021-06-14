package Recorders;

import Sources.Settings;
import Utils.LectoriumThreadExecutor;
import Utils.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessExecutor {
    private Process process;
    private final Settings setupSettings;
    private static final Logger log = LoggerFactory.getLogger(ProcessExecutor.class);

    public ProcessExecutor(Settings setupSettings) {
        this.setupSettings = setupSettings;
    }

    public void start() {
        LectoriumThreadExecutor.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command(setupSettings.getSetupSettings());
                    processBuilder.redirectErrorStream(true);
                    log.info("Stream start");
                    process = processBuilder.start();
                    ResultStreamHandler res = new ResultStreamHandler(process.getInputStream());
                    res.run();
//                    process.waitFor();
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
            log.error("Process has ben stop");
        } catch (InterruptedException e) {
            log.error("Error during waiting destroy process");
        }
    }

    private class ResultStreamHandler implements Runnable {
        private final InputStream inputStream;

        private ResultStreamHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                Pattern patter = Pattern.compile("Failed to capture image");
                Pattern error = Pattern.compile("error");
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    log.info(line);
                    if (patter.matcher(line).find()) {
                        stop();
                    } else if (error.matcher(line).find()) {
                        log.error(line);
                    }
                }
            } catch (Throwable t) {
                log.error(t.toString());
            }
        }
    }
}
