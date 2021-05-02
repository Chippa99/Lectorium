package Presentation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class SlidePanel extends JPanel {
    private BufferedImage image;

    public SlidePanel(Path path) {
        super(new GridBagLayout());
        try {
            image = ImageIO.read(path.toFile());
        } catch (IOException ex) {
            //TODO add logs
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image.getScaledInstance(getWidth(),getHeight(),Image.SCALE_SMOOTH), 0, 0, this); // see javadoc for more info on the parameters
    }

    public void setImage(Path path) {
        try {
            this.image = ImageIO.read(path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
