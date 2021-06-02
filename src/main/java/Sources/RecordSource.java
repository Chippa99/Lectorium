package Sources;

import Utils.SystemInfo;

import java.awt.*;

public class RecordSource {
    private final String frameName;
    private final Rectangle screenSize;

    public RecordSource(Rectangle screenSize) {
        this.screenSize = screenSize;
        frameName = null;
    }

    public RecordSource(String frameName) {
        this.screenSize = null;
        this.frameName = frameName;
    }

    public Settings getSetupSettings(String file) {
       return screenSize != null ?
                new ScreenAreaSettings(file, screenSize) :
                new CaptureFrameSettings(file, frameName);
    }
}
