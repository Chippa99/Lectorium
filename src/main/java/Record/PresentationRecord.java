package Record;

import Utils.RecordUtils;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class PresentationRecord extends AbstractRecord {
    private volatile BufferedImage image;

    public PresentationRecord(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void record() {

    }

    protected void recordSlide(Path outputFilename) {
        IS_RECORD.set(true);
        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename.toString());
        writer.addVideoStream(
                0,
                0,
                ICodec.ID.CODEC_ID_MPEG4,
                image.getWidth(),
                image.getHeight()
        );

        long startTime = System.nanoTime();

        // BufferedImage bgrScreen = RecordUtils.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
        writer.encodeVideo(0, image, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        try {
            Thread.sleep((long) (1000 / FRAME_RATE));
        } catch (InterruptedException e) {
            // ignore
        }
        writer.close();
    }
}
