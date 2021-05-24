package Recorders;


import Sources.BaseSource;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class FileRecord extends AbstractRecord {

    public FileRecord(BaseSource source) {
        super(source);
    }

    @Override
    public void start(Path output) {
        final IMediaWriter writer = ToolFactory.makeWriter(output.toString());
        BufferedImage b = source.getImage();
        writer.addVideoStream(
                0,
                0,
                ICodec.ID.CODEC_ID_MPEG4,
                b.getWidth(),
                b.getHeight()
        );

        long startTime = System.nanoTime();
        executor.execute(() -> {
            while (IS_RECORD.get()) {
                BufferedImage bgrScreen = source.getImage();
                writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
                try {
                    Thread.sleep((long) (1000 / FRAME_RATE));
                } catch (InterruptedException e) {
                    //TODO add logs
                }
            }
            writer.close();
        });
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
