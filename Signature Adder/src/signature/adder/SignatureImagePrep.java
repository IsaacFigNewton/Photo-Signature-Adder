/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signature.adder;

/**
 *
 * @author Owner
 */

import images.APImage;
import images.Pixel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SignatureImagePrep {
    
    public static APImage prepImage (String path) throws IOException {
        try {
            APImage img = new APImage(path);
            
//            //debug
//            System.out.println("5");
            
            //scale image
            resizeSignature(path, 256);

            //update image
            img = new APImage(path);
            
            //convert to black and white
            for (Pixel p : img) {
                int avg = (p.getRed() + p.getGreen() + p.getBlue())/3;
                p.setRed(avg);
                p.setGreen(avg);
                p.setBlue(avg);
            }
            
//            //debug
//            System.out.println("7");
            
            return img;
        } catch (IOException ex) {
            System.out.println("Error resizing the image.");
            ex.printStackTrace();
            return new APImage("placeholder.jpg");
        }
    }
    
    //image resizing method
    public static void resizeSignature(String imagePath, int size) throws IOException {
        try {
//            //debug
//            System.out.println("6");
            
            // reads input image
            File inputFile = new File(imagePath);
            BufferedImage inputImage = ImageIO.read(inputFile);

            // creates output image
            BufferedImage outputImage = new BufferedImage(size,
                    size, inputImage.getType());

            // scales the input image to the output image
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, size, size, null);
            g2d.dispose();

            // extracts extension of output file
            String formatName = imagePath.substring(imagePath
                    .lastIndexOf(".") + 1);

            // writes to output file
            ImageIO.write(outputImage, formatName, new File(imagePath));

        } catch (IOException ex) {
            System.out.println("Error resizing the image.");
            ex.printStackTrace();
        }
    }
}
