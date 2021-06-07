package Sources;

public class CaptureFrameSettings implements Settings {
    private final static String SETTINGS = "%s/ffmpeg.exe|-y|-f|gdigrab|-r|30|-rtbufsize|250M" +
            "|-i|title=%s|-probesize|10M|-vcodec|h264|-f|dshow|-i|audio=%s|-c:v|libx264|-r|30|-preset|%s|-tune|zerolatency" +
            "|-crf|20|-pix_fmt|yuv420p|-b:v|15000k|-maxrate|10000k|-minrate|5000k|-f|flv|%s|-vf|scale=ceil(iw/2)*2:ceil(ih/2)*2";
    private final String source;
    private final String frame;
    private final String preset;

    public CaptureFrameSettings(String source, String frame, String preset) {
        this.source = source;
        this.frame = frame;
        this.preset = preset;
    }

    @Override
    public String[] getSetupSettings() {
        String settings =
                String.format(SETTINGS, systemInfo.getFfmpegPath(), frame, systemInfo.getMicroName(), preset, source);
        return settings.split(SEPARATOR);
    }
}
