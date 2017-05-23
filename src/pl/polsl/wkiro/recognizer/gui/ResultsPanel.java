package pl.polsl.wkiro.recognizer.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pl.polsl.wkiro.recognizer.dto.ProcessingResult;
import pl.polsl.wkiro.recognizer.utils.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResultsPanel extends AbstractPanel implements ActionListener, ListSelectionListener, ChangeListener {

    private static final Logger log = new Logger(ResultsPanel.class);

    private static final String ZOOM_PLUS_CHAR = "+";
    private static final String ZOOM_MINUS_CHAR = "-";
    private static final String ZOOM_ADJUST_CHAR = "A";
    private static final String ZOOM_PERCENTAGE_CHAR = "%";
    private static final int FULL_SIZE_PERCENTAGE = 100;
    private static final int MIN_ZOOM_PERCENTAGE = 1;
    private static final int MAX_ZOOM_PERCENTAGE = 300;
    private static final int ZOOM_STEP = 10;

    private JPanel resultPanel;

    private JSplitPane resultSplitPanel;
    private JList recognitionsList;
    private JScrollPane recognitionsListScrollablePanel;
    private JScrollPane platesResultScrollablePanel;
    private JLabel platePicture;

    private JPanel pictureToolsPanel;
    private int currentZoom;
    private JSlider zoomSlider;
    private JButton zoomPlusButton;
    private JButton zoomMinusButton;
    private JButton zoomAdjustButton;
    private JLabel zoomCurrentLabel;
    private JButton showDetailsButton;
    private ImageIcon currentImage;

    private JPanel exportPanel;
    private JScrollPane detailsPanel;
    private JTextArea detailsArea;
    private JDialog detailsWindow;

    private ProcessingResult result;

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting()) {
            return;
        }
        //reload pic
        if(e.getSource() instanceof JList) {
            if(e.getSource().equals(recognitionsList)) {
                handleFrameViewChange();
            }
        }

        handleZoomAuto();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() instanceof JButton) {
            if (e.getSource().equals(zoomPlusButton)) {
                handleZoomPlus();
            } else if (e.getSource().equals(zoomMinusButton)) {
                handleZoomMinus();
            } else if (e.getSource().equals(zoomAdjustButton)) {
                handleZoomAuto();
            } else if(e.getSource().equals(showDetailsButton)) {
                prepareDetailsWindow();
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        if(e.getSource() instanceof JSlider) {
            if(e.getSource().equals(zoomSlider)) {
                if(zoomSlider.getValueIsAdjusting()) {
                    //only label change
                    zoomCurrentLabel.setText(zoomSlider.getValue() + ZOOM_PERCENTAGE_CHAR);
                    return;
                }

                handleZoomSlider(zoomSlider.getValue());
            }
        }
    }

    @Override
    protected void initialize() {

        setBorder(new TitledBorder("Results"));
        resultPanel = new JPanel(new BorderLayout());

        recognitionsList = new JList(new String[]{});
        recognitionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recognitionsList.addListSelectionListener(this);

        recognitionsListScrollablePanel = new JScrollPane(recognitionsList);

        platePicture = new JLabel();
        platePicture.setFont(platePicture.getFont().deriveFont(Font.ITALIC));

        platesResultScrollablePanel = new JScrollPane(platePicture);

        resultSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, recognitionsListScrollablePanel, platesResultScrollablePanel);
        resultPanel.add(resultSplitPanel, BorderLayout.CENTER);

        pictureToolsPanel = new JPanel(new BorderLayout());

        zoomPlusButton = new JButton(ZOOM_PLUS_CHAR);
        zoomPlusButton.setToolTipText("Zoom in");
        zoomPlusButton.addActionListener(this);
        zoomMinusButton = new JButton(ZOOM_MINUS_CHAR);
        zoomMinusButton.setToolTipText("Zoom out");
        zoomMinusButton.addActionListener(this);
        zoomAdjustButton = new JButton(ZOOM_ADJUST_CHAR);
        zoomAdjustButton.setToolTipText("Auto adjust zoom");
        zoomAdjustButton.addActionListener(this);

        pictureToolsPanel.add(new JPanel(new GridLayout(3, 1)) {{
            add(zoomPlusButton);
            add(zoomMinusButton);
            add(zoomAdjustButton);
        }}, BorderLayout.NORTH);

        zoomSlider = new JSlider(JSlider.VERTICAL, MIN_ZOOM_PERCENTAGE,MAX_ZOOM_PERCENTAGE, FULL_SIZE_PERCENTAGE);
        zoomSlider.addChangeListener(this);
        pictureToolsPanel.add(zoomSlider);

        zoomCurrentLabel = new JLabel(currentZoom + ZOOM_PERCENTAGE_CHAR);
        pictureToolsPanel.add(zoomCurrentLabel, BorderLayout.SOUTH);

        resultPanel.add(pictureToolsPanel, BorderLayout.EAST);

        exportPanel = new JPanel();
        showDetailsButton = new JButton("Show details");
        showDetailsButton.addActionListener(this);
        exportPanel.add(showDetailsButton);
        resultPanel.add(exportPanel, BorderLayout.SOUTH);

        add(resultPanel);

        currentImage = new ImageIcon();
    }

    public void displayProcessingResults(ProcessingResult result) {
        this.result = result;

        if(result == null || result.getFrameNames() == null || result.getFrameNames().isEmpty()) {
            recognitionsList.setListData(new String[]{});
            log.debug("displayProcessingResults", "No results found: empty result list");
        } else {
            recognitionsList.setListData(result.getFrameNames().toArray());
            log.debug("displayProcessingResults", "Results found: " + result.getFrameNames().size());
        }
    }

    public void displayVideoProcessingResults(ProcessingResult result) {

        recognitionsList.setListData(new String[]{});
        //TODO: new panel with media player or no :)
    }

    private void handleZoomPlus() {
        currentZoom += ZOOM_STEP;
        zoomSlider.setValue(currentZoom);
        zoomCurrentLabel.setText(currentZoom + ZOOM_PERCENTAGE_CHAR);
        new Thread(() -> {
            repaintRecognition();
        });
    }

    private void handleZoomMinus() {
        currentZoom -= ZOOM_STEP;
        zoomSlider.setValue(currentZoom);
        zoomCurrentLabel.setText(currentZoom + ZOOM_PERCENTAGE_CHAR);
        new Thread(() -> {
            repaintRecognition();
        });
    }

    private void handleZoomAuto() {
        int height = currentImage.getIconHeight();
        int windowHeight = platesResultScrollablePanel.getHeight();
        double heightScale = (double)windowHeight / (double)height;
        currentZoom = (int)(FULL_SIZE_PERCENTAGE * heightScale);

        zoomSlider.setValue(currentZoom);
        zoomCurrentLabel.setText(currentZoom + ZOOM_PERCENTAGE_CHAR);

        new Thread(()-> {
            repaintRecognition();
        }).start();
    }

    private void handleZoomSlider(int sliderZoom) {
        currentZoom = sliderZoom;
        zoomCurrentLabel.setText(currentZoom + ZOOM_PERCENTAGE_CHAR);
        new Thread(()-> {
            repaintRecognition();
        }).start();
    }

    private void handleFrameViewChange() {

        if(recognitionsList.getSelectedValue() != null) {
            log.debug("handleFrameViewChange", "Selected list value: " + recognitionsList.getSelectedValue());
            ProcessingResult.ProcessedFrame frame = result.getFrames().get((String) recognitionsList.getSelectedValue());

            if (frame != null) {
                currentImage = new ImageIcon(frame.getPathToFrame());
                log.debug("handleFrameViewChange", "Frame reloaded to: " + frame.getPathToFrame());
            } else {
                currentImage = new ImageIcon();
                log.warn("handleFrameViewChange", "Frame not loaded - frame is null");
            }
        } else {
            //select empty
            log.debug("handleFrameViewChange", "No value selected - no data to display");
            currentImage = new ImageIcon();
            currentZoom = 100;
        }
    }

    private synchronized void repaintRecognition() {
        int w = currentImage.getIconWidth();
        int h = currentImage.getIconHeight();
        Image imgToScale = currentImage.getImage();

        if(imgToScale != null) {
            Image scaled = imgToScale.getScaledInstance(w * currentZoom / FULL_SIZE_PERCENTAGE, h * currentZoom / FULL_SIZE_PERCENTAGE, java.awt.Image.SCALE_SMOOTH);
            platePicture.setIcon(new ImageIcon(scaled));
            platesResultScrollablePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            platesResultScrollablePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        } else {
            platePicture.setIcon(new ImageIcon());
        }
    }

    private void prepareDetailsWindow() {

        if(recognitionsList.getSelectedValue() == null) {
            log.info("prepareDetailsWindow", "No data selected to display details");
            return;
        } else {
            log.debug("prepareDetailsWindow", "Details for: " + recognitionsList.getSelectedValue());
        }

        detailsWindow = new JDialog(ApplicationGUI.getInstance().getMainFrame(), "Details");
        detailsWindow.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        detailsWindow.setSize(new Dimension(500, 700));

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapStyleWord(true);

        ProcessingResult.ProcessedFrame frame = result.getFrames().get((String) recognitionsList.getSelectedValue());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String details = gson.toJson(frame);
        detailsArea.setText(details);

        detailsPanel = new JScrollPane(detailsArea);
        detailsPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        detailsWindow.add(detailsPanel);

        detailsWindow.setVisible(true);
    }
}
