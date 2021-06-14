package Sources;

import java.awt.*;

public class ScreenAreaSettings extends AbstractSettings {
//    private final static String SETTINGS = "%s/ffmpeg.exe|-y|-f|gdigrab|-rtbufsize|%sM|-offset_x|%s|-offset_y|%s" +
//            "|-s|%s|-i|desktop|-probesize|10M|-f|dshow|-i|audio=%s|-c:v|h264|-r|%s|-preset|%s|-tune|zerolatency" +
//            "|-crf|%s|-pix_fmt|yuv420p|-b:v|%sk|-b:a|600k|-maxrate|10000k|-minrate|4500k|-f|flv|%s";
    private final String source;

    public ScreenAreaSettings(String source, Rectangle frame) {
        super("-offset_x|" + frame.x + "|-offset_y|" + frame.y + "|-s|" + frame.width + "x" + frame.height, "desktop");
        this.source = source;
    }

    @Override
    public String getSetupSettings() {
        return String.format(DETAILED_SETTINGS,
                        1024,
                        buffer,
                        systemInfo.getMicroName(),
                        fps,
                        preset,
                        crt,
                        bitrate,
                        source
                );

    }
}
