package Sources;

abstract class AbstractSettings implements Settings {
    protected int fps = 15;
    protected int crt = 30;
    protected int bitrate = 15000;
    protected int buffer = 250;
    protected String preset;

    @Override
    public void buildToStream() {
        preset = PresetType.ultrafast.toString();
        fps = 30;
        crt = 30;
        bitrate = 6000;
        buffer = 400;
    }

    @Override
    public void buildToFile() {
        preset = PresetType.slow.toString();
        fps = 15;
        crt = 30;
        bitrate = 2000;
        buffer = 50;
    }
}
