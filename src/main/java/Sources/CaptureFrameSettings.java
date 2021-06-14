package Sources;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CaptureFrameSettings extends AbstractSettings {
//    private final static String SETTINGS = "%s/ffmpeg.exe|-y|-f|gdigrab|-rtbufsize|%sM" +
//            "|-i|title=%s|-probesize|10M|-f|dshow|-i|audio=%s|-c:v|h264|-r|%s|-preset|%s|-tune|zerolatency" +
//            "|-crf|%s|-pix_fmt|yuv420p|-b:v|%sk|-maxrate|10000k|-minrate|5000k|-f|flv|%s|-vf|scale=ceil(iw/2)*2:ceil(ih/2)*2";
    private final String source;

    public CaptureFrameSettings(String source, String frame) {
        super("", "title=" + frame);
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
