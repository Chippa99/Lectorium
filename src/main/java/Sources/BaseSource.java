package Sources;

import java.awt.image.BufferedImage;

public  interface BaseSource {
    int getX();
    int getY();
    BufferedImage getImage();
}
