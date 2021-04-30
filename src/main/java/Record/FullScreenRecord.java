package Record;

import java.awt.*;
import java.nio.file.Path;

public class FullScreenRecord extends AbstractRecord {
    private final Path filePath;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public FullScreenRecord(Path filePath) {
      this.filePath = filePath;
    }

    @Override
    public void record() {
        start(filePath,  new Rectangle(0,0, screenSize.width, screenSize.height));
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof AbstractRecord;
    }

    @Override
    public int hashCode(){
        return AbstractRecord.class.getName().hashCode();
    }
}
