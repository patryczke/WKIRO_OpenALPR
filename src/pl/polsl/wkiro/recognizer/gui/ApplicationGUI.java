package pl.polsl.wkiro.recognizer.gui;


import pl.polsl.wkiro.recognizer.utils.Constants;
import pl.polsl.wkiro.recognizer.utils.Logger;

import javax.swing.*;
import java.awt.*;

public class ApplicationGUI {

    private static final Logger log = new Logger(ApplicationGUI.class);

    private ApplicationGUI() {
    }

    private static class SingletonLoader {
        private static ApplicationGUI INSTANCE = new ApplicationGUI();
    }

    public static ApplicationGUI getInstance() {
        return SingletonLoader.INSTANCE;
    }

    // GUI config
    //------------

    private JFrame mainFrame;
    private ConfigurationPanel configurationPanel;
    private ProcessPanel processPanel;
    private ResultsPanel resultsPanel;

    public void initialize() {
        initMainWindow();
    }

    private void initMainWindow() {
        log.debug("initMainWindow", "Before init application");

        mainFrame = new JFrame(Constants.MainWindow.APPLICATION_NAME);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setMinimumSize(new Dimension(Constants.MainWindow.MIN_WIDTH, Constants.MainWindow.MIN_HEIGHT));

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        mainFrame.add(northPanel, BorderLayout.NORTH);

        configurationPanel = new ConfigurationPanel();
        northPanel.add(configurationPanel, BorderLayout.NORTH);

        processPanel = new ProcessPanel();
        northPanel.add(processPanel);

        resultsPanel = new ResultsPanel();
        mainFrame.add(resultsPanel, BorderLayout.CENTER);

        mainFrame.setLocationRelativeTo(null);
        mainFrame.pack();
        mainFrame.setVisible(true);

        log.debug("initMainWindow", "After init application");
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }
    public ConfigurationPanel getConfigurationPanel() {
        return configurationPanel;
    }
    public ProcessPanel getProcessPanel() {
        return processPanel;
    }
    public ResultsPanel getResultsPanel() {
        return resultsPanel;
    }
}
