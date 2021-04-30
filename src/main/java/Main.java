import Utils.RecordUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import Record.*;

public class Main extends JFrame {
    private JButton start;
    private JButton stop;
    private JPanel mainPanel;
    private JLabel textPath;
    private JButton path;
    private JRadioButton screenRadioButton;
    private JRadioButton partOfScreenRadioButton;
    private JRadioButton twichRadioButton;
    private JPanel screenSettingsPanel;
    private JRadioButton fullScreenRadioButton;
    private JRadioButton screenAreaRadioButton;
    private JRadioButton windowCaptureRadioButton;
    private JLabel twitchRec;
    private JLabel projectorRec;
    private JLabel screenRec;
    private Path recordFilePath = RecordUtils.getFreeFileName(Paths.get("record.mp4").toAbsolutePath());
    private final List<AbstractRecord> recordList = new ArrayList<>();
    private Point firstMousePoint;
    private Point secondMousePoint;
    private JFrame transFrame;

    public Main() {
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!recordList.isEmpty()) {
                            recordList.forEach(record -> {
                                record.record();
                                recordDebugUpdate(record);
                            });
                        }
                    }
                });
            }
        });
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recordList.forEach(record -> {
                    if (record.IS_RECORD.get()) {
                        record.stop();
                        recordDebugUpdate(record);
                        recordFilePath = RecordUtils.getFreeFileName(recordFilePath.toAbsolutePath());
                    }
                });
            }
        });
        path.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(recordFilePath.toFile());
                chooser.setDialogTitle("Select directory for recording");


                chooser.setSelectedFile(recordFilePath.toFile());
                int returnVal = chooser.showOpenDialog(mainPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    textPath.setText(chooser.getSelectedFile().getAbsolutePath());
                    recordFilePath = chooser.getSelectedFile().getAbsoluteFile().toPath();
                }
            }
        });
        screenRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                screenSettingsPanel.setVisible(!screenSettingsPanel.isVisible());
            }
        });
        fullScreenRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FullScreenRecord record = new FullScreenRecord(recordFilePath);
                recordList.remove(record);
                recordList.add(record);
            }
        });
        screenAreaRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transFrame = createTransparentFrame(new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize());
                //TODO
                transFrame.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        firstMousePoint = e.getLocationOnScreen();
                        transFrame.dispatchEvent(new WindowEvent(transFrame, WindowEvent.WINDOW_CLOSING));
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        secondMousePoint = e.getLocationOnScreen();
                        Rectangle rec = new Rectangle(
                                firstMousePoint.x,
                                firstMousePoint.y,
                                e.getLocationOnScreen().x - firstMousePoint.x,
                                e.getLocationOnScreen().y - firstMousePoint.y
                        );
                        //TODO create blank form
                        ScreenAreaRecord record = new ScreenAreaRecord(recordFilePath, rec);
                        recordList.remove(record);
                        recordList.add(record);
                        transFrame.dispatchEvent(new WindowEvent(transFrame, WindowEvent.WINDOW_CLOSING));
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });
                transFrame.addMouseMotionListener(new MouseMotionListener() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        transFrame.dispatchEvent(new WindowEvent(transFrame, WindowEvent.WINDOW_CLOSING));
                        transFrame = createTransparentFrame(
                                firstMousePoint,
                                new Dimension(
                                        e.getLocationOnScreen().x - firstMousePoint.x,
                                        e.getLocationOnScreen().y - firstMousePoint.y
                                )
                        );
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {

                    }
                });
                transFrame.show();
                pack();
            }
        });
        textPath.setText("Paths to record file :" + recordFilePath);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(fullScreenRadioButton);
        buttonGroup.add(screenAreaRadioButton);
        buttonGroup.add(windowCaptureRadioButton);

        setContentPane(mainPanel);
        setResizable(false);
        setMinimumSize(new Dimension(650, 400));
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void recordDebugUpdate(AbstractRecord record) {
        if (record instanceof FullScreenRecord || record instanceof ScreenAreaRecord) {
            if (record.IS_RECORD.get())
                screenRec.setText("Rec");
            else
                screenRec.setText("Not rec");
            repaint();
        } /*else if (record instanceof ) {
                if (record.IS_RECORD.get())
                    screenRec.setBackground(Color.GREEN);
                else
                    screenRec.setBackground(Color.RED);

            } else if (record instanceof FullScreenRecord) {
                if (record.IS_RECORD.get())
                    screenRec.setBackground(Color.GREEN);
                else
                    screenRec.setBackground(Color.RED);
            }*/
    }

    private JFrame createTransparentFrame(Point loc, Dimension size) {
        JFrame f = new JFrame("Select a screen area");
        TransparentFrame bg = new TransparentFrame(f);
        bg.setLayout(new BorderLayout());
        f.getContentPane().add("Center", bg);
        f.pack();
        f.setSize(size);
        f.setLocation(loc);
        f.setResizable(false);
        f.setAlwaysOnTop(true);
        f.show();
        return f;
    }

    public static void main(String[] args) {
        new Main().show();
    }
}
