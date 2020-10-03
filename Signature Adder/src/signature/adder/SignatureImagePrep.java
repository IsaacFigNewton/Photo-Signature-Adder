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
import java.awt.*;
import images.Pixel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SignatureImagePrep {
    //signature's scale
            public static int signatureScale;
            //signature's y scale with respect to the x scale
            public static int sigYDiff = 8;
    public static APImage prepImage (String path) throws IOException {
        try {
            APImage img = new APImage(path);
            APImage signature = new APImage();
            int maxX = img.getWidth();
            signatureScale = maxX/16;
            int maxY = img.getHeight();
            //amount of padding between the signature and sides of photos in pixels
            int padding = 10;
            
//            //debug
//            System.out.println("5");
            
            //scale signature to 1/16th the photo's width
            resizeSignature(signatureScale);
            
            //"this is where a little studio engineering comes in handy, my hard-rockin' amigo!"
            //make a BufferedImage object that is virtually the same as the temporary signature, except we can get the RGB values from it
            BufferedImage tempSig = ImageIO.read(new File("Signature\\Temp Signature.png"));

            //read signature image's pixels to each photo's respective pixels in the photo's lower right-hand corner
            int x = 0;
            int y = 0;
            for (Pixel p : img) {
                //if the photo's pixel is within the bounds of a bounding box representing the size of the signature
                if ((x > maxX - (signatureScale + padding) && (x < maxX - padding)) && (y > maxY - (signatureScale + padding) && (y < maxY - padding))) {
                    //this is where we use the buffered image data
                    p.setRed((new Color(tempSig.getRGB(x, y))).getRed());
                    p.setGreen((new Color(tempSig.getRGB(x, y))).getGreen());
                    p.setBlue((new Color(tempSig.getRGB(x, y))).getBlue());
                }
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
    public static void resizeSignature(int size) throws IOException {
        try {
//            //debug
//            System.out.println("6");
            String signaturePath = "Signature\\Signature.png";
            
            // reads input image
            File inputFile = new File(signaturePath);
            BufferedImage inputImage = ImageIO.read(inputFile);

            // creates output image
            BufferedImage outputImage = new BufferedImage(size,
                    size/8, inputImage.getType());

            // scales the input image to the output image
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, size, size/sigYDiff, null);
            g2d.dispose();

            // extracts extension of output file
            String formatName = signaturePath.substring(signaturePath.lastIndexOf(".") + 1);

            // writes to output file (temporary, resized signature)
            ImageIO.write(outputImage, formatName, new File("Signature\\Temp Signature.png"));

        } catch (IOException ex) {
            System.out.println("Error resizing the image.");
            ex.printStackTrace();
        }
    }
}
