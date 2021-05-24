package Sources;

import Utils.RecordUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenAreaSource implements BaseSource {
    private final Rectangle rectangle;

    public ScreenAreaSource() {
        this.rectangle = new Rectangle(
                0,
                0,
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height
        );
    }

    public ScreenAreaSource(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    @Override
    public BufferedImage getImage() {
        BufferedImage screen = RecordUtils.getDesktopScreenshot(rectangle);
        return RecordUtils.convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
    }
}
