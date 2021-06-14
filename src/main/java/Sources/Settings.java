package Sources;

import Utils.SystemInfo;

public interface Settings {
    String SETTINGS = "%s/ffmpeg.exe|-y|-f|gdigrab|%s|-i|%s|";
    //ceil(iw/2)*2:ceil(ih/2)*2
    String DETAILED_SETTINGS = "-thread_queue_size|%d|-rtbufsize|%dM|-probesize|10M|-f|dshow|-i|audio=%s|-ac|2|" +
            "-acodec|aac|-c:v|libx264|-vf|scale=1280:720|-r|%d|-preset|%s|-tune|zerolatency" +
            "|-crf|%d|-pix_fmt|nv12|-async|1|-vsync|1|-segment_time|900|-segment_wrap|9|-b:v|%dk|-b:a|160k|-maxrate|10M|" +
            "-minrate|2M|-f|flv|%s|";
    String SEPARATOR = "\\|";
    SystemInfo systemInfo = new SystemInfo();
    String getSetupSettings();
    void buildToStream();
    void buildToFile();
}



