package pl.polsl.wkiro.recognizer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private Class className;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Logger(Class className) {
        this.className = className;
    }

    public void debug(String message) {
        debug(null, message);
    }
    public void debug(String methodName, String message) {
        processLog(Constants.LogLevel.DEBUG, methodName, message);
    }

    public void info(String message) {
        info(null, message);
    }
    public void info(String methodName, String message) {
        processLog(Constants.LogLevel.INFO, methodName, message);
    }

    public void warn(String message) {
        warn(null, message);
    }
    public void warn(String methodName, String message) {
        processLog(Constants.LogLevel.WARN, methodName, message);
    }

    public void error(String message) {
        error(null, message);
    }
    public void error(String methodName, String message) {
        processLog(Constants.LogLevel.ERROR, methodName, message);
    }

    private void processLog(Constants.LogLevel level, String methodName, String message) {

        if(level.value() >= Constants.LOG_LEVEL.value()) {
            String date = DATE_FORMAT.format(new Date());
            String msg = String.format("[%-5s]%s|%s|%s|%s|", level, date, className, methodName, message);
            if(level == Constants.LogLevel.ERROR) {
                System.err.println(msg);
            } else {
                System.out.println(msg);
            }
        }
    }

}
