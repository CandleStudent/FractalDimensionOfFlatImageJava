import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class BWImage {
    private final int height;
    private final int width;
    private BufferedImage img;

    // a black-and-white image is created immediately
    public BWImage(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.img = img;
        this.height = Objects.requireNonNull(img).getHeight();
        this.width = img.getWidth();
        convertToBw();
    }

    public BWImage(int height, int width, BufferedImage img) {
        this.height = height;
        this.width = width;
        this.img = img;
        convertToBw();
    }

    public BufferedImage getImg() {
        return img;
    }

    private void convertToBw() {
        BufferedImage bw = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D graphic = bw.createGraphics();
        graphic.drawImage(img, 0, 0, null);
        this.img = bw;
    }

    private void writeImageJPEG(String path) throws IOException {
        ImageIO.write(img, "jpg", new File(path));
    }

    //  we get a matrix of dimension with image, where black is true and white is false
    public boolean[][] convertToBwMatrix() {
        boolean[][] bw = new boolean[img.getWidth()][img.getHeight()];
        for (int i = 0; i < bw.length; i++) {
            for (int j = 0; j < bw[0].length; j++) {
                bw[i][j] = (img.getRGB(i, j) != -1);
            }
        }
        return bw;
    }
}
