package pl.polsl.wkiro.recognizer.dto;

public class Settings {

    private int topMatchResult;
    private int frameSkippingOffset; //in ms
    private String plateRegion;
    private String specificRegion;
    private boolean exportToJson;
    private boolean saveMatchedFrames;
    private boolean selectFoundPlates;
    private boolean drawRecognitionOnPlate;
    private String inputFilePath;
    private String outputDirectoryPath;

    public void setProcessingParameters(int topMatchResult, int frameSkippingOffset) {
        this.topMatchResult = topMatchResult;
        this.frameSkippingOffset = frameSkippingOffset;
    }

    public void setExportFlags(boolean exportToJson, boolean saveMatchedFrames, boolean drawRecognitionOnPlate, boolean selectFoundPlates) {
        this.exportToJson = exportToJson;
        this.saveMatchedFrames = saveMatchedFrames;
        this.drawRecognitionOnPlate = drawRecognitionOnPlate;
        this.selectFoundPlates = selectFoundPlates;
    }

    public void setRegions(String plateRegion, String specificRegion) {
        this.plateRegion = plateRegion;
        this.specificRegion = specificRegion;
    }

    public void setProcessingPaths(String inputFilePath, String outputDirectoryPath) {
        this.inputFilePath = inputFilePath;
        this.outputDirectoryPath = outputDirectoryPath;
    }

    public int getTopMatchResult() {
        return topMatchResult;
    }
    public void setTopMatchResult(int topMatchResult) {
        this.topMatchResult = topMatchResult;
    }

    public int getFrameSkippingOffset() {
        return frameSkippingOffset;
    }
    public void setFrameSkippingOffset(int frameSkippingOffset) {
        this.frameSkippingOffset = frameSkippingOffset;
    }

    public String getPlateRegion() {
        return plateRegion;
    }
    public void setPlateRegion(String plateRegion) {
        this.plateRegion = plateRegion;
    }

    public String getSpecificRegion() {
        return specificRegion;
    }
    public void setSpecificRegion(String specificRegion) {
        this.specificRegion = specificRegion;
    }

    public boolean isExportToJson() {
        return exportToJson;
    }
    public void setExportToJson(boolean exportToJson) {
        this.exportToJson = exportToJson;
    }

    public boolean isSaveMatchedFrames() {
        return saveMatchedFrames;
    }
    public void setSaveMatchedFrames(boolean saveMatchedFrames) {
        this.saveMatchedFrames = saveMatchedFrames;
    }

    public boolean isDrawRecognitionOnPlate() {
        return drawRecognitionOnPlate;
    }
    public void setDrawRecognitionOnPlate(boolean drawRecognitionOnPlate) {
        this.drawRecognitionOnPlate = drawRecognitionOnPlate;
    }

    public boolean isSelectFoundPlates() {
        return selectFoundPlates;
    }
    public void setSelectFoundPlates(boolean selectFoundPlates) {
        this.selectFoundPlates = selectFoundPlates;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }
    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutputDirectoryPath() {
        return outputDirectoryPath;
    }
    public void setOutputDirectoryPath(String outputDirectoryPath) {
        this.outputDirectoryPath = outputDirectoryPath;
    }
}
