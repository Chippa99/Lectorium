package ApiYoutube;

import Recorders.AbstractRecord;
import Sources.BaseSource;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class RTMPEncoder extends AbstractRecord {
    private final String url;
    private final String streamName;

    public RTMPEncoder(String url, String streamName, BaseSource source) {
        super(source);
        this.url = url;
        this.streamName = streamName;
    }

    @Override
    public void start(Path outputFilename) {
        try {
            IContainer container = IContainer.make();
            IContainerFormat containerFormat_live = IContainerFormat.make();
            containerFormat_live.setOutputFormat("flv", url + "/" + streamName, null);
            container.setInputBufferLength(0);
            int retVal = container.open(url + "/" + streamName, IContainer.Type.WRITE, containerFormat_live);
            if (retVal < 0) {
                System.err.println("Could not open output container for live stream");
                System.exit(1);
            }

            IStream stream = container.addNewStream(0);
            IStreamCoder coder = stream.getStreamCoder();
            ICodec codec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264); //FIXME
            coder.setNumPicturesInGroupOfPictures(4);
            coder.setCodec(codec);
            coder.setBitRate(500000);
            coder.setPixelType(IPixelFormat.Type.GBRP); //FIXME
            coder.setHeight(source.getImage().getHeight());
            coder.setWidth(source.getImage().getWidth());
            coder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
            coder.setGlobalQuality(0);
            IRational frameRate = IRational.make(24, 1);
            coder.setFrameRate(frameRate);
            coder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));

            coder.open();
            container.writeHeader();
            long firstTimeStamp = System.currentTimeMillis();
            long lastTimeStamp = -1;
            int i = 0;

            while (true) {
                long now = System.currentTimeMillis();
                BufferedImage image = source.getImage();
                BufferedImage currentScreenshot = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                currentScreenshot.getGraphics().drawImage(image, 0, 0, null);

                IPacket packet = IPacket.make();
                IConverter converter = ConverterFactory.createConverter(currentScreenshot, IPixelFormat.Type.YUV420P);
                long timeStamp = (now - firstTimeStamp) * 1000;
                IVideoPicture outFrame = converter.toPicture(currentScreenshot, timeStamp);
                if (i == 0) {
                    outFrame.setKeyFrame(true);
                }
                outFrame.setQuality(0);
                coder.encodeVideo(packet, outFrame, 0);
                coder.encodeAudio(packet, IAudioSamples.make(0, 1), 0);
                outFrame.delete();
                if (packet.isComplete()) {
                    container.writePacket(packet);
                    System.out.println("[ENCODER] writing packet of size " + packet.getSize() + " for elapsed time " + ((timeStamp - lastTimeStamp) / 1000));
                    lastTimeStamp = timeStamp;
                }
                System.out.println("[ENCODER] encoded image " + i + " in " + (System.currentTimeMillis() - now));
                i++;
                try {
                    Thread.sleep(Math.max((long) (1000 / frameRate.getDouble()) - (System.currentTimeMillis() - now), 0));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
