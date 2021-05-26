package ApiYoutube;

import Recorders.AbstractRecord;
import Sources.BaseSource;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

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
            IContainer videoContainer = IContainer.make();
            IContainerFormat containerFormat_live = IContainerFormat.make();
            containerFormat_live.setOutputFormat("flv", url + "/" + streamName, null);
            videoContainer.setInputBufferLength(0);
            int retVal = videoContainer.open(url + "/" + streamName, IContainer.Type.WRITE, containerFormat_live);
            if (retVal < 0) {
                System.err.println("Could not open output container for live stream");
                System.exit(1);
            }
            IStream stream = videoContainer.addNewStream(0);
            IStreamCoder coder = stream.getStreamCoder();
            ICodec codec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264); //FIXME
            coder.setNumPicturesInGroupOfPictures(4);
            coder.setCodec(codec);
            coder.setBitRate(5000);
            coder.setPixelType(IPixelFormat.Type.YUV420P); //FIXME
            coder.setHeight(source.getImage().getHeight());
            coder.setWidth(source.getImage().getWidth());
            coder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
            coder.setGlobalQuality(0);
            IRational frameRate = IRational.make(24, 1);
            coder.setFrameRate(frameRate);
            coder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));
            coder.open();
            videoContainer.writeHeader();

            IMediaWriter mWriter = ToolFactory.makeWriter(url + "/" + streamName, videoContainer);
            mWriter.addVideoStream(
                    0,
                    0,                    ICodec.ID.CODEC_ID_MPEG4,
                    source.getImage().getWidth(),
                    source.getImage().getHeight()
            );
            mWriter.addAudioStream(0,0, 1, 1600);
            mWriter.open();

//            IContainer audioContainer = IContainer.make();
//            audioContainer.setInputBufferLength(0);
//            int retVal1 = audioContainer.open(url + "/" + streamName, IContainer.Type.WRITE, containerFormat_live);
//            if (retVal1 < 0) {
//                System.err.println("Could not open output container for live stream");
//                System.exit(1);
//            }
//            IStream audioStream = audioContainer.addNewStream(0);
//            IStreamCoder audioCoder = audioStream.getStreamCoder();
//            ICodec audioCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_MP3); //FIXME
//            audioCoder.setCodec(audioCodec);
//            audioCoder.setBitRate(5000);
//            audioCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
//            audioCoder.setGlobalQuality(0);
//            audioCoder.setFrameRate(IRational.make(24, 1));
//            audioCoder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));
//            audioCoder.open();
//            audioContainer.writeHeader();

            long firstTimeStamp = System.currentTimeMillis();
            long lastTimeStamp = -1;
            int i = 0;
            long startTime = System.nanoTime();
            while (true) {
                long now = System.currentTimeMillis();
                BufferedImage image = source.getImage();
                BufferedImage currentScreenshot = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                currentScreenshot.getGraphics().drawImage(image, 0, 0, null);

               // IPacket videoPacket = IPacket.make();
               // IConverter converter = ConverterFactory.createConverter(currentScreenshot, IPixelFormat.Type.YUV420P);
//                long timeStamp = (now - firstTimeStamp) * 1000;
//                IVideoPicture outFrame = converter.toPicture(currentScreenshot, timeStamp);
//                if (i == 0) {
//                    outFrame.setKeyFrame(true);
//                }
//                outFrame.setQuality(0);
                mWriter.encodeVideo(0, image, now - startTime, TimeUnit.NANOSECONDS);
//                outFrame.delete();

//                if (videoPacket.isComplete()) {
//                    videoContainer.writePacket(videoPacket);
//                  //  audioContainer.writePacket();
//                    System.out.println("[ENCODER] writing videoPacket of size " + videoPacket.getSize() + " for elapsed time " + ((timeStamp - lastTimeStamp) / 1000));
//                    lastTimeStamp = timeStamp;
//                }
                System.out.println("[ENCODER] encoded image " + i + " in " + (System.currentTimeMillis() - now));
                i++;
                try {
                    Thread.sleep((long) (1000 / FRAME_RATE));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
