package client;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import java.io.File;

import javax.imageio.ImageIO;

public class ImageHolder {
    private BufferedImage[][][][] Images;
    private String RouteDirectory;
    public ImageHolder(String routeDirectory) {
        super();
        this.Images = new BufferedImage[3][8][7][6];
        this.RouteDirectory = routeDirectory;
        //System.out.println("Created empty image array");
        for (int idx1 = 0; idx1 < 3; idx1++) {
            for (int idx2 = 0; idx2 < 8; idx2++) {
                for (int idx3 = 0; idx3 < 7; idx3++) {
                    for (int idx4 = 0; idx4 < 6; idx4++) {
                        BufferedImage img;
                        try {
                            img =
                                ImageIO.read(new File(this.RouteDirectory + "Images - Copy\\" + idx1 + idx2 + idx3 +
                                                      idx4 + ".bmp"));
                            //System.out.println("Loaded image " + idx1 + idx2 + idx3 + idx4);
                            BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), 2);
                            for (int x = 0; x < img.getWidth(); x++) {
                                for (int y = 0; y < img.getHeight(); y++) {
                                    int c = img.getRGB(x, y);
                                    if (c != 0xFFFFFFFF) {
                                        img2.setRGB(x, y, c);
                                    } else {
                                        img2.setRGB(x, y, 0x00000000);
                                    }
                                }
                            }
                            Images[idx1][idx2][idx3][idx4] = img2;
                        } catch (Exception e) {
                            System.out.println("Picture [" + idx1 + "]["+ idx2 + "][" + idx3 + "][" + idx4 + "]" + " empty");
                            //e.getMessage();
                        }
                    }
                }
            }
        }
    }
    public BufferedImage getImageByRef(String ref){
        int prt1 = Integer.parseInt(ref.substring(0, 1));
        int prt2 = Integer.parseInt(ref.substring(1, 2));
        int prt3 = Integer.parseInt(ref.substring(2, 3));
        int prt4 = Integer.parseInt(ref.substring(3, 4));
        BufferedImage img = this.Images[prt1][prt2][prt3][prt4];
        //BufferedImage img2 = this.toCompatibleImage(img);
        return img;
    }

    private BufferedImage toCompatibleImage(BufferedImage image) {
        // obtain the current system graphical settings
        GraphicsConfiguration gfx_config =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        /*
             * if image is already compatible and optimized for current system
             * settings, simply return it
             */
        if (image.getColorModel().equals(gfx_config.getColorModel()))
            return image;

        // image is not optimized, so create a new image that is
        BufferedImage new_image =
            gfx_config.createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());

        // get the graphics context of the new image to draw the old image on
        Graphics2D g2d = (Graphics2D) new_image.getGraphics();

        // actually draw the image and dispose of context no longer needed
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // return the new optimized image
        return new_image;
    }
    public BufferedImage[][][][] getImages(){
        return this.Images;
    }
}
