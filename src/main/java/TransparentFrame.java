import javax.swing.*;
import java.awt.*;

public class TransparentFrame extends JComponent{
    private JFrame frame;
    private Image background;

    public TransparentFrame(JFrame frame) {
        this.frame = frame;
        updateBackground( );
    }

    public void updateBackground( ) {
        try {
            Robot rbt = new Robot( );
            Toolkit tk = Toolkit.getDefaultToolkit( );
            Dimension dim = tk.getScreenSize( );
            background = rbt.createScreenCapture(
                    new Rectangle(0,0,(int)dim.getWidth( ),
                            (int)dim.getHeight( )));
        } catch (Exception ex) {
            ex.printStackTrace( );
        }
    }
    public void paintComponent(Graphics g) {
        Point pos = this.getLocationOnScreen( );
        Point offset = new Point(-pos.x,-pos.y);
        g.drawImage(background,offset.x,offset.y,null);
    }
}
