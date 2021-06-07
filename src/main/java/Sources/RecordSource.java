package Sources;

import java.awt.*;

public class RecordSource {
    private String frameName;
    private Rectangle screenSize;

    public RecordSource(Rectangle screenSize) {
        this.screenSize = screenSize;
    }

    public void setScreenArea(Rectangle screenSize) {
        this.screenSize = screenSize;
        frameName = null;
    }

    public void setCaptureFrame(String frameName) {
        this.screenSize = null;
        this.frameName = frameName;
    }

    public Settings getSetupSettings(String source, String preset) {
       return screenSize != null ?
                new ScreenAreaSettings(source, screenSize, preset) :
                new CaptureFrameSettings(source, frameName, preset);
    }
}
