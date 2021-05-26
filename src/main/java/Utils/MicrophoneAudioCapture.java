package Utils;

import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileWriter;

public class MicrophoneAudioCapture {
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private AudioFormat format = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            44100,
            16,
            1,
            2,
            44100,
            true
    );
    private TargetDataLine mike;

    public AudioInputStream startRecording() {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            throw new IllegalArgumentException("Line not supported" + info);
        }
        try {
            mike = (TargetDataLine) AudioSystem.getLine(info);
            mike.open(format, mike.getBufferSize());
            AudioInputStream sound = new AudioInputStream(mike);
            mike.start();
            return sound;
        } catch (LineUnavailableException ex) {
            return null;
        }

    }

    public void stopRecording() {
        if (mike != null) {
            mike.stop();
            mike.close();
        }
    }
}
