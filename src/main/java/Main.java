import ApiYoutube.BroadcastCreator;
import Recorders.ProcessExecutor;
import Sources.*;
import Presentation.SlideController;
import Presentation.SlidePanel;
import Utils.RecordUtils;
import Utils.WindowsInfo;
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

import static Utils.RecordUtils.getScreenSize;

public class Main extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final Path PATH_TO_PRESENTATIONS = Paths.get("presentations").toAbsolutePath();
    private static final SlideController CONTROLLER = new SlideController(PATH_TO_PRESENTATIONS);

    private enum SOURCE_TYPE {NONE, FULL, AREA, CAPTURE_FRAME, PRESENTATION}

    private Path recordFilePath = RecordUtils.getFreeFileName(Paths.get("record.mp4").toAbsolutePath());
    private JButton start;
    private JButton stop;
    private JPanel mainPanel;
    private JLabel textPath;
    private JButton path;
    private JRadioButton screenRadioButton;
    private JRadioButton projectorRadioButton;
    private JRadioButton twitchRadioButton;
    private JLabel twitchRec;
    private JLabel projectorRec;
    private JLabel screenRec;
    private SlidePanel slidePanel;

    private final List<ProcessExecutor> executors = new ArrayList<>();
    private RecordSource recordSource = new RecordSource(getScreenSize());
    private Point firstMousePoint;
    private Point secondMousePoint;
    private JFrame transFrame;
    private JButton previousSlideButton;
    private JButton nextSlideButton;
    private JPanel tmpPanel;
    private JComboBox presentationsList;
    private JComboBox sourceType;
    private JLabel presText;
    private JLabel slideLable;

    public Main() {
        BasicConfigurator.configure();
        log.info("Start tool");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!executors.isEmpty()) {
                            if (sourceType.getSelectedItem().equals(SOURCE_TYPE.PRESENTATION)) {
                                createPresentationFrame("");
                            }
                            //recordDebugUpdate(record);
                            executors.forEach(ProcessExecutor::start);
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
                recordFilePath = RecordUtils.getFreeFileName(recordFilePath.toAbsolutePath());
                executors.forEach(record -> {
                    record.stop();
                    record.getSetupSettings().refreshPath(recordFilePath.toString());
                });
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
                SetupSettings.Settings settings = recordSource.getSetupSettings(recordFilePath.toString());
                replaceExecutor(new ProcessExecutor(settings));
            }
        });

        twitchRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
               // String url = "rtmp://a.rtmp.youtube.com/live2/2zre-4z94-bxbf-v2b4-8ps9";
                String streamUrl = BroadcastCreator.createStream();
                SetupSettings.Settings settings = recordSource.getSetupSettings(streamUrl);
                replaceExecutor(new ProcessExecutor(settings));
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
               // ((SlidePanel) tmpPanel).setImage(CONTROLLER.currentSlide());
              //  tmpPanel.repaint();
            }
        });

        sourceType.addItem(SOURCE_TYPE.NONE);
        sourceType.addItem(SOURCE_TYPE.PRESENTATION);
        sourceType.addItem(SOURCE_TYPE.FULL);
        sourceType.addItem(SOURCE_TYPE.AREA);
        sourceType.addItem(SOURCE_TYPE.CAPTURE_FRAME);
        sourceType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousSlideButton.setVisible(false);
                nextSlideButton.setVisible(false);
                tmpPanel.setVisible(false);
                presentationsList.setVisible(false);
                presText.setVisible(false);

                Object source_type = sourceType.getSelectedItem();
                if (source_type.equals(SOURCE_TYPE.FULL)) {
                    recordSource = new RecordSource(getScreenSize());
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
                            recordSource = new RecordSource(rec);
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
                } else if (source_type.equals(SOURCE_TYPE.CAPTURE_FRAME)) {
                    selectedCapturedFrame();
                } else if (source_type.equals(SOURCE_TYPE.PRESENTATION)) {
//                    baseSource = new PresentationSource(CONTROLLER);
//                    previousSlideButton.setVisible(true);
//                    nextSlideButton.setVisible(true);
//                    tmpPanel.setVisible(true);
                      recordSource = new RecordSource("Presentation");

                      presentationsList.setVisible(true);
                      presText.setVisible(true);
                }

                screenRadioButton.setSelected(false);
                projectorRadioButton.setSelected(false);
                twitchRadioButton.setSelected(false);
            }
        });

        setContentPane(mainPanel);
        setResizable(false);
        setMinimumSize(new Dimension(650, 400));
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void selectedCapturedFrame() {
        setVisible(false);
        JFrame captureTransFrame = createTransparentFrame(new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize());
        captureTransFrame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                captureTransFrame.dispatchEvent(new WindowEvent(captureTransFrame, WindowEvent.WINDOW_CLOSING));
                String winName = WindowsInfo.find0();
                int res = JOptionPane.showConfirmDialog(
                        mainPanel,
                        "Do you want to capture a window - " + winName + " ?",
                        "Capture window selection",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );
                if (res == 0) {
                    setVisible(true);
                    recordSource = new RecordSource(winName);
                } else if (res == 1) {
                    selectedCapturedFrame();
                } else if (res == 2) {
                    setVisible(true);
                    return;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    private JFrame createPresentationFrame(String name) {
        Rectangle rec = getScreenSize();
        rec.height = rec.height - 1;

        JFrame f = new JFrame("Presentation");
        f.setLayout(new BorderLayout());

        SlidePanel panel = new SlidePanel(CONTROLLER.currentSlide());
        panel.setSize(rec.getSize());
        panel.setLocation(rec.getLocation());
        panel.setVisible(true);
        f.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    panel.setImage(CONTROLLER.prevSlide());
                    panel.repaint();

                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    panel.setImage(CONTROLLER.nextSlide());
                    panel.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stop.doClick();
                super.windowClosing(e);
            }
        });
        f.add(panel);

        f.pack();
        f.setSize(rec.getSize());
        f.setLocation(rec.getLocation());
        f.setResizable(false);
        f.setAlwaysOnTop(true);
        f.show();
        f.setExtendedState(MAXIMIZED_HORIZ);
        return f;
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

    public void replaceExecutor(ProcessExecutor executor) {
        executors.remove(executor);
        executors.add(executor);

        log.info("Add new record mode {} to records, size: {}", executor.getClass(), executors.size());
    }
}
