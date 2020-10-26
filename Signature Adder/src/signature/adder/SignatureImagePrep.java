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

import java.awt.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SignatureImagePrep {
            //scales signature scaling based on area (for aesthetic reasons), which is why it takes the sqrt of whatever you enter
            public static final double SIGNATURE_SCALE_SCALE = Math.sqrt(0.5);
            //signature's y scale with respect to the main signature scale (not the one above)
            public static final double SIGNATURE_HEIGHT_SCALE = 64/5;
            
            //black RGB code
            public static final int BLACK = -16777216;
            //white RGB code
            public static final int WHITE = -1;
                        
    public static BufferedImage prepImage (String path) throws IOException {
        try {
            //set up the images
            BufferedImage imgIn = ImageIO.read(new File(path));
                      
            //make a BufferedImage object that is virtually the same as imgIn, except we can automatically save it more easily
            BufferedImage imgOut = ImageIO.read(new File(path));
            BufferedImage signature = ImageIO.read(new File("Signature\\Signature.jpg"));
            BufferedImage signature2 = ImageIO.read(new File("Signature\\Inverted Signature.jpg"));
            
            int maxX = imgIn.getWidth();
            int maxY = imgIn.getHeight();
            int signatureScale = (int)(SIGNATURE_SCALE_SCALE *(maxX+maxY))/8;
            //amount of padding between the signature and sides of photos in pixels
            int padding = (int)(SIGNATURE_SCALE_SCALE * (maxX+maxY))/400;
            int signatureOffset = (3*padding)/8;
            
//            //debug
//            System.out.print(", 5");
            
            //scale signature to 1/16th the photo's width
            resizeSignature(signatureScale, "Signature\\Signature.jpg", "Signature\\Temp Signature.jpg");
            resizeSignature(signatureScale, "Signature\\Inverted Signature.jpg", "Signature\\Inverted Temp Signature.jpg");
            
            
            //"this is where a little studio engineering comes in handy, my hard-rockin' amigo!"
            //make a BufferedImage object that is virtually the same as the temporary signature, except we can get the RGB values from it
            BufferedImage tempSig = ImageIO.read(new File("Signature\\Temp Signature.jpg"));
            BufferedImage invTempSig = ImageIO.read(new File("Signature\\Inverted Temp Signature.jpg"));
            signatureScale = tempSig.getWidth();

            //Start of processing indicator
            System.out.println("Processing...");
            
            int i = 0;
            
            //the starting x coordinate of the "bounding box"
            int startX = maxX - signatureScale - padding;
            //read signature image's pixels to each photo's respective pixels in the photo's lower right-hand corner
            int x = startX;
            //the ending x coordinate of the "bounding box"
            int endX = maxX - padding;
            
            //the starting x coordinate of the "bounding box"
            int startY = maxY - (int)(signatureScale/SIGNATURE_HEIGHT_SCALE) - padding;
            //read signature image's pixels to each photo's respective pixels in the photo's lower right-hand corner            
            int y = startY;
            //the ending y coordinate of the "bounding box"
            int endY = maxY - padding;
            
            //255 = 1111 1111
            //255 255 255 = 1111 1111 1111 1111 1111 1111 = 16777215
            //There must be an extra bit for sign, therefore to increase brightness by 1 level, you'd do
            //001 001 001 = 0000 0001 0000 0001 0000
            //I figured it out using methods but forgot to note it here, what I wrote did help me though, so I'm leaving it here
            int colorInterval = 255;
            while (y < endY) {
                //if the photo's pixel is within the bounds of a bounding box representing the size of the signature
                if ((x > startX && x < endX) && (y > startY && y < endY)) {
                    //this is where we use the buffered images' data
                    //Get the color of the temporary signature's pixel at the location on the main photo minus the starting coordinates of the "bounding box"
                    //Then set the respective pixel on the main photo to that color
                    //don't include the whitespace in the signature writing (use the upper leftmost pixel as a reference pixel for the background)
                    //include all pixels within a certain BW color range
                    for (int j = 0; j <= colorInterval; j++) {
                        if (tempSig.getRGB(x - startX, y - startY) == incrementRGB(BLACK, j)) {
                            imgOut.setRGB(x, y, BLACK); //tempSig.getRGB(x - startX, y - startY)
                        }
                        //do the same thing for the normal signature with the inverted one, but with an offset so that they're both discernible independently and together
                        if (invTempSig.getRGB(x - startX, y - startY) == decrementRGB(WHITE, j)) {
                            if (signatureOffset <= padding && signatureOffset >= 1) {
                                imgOut.setRGB(x + signatureOffset, y + signatureOffset, WHITE); //invTempSig.getRGB(x - startX, y - startY)
                            } else {
                                imgOut.setRGB(x + padding, y + padding, WHITE); //invTempSig.getRGB(x - startX, y - startY)
                            }
                        }
                    }
                }
                
                //move on to next pixel
                if (x >= maxX){
                    x = 0;
                    y++;
                } else {
                    x++;
                }
                i++;
            }
            
//            //debug
//            System.out.print("7");
            
            //save signed photo
            // extracts extension of input file
            String formatName = path.substring(path.lastIndexOf(".") + 1);
            //saves imgOut to Output folder
            ImageIO.write(imgOut, formatName, new File("Output\\" + path.substring(path.lastIndexOf("\\") + 1)));
            
//            //debug
//            System.out.println(", 8");
            
            return imgIn;
        } catch (IOException ex) {
            System.out.println("Error resizing the image.");
            ex.printStackTrace();
            return new BufferedImage(100, 100, 1);
        }
    }
    
    //image resizing method
    public static void resizeSignature(int size, String inPath, String outPath) throws IOException {
        try {
//            //debug
//            System.out.println(", 6");
            
            // reads input image
            File inputFile = new File(inPath);
            BufferedImage inputImage = ImageIO.read(inputFile);

            // creates output image
            BufferedImage outputImage = new BufferedImage(size,
                    size/8, inputImage.getType());

            // scales the input image to the output image
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, size, (int)(size/SIGNATURE_HEIGHT_SCALE), null);
            g2d.dispose();

            // extracts extension of output file
            String formatName = inPath.substring(inPath.lastIndexOf(".") + 1);

            // writes to output file (temporary, resized signature)
            ImageIO.write(outputImage, formatName, new File(outPath));

        } catch (IOException ex) {
            System.out.println("Error resizing the image.");
            ex.printStackTrace();
        }
    }
    
    public static int incrementRGB (int RGB, int increment) {
        int [] incrementBinary = incrementBase10ToBinary(increment);
        int incrementRGB = RGBBinaryToBase10(incrementBinary);
        int incrementedRGB = RGB + incrementRGB;
        System.out.println("There's probably a logical error somewhere in one of the RGB code incrementation methods");
        return incrementedRGB;
    }
    
    public static int decrementRGB (int RGB, int decrement) {
        int [] decrementBinary = incrementBase10ToBinary(decrement);
        int decrementRGB = RGBBinaryToBase10(decrementBinary);
        int decrementedRGB = RGB + decrementRGB;
        return decrementedRGB;
    }
    
    public static int RGBBinaryToBase10 (int [] base2) {
        int [] base2Leftover = base2;
        int base10 = 0;
        int base2Count = 0;
        for (int i : base2Leftover)
            base2Count += i;
        while (base2Count > 0) {
            int indexOfFirst1 = 0;
            for (int j = 0; base2Leftover[j] == 0; j++)
                indexOfFirst1++;
            base10 += (int) Math.pow(2, indexOfFirst1);
            base2Leftover[indexOfFirst1] = 0;
            base2Count = 0;
            for (int i : base2Leftover)
                base2Count += i;
        }
        return base10;
    }
    
    public static int [] RGBBase10ToBinary (int base10) {
        int base10Leftover = Math.abs(base10);
        int [] base2 = new int [] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        while (base10Leftover > 0) {
            int startingSpot = (int)(Math.log(Math.abs(base10))/Math.log(2));
            base2[startingSpot] = 1;
            base10Leftover -= (int) Math.pow(2, startingSpot);
        }
        return base2;
    }
    
    public static int [] incrementBase10ToBinary (int base10) {
        int base10Leftover = Math.abs(base10);
        int [] base2 = new int [] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        while (base10Leftover > 0) {
            int startingSpot = (int)(Math.log(Math.abs(base10))/Math.log(2));
            base2[startingSpot] = 1;
            base2[startingSpot+8] = 1;
            base2[startingSpot+16] = 1;
            base10Leftover -= (int) Math.pow(2, startingSpot);
        }
        return base2;
    }
    
    public static int [] addBinaries (int [] a, int [] b) {
        
        return a;
    }
}
