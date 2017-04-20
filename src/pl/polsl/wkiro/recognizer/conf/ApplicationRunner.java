package pl.polsl.wkiro.recognizer.conf;

import pl.polsl.wkiro.recognizer.gui.ApplicationGUI;
import pl.polsl.wkiro.recognizer.utils.Logger;

import javax.swing.*;

public class ApplicationRunner {

    private static final Logger log = new Logger(ApplicationRunner.class);

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                log.error("main", "While initializing application: " + ex.getMessage());
            }

            ApplicationGUI gui = ApplicationGUI.getInstance();
            gui.initialize();
        });
    }
}
