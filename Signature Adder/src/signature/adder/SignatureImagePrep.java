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
            public static String loadingBar;
            
    public static APImage prepImage (String path) throws IOException {
        try {
            APImage imgIn = new APImage(path);
            //make a BufferedImage object that is virtually the same as imgIn, except we can automatically save it more easily
            BufferedImage imgOut = ImageIO.read(new File(path));
            APImage signature = new APImage("Signature\\Signature.jpg");
            int maxX = imgIn.getWidth();
            signatureScale = maxX/1;
            int maxY = imgIn.getHeight();
            //amount of padding between the signature and sides of photos in pixels
            int padding = (maxX+maxY)/200;
            
            //debug
            System.out.print(", 5");
            
            //scale signature to 1/16th the photo's width
            resizeSignature(signatureScale);
            
            //"this is where a little studio engineering comes in handy, my hard-rockin' amigo!"
            //make a BufferedImage object that is virtually the same as the temporary signature, except we can get the RGB values from it
            BufferedImage tempSig = ImageIO.read(new File("Signature\\Temp Signature.jpg"));

            //Start of Loading Bar
            System.out.println("Loading...");
            System.out.printf("[");
            int i = 0;
            //read signature image's pixels to each photo's respective pixels in the photo's lower right-hand corner
            int x = 0;
            //the starting x coordinate of the "bounding box"
            int startX = maxX - signatureScale + padding;
            //the ending x coordinate of the "bounding box"
            int endX = maxX - padding;
            int y = 0;
            //the starting x coordinate of the "bounding box"
            int startY = maxY - signatureScale/sigYDiff + padding;
            //the ending y coordinate of the "bounding box"
            int endY = maxY - padding;
            for (Pixel p : imgIn) {
                //if the photo's pixel is within the bounds of a bounding box representing the size of the signature
                if ((x > startX && x < endX) && (y > startY && y < endY)) {
//                    System.out.println("Width: " + maxX);
//                    System.out.println("x: " + x);
//                    System.out.println("Height: " + maxY);
//                    System.out.println("y: " + y);
                    
                    if (i%50000 == 0)
                        System.out.print("|");
                    
                    //this is where we use the buffered images' data
                    //Get the color of the temporary signature's pixel at the location on the main photo minus the starting coordinates of the "bounding box"
                    Color tempSigPixelColor =new Color(tempSig.getRGB(x - startX, y - startY));
                    //Then set the respective pixel on the main photo to that color
                    imgOut.setRGB(tempSigPixelColor.getRed(), tempSigPixelColor.getGreen(), tempSigPixelColor.getBlue());
                }
                
                if (x >= maxX)
                    x = 0;
                else
                    x++;
                
                if (y >= maxY)
                    y = 0;
                else
                    y++;
                
                i++;
            }
            //end of Loading Bar
            System.out.println("]");
            
            //Logical error is in section above
            System.out.println("Logical malfunction SignatureImagePrep.java; will only draw pixels of signature as diagonal, 45-degree black line in upper-right hand corner");
            System.out.println("See line 95 for slightly more information.");
            //debug
            System.out.print("7");
            
            //save signed photo
            // extracts extension of input file
            String formatName = path.substring(path.lastIndexOf(".") + 1);
            //saves imgOut to Output folder
            ImageIO.write(imgOut, formatName, new File("Output\\" + path.substring(path.lastIndexOf("\\") + 1)));
            
            //debug
            System.out.println(", 8");
            
            return imgIn;
        } catch (IOException ex) {
            System.out.println("Error resizing the image.");
            ex.printStackTrace();
            return new APImage("placeholder.jpg");
        }
    }
    
    //image resizing method
    public static void resizeSignature(int size) throws IOException {
        try {
            //debug
            System.out.println(", 6");
            String signaturePath = "Signature\\Signature.jpg";
            
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
            ImageIO.write(outputImage, formatName, new File("Signature\\Temp Signature.jpg"));

        } catch (IOException ex) {
            System.out.println("Error resizing the image.");
            ex.printStackTrace();
        }
    }
}
