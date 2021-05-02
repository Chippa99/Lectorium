package Record;

import java.awt.*;
import java.nio.file.Path;

public class ScreenAreaRecord extends AbstractRecord {
    private final Path filePath;
    private final Rectangle windowSize;

    public ScreenAreaRecord(Path filePath) {
        this.filePath = filePath;
        this.windowSize = new Rectangle(
                0,
                0,
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height
        );
    }

    public ScreenAreaRecord(Path filePath, Rectangle windowSize) {
        this.filePath = filePath;
        this.windowSize = windowSize;
    }

    @Override
    public void record() {
        start(filePath, windowSize);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractRecord;
    }

    @Override
    public int hashCode() {
        return AbstractRecord.class.getName().hashCode();
    }
}
