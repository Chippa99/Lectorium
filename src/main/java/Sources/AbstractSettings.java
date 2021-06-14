package Sources;

abstract class AbstractSettings implements Settings {
   // private final String captureSettings = "-probesize|10M|-f|dshow|-i|audio=%s|-ac|2|-acodec|aac|-c:v|libx264|-vf|scale=ceil(iw/2)*2:ceil(ih/2)*2|-r|%d|-preset|%s|-tune|zerolatency" +
   //         "|-crf|%d|-pix_fmt|nv12|-async|1|-vsync|1|-segment_time|900|-segment_wrap|9|-b:v|%dk|-b:a|2000k|-maxrate|10M|-minrate|2M|-f|flv|%s|-vf";
    protected int fps = 15;
    protected int crt = 30;
    protected int bitrate = 15000;
    protected int buffer = 250;
    protected String preset;
    public final String generalSettings;

    public AbstractSettings(String frame, String settings) {
        generalSettings = String.format(
                SETTINGS,
                systemInfo.getFfmpegPath(),
                frame,
                settings
        );
    }

    @Override
    public void buildToStream() {
        preset = PresetType.ultrafast.toString();
        fps = 30;
        crt = 30;
        bitrate = 6000;
        buffer = 400;
    }

    @Override
    public void buildToFile() {
        preset = PresetType.ultrafast.toString();
        fps = 15;
        crt = 30;
        bitrate = 2000;
        buffer = 50;
    }



}
