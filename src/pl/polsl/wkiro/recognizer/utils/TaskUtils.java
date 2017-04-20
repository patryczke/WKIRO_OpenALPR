package pl.polsl.wkiro.recognizer.utils;


import com.openalpr.jni.exceptions.TaskInterruptedException;

public class TaskUtils {

    public static boolean checkTaskInterrupted(boolean throwException) {
        boolean result = false;
        if (Thread.interrupted()) {
            if(throwException) {
                throw new TaskInterruptedException("Process interrupted");
            } else {
                result = true;
            }
        }
        return result;
    }
}
