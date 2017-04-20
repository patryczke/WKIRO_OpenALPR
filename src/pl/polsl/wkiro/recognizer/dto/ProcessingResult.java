package pl.polsl.wkiro.recognizer.dto;

import com.openalpr.jni.AlprPlateResult;

import java.util.List;
import java.util.Map;

public class ProcessingResult {

    private Map<String, ProcessedFrame> frames;
    private Settings settings;
    private float totalProcessingTime;
    private int frameWidth;
    private int frameHeight;
    private List<String> frameNames;

    public Map<String, ProcessedFrame>  getFrames() {
        return frames;
    }
    public void setFrames(Map<String, ProcessedFrame> frames) {
        this.frames = frames;
    }

    public Settings getSettings() {
        return settings;
    }
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public float getTotalProcessingTime() {
        return totalProcessingTime;
    }
    public void setTotalProcessingTime(float totalProcessingTime) {
        this.totalProcessingTime = totalProcessingTime;
    }

    public int getFrameWidth() {
        return frameWidth;
    }
    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }
    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    public List<String> getFrameNames() {
        return frameNames;
    }
    public void setFrameNames(List<String> frameNames) {
        this.frameNames = frameNames;
    }

    public static class ProcessedFrame {
        private float processingTime;
        private String pathToFrame;
        private List<AlprPlateResult> plates;
        private String name;

        public ProcessedFrame() {}

        public ProcessedFrame(List<AlprPlateResult> plates, String name, String pathToFrame, float processingTime) {
            this.plates = plates;
            this.name = name;
            this.pathToFrame = pathToFrame;
            this.processingTime = processingTime;
        }

        public float getProcessingTime() {
            return processingTime;
        }
        public void setProcessingTime(float processingTime) {
            this.processingTime = processingTime;
        }

        public String getPathToFrame() {
            return pathToFrame;
        }
        public void setPathToFrame(String pathToFrame) {
            this.pathToFrame = pathToFrame;
        }

        public List<AlprPlateResult> getPlates() {
            return plates;
        }
        public void setPlates(List<AlprPlateResult> plates) {
            this.plates = plates;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
}
