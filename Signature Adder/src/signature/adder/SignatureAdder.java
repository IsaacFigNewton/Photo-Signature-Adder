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

java -Xmx<new size>m <program file name>

where <new size> is an integer.  Example:

java -Xmx128m TestDraw

allows up to 128 megabytes of heap space.
*/

import signature.adder.SignatureImagePrep;
import images.APImage;
import images.Pixel;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
//import Main.SignatureImagePrep;

public class SignatureAdder{
   
   public static int photoIndex = 0;
   public static int numPhotos = 1;
   //                                                                           Main Class \/
   public static void main(String[]args) throws IOException{
        Scanner reader = new Scanner(System.in);
        //debug
        System.out.println("1");
        while (numPhotos > 0 && photoIndex < numPhotos) {
            //maybe the buffered images aren't getting reset each time and that's what's causing the memory overload?
//            resetImages();
            processImage();
        }
}
   
   public static void processImage () throws IOException {
        try {
            APImage theImage;
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

            //debug
            System.out.print("2");

            //select the photo and create index or next photo
            fileName = photos[photoIndex];
            photoIndex++;
           
           //debug
           System.out.print(", 3");

            String filePath = "Input\\" + fileName;
            theImage = new APImage(filePath);
            
           //debug
           System.out.print(", 4");
           
           //add the signature to the photo
           theImage = SignatureImagePrep.prepImage(filePath);
           
           
        } catch (IOException ex) {
            System.out.println("Something went wrong while getting and processing the training image.");
            ex.printStackTrace();
        }
   }
    
   
}


