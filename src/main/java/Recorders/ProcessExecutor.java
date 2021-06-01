package Recorders;

import Sources.SetupSettings;
import Utils.LectoriumThreadExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ProcessExecutor {
    private Process process;
    private SetupSettings.Settings setupSettings;
    private static final Logger log = LoggerFactory.getLogger(ProcessExecutor.class);

    public ProcessExecutor(SetupSettings.Settings setupSettings) {
        this.setupSettings = setupSettings;
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
                    if (!process.waitFor(30, TimeUnit.MINUTES)) {
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
//        getMicrophone();
    }

    public static void main(String[] args) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("ffmpeg", "-list_devices", "true", "-f", "dshow", "-i", "dummy");
            log.info("Start find microphone");
            Process process = processBuilder.start();
            ResultStreamHandler res = new ResultStreamHandler(process.getInputStream());
            LectoriumThreadExecutor.getExecutor().execute(res);
            if (!process.waitFor(30, TimeUnit.MINUTES)) {
                process.destroy();
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
            }
            String resString = res.getInput();

            //       log.info("Stream stop: {}", s);
        } catch (Exception e) {
            log.error(e.toString());
        }
       // return "";
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

    public SetupSettings.Settings getSetupSettings() {
        return setupSettings;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProcessExecutor
                && Arrays.equals(((ProcessExecutor) obj).setupSettings.getSetupSettings(), setupSettings.getSetupSettings());
    }

    @Override
    public int hashCode() {
        return ProcessExecutor.class.getName().hashCode();
    }

    private static class ResultStreamHandler implements Runnable {
        private InputStream inputStream;
        private final StringBuilder builder = new StringBuilder();

        private ResultStreamHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void run() {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
            } catch (Throwable t) {
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
        }

        public String getInput() {
            return builder.toString();
        }
    }
}
