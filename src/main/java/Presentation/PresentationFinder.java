package Presentation;

import Utils.RecordUtils;
import Utils.WindowsInfo;
import java.util.Map;

public class PresentationFinder {
    private final static String PRESENTATION_FRAME_CLASS_NAME = "screenClass";

    public String find() {
        Map<String, String> names = WindowsInfo.findAllClassNames();
        if (names.containsKey(PRESENTATION_FRAME_CLASS_NAME)) {
            RecordUtils.callNotificationFrame("Запись", "Лекториум автоматически начал запись презентации");
            return names.get(PRESENTATION_FRAME_CLASS_NAME);
        }
        return null;
    }
}
