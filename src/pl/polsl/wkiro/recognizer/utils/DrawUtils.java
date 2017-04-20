package pl.polsl.wkiro.recognizer.utils;

import com.openalpr.jni.AlprCoordinate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static pl.polsl.wkiro.recognizer.utils.Constants.Factors.FONT_FACTOR;
import static pl.polsl.wkiro.recognizer.utils.Constants.Factors.PLATE_BORDER_STROKE;
import static pl.polsl.wkiro.recognizer.utils.Constants.Factors.RESIZE_PLATE_FACTOR;

public class DrawUtils {

    private static Logger log = new Logger(DrawUtils.class);
    private static final String EXTENSION_DOT = ".";

    private static Image PLATE_TEMPLATE;
    static {
        try {
            PLATE_TEMPLATE = ImageIO.read(new File("resources/images/template.png"));
        } catch(Throwable e) {
            log.error("static", e.getMessage());
        }
    }

    public static String savePicture(BufferedImage image, String path, String fileName, String extension) throws IOException {

        if(image == null) {
            return null;
        }
        String outputPath = path + fileName + EXTENSION_DOT + extension;
        ImageIO.write(image, extension, new File(path));

        return outputPath;
    }

    public static void drawPlates(boolean drawFrame, boolean drawPlate, BufferedImage image, List<AlprCoordinate> plateCords, String recognition) {

        Graphics2D picture = (Graphics2D) image.getGraphics();

        picture.setStroke(new BasicStroke(PLATE_BORDER_STROKE));
        picture.setColor(Color.CYAN);

        int leftUpperX = plateCords.get(0).getX();
        leftUpperX -= RESIZE_PLATE_FACTOR * leftUpperX;
        int leftUpperY = plateCords.get(0).getY();
        leftUpperY -= RESIZE_PLATE_FACTOR * leftUpperY;
        int rightUpperX = plateCords.get(1).getX();
        rightUpperX += RESIZE_PLATE_FACTOR * rightUpperX;
        int rightUpperY = plateCords.get(1).getY();
        rightUpperY -= RESIZE_PLATE_FACTOR * rightUpperY;
        int rightLowerX = plateCords.get(2).getX();
        rightLowerX += RESIZE_PLATE_FACTOR * rightLowerX;
        int rightLowerY = plateCords.get(2).getY();
        rightLowerY += RESIZE_PLATE_FACTOR * rightLowerY;
        int leftLowerX = plateCords.get(3).getX();
        leftLowerX -= RESIZE_PLATE_FACTOR * leftLowerX;
        int leftLowerY = plateCords.get(3).getY();
        leftLowerY += RESIZE_PLATE_FACTOR * leftLowerY;

        int height = leftLowerY - leftUpperY;
        int width = rightUpperX - leftLowerX;

        if (drawFrame) {
            picture.drawLine(leftUpperX, leftUpperY, rightUpperX, rightUpperY);
            picture.drawLine(rightUpperX, rightUpperY, rightLowerX, rightLowerY);
            picture.drawLine(rightLowerX, rightLowerY, leftLowerX, leftLowerY);
            picture.drawLine(leftLowerX, leftLowerY, leftUpperX, leftUpperY);
        }

        if (drawPlate) {
            picture.setFont(new Font(Constants.Factors.PLATE_FONT, Font.BOLD, (int) (FONT_FACTOR * height)));
            picture.setColor(Color.BLACK);

            //find max length
            int toLeft = leftUpperX;
            int toRight = image.getWidth() - rightUpperX;
            int toUp = leftUpperY;
            int toDown = image.getHeight() - leftLowerY;

            int width02 = (int) (0.2 * width);
            int height02 = (int) (0.2 * height);
            int charactersLength = recognition.length();

            picture.drawImage(PLATE_TEMPLATE, leftUpperX - width02/2, leftUpperY-height, (int) (0.6 * charactersLength * height), (int) (1.2 * height), null);
            picture.drawString(recognition, leftUpperX - width02 / 2, leftUpperY - height02 / 2);
        }
        picture.dispose();
    }
}
