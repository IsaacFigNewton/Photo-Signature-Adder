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
//import Main.SignatureImagePrep;

public class SignatureAdder{
   
   public static APImage theImage = new APImage("placeholder.png");
   
   //                                                                           Main Class \/
   public static void main(String[]args) throws IOException{
        Scanner reader = new Scanner(System.in);
        
//        //debug
//        System.out.println("1");
        

   }
   
   
    
   public static void getImage () throws IOException {
        try {
//            //debug
//            System.out.println("3");
            
            //instantiate variables
            int categoryIndex;
            String category;
            File categoryFolder;
            File fileFolder;

            String [] categoriesInFolder;
            String [] filesInCategory;

            ArrayList<String> files = new ArrayList<String> ();
            int fileIndex = 0;
            String fileName = "";
            String pathway = "";

//           //debug
//           System.out.println("3.1");
            
           //get a random category
           categoryIndex = (int)(Math.random()*257);
           
//           //debug
//           System.out.println("3.2");
           
           //create a list of potential categories
           categoryFolder = new File("ObjectCategories");
           categoriesInFolder = categoryFolder.list();
           //choose category
           category = categoriesInFolder[categoryIndex];
           
//           //debug
//           System.out.println("3.3");
//           System.out.println(category);
//           if (categoryFolder.isDirectory())
//           System.out.println(categoriesInFolder);
           
           //create a list of potential files
           fileFolder = new File("ObjectCategories\\" + category);
           filesInCategory = fileFolder.list();
           
//           //debug
//           System.out.println("3.4");
//           if (fileFolder.isDirectory())
//           System.out.println(filesInCategory);

           //select a random file
           fileIndex = (int) (Math.random()*filesInCategory.length);
           fileName = filesInCategory[fileIndex];
           
//           //debug
//           System.out.println("3.5");

           String filePath = "ObjectCategories" + "\\" + category + "\\" + fileName;
    //       theImage = new APImage(new String("ObjectCategories" + "\\" + category + "\\" + fileName));

//           //debug
//           System.out.println("4");
//           if (categoryFolder.isDirectory())
//           System.out.println(filesInCategory);
           
           //prepare the image for reading and set theImage to it
           theImage = SignatureImagePrep.prepImage(filePath);
           
           //visual aid for trainer
           theImage.draw();
           
        } catch (IOException ex) {
            System.out.println("Something went wrong while getting and processing the training image.");
            ex.printStackTrace();
        }
   }
     
   public static void readImage(){
      Scanner reader = new Scanner(System.in);
      
      // Read all pixels into input array
      for (int y = 0; y < theImage.getHeight(); y++) {
        for (int x = 0; x < theImage.getHeight(); x++){
            // Obtain info for a pixel and add it to pixel color arrays
            // at a position corresponding to the y and x values respectively
            Pixel pixel = theImage.getPixel(x, y);
        }
      }
   }
   
}


