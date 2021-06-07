package Sources;

import java.awt.*;

public class ScreenAreaSettings implements Settings {
    private final static String SETTINGS = "%s/ffmpeg.exe|-y|-f|gdigrab|-r|30|-rtbufsize|250M|-offset_x|%s|-offset_y|%s" +
            "|-s|%s|-i|desktop|-probesize|10M|-vcodec|h264|-f|dshow|-i|audio=%s|-c:v|libx264|-r|30|-preset|%s|-tune|zerolatency" +
            "|-crf|20|-pix_fmt|yuv420p|-b:v|15000k|-maxrate|10000k|-minrate|5000k|-f|flv|%s";
    private final String source;
    private final Rectangle frame;
    private final String preset;

    public ScreenAreaSettings(String source, Rectangle frame, String preset) {
        this.source = source;
        this.frame = frame;
        this.preset = preset;
    }

    @Override
    public String[] getSetupSettings() {
        String size = frame.width + "x" + frame.height;
        String settings =
                String.format(SETTINGS, systemInfo.getFfmpegPath(), frame.x, frame.y, size, systemInfo.getMicroName(), preset, source);
        return settings.split(SEPARATOR);
    }
}
