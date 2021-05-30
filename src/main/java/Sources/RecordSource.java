package Sources;

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

    public String getFrameName() {
        return frameName;
    }

    public Rectangle getScreenSize() {
        return screenSize;
    }

    public SetupSettings.Settings getSetupSettings(String file) {
       return screenSize == null ?
                new SetupSettings().buildCaptureFrameSettings(file, frameName) :
                new SetupSettings().buildScreenAreaSettings(file, screenSize);
    }
}
