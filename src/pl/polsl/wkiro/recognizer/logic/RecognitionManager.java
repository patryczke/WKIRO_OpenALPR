package pl.polsl.wkiro.recognizer.logic;

import com.openalpr.jni.exceptions.ProcessingException;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IError;
import pl.polsl.wkiro.recognizer.dto.ProcessingResult;
import pl.polsl.wkiro.recognizer.dto.Settings;
import pl.polsl.wkiro.recognizer.gui.ApplicationGUI;
import pl.polsl.wkiro.recognizer.utils.Logger;
import pl.polsl.wkiro.recognizer.utils.TaskUtils;

import java.awt.image.BufferedImage;

public class RecognitionManager extends MediaListenerAdapter {

    private static Logger log = new Logger(RecognitionManager.class);
    private static final int MILIS_IN_SECOND = 1000;

    private long microSecondsBetweenFrames;
    private Settings settings;
    private int mVideoStreamIndex;
    private long mLastPtsWrite = Global.NO_PTS;

    private ProcessingResult processingResult;
    private long totalVideoDuration = -1;
    private IMediaReader mediaReader;

    public RecognitionManager(Settings settings) {
        this.settings = settings;
        int frameOffset = settings.getFrameSkippingOffset();

        microSecondsBetweenFrames = MILIS_IN_SECOND * frameOffset;
        mVideoStreamIndex = -1;
        mLastPtsWrite = 0;

        processingResult = new ProcessingResult();
    }


    public ProcessingResult process() throws ProcessingException {


        try {
            ToolFactory.setTurboCharged(true);
            mediaReader = ToolFactory.makeReader(settings.getInputFilePath());
            mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
            mediaReader.addListener(this);

            IError readError = null;
            while ((readError = mediaReader.readPacket()) == null) {
                ; //wait while processing
                TaskUtils.checkTaskInterrupted(true);
            }
        } catch (Throwable e) {
            log.error("process", "An exception while processing video: " + e.getMessage());

        } finally {
            mediaReader.close();
            log.debug("process", "Media reader closed");
        }

        return processingResult;
    }

    @Override
    public void onVideoPicture(IVideoPictureEvent event) {

        if(totalVideoDuration == -1) {
            totalVideoDuration = mediaReader.getContainer().getDuration();
            log.info("Total video duration: " + totalVideoDuration + "[us]");
        }

        if (event.getStreamIndex() != mVideoStreamIndex) {
            if (mVideoStreamIndex == -1) {
                mVideoStreamIndex = event.getStreamIndex();
            } else {
                return;
            }
        }

        if (mLastPtsWrite == Global.NO_PTS) {
            mLastPtsWrite = microSecondsBetweenFrames;
        }

        if (event.getTimeStamp() - mLastPtsWrite >= microSecondsBetweenFrames) {
            RecognitionProcessor alprProcessor = new RecognitionProcessor();
            alprProcessor.recognize(event.getImage(), settings, event.getTimeStamp(), processingResult);

            log.debug("onVideoPicture", "Frame saved. Time: " + event.getTimeStamp() + ", Last write: " + mLastPtsWrite);
            mLastPtsWrite += microSecondsBetweenFrames;

            new Thread(() -> {
                if(totalVideoDuration > 0) {
                    double progress = ((double)event.getTimeStamp()) / (double)totalVideoDuration;
                    ApplicationGUI.getInstance().getProcessPanel().setProgressBarValue(progress);
                    System.out.println(">>> PROGRESS: " + progress * 100);
                }
            }).start();
        }
    }
}
