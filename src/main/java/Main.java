import ApiYoutube.BroadcastCreator;
import Presentation.PresentationFinder;
import Presentation.PresentationInfo;
import Presentation.SlidePanel;
import Recorders.ProcessExecutor;
import Sources.*;
import Presentation.SlideController;
import Utils.LectoriumThreadExecutor;
import Utils.RecordUtils;
import Utils.WindowsInfo;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static Utils.RecordUtils.getScreenSize;
import static Utils.RecordUtils.toUTF8;

public class Main extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final Path PATH_TO_PRESENTATIONS = Paths.get("presentations").toAbsolutePath();
    private SlideController presentationController = new SlideController(PATH_TO_PRESENTATIONS);
    private boolean FOUND_PRESENTATION = false;
    private final List<ProcessExecutor> executors = new ArrayList<>();
    private String input = null;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(7, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setOpaque(true);
        mainPanel.putClientProperty("html.disable", Boolean.FALSE);
        sourceType = new JComboBox();
        sourceType.setEditable(false);
        mainPanel.add(sourceType, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(196, -1), new Dimension(349, -1), 0, false));
        recordSourceText = new JLabel();
        recordSourceText.setAlignmentX(0.0f);
        recordSourceText.setText("Источник записи");
        recordSourceText.setVerticalAlignment(0);
        mainPanel.add(recordSourceText, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        presText = new JLabel();
        presText.setText("Доступные презентации");
        presText.setVisible(false);
        mainPanel.add(presText, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        presentationsList = new JComboBox();
        presentationsList.setEditable(true);
        presentationsList.setVisible(false);
        mainPanel.add(presentationsList, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(300, -1), 0, false));
        setUrlOnStreamRadioButton = new JRadioButton();
        setUrlOnStreamRadioButton.setText("Ввести ключ трансляции");
        setUrlOnStreamRadioButton.setVisible(false);
        mainPanel.add(setUrlOnStreamRadioButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        createStreamRadioButton = new JRadioButton();
        createStreamRadioButton.setText("Создать трансляцию");
        createStreamRadioButton.setVisible(false);
        mainPanel.add(createStreamRadioButton, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        start = new JButton();
        start.setText("Начать запись");
        mainPanel.add(start, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(196, -1), new Dimension(196, -1), null, 0, false));
        final JSeparator separator1 = new JSeparator();
        mainPanel.add(separator1, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        intTrans = new JLabel();
        intTrans.setRequestFocusEnabled(false);
        intTrans.setText("Трансляция");
        panel1.add(intTrans, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        intRec = new JLabel();
        intRec.setText("Запись");
        panel1.add(intRec, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        fileIndicator = new JRadioButton();
        fileIndicator.setEnabled(false);
        fileIndicator.setForeground(new Color(-12515822));
        fileIndicator.setSelected(false);
        fileIndicator.setText("");
        panel1.add(fileIndicator, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        streamIndicator = new JRadioButton();
        streamIndicator.setEnabled(false);
        streamIndicator.setText("");
        panel1.add(streamIndicator, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        youtubeCheckBox = new JCheckBox();
        youtubeCheckBox.setText("YouTube трансляция");
        mainPanel.add(youtubeCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileCheckBox = new JCheckBox();
        fileCheckBox.setText("Запись в файл");
        mainPanel.add(fileCheckBox, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathToPresentation = new JButton();
        pathToPresentation.setActionCommand("Своя презентация");
        Font pathToPresentationFont = this.$$$getFont$$$(null, -1, -1, pathToPresentation.getFont());
        if (pathToPresentationFont != null) pathToPresentation.setFont(pathToPresentationFont);
        pathToPresentation.setHideActionText(false);
        pathToPresentation.setHorizontalAlignment(0);
        pathToPresentation.setIcon(new ImageIcon(getClass().getResource("/javax/swing/plaf/metal/icons/ocean/directory.gif")));
        pathToPresentation.setIconTextGap(3);
        pathToPresentation.setLabel("");
        pathToPresentation.setText("");
        pathToPresentation.setVisible(false);
        mainPanel.add(pathToPresentation, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 26), new Dimension(33, 26), new Dimension(33, -1), 0, false));
        stop = new JButton();
        stop.setEnabled(false);
        stop.setText("Остановить");
        mainPanel.add(stop, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(196, -1), new Dimension(196, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private Path recordFilePath;
    private JButton start;
    private JButton stop;
    private JPanel mainPanel;
    private SlidePanel slidePanel;

    private final RecordSource recordSource = new RecordSource(getScreenSize());
    private Point firstMousePoint;
    private Point secondMousePoint;
    private JFrame transFrame;
    private JComboBox presentationsList;
    private JComboBox sourceType;
    private JLabel presText;
    private JRadioButton setUrlOnStreamRadioButton;
    private JRadioButton createStreamRadioButton;
    private JCheckBox youtubeCheckBox;
    private JCheckBox fileCheckBox;
    private JButton pathToPresentation;
    private JRadioButton fileIndicator;
    private JRadioButton streamIndicator;
    private JLabel recordSourceText;
    private JLabel intRec;
    private JLabel intTrans;
    private JLabel slideLable;

    public Main() throws UnsupportedEncodingException {
        BasicConfigurator.configure();
        setTitle("Лекттоп");
        log.info("Start tool");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        recordFilePath = RecordUtils.getFreeFileName(Paths.get("record.mp4").toAbsolutePath());
                        FOUND_PRESENTATION = false;
                        if (fileCheckBox.isSelected() && recordFilePath != null) {
                            Settings settings = recordSource.getSetupSettings(recordFilePath.toString());
                            settings.buildToFile();
                            executors.add(new ProcessExecutor(settings));
                            fileIndicator.setSelected(true);
                        }
                        if (youtubeCheckBox.isSelected() && input != null) {
                            Settings settings = recordSource.getSetupSettings(input);
                            settings.buildToStream();
                            executors.add(new ProcessExecutor(settings));
                            streamIndicator.setSelected(true);
                        }
                        if (sourceType.getSelectedItem().equals(SourceType.PRESENTATION)) {
                            createPresentationFrame();
                        }
                        executors.forEach(ProcessExecutor::start);
                        stop.setEnabled(true);
                        start.setEnabled(false);
                    }
                });
            }
        });
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!executors.isEmpty()) {
                    recordFilePath = RecordUtils.getFreeFileName(recordFilePath.toAbsolutePath());
                    executors.forEach(ProcessExecutor::stop);
                    executors.clear();
                    stop.setEnabled(false);
                    start.setEnabled(true);
                    fileIndicator.setSelected(false);
                    streamIndicator.setSelected(false);
                }
            }
        });
        fileCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileCheckBox.isSelected()) {
                    recordFilePath = RecordUtils.getFreeFileName(Paths.get("record.mp4").toAbsolutePath());
                } else {
                    recordFilePath = null;
                }
                start.setEnabled(isStartEnable());
            }
        });

        youtubeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (youtubeCheckBox.isSelected()) {
                    setSize(getWidth() + 28, getHeight());
                } else {
                    setSize(getWidth() - 28, getHeight());
                }
                setUrlOnStreamRadioButton.setVisible(!setUrlOnStreamRadioButton.isVisible());
                createStreamRadioButton.setVisible(!createStreamRadioButton.isVisible());
                if (!setUrlOnStreamRadioButton.isVisible()) {
                    input = null;
                }
                //TODO
                // String url = "rtmp://a.rtmp.youtube.com/live2/2zre-4z94-bxbf-v2b4-8ps9";
                start.setEnabled(isStartEnable());
            }
        });
        setUrlOnStreamRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (setUrlOnStreamRadioButton.isSelected()) {
                    String streamUrl = JOptionPane.showInputDialog("Введите ключ трансляции", "<<Ключ трансляци сюда>>");
                    input = "rtmp://a.rtmp.youtube.com/live2/" + streamUrl;
                } else {
                    input = null;
                }
            }
        });
        createStreamRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (createStreamRadioButton.isSelected()) {
                    //FIXME make call this function only once
                    input = BroadcastCreator.createStream();
                } else {
                    input = null;
                }
            }
        });
        refreshPresentationFolder();

        sourceType.addItem(SourceType.AUTO);
        sourceType.addItem(SourceType.PRESENTATION);
        sourceType.addItem(SourceType.FULL);
        sourceType.addItem(SourceType.AREA);
        sourceType.addItem(SourceType.CAPTURE_FRAME);
        sourceType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSize(getWidth(), 210);
                presentationsList.setVisible(false);
                presText.setVisible(false);
                FOUND_PRESENTATION = false;
                //  start.setEnabled(true);
                pathToPresentation.setVisible(false);

                Object source_type = sourceType.getSelectedItem();
                if (source_type.equals(SourceType.FULL)) {
                    recordSource.setScreenArea(getScreenSize());
                } else if (source_type.equals(SourceType.AREA)) {
                    transFrame = createTransparentFrame(new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize());
                    transFrame.addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            firstMousePoint = e.getLocationOnScreen();
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
                        public void mouseReleased(MouseEvent e) {
                            secondMousePoint = e.getLocationOnScreen();
                            Rectangle rec = new Rectangle(
                                    firstMousePoint.x,
                                    firstMousePoint.y,
                                    e.getLocationOnScreen().x - firstMousePoint.x,
                                    e.getLocationOnScreen().y - firstMousePoint.y
                            );
                            recordSource.setScreenArea(rec);
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
                            transFrame.setSize(
                                    new Dimension(
                                            e.getLocationOnScreen().x - firstMousePoint.x,
                                            e.getLocationOnScreen().y - firstMousePoint.y
                                    )
                            );
                            transFrame.repaint();
                        }

                        @Override
                        public void mouseMoved(MouseEvent e) {
                        }
                    });
                    transFrame.show();
                } else if (source_type.equals(SourceType.CAPTURE_FRAME)) {
                    selectedCapturedFrame();
                } else if (source_type.equals(SourceType.PRESENTATION)) {
                    setSize(getWidth(), getHeight() + 30);
                    recordSource.setCaptureFrame("Presentation");
                    presentationsList.setVisible(true);
                    presText.setVisible(true);
                    pathToPresentation.setVisible(true);
                } else if (source_type.equals(SourceType.AUTO)) {
                    startFoundPresentation();
                }
                start.setEnabled(isStartEnable());
            }
        });
        pathToPresentation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Path presentationFolder = createFileChooser(toUTF8("Выберите папку с презентацией"), PATH_TO_PRESENTATIONS);
                presentationController = new SlideController(presentationFolder);
                refreshPresentationFolder();
            }
        });
        createMenu();
        startFoundPresentation();
        setContentPane(mainPanel);
        setResizable(false);
        setMinimumSize(new Dimension(100, 100));
        setSize(390, 210);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private boolean isStartEnable() {
        return (youtubeCheckBox.isSelected() || fileCheckBox.isSelected())
                && !sourceType.getSelectedItem().equals(SourceType.AUTO);
    }

    private void refreshPresentationFolder() {
        presentationsList.removeAllItems();
        presentationController.getPresentationsNames().forEach(presentationsList::addItem);
        presentationsList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentationController.setCurrentPresentation((PresentationInfo) presentationsList.getSelectedItem());
            }
        });
    }

    private void createMenu() {
        Font font = new Font(mainPanel.getFont().toString(), Font.BOLD, 12);
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(toUTF8("Файл"));
        fileMenu.setFont(font);

        JMenuItem txtFileItem = new JMenuItem(toUTF8("Изменить"));
        txtFileItem.setFont(font);
        txtFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recordFilePath = createFileChooser(
                        "Выберите новый файл для записи",
                        RecordUtils.getFreeFileName(Paths.get("record.mp4").toAbsolutePath())
                );
            }
        });
        fileMenu.add(txtFileItem);


        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private Path createFileChooser(String text, Path defaultFile) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(Paths.get("").toFile());
        chooser.setDialogTitle(text);
        if (Files.isDirectory(defaultFile)) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        chooser.setSelectedFile(defaultFile.toFile());
        chooser.showOpenDialog(mainPanel);
        File path = chooser.getSelectedFile();
        return path != null ? chooser.getSelectedFile().getAbsoluteFile().toPath() : defaultFile;
    }

    private void startFoundPresentation() {
        if (!FOUND_PRESENTATION) {
            log.info("Presentation auto search ON");
            FOUND_PRESENTATION = true;
            start.setEnabled(false);
            LectoriumThreadExecutor.getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    while (FOUND_PRESENTATION) {
                        PresentationFinder finder = new PresentationFinder();
                        String className = finder.find();
                        if (className != null) {
                            if (!(fileCheckBox.isSelected() || youtubeCheckBox.isSelected())) {
                                RecordUtils.callNotificationFrame("Внимание !", "Перед началом записи презентации " +
                                        "выберите режимы работы в приложении");
                            } else {
                                RecordUtils.callNotificationFrame("Запись", "Лекттоп автоматически начал запись презентации");
                                recordSource.setCaptureFrame(className);
                                start.setEnabled(true);
                                start.doClick();
                                stop.setEnabled(true);
                                start.setEnabled(false);
                                FOUND_PRESENTATION = false;
                                return;
                            }
                        }
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        }
                        log.info("Found presentation");
                    }
                    log.info("Presentation auto search OFF");
                }
            });
        }
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
                String winName = WindowsInfo.findFirstFrame();
                int res = JOptionPane.showConfirmDialog(
                        mainPanel,
                        "Do you want to capture a window - " + winName + " ?",
                        "Capture window selection",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );
                if (res == 0) {
                    setVisible(true);
                    recordSource.setCaptureFrame(winName);
                } else if (res == 1) {
                    selectedCapturedFrame();
                } else if (res == 2) {
                    setVisible(true);
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

    private void createPresentationFrame() {
        Rectangle rec = getScreenSize();
        JFrame f = new JFrame("Presentation");
        f.setLayout(new BorderLayout());
        f.setUndecorated(true);
        f.toFront();
        SlidePanel panel = new SlidePanel(presentationController.currentSlide());
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
                    panel.setImage(presentationController.prevSlide());
                    panel.repaint();

                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    panel.setImage(presentationController.nextSlide());
                    panel.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
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
        //   f.setAlwaysOnTop(true);
        f.show();
        f.setExtendedState(MAXIMIZED_HORIZ);
        RecordUtils.callNotificationFrame("Запись презентации началась", "Для остановки трансляции нажмите клавишу `Esc`");
    }

    private JFrame createTransparentFrame(Point loc, Dimension size) {
        JFrame f = new JFrame("Выделите область экрана");
        TransparentFrame bg = new TransparentFrame(f);
        bg.setLayout(new BorderLayout());
        bg.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED));
        f.setUndecorated(true);
        f.toFront();
        f.getContentPane().add("Center", bg);
        f.pack();
        f.setSize(size);
        f.setLocation(loc);
        f.setResizable(false);
        f.setAlwaysOnTop(true);
        f.show();
        f.setExtendedState(MAXIMIZED_HORIZ);
        return f;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        new Main().show();
    }
}
