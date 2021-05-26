package ApiYoutube;

import Sources.BaseSource;
import Utils.LectoriumThreadExecutor;
import com.google.api.client.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class StreamExecutor {
    private final String url;
    private final String streamName;
    private final BaseSource source;
    private Process process;
    private static final Logger log = LoggerFactory.getLogger(StreamExecutor.class);

    public StreamExecutor(String url, String streamName, BaseSource source) {
        this.url = url;
        this.streamName = streamName;
        this.source = source;
    }

    public void start() {
        LectoriumThreadExecutor.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    String size = source.getImage().getWidth() + "x" + source.getImage().getHeight();
                    String posX = String.valueOf(source.getX());
                    String posY = String.valueOf(source.getY());

                    processBuilder.command("ffmpeg.exe","-y", "-f","gdigrab","-r", "30", "-rtbufsize", "100M", "-offset_x", posX, "-offset_y",posY,
                            "-s", size  , "-i","desktop",
                            "-probesize", "10M", "-f", "dshow", "-i",
                            "audio=Микрофон (Realtek High Definition Audio)", "-c:v", "libx264",
                           "-r", "30", "-preset", "ultrafast", "-tune", "zerolatency", "-crf", "20", "-pix_fmt", "yuv420p", "-b:v", "10000k", "-maxrate", "15000k", "-minrate", "10000k",
                            "-f", "flv", "\"" + url + "/" + streamName + "\"");
                    processBuilder.inheritIO();
                    log.info("Stream start");
                    process = processBuilder.start();
                    if (!process.waitFor(30, TimeUnit.SECONDS)) {
                       stop();
                    }
                    log.info("Stream stop");
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        });
//        getMicrophone();
    }

    private String getMicrophone() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("ffmpeg", "-list_devices", "true", "-f", "dshow", "-i", "dummy");
            log.info("Start find microphone");
            process = processBuilder.start();
            String s = InputReader.read(process.getInputStream());
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                stop();
            }
            log.info("Stream stop: {}", s);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return "";
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

    private static class InputReader {
        public static String read(InputStream inputStream) {
            StringBuilder builder = new StringBuilder();
            LectoriumThreadExecutor.getExecutor().execute(new Runnable() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                @Override
                public void run() {
                    String s;
                    try {
                        while ((s = reader.readLine()) != null) {
                            builder.append(s + "\n");
                        }
                    } catch (IOException e) {
                        log.error(e.toString());
                    }
                }
            });
            return builder.toString();
        }
    }
}
