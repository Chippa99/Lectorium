package Utils;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RecordUtils {
    private final static String SEPARATOR = "_";

    public static Path getFreeFileName(Path path) {
        int count = 0;
        if (Files.exists(path)) {
            Path parent = path.getParent();
            String format = path.getFileName().toString().split("\\.")[1];
            String[] splitName = path
                    .getFileName()
                    .toString()
                    .replaceFirst("\\." + format, "")
                    .split(SEPARATOR);
            if (splitName.length >= 2) {
                count = Integer.parseInt(splitName[1]);
            }
            Path fileName = Paths.get(splitName[0] + SEPARATOR + ++count + "." + format);
            Path newFileName = parent.resolve(fileName);
            return getFreeFileName(newFileName);
        } else {
            return path;
        }
    }

    public static Rectangle getScreenSize() {
        Dimension dem = Toolkit.getDefaultToolkit().getScreenSize();
        return new Rectangle(0, 0, dem.width, dem.height);
    }

    public static void callNotificationFrame(String title, String message) {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            throw new IllegalStateException(e);
        }

        trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
    }

    public static String toUTF8(String name) {
        return new String(name.getBytes(), StandardCharsets.UTF_8);
    }
}
