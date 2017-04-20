package pl.polsl.wkiro.recognizer.logic;


import com.openalpr.jni.Alpr;
import com.openalpr.jni.AlprPlateResult;
import com.openalpr.jni.AlprResults;
import com.openalpr.jni.exceptions.ProcessingException;
import com.openalpr.jni.exceptions.TaskInterruptedException;
import pl.polsl.wkiro.recognizer.dto.ProcessingResult;
import pl.polsl.wkiro.recognizer.dto.Settings;
import pl.polsl.wkiro.recognizer.utils.Dictionary;
import pl.polsl.wkiro.recognizer.utils.DrawUtils;
import pl.polsl.wkiro.recognizer.utils.Logger;
import pl.polsl.wkiro.recognizer.utils.TaskUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static pl.polsl.wkiro.recognizer.utils.Constants.Paths.OPENALPR_CONFIG;
import static pl.polsl.wkiro.recognizer.utils.Constants.Paths.OPENALPR_RUNTIME_DIR;

public class RecognitionProcessor {

    private static final Logger log = new Logger(RecognitionProcessor.class);

    public ProcessingResult recognize(BufferedImage image, Settings settings, long timeMicroSeconds, ProcessingResult processingResult) throws ProcessingException, TaskInterruptedException {

        TaskUtils.checkTaskInterrupted(true);

        if (processingResult == null) {
            processingResult = new ProcessingResult();
        }

        if (image == null || image.getGraphics() == null) {
            String msg = "No valid image provided to recognize";
            log.error("recognize", msg);
            throw new ProcessingException(msg);
        }

        String key = formatTime(timeMicroSeconds);
        String path = settings.getOutputDirectoryPath() + "/" + key + ".png";

        key = key.replace("_", ":");

        Alpr alprProcessor = null;
        AlprResults results = null;
        try {
            byte[] imageByteArray = convertToByteArray(image);
            log.debug("recognize", "Process start, image size: " + imageByteArray.length);

            alprProcessor = preconfigure(settings);
            results = alprProcessor.recognize(imageByteArray);

            Set<String> recognizedPlates = new HashSet<>();

            for (AlprPlateResult result : results.getPlates()) {
                TaskUtils.checkTaskInterrupted(true);

                recognizedPlates.add(result.getBestPlate().getCharacters());
                DrawUtils.drawPlates(settings.isSelectFoundPlates(), settings.isDrawRecognitionOnPlate(), image, result.getPlatePoints(), result.getBestPlate().getCharacters());
            }

            if (results == null || results.getPlates().isEmpty()) {
                return null;
            }

            ImageIO.write(image, "png", new File(path));
            log.info("recognize", "Frame saved to: " + path + "\n" + "Plates recognized: " + Arrays.toString(recognizedPlates.toArray()));

            if (processingResult.getFrameNames() == null) {
                processingResult.setFrameNames(new ArrayList<>());
            }

            processingResult.getFrameNames().add(key);

            if (processingResult.getFrames() == null) {
                processingResult.setFrames(new LinkedHashMap<>());
            }
            processingResult.getFrames().put(key, new ProcessingResult.ProcessedFrame(results.getPlates(), key, path, results.getTotalProcessingTimeMs()));
        } catch (Throwable e) {
            log.error("recognize", "Error while recognizing plates: " + e.getMessage());
            throw new ProcessingException(e.getMessage(), e);
        } finally {
            if (alprProcessor != null) {
                alprProcessor.unload();
            }
            log.debug("recognize", "Unloaded ALPR processor");
        }

        log.debug("recognize", "Processing result: " + results.getPlates());
        return processingResult;
    }

    private Alpr preconfigure(Settings settings) {

        Alpr alpr = new Alpr(settings.getPlateRegion(), OPENALPR_CONFIG, OPENALPR_RUNTIME_DIR);
        alpr.setTopN(settings.getTopMatchResult());

        if (!Dictionary.NOT_SELECTED.equals(settings.getSpecificRegion())) {
            alpr.setDefaultRegion(settings.getSpecificRegion());
        } else {
            alpr.setDefaultRegion("");
        }

        return alpr;
    }

    private String formatTime(long timeMicro) {
        final int MILISECOND_FACTOR = 1000;
        final String TIME_FORMAT = "mm_ss_SSS";

        Date date = new Date(timeMicro / MILISECOND_FACTOR);
        DateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        String dateFormatted = formatter.format(date);

        long hours = (timeMicro / (MILISECOND_FACTOR * MILISECOND_FACTOR)) / 3600;
        dateFormatted = String.format("%02d_%s", hours, dateFormatted);

        return dateFormatted;
    }

    private byte[] convertToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            return baos.toByteArray();
        } finally {
            baos.close();
        }
    }

}
