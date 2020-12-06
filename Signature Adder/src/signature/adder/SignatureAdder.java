/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signature.adder;

/**
 *
 * @author IsaacMRudnick
 */
/*

Example 5.1

You can reset maximum heap size when running the program if you run out of memory
for large images, as follows:

run the "configure java" application

go to the "Java" tab

click the "view..." button

enter the following text into one or more of the "parameters" section of the items that pop up:

java -Xmx<new size>m <program file name>

where <new size> is an integer.  Example:

java -Xmx128m TestDraw

allows up to 128 megabytes of heap space.
*/

import signature.adder.SignatureImagePrep;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class SignatureAdder{
   
   public static int photoIndex = 0;
   public static int numPhotos = 1;
   //                                                                           Main Class \/
   public static void main(String[]args) throws IOException{
        try {
//        //debug
//        System.out.println("1");
            while (numPhotos > 0 && photoIndex < numPhotos) {
                processImage();
            }
        } catch (IOException ex) {
            System.out.println("Couldn't process one of the images");
        }
        
}
   
   public static void processImage () throws IOException {
        try {
            BufferedImage theImage;
            //debug
            System.out.println();
            System.out.println("Photo number " + (photoIndex + 1));
            
            //instantiate variables
            
            File inputFolder;

            String [] photos;

            String fileName;

            //create a list of potential files
            inputFolder = new File("Input\\");
            photos = inputFolder.list();
            numPhotos = photos.length;

//            //debug
//            System.out.print("2");

            //select the photo and create index or next photo
            fileName = photos[photoIndex];
            photoIndex++;
           
//           //debug
//           System.out.print(", 3");

            String filePath = "Input\\" + fileName;
            theImage = ImageIO.read(new File(filePath));
            
//           //debug
//           System.out.print(", 4");
           
           //add the signature to the photo
           theImage = SignatureImagePrep.prepImage(filePath);
           
           
        } catch (IOException ex) {
            System.out.println("Something went wrong while getting and processing the training image.");
            ex.printStackTrace();
        }
   }
    
   
}
/*
Sources:
    Debugging                                       Mr. Crow my Cybersecurity teacher
    Signature free from .jpeg artifacts:            https://web.archive.org/web/20080516181120/http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
                                                    https://docs.oracle.com/javase/7/docs/api/java/awt/Image.html
    Converting Image objects to BufferedImage ones: https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
    
*/

//Extra thanks to Mr. Crow, my Cybersecurity teacher, for helping me debug this, I never would've finished it without his help
