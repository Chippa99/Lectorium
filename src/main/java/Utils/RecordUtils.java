package Utils;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecordUtils {
    private final static String SEPARATOR = "_";

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;

        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } else {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    public static BufferedImage getDesktopScreenshot(Rectangle screenBounds) {
        try {
            Robot robot = new Robot();
            return robot.createScreenCapture(screenBounds);
        } catch (AWTException e) {
            e.printStackTrace();
            return null;
        }
    }

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

    public static String convertToUTF8(String str) {
        Charset cset = StandardCharsets.UTF_8;
        ByteBuffer buf = cset.encode(str);
        byte[] b = buf.array();
        return new String(b);
    }
}
