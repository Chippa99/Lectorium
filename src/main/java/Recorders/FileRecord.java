package Recorders;


import Sources.BaseSource;
import Utils.LectoriumThreadExecutor;
import Utils.MicrophoneAudioCapture;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import javax.sound.sampled.AudioInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class FileRecord extends AbstractRecord {
    private final MicrophoneAudioCapture microphone;


    public FileRecord(BaseSource source) {
        super(source);
        microphone = new MicrophoneAudioCapture();
    }

    @Override
    public void start(Path output) {
        final IMediaWriter writer = ToolFactory.makeWriter(output.toString());
        BufferedImage b = source.getImage();
//        AudioInputStream audioInputStream = microphone.startRecording();
        writer.addVideoStream(
                0,
                0,
                ICodec.ID.CODEC_ID_MPEG4,
                b.getWidth(),
                b.getHeight()
        );
//        writer.addAudioStream(1, 1, ICodec.ID.CODEC_ID_AAC, 1, 16000);
        long startTime = System.nanoTime();
        LectoriumThreadExecutor.getExecutor().execute(() -> {
            int size = 0;
            while (IS_RECORD.get()) {
                BufferedImage bgrScreen = source.getImage();
//                byte[] s = new byte[30];
//                try {
//                  size += audioInputStream.read(s, size, s.length + size);
//                } catch (IOException e) {
//                    System.out.println("Error start sound record in file");
//                }
//                short[] shortBuf =  new short[s.length];
//                for(int i = 0; i < s.length; i++) {
//                    shortBuf[i] = (short) s[i]/*((s[2*i + 1] << 8) | s[2*i])*/;
//                }

                writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
               // writer.encodeAudio(1, shortBuf, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
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
    public void stop() {
        IS_RECORD.set(false);
        microphone.stopRecording();
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
