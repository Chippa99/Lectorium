import ApiYoutube.StreamExecutor;
import Recorders.FileRecord;
import Sources.BaseSource;
import Recorders.AbstractRecord;
import Sources.PresentationSource;
import Sources.ScreenAreaSource;
import Presentation.SlideController;
import Presentation.SlidePanel;
import Utils.RecordUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final Path PATH_TO_PRESENTATIONS = Paths.get("presentations").toAbsolutePath();
    private static final SlideController CONTROLLER = new SlideController(PATH_TO_PRESENTATIONS);

    private enum SOURCE_TYPE {FULL, AREA, PRESENTATION}

    private Path recordFilePath = RecordUtils.getFreeFileName(Paths.get("record.mp4").toAbsolutePath());
    private JButton start;
    private JButton stop;
    private JPanel mainPanel;
    private JLabel textPath;
    private JButton path;
    private JRadioButton screenRadioButton;
    private JRadioButton partOfScreenRadioButton;
    private JRadioButton twitchRadioButton;
    private JLabel twitchRec;
    private JLabel projectorRec;
    private JLabel screenRec;
    private SlidePanel slidePanel;

    private BaseSource baseSource = new ScreenAreaSource();
    private final List<AbstractRecord> recordersList = new ArrayList<>();
    private AbstractRecord recorder;
    private Point firstMousePoint;
    private Point secondMousePoint;
    private JFrame transFrame;
    private JButton previousSlideButton;
    private JButton nextSlideButton;
    private JPanel tmpPanel;
    private JComboBox presentationsList;
    private JComboBox sourceType;
    private JLabel slideLable;
    private StreamExecutor streamExecutor;

    public Main() {
        BasicConfigurator.configure();
        log.info("Start tool");
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
                AbstractRecord record = new FileRecord(baseSource);
                replaceRecordMode(record);
            }
        });
        previousSlideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((SlidePanel) tmpPanel).setImage(CONTROLLER.prevSlide());
                tmpPanel.repaint();
            }
        });
        nextSlideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((SlidePanel) tmpPanel).setImage(CONTROLLER.nextSlide());
                tmpPanel.repaint();
            }
        });
        textPath.setText("Paths to record file :" + recordFilePath);
        CONTROLLER.getPresentationsNames().forEach(name -> {
            presentationsList.addItem(name);
        });
        presentationsList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CONTROLLER.setCurrentPresentation(presentationsList.getSelectedItem().toString());
                ((SlidePanel) tmpPanel).setImage(CONTROLLER.currentSlide());
                tmpPanel.repaint();
            }
        });
        twitchRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
                streamExecutor = new StreamExecutor(
                        "rtmp://a.rtmp.youtube.com/live2",
                        "2zre-4z94-bxbf-v2b4-8ps9",
                        new ScreenAreaSource(new Rectangle(300, 300, 900, 600))
                );
                streamExecutor.start();
            }
        });

        sourceType.addItem(SOURCE_TYPE.FULL);
        sourceType.addItem(SOURCE_TYPE.AREA);
        sourceType.addItem(SOURCE_TYPE.PRESENTATION);
        sourceType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousSlideButton.setVisible(false);
                nextSlideButton.setVisible(false);
                tmpPanel.setVisible(false);
                presentationsList.setVisible(false);

                Object source_type = sourceType.getSelectedItem();
                if (source_type.equals(SOURCE_TYPE.FULL)) {
                    baseSource = new ScreenAreaSource();
                } else if (source_type.equals(SOURCE_TYPE.AREA)) {
                    transFrame = createTransparentFrame(new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize());
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
                            baseSource = new ScreenAreaSource(rec);
                            transFrame.dispatchEvent(new WindowEvent(transFrame, WindowEvent.WINDOW_CLOSING));
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                    //FIXME
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
                } else if (source_type.equals(SOURCE_TYPE.PRESENTATION)) {
                    baseSource = new PresentationSource(CONTROLLER);
                    previousSlideButton.setVisible(true);
                    nextSlideButton.setVisible(true);
                    tmpPanel.setVisible(true);
                    presentationsList.setVisible(true);
                }
            }
        });

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
        tmpPanel = new SlidePanel(CONTROLLER.currentSlide());
    }

    public void replaceRecordMode(AbstractRecord record) {
        recordersList.remove(record);
        recordersList.add(record);

        log.info("Add new record mode {} to records, size: {}", record.getClass(), recordersList.size());
    }
}
