package Record;

import Utils.RecordUtils;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractRecord {
    private final static ExecutorService executor = Executors.newSingleThreadExecutor();
    protected final double FRAME_RATE = 15;
    public final AtomicBoolean IS_RECORD = new AtomicBoolean(false);

    abstract public void record();

    protected void start(Path outputFilename, Rectangle screenBounds) {
        IS_RECORD.set(true);
        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename.toString());
        writer.addVideoStream(
                0,
                0,
                ICodec.ID.CODEC_ID_MPEG4,
                screenBounds.width,
                screenBounds.height
        );

        long startTime = System.nanoTime();
        executor.execute(() -> {
            while (IS_RECORD.get()) {
                BufferedImage screen = RecordUtils.getDesktopScreenshot(screenBounds);
                BufferedImage bgrScreen = RecordUtils.convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
                writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
                try {
                    Thread.sleep((long) (1000 / FRAME_RATE));
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            writer.close();
        });
    }

    public void stop() {
        IS_RECORD.set(false);
    }
}
