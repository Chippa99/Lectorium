package Sources;

import Utils.SystemInfo;

public class CaptureFrameSettings implements Settings {
    private final String[] settings;

    public CaptureFrameSettings(String source, String frameName) {
        settings = new String[]{systemInfo.getFfmpegPath() + "/ffmpeg.exe", "-y", "-f", "gdigrab", "-r", "30", "-rtbufsize", "100M",
                "-i", "title=" + frameName, "-probesize", "10M", "-f", "dshow", "-i", "audio=" + systemInfo.getMicroName(),
                "-c:v", "libx264", "-r", "30"/*, "-preset", "ultrafast", "-tune", "zerolatency"*/, "-crf", "20", "-pix_fmt",
                "yuv420p", "-b:v", "10000k", "-maxrate", "15000k", "-minrate", "10000k", "-f", "flv", "\"" + source + "\""
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
