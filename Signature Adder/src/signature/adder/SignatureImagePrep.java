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
            //signature's y scale with respect to the x scale
            public static final double SIGNATURE_HEIGHT_SCALE = 64/5;
            
            public static APImage signature;
            
    public static APImage prepImage (String path) throws IOException {
        try {
            //set up the images
            APImage imgIn = new APImage(path);
            //make a BufferedImage object that is virtually the same as imgIn, except we can automatically save it more easily
            BufferedImage imgOut = ImageIO.read(new File(path));
            signature = new APImage("Signature\\Signature.jpg");
            BufferedImage signature2 = ImageIO.read(new File("Signature\\Signature.jpg"));
            //get color of the original signature image's reference pixels to determine the approximate text color
            int textColor1 = signature2.getRGB(0, 0);
            int textColor2 = signature2.getRGB(1, 0);
            int textColor3 = signature2.getRGB(2, 0);
            
            int maxX = imgIn.getWidth();
            int signatureScale = maxX/4;
            int maxY = imgIn.getHeight();
            //amount of padding between the signature and sides of photos in pixels
            int padding = (maxX+maxY)/400;
            
            //debug
            System.out.print(", 5");
            
            //scale signature to 1/16th the photo's width
            resizeSignature(signatureScale);
            
            
            //"this is where a little studio engineering comes in handy, my hard-rockin' amigo!"
            //make a BufferedImage object that is virtually the same as the temporary signature, except we can get the RGB values from it
            BufferedImage tempSig = ImageIO.read(new File("Signature\\Temp Signature.jpg"));
            signatureScale = tempSig.getWidth();

            //Start of Loading Bar
            System.out.println("Processing...");
            System.out.printf("[");
            int i = 0;
            //the starting x coordinate of the "bounding box"
            int startX = maxX - signatureScale - padding;
            //read signature image's pixels to each photo's respective pixels in the photo's lower right-hand corner
            int x = startX;
            //the ending x coordinate of the "bounding box"
            int endX = maxX - padding;
            //the starting x coordinate of the "bounding box"
            int startY = maxY - (int)(signatureScale/SIGNATURE_HEIGHT_SCALE) - padding;
            
            int y = startY;
            //the ending y coordinate of the "bounding box"
            int endY = maxY - padding;
            while (y < endY) {
                //if the photo's pixel is within the bounds of a bounding box representing the size of the signature
                if ((x > startX && x < endX) && (y > startY && y < endY)) {
                    //loading bar
                    if (i%50000 == 0)
                        System.out.print("|");
                    
                    //this is where we use the buffered images' data
                    //Get the color of the temporary signature's pixel at the location on the main photo minus the starting coordinates of the "bounding box"
                    //Then set the respective pixel on the main photo to that color
                    //don't include the whitespace in the signature writing (use the upper leftmost pixel as a reference pixel for the background)
                    if ((tempSig.getRGB(x - startX, y - startY) == textColor1) || (tempSig.getRGB(x - startX, y - startY) == textColor2) || (tempSig.getRGB(x - startX, y - startY) == textColor3))
                    imgOut.setRGB(x, y, tempSig.getRGB(x - startX, y - startY));
                }
                
                //move 
                if (x >= maxX){
                    x = 0;
                    y++;
                } else {
                    x++;
                }
                i++;
            }
            //end of Loading Bar
            System.out.println("]");
            
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
            g2d.drawImage(inputImage, 0, 0, size, (int)(size/SIGNATURE_HEIGHT_SCALE), null);
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
