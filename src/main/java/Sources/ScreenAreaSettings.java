package Sources;

import Utils.SystemInfo;

import java.awt.*;

public class ScreenAreaSettings implements Settings {
    private final String[] settings;

    public ScreenAreaSettings(String source, Rectangle frame) {
        String size = frame.width + "x" + frame.height;
        settings = new String[]{systemInfo.getFfmpegPath() + "/ffmpeg.exe", "-y", "-f", "gdigrab", "-r", "30", "-rtbufsize", "100M",
                "-offset_x", frame.x + "", "-offset_y", frame.y + "", "-s", size, "-i", "desktop", "-probesize", "10M", "-f",
                "dshow", "-i", "audio=" + systemInfo.getMicroName(), "-c:v", "libx264", "-r", "30", "-preset",
                "ultrafast", "-tune", "zerolatency", "-crf", "20", "-pix_fmt", "yuv420p", "-b:v", "10000k", "-maxrate",
                "15000k", "-minrate", "10000k", "-f", "flv", "\"" + source + "\""
        };
    }

    @Override
    public String[] getSetupSettings() {
        return settings;
    }

    @Override
    public void refreshPath(String newSource) {
        settings[settings.length - 1] = "\"" + newSource + "\"";
    }
}
