import NewStructure.Recorders.FileRecord;
import NewStructure.Sources.BaseSource;
import NewStructure.Recorders.AbstractRecord;
import NewStructure.Sources.PresentationSource;
import NewStructure.Sources.ScreenAreaSource;
import Presentation.SlideController;
import Presentation.SlidePanel;
import Utils.RecordUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    private static final Path PATH_TO_PRESENTATIONS = Paths.get("presentations").toAbsolutePath();
    private Path recordFilePath = RecordUtils.getFreeFileName(Paths.get("record.mp4").toAbsolutePath());
    private static final SlideController controller = new SlideController(PATH_TO_PRESENTATIONS);
    private JButton start;
    private JButton stop;
    private JPanel mainPanel;
    private JLabel textPath;
    private JButton path;
    private JRadioButton screenRadioButton;
    private JRadioButton partOfScreenRadioButton;
    private JRadioButton twitchRadioButton;
    private JPanel screenSettingsPanel;
    private JRadioButton fullScreenRadioButton;
    private JRadioButton screenAreaRadioButton;
    private JRadioButton windowCaptureRadioButton;
    private JLabel twitchRec;
    private JLabel projectorRec;
    private JLabel screenRec;
    private JRadioButton presentationMode;
    private SlidePanel slidePanel;

    private BaseSource baseSource;
    private final List<AbstractRecord> recordersList = new ArrayList<>();
    private AbstractRecord recorder;
    private Point firstMousePoint;
    private Point secondMousePoint;
    private JFrame transFrame;
    private JButton previousSlideButton;
    private JButton nextSlideButton;
    private JPanel tmpPanel;
    private JComboBox presentationsList;
    private JLabel slideLable;

    public Main() {
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!recordersList.isEmpty()) {
                            recordersList.forEach(record -> {
                                record.record(recordFilePath);
                                //recordDebugUpdate(record);
                            });
                            stop.setEnabled(true);
                            start.setEnabled(false);
                        }
                    }
                });
            }
        });
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recordersList.forEach(record -> {
                    record.stop();
                    recordFilePath = RecordUtils.getFreeFileName(recordFilePath.toAbsolutePath());
                });
                recordFilePath = RecordUtils.getFreeFileName(recordFilePath.toAbsolutePath());
                stop.setEnabled(false);
                start.setEnabled(true);
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
                BaseSource source = new ScreenAreaSource();
                AbstractRecord record = new FileRecord(source);
                replaceRecordMode(record);
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
                        BaseSource source = new ScreenAreaSource(rec);
                        AbstractRecord record = new FileRecord(source);
                        replaceRecordMode(record);
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
        presentationMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BaseSource source = new PresentationSource(controller);
                AbstractRecord record = new FileRecord(source);
                replaceRecordMode(record);
                previousSlideButton.setVisible(!previousSlideButton.isVisible());
                nextSlideButton.setVisible(!nextSlideButton.isVisible());
                tmpPanel.setVisible(!tmpPanel.isVisible());
                presentationsList.setVisible(!presentationsList.isVisible());
            }
        });
        previousSlideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((SlidePanel) tmpPanel).setImage(controller.prevSlide());
                tmpPanel.repaint();
            }
        });
        nextSlideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((SlidePanel) tmpPanel).setImage(controller.nextSlide());
                tmpPanel.repaint();
            }
        });
        textPath.setText("Paths to record file :" + recordFilePath);
        controller.getPresentationsNames().forEach(name -> {
            presentationsList.addItem(name);
        });
        presentationsList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setCurrentPresentation(presentationsList.getSelectedItem().toString());
                ((SlidePanel) tmpPanel).setImage(controller.currentSlide());
                tmpPanel.repaint();
            }
        });

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(fullScreenRadioButton);
        buttonGroup.add(screenAreaRadioButton);
        buttonGroup.add(windowCaptureRadioButton);
        buttonGroup.add(presentationMode);

        setContentPane(mainPanel);
        setResizable(false);
        setMinimumSize(new Dimension(650, 400));
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    private void createUIComponents() {
        tmpPanel = new SlidePanel(controller.currentSlide());
    }

    public void replaceRecordMode(AbstractRecord record) {
        recordersList.remove(record);
        recordersList.add(record);
    }
}
