package Sources;


import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static Sources.Settings.SEPARATOR;

public class RecordSource {
    private String frameName;
    private Rectangle screenSize;
    private final List<AbstractSettings> settingsList = new ArrayList<>();

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

    private AbstractSettings getSetupSettings(String source) {
       return screenSize != null ?
                new ScreenAreaSettings(source, screenSize) :
                new CaptureFrameSettings(source, frameName);
    }

    public void addFileSettings(String source) {
        AbstractSettings settings = getSetupSettings(source);
        settings.buildToFile();
        settingsList.add(settings);
    }

    public void addStreamSettings(String source) {
        AbstractSettings settings = getSetupSettings(source);
        settings.buildToStream();
        settingsList.add(settings);
    }

    public void clearSettings() {
        settingsList.clear();
    }

    public String[] buildSettings() {
        final String[] generalSettings = {settingsList.get(0).generalSettings};
        settingsList.forEach(it -> {
            generalSettings[0] += it.getSetupSettings();
        });
        return Arrays.stream(generalSettings[0].split(SEPARATOR)).filter(it -> !it.isEmpty()).collect(Collectors.toList()).toArray(new String[]{});
    }
}
