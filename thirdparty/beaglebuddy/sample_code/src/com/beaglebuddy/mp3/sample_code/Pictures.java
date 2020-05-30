package com.beaglebuddy.mp3.sample_code;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.beaglebuddy.mp3.MP3;
import com.beaglebuddy.id3.enums.PictureType;
import com.beaglebuddy.id3.pojo.AttachedPicture;


/**
 * This code demonstrates how to find out what images, if any, are stored inside your .mp3 files.
 * Also take a look at the javadocs for the MP3 class for an example of how to add your own pictures,
 * such as the front/back cover of the cd, or the band's logo, etc., to an .mp3 file.
 */
public class Pictures
{
   /**
    * shows how to use the Beaglebuddy MP3 library to list the images stored inside an .mp3 file.
    * @param args  command line arguments.
    */
   public static void main(String[] args)
   {
      try
      {
         // find out what pictures are stored inside your .mp3 file
         MP3 mp3 = new MP3("c:\\music\\bon jovi\\livin on a prayer.mp3");

         // if there was any invalid information (ie, ID3v2.x frames) in the .mp3 file,
         // then display the errors to the user
         if (mp3.hasErrors())
         {
            mp3.displayErrors(System.out);      // display the errors that were found
            mp3.save();                         // discard the invalid information (ID3v2.x frames) and
         }                                      // save only the valid frames back to the .mp3 file

         // print out each picture's type, picture's mime type, optional description of the picture, and the number of bytes in the image
         List<AttachedPicture> attachedPictures = mp3.getPictures();
         for(AttachedPicture picture : attachedPictures)
            System.out.println(picture);

         // store a picture of the cd's front cover in the .mp3 file
         // get the cd's front cover from a URL
         // note: remember, if you use a proxy server, specify the host and port as shown in the BasicURL.java example
         AttachedPicture picture = new AttachedPicture(PictureType.FRONT_COVER, "image/jpg", "front cover of CD",
                                   new URL("http://www.beaglebuddy.com/content/downloads/mp3/bon%20jovi.slippery%20when%20wet.cd%20front%20cover.jpg"));
         mp3.setPicture(picture);

         // store a picture of the cd's back cover in the .mp3 file
         // get the cd's back cover from a local file
         mp3.setPicture(PictureType.BACK_COVER, "c:/images/bon jovi.slippery when wet.cd back cover.jpg");

         // save the pictures we added to the .mp3 file
         mp3.save();
      }
      catch (IOException ex)
      {
         // an error occurred reading/saving the .mp3 file.
         // you may try to read it again to see if the error still occurs.
         ex.printStackTrace();
      }
   }
}
