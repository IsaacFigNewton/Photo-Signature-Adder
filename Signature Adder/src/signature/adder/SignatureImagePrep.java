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
            public static final double SIGNATURE_HEIGHT_SCALE = 7/64.0;
            
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
            int startY = maxY - (int)(signatureScale*SIGNATURE_HEIGHT_SCALE) - padding;
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
            BufferedImage outputImage;
            
            //for determining the interpolation hint:
            //if upscaling (new area > old area)
            if (size*(size*SIGNATURE_HEIGHT_SCALE) > inputImage.getWidth()*inputImage.getHeight())
            {
                //if the downscaling factor (sqrt(old area/new))is more than 2, use the nearest neighbor method
                //higher performance, but lower-quality, "blocky" results
                if (Math.sqrt((size*(size*SIGNATURE_HEIGHT_SCALE))/(inputImage.getWidth()*inputImage.getHeight())) > 2) {
                    // creates output image
                    //debugging
                    System.out.println("Upscaling signature with VALUE_INTERPOLATION_NEAREST_NEIGHBOR method");
                    outputImage = getScaledInstance(inputImage, size, (int) (size*SIGNATURE_HEIGHT_SCALE),RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR , true);//new BufferedImage(size, size/8, inputImage.getType());
                }
                //if the downscaling factor (sqrt(old area/new))is more than 1.5, use the bilinear method
                //a bit slower, but provides higher-quality, "filtered" results.
                else if (Math.sqrt((size*(size*SIGNATURE_HEIGHT_SCALE))/(inputImage.getWidth()*inputImage.getHeight())) > 1.5)
                {
                    // creates output image
                    //debugging
                    System.out.println("Upscaling signature with VALUE_INTERPOLATION_BILINEAR method");
                    outputImage = getScaledInstance(inputImage, size, (int) (size*SIGNATURE_HEIGHT_SCALE),RenderingHints.VALUE_INTERPOLATION_BILINEAR , true);//new BufferedImage(size, size/8, inputImage.getType());
                }
                //if the downscaling factor (sqrt(old area/new))is more than 1, use the bicubic method
                //similar to BILINEAR except that it uses more samples when filtering
                else
                {
                    // creates output image
                    //debugging
                    System.out.println("Upscaling signature with VALUE_INTERPOLATION_BICUBIC method");
                    outputImage = getScaledInstance(inputImage, size, (int) (size*SIGNATURE_HEIGHT_SCALE),RenderingHints.VALUE_INTERPOLATION_BICUBIC , true);//new BufferedImage(size, size/8, inputImage.getType());
                }
            }
            //if downscaling
            else 
            {
                //if downscaling past 0.8 original size use the older technique for smoother downscaling 
                if (Math.sqrt((size*(size*SIGNATURE_HEIGHT_SCALE))/(inputImage.getWidth()*inputImage.getHeight())) < 0.8) {
                    // creates output image
                    outputImage = olderMethod(inputImage, size, (int) (size*SIGNATURE_HEIGHT_SCALE), Image.SCALE_AREA_AVERAGING);
                }
                //if the downscaling factor (sqrt(old area/new))is less than 0.9, use the bilinear method
                //similar to BILINEAR except that it uses more samples when filtering
                else if (Math.sqrt((size*(size*SIGNATURE_HEIGHT_SCALE))/(inputImage.getWidth()*inputImage.getHeight())) < 0.9)
                {
                    // creates output image
                    //debugging
                    System.out.println(Math.sqrt((size*(size*SIGNATURE_HEIGHT_SCALE))/(inputImage.getWidth()*inputImage.getHeight())));
                    System.out.println("Downscaling signature with VALUE_INTERPOLATION_BICUBIC method");
                    outputImage = getScaledInstance(inputImage, size, (int) (size*SIGNATURE_HEIGHT_SCALE), RenderingHints.VALUE_INTERPOLATION_BICUBIC, false);//new BufferedImage(size, size/8, inputImage.getType());
                }
                //if the downscaling factor (sqrt(old area/new))is less than 1, use the bilinear method
                //a bit slower, but provides higher-quality, "filtered" results.
                else //(Math.sqrt((size*(size*SIGNATURE_HEIGHT_SCALE))/(inputImage.getWidth()*inputImage.getHeight())) < 1)
                {
                    // creates output image
                    //debugging
                    System.out.println("Downscaling signature with VALUE_INTERPOLATION_BILINEAR method");
                    outputImage = getScaledInstance(inputImage, size, (int) (size*SIGNATURE_HEIGHT_SCALE), RenderingHints.VALUE_INTERPOLATION_BILINEAR, false);//new BufferedImage(size, size/8, inputImage.getType());
                }
//                
            }
            
            // scales the input image to the output image
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, size, (int)(size*SIGNATURE_HEIGHT_SCALE), null);
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
    
    //see https://web.archive.org/web/20080516181120/http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
    /**
    * Convenience method that returns a scaled instance of the
    * provided {@code BufferedImage}.
    *
    * @param img the original image to be scaled
    * @param targetWidth the desired width of the scaled instance,
    *    in pixels
    * @param targetHeight the desired height of the scaled instance,
    *    in pixels
    * @param hint one of the rendering hints that corresponds to
    *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
    *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
    *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
    *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
    * @param higherQuality if true, this method will use a multi-step
    *    scaling technique that provides higher quality than the usual
    *    one-step technique (only useful in down scaling cases, where
    *    {@code targetWidth} or {@code targetHeight} is
    *    smaller than the original dimensions, and generally only when
    *    the {@code BILINEAR} hint is specified)
    * @return a scaled version of the original {@code BufferedImage}
    */
    public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
    
    public static BufferedImage olderMethod (BufferedImage inputImage, int newWidth, int newHeight, int hint) {
        return toBufferedImage(inputImage.getScaledInstance(newWidth, newHeight, hint));
    }
    
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
    
    public static int incrementRGB (int RGB, int increment) {
        int [] incrementBinary = incrementBase10ToBinary(increment);
        int incrementRGB = RGBBinaryToBase10(incrementBinary);
        int incrementedRGB = RGB + incrementRGB;
//        System.out.println("There's probably a logical error somewhere in one of the RGB code incrementation methods");
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
        //initializing the returned array
        int [] aPLUSb;
        //if a is the longer binary value
        if (a.length >= b.length)
            aPLUSb = new int [a.length];
        //else if b is longer
        else
            aPLUSb = new int [b.length];
        
        boolean carryTheOne = false;

        for (int i = 0; i < aPLUSb.length; i++) {
            if (carryTheOne) {

            }
        }

        return aPLUSb; 
    }
}
