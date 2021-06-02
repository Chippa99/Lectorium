package Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemInfo {
    private static final Logger log = LoggerFactory.getLogger(SystemInfo.class);
    private static final String FFMPEG_STRING = "ffmpeg";
    private static final String SEPARATOR = ";";
    private final String microName;
    private final Path ffmpegPath;

    public SystemInfo() {
        String path = System.getenv("FFMPEG_PATH");
        if (path != null) {
            ffmpegPath = Paths.get(path);
        } else {
            ffmpegPath = Paths.get(getPath());
        }
        microName = foundMicrophone();
    }

    public String getPath() {
        String env = System.getenv("PATH");
        String[] paths = env.split(SEPARATOR);
        for(int i = 0; i < paths.length; i++) {
            if (paths[i].contains(FFMPEG_STRING)) {
                return paths[i];
            }
        }
        throw new IllegalArgumentException("Missing path to ffmpeg: [" + env + "]");
    }

    public String foundMicrophone() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(ffmpegPath + "/ffmpeg", "-list_devices", "true", "-f", "dshow", "-i", "dummy");
            processBuilder.redirectErrorStream(true);
            log.info("Start find microphone");
            Process process = processBuilder.start();

            ResultStreamHandler res = new ResultStreamHandler(process.getInputStream());
            res.run();
            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroy();
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
            }
            Pattern r = Pattern.compile("\\[dshow\\s@\\s\\w{16}\\]\\sDirectShow\\saudio\\sdevices\\s\\[dshow\\s@\\s\\w{16}\\]\\s\\s\"(.{0,60})\"\\s");
            Matcher m = r.matcher(res.getInput());
            if (m.find()) {
                log.info("Found microphone: [{}]", m.group(1));
                return m.group(1);
            } else {
                throw new IllegalArgumentException("Could not found microphone");
            }
        } catch (Exception e) {
            log.error(e.toString());
            throw new IllegalArgumentException(e);
        }
    }

    private static class ResultStreamHandler implements Runnable {
        private final InputStream inputStream;
        private final StringBuilder builder = new StringBuilder();

        private ResultStreamHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line).append(" ");
                }
                System.out.println();
            } catch (Throwable t) {
                log.error(t.toString());
            }
        }

        public String getInput() {
            return builder.toString();
        }
    }

    public String getMicroName() {
        return microName;
    }

    public Path getFfmpegPath() {
        return ffmpegPath;
    }
}
