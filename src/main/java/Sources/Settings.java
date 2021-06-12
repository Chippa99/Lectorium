package Sources;

import Utils.SystemInfo;

public interface Settings {
    String SETTINGS = "%s/ffmpeg.exe|-y|-thread_queue_size|%d|-f|gdigrab|-rtbufsize|%dM|%s" +
            "|-i|%s|-probesize|10M|-f|dshow|-i|audio=%s|-ac|2|-acodec|aac|-c:v|libx264|-vf|scale=1366:768|-r|%d|-preset|%s|-tune|zerolatency" +
            "|-crf|%d|-pix_fmt|nv12|-async|1|-vsync|1|-segment_time|900|-segment_wrap|9|-b:v|%dk|-b:a|2000k|-maxrate|10M|-minrate|2M|-f|flv|%s|-vf|scale=ceil(iw/2)*2:ceil(ih/2)*2";
    String SEPARATOR = "\\|";
    SystemInfo systemInfo = new SystemInfo();
    String[] getSetupSettings();
    void buildToStream();
    void buildToFile();
}



