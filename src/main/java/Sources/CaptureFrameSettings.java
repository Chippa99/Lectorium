package Sources;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CaptureFrameSettings extends AbstractSettings {
//    private final static String SETTINGS = "%s/ffmpeg.exe|-y|-f|gdigrab|-rtbufsize|%sM" +
//            "|-i|title=%s|-probesize|10M|-f|dshow|-i|audio=%s|-c:v|h264|-r|%s|-preset|%s|-tune|zerolatency" +
//            "|-crf|%s|-pix_fmt|yuv420p|-b:v|%sk|-maxrate|10000k|-minrate|5000k|-f|flv|%s|-vf|scale=ceil(iw/2)*2:ceil(ih/2)*2";
    private final String source;
    private final String frame;

    public CaptureFrameSettings(String source, String frame) {
        this.source = source;
        this.frame = frame;
    }

    @Override
    public String[] getSetupSettings() {
        String settings =
                String.format(SETTINGS,
                        systemInfo.getFfmpegPath(),
                        1024,
                        buffer,
                        "",
                        "title=" + frame,
                        systemInfo.getMicroName(),
                        fps,
                        preset,
                        crt,
                        bitrate,
                        source
                );
        return Arrays.stream(settings.split(SEPARATOR)).filter(it -> !it.isEmpty()).collect(Collectors.toList()).toArray(new String[]{});
    }
}
