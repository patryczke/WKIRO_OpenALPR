package pl.polsl.wkiro.recognizer.logic;

import com.openalpr.jni.exceptions.ProcessingException;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IStreamCoder;
import pl.polsl.wkiro.recognizer.dto.ProcessingResult;
import pl.polsl.wkiro.recognizer.dto.Settings;
import pl.polsl.wkiro.recognizer.gui.ApplicationGUI;
import pl.polsl.wkiro.recognizer.utils.Logger;
import pl.polsl.wkiro.recognizer.utils.TaskUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

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
    private IMediaWriter writer;

    private boolean ENABLE_VIDEO_WRITER = false;

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
            throw new ProcessingException(e.getMessage());
        } finally {
            if(mediaReader != null) {
                mediaReader.close();
            }
            if(writer != null) {
                writer.close();
            }

            log.debug("process", "Media closed");
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

        if(writer == null && ENABLE_VIDEO_WRITER) {
            IStreamCoder coder = mediaReader.getContainer().getStream(0).getStreamCoder();
            Dimension screenBounds = new Dimension(coder.getWidth(), coder.getHeight());

            writer = ToolFactory.makeWriter(settings.getOutputDirectoryPath() + "\\processedVideo.mp4");
            writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, screenBounds.width, screenBounds.height);
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
                    log.debug("Progress: " + progress * 100);
                }
            }).start();
        }

        if(ENABLE_VIDEO_WRITER) {
            BufferedImage img = convertToType(event.getImage(), BufferedImage.TYPE_3BYTE_BGR);
            try {
                writer.encodeVideo(0, img, event.getTimeStamp(), TimeUnit.MICROSECONDS);
            } catch(Throwable e) {
                log.error("onVideoPicture", "Error while encoding video frame at: " + event.getTimeStamp() + ", " + e.getMessage());
            }
        }
    }


    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {

        BufferedImage image;
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } else {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
    }
}
