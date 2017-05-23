package pl.polsl.wkiro.recognizer.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.openalpr.jni.exceptions.ProcessingException;
import com.openalpr.jni.exceptions.TaskInterruptedException;
import pl.polsl.wkiro.recognizer.dto.ProcessingResult;
import pl.polsl.wkiro.recognizer.dto.Settings;
import pl.polsl.wkiro.recognizer.logic.RecognitionManager;
import pl.polsl.wkiro.recognizer.utils.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProcessPanel extends AbstractPanel implements ActionListener {

    private static final Logger log = new Logger(ProcessPanel.class);

    private JPanel inputOutputPanel;
    private JFileChooser inputFileChooser;
    private JFileChooser outputFileChooser;
    private JLabel inputFileLabel;
    private JLabel outputFileLabel;
    private JButton inputFileSelectButton;
    private JButton outputFileSelectButton;
    private JLabel inputFilePath;
    private JLabel outputFilePath;

    private JPanel startStopPanel;
    private JButton startProcessButton;
    private JButton stopProcessButton;

    private JPanel statisticsPanel;
    private JLabel elapsedTimeLabel;
    private long processingTime;
    private JLabel elapsedTimeValue;
    private JProgressBar progressBar;
    private JLabel progressNumeric;

    private Thread processTask;
    private ProcessingResult processResult;

    private static Font FONT_BOLD;
    private static Font FONT_ITALIC;
    static {
        Font defaultFont = new JLabel().getFont();
        FONT_BOLD = new Font(defaultFont.getFontName(), Font.BOLD, defaultFont.getSize() + 1);
        FONT_ITALIC = new Font(defaultFont.getFontName(), Font.ITALIC, defaultFont.getSize() - 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if(e.getSource() instanceof JButton) {
            if(e.getSource().equals(inputFileSelectButton)) {
                handleSelectInputFile();
            } else if(e.getSource().equals(outputFileSelectButton)) {
                handleSelectOutputFile();
            } else if(e.getSource().equals(startProcessButton)) {
                handleStartProcessing();
            } else if(e.getSource().equals(stopProcessButton)) {
                handleStopProcessing(true);
            }
        }
    }

    @Override
    protected void initialize() {

        setBorder(new TitledBorder("Process"));
        prepareFileChoosers();
        prepareStartStopButton();
        prepareStatisticsDetails();
    }

    private void prepareFileChoosers() {
        inputOutputPanel = new JPanel(new BorderLayout());

        inputFileChooser = new JFileChooser();
        outputFileChooser = new JFileChooser();
        outputFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        JPanel inputOutputLablesPanel = new JPanel(new GridLayout(2, 1));
        inputFileLabel = new JLabel("Video to process:");
        inputFileLabel.setFont(FONT_BOLD);
        inputOutputLablesPanel.add(inputFileLabel);
        outputFileLabel = new JLabel("Output directory:");
        outputFileLabel.setFont(FONT_BOLD);
        inputOutputLablesPanel.add(outputFileLabel);
        inputOutputPanel.add(inputOutputLablesPanel, BorderLayout.WEST);

        JPanel inputOutputPathsPanel = new JPanel(new GridLayout(2, 1));
        inputFilePath = new JLabel("path not selected");
        inputFilePath.setFont(FONT_ITALIC);
        inputOutputPathsPanel.add(inputFilePath);
        outputFilePath = new JLabel("path not selected");
        outputFilePath.setFont(FONT_ITALIC);
        inputOutputPathsPanel.add(outputFilePath);
        inputOutputPanel.add(inputOutputPathsPanel);

        JPanel inputOutputSelectButtonsPanel = new JPanel(new GridLayout(2, 1));
        inputFileSelectButton = new JButton("Select");
        inputFileSelectButton.addActionListener(this);
        inputOutputSelectButtonsPanel.add(inputFileSelectButton);
        outputFileSelectButton = new JButton("Select");
        outputFileSelectButton.addActionListener(this);
        inputOutputSelectButtonsPanel.add(outputFileSelectButton);
        inputOutputPanel.add(inputOutputSelectButtonsPanel, BorderLayout.EAST);

        //TODO: validate input as video
        //TODO: validate output as directory

        add(inputOutputPanel, BorderLayout.NORTH);
    }

    private void prepareStartStopButton() {
        startStopPanel = new JPanel();

        startProcessButton = new JButton("START PROCESSING");
        startProcessButton.addActionListener(this);
        startStopPanel.add(startProcessButton);
        stopProcessButton = new JButton("STOP PROCESSING");
        stopProcessButton.setEnabled(false);
        stopProcessButton.addActionListener(this);
        startStopPanel.add(stopProcessButton);

        add(startStopPanel);
    }

    private void prepareStatisticsDetails() {
        statisticsPanel = new JPanel();

        elapsedTimeLabel = new JLabel("Time elapsed [sec]: ");
        statisticsPanel.add(elapsedTimeLabel);
        elapsedTimeValue = new JLabel("0.00");
        statisticsPanel.add(elapsedTimeValue);
        progressBar = new JProgressBar();
        statisticsPanel.add(progressBar);
        progressNumeric = new JLabel();
        statisticsPanel.add(progressNumeric);

        //TODO: update progress (time elapsed + progress %)

        add(statisticsPanel, BorderLayout.SOUTH);
    }


    private void handleSelectInputFile() {
        int result = inputFileChooser.showOpenDialog(this);
        if(JFileChooser.APPROVE_OPTION == result) {
            inputFilePath.setText(inputFileChooser.getSelectedFile().toString());
            log.info("handleSelectInputFile", inputFileChooser.getSelectedFile().toString());
        }
    }

    private void handleSelectOutputFile() {
        int result = outputFileChooser.showOpenDialog(this);
        if(JFileChooser.APPROVE_OPTION == result) {
            outputFilePath.setText(outputFileChooser.getSelectedFile().toString());
            log.info("handleSelectOutputFile", outputFileChooser.getSelectedFile().toString());
        }
    }

    private void handleStartProcessing() {

        final String msg = "No file selected for processing";

        if(inputFileChooser.getSelectedFile() == null) {
            log.error("handleStartProcessing", msg);
            JOptionPane.showMessageDialog(this, msg);
            return;
        } else if(outputFileChooser.getSelectedFile() == null) {
            log.error("handleStartProcessing", msg);
            JOptionPane.showMessageDialog(this, msg);
            return;
        }

        startProcessButton.setEnabled(false);
        stopProcessButton.setEnabled(true);
        inputFileSelectButton.setEnabled(false);
        outputFileSelectButton.setEnabled(false);
        progressBar.setValue(0);
        progressNumeric.setText("0.00%");

        processTask = new Thread(() -> {
            try {
                processInternal();
            } catch(TaskInterruptedException tie) {
                log.warn("handleStartProcessing", tie.getMessage());
            }
        });
        processTask.start();
    }

    private void handleStopProcessing(boolean interrupt) {
        if(interrupt) {
            processTask.interrupt();
        }

        startProcessButton.setEnabled(true);
        stopProcessButton.setEnabled(false);
        inputFileSelectButton.setEnabled(true);
        outputFileSelectButton.setEnabled(true);

        progressBar.setValue(0);
        progressNumeric.setText("");
    }

    private void processInternal() {
        Settings setts = ((ConfigurationPanel)ApplicationGUI.getInstance().getConfigurationPanel()).getSettings();
        setts.setProcessingPaths(inputFileChooser.getSelectedFile().getPath(), outputFileChooser.getSelectedFile().getPath());
        log.debug("processInternal", "Processing with settings: " + new Gson().toJson(setts));

        try {
            //processResult = RRecognitionManager.getInstance().process(setts);
            processResult = new RecognitionManager(setts).process();

            if(setts.isExportToJson()) {
                exportReportToFile(processResult, setts.getOutputDirectoryPath());
            }
            log.debug("processResult: \n" + new Gson().toJson(processResult));

        } catch(ProcessingException pe) {
            log.error("processInternal", pe.getMessage());
            JOptionPane.showMessageDialog(this, "An error occurred while processing:\n" + pe.getMessage());
        }

        ApplicationGUI.getInstance().getResultsPanel().displayProcessingResults(processResult);
        handleStopProcessing(true);
    }

    public ProcessingResult getProcessResult() {
        return processResult;
    }

    public void setProgressBarValue(double percentageProgress) {
        int val = (int)(percentageProgress * 100);
        progressBar.setValue(val);
        progressBar.repaint();
        progressNumeric.setText(String.format("%.2f", percentageProgress * 100) + "%");
    }

    private void exportReportToFile(ProcessingResult result, String filePath) {

        String path = filePath + "/processingResult.txt";
        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String details = gson.toJson(result);
            writer.println(details);
        } catch (IOException e) {
            log.error("exportReportToFile", e.getMessage());
        }
    }
}
