package NewStructure.Sources;

import Presentation.SlideController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PresentationSource implements BaseSource {
    private final SlideController controller;

    public PresentationSource(SlideController controller) {
       this.controller = controller;
    }

    @Override
    public BufferedImage getImage() {
        try {
            return ImageIO.read(controller.currentSlide().toFile());
        } catch (IOException e) {
            throw new NullPointerException("Cold not read image to path: [" + controller.currentSlide().toString() + "]");
        }
    }
}
