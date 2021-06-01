package Sources;

import java.awt.*;

public class SetupSettings {
   public interface Settings {
        String[] getSetupSettings();
        void refreshPath(String newSource);
    }

    static class ScreenAreaSettings implements Settings {
        private final String[] settings;

        ScreenAreaSettings(String source, Rectangle frame) {
            String size = frame.width + "x" + frame.height;
            settings = new String[]{"ffmpeg.exe","-y", "-f","gdigrab","-r", "30", "-rtbufsize", "100M",
                    "-offset_x", frame.x + "", "-offset_y", frame.y + "", "-s", size, "-i", "desktop", "-probesize", "10M", "-f",
                    "dshow", "-i", "audio=Микрофон (Realtek High Definition Audio)", "-c:v", "libx264", "-r", "30", "-preset",
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
            settings[settings.length-1] = "\"" + newSource + "\"";
        }
    }

    static class CaptureFrameSettings implements Settings {
        private final String[] settings;

        CaptureFrameSettings(String source, String frameName) {
            settings = new String[]{"ffmpeg.exe","-y", "-f","gdigrab","-r", "30", "-rtbufsize", "100M",
                    "-i", "title=" + frameName, "-probesize", "10M", "-f", "dshow", "-i", "audio=Микрофон (Realtek High Definition Audio)",
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
            settings[settings.length-1] = "\"" + newSource + "\"";
        }
    }

    public ScreenAreaSettings buildScreenAreaSettings(String source, Rectangle frame) {
        return new ScreenAreaSettings(source, frame);
    }

    public CaptureFrameSettings buildCaptureFrameSettings(String source, String frameName) {
        return new CaptureFrameSettings(source, frameName);
    }
}


