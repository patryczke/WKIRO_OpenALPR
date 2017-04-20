package pl.polsl.wkiro.recognizer.utils;

import sun.rmi.runtime.Log;

public class Constants {

    public enum LogLevel {
        DEBUG(0), INFO(1), WARN(2), ERROR(3);
        LogLevel(int val) {
            this.val = val;
        }
        private int val;
        public int value() { return val; }
    }
    public static final LogLevel LOG_LEVEL = LogLevel.INFO;

    public interface MainWindow {
        String APPLICATION_NAME = "WKIRO - plate recognizer";

        int MIN_WIDTH = 800;
        int MIN_HEIGHT = 600;
    }

    public interface Paths {
        String OPENALPR_CONFIG = "resources/openalpr.conf";
        String OPENALPR_RUNTIME_DIR = "resources/runtime_data";
    }

    public interface Factors {

        int PLATE_BORDER_STROKE = 5;
        String PLATE_FONT = "Impact";
        double FONT_FACTOR = 0.8d;
        double RESIZE_PLATE_FACTOR = 0.01d;

    }

}
