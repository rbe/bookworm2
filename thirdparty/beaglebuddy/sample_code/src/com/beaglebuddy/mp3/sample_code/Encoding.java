package com.beaglebuddy.mp3.sample_code;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.beaglebuddy.mp3.MP3;



/**
 * <p>
 * This checks all your .mp3 files and displays information about how they were encoded.
 * This information is obtained from reading in the mpeg audio frames (as opposed to the ID3v2.x frames found in the ID3v2.x tag).
 * </p>
 */
public class Encoding
{
   /**
    * shows how to use the Beaglebuddy MP3 library to obtain encoding information about your .mp3 files.
    * this program will traverse your file system from the specified root directory and display the encoding information about the .mp3 files it finds.
    * you can specify a root directory on the command line, or you can edit this program to hard code your own directory.
    * @param args  command line argument specifying the root directory containing your .mp3 files.
    *              If a root directory is not specified on the command line, then c:\mp3 will be searched.
    */
   public static void main(String[] args)
   {
      System.out.println(pad("mp3 file", 100) + " - " + "Codec           " +  " - " + "Typ" + " - " + "Bit" + " - " + "Frequ" + " - " + "Channel Mode" + " - " + "Time" + " - " + "Pad" + " - " + "ID3v1" + " - " + "APE  " + " - " + "Lyrics3v2" + " - " + "MPEG Audio Errors");

      // start looking for mp3 files in the c:\mp3\ directory, unless the user specified a different one
      processMp3Files(new File(args.length != 0 ? args[0] : "c:\\mp3"));
   }

   /**
    * perform the following clean up activities on all .mp3 files in the specified directory: <br/>
    * 1. remove invalid information (frames)
    * 2. remove the obsolete ID3v1 tag if present
    * 3. set the "Artist" and "Contributing Artist" fields
    * 4. set the length of the song if it has not already been set
    * @param directory   root directory containing .mp3 files.
    */
   public static void processMp3Files(File directory)
   {
      for(File file : directory.listFiles())
      {
         // if this is a sub-directory, then go look for .mp3 files in it
         if (file.isDirectory())
         {
            processMp3Files(file);
         }
         else if (file.getName().endsWith(".mp3"))
         {
            MP3 mp3 = null;
            try
            {
               mp3 = new MP3(file);
               System.out.print(pad(mp3.getPath(), 100) + " - " + mp3.getCodec() +  " - " + mp3.getBitrateType()  + " - " + mp3.getBitrate() + " - " + mp3.getFrequency() + " - " +
                                pad(mp3.getChannelMode().toString(), 12) + " - " + formatTime(mp3.getAudioDuration()) + " - " + mp3.getID3v2xPadding() + " - " +
                                (mp3.hasID3v1Tag()     ? mp3.getID3v1Tag().getVersion()       : "     "    ) + " - " +
                                (mp3.hasAPETag()       ? mp3.getAPETag  ().getVersionString() : "     "    ) + " - " +
                                (mp3.hasLyrics3v2Tag() ? "Lyrics3v2"                          : "         ") + " - ");

               // see if the .mp3 file has valid MPEG audio frames
               List<String> errors = mp3.validateMPEGFrames();

               if (errors.size() != 0)
               {
                  for(String error : errors)
                     System.out.print(error);
               }
               System.out.println("");
            }
            catch (IOException ex)
            {
               // an error occurred reading the .mp3 file.
               // you may try to read it again to see if the error still occurs.
               System.err.println("Error occurred while reading the mp3 at " + file.getPath() + ".");
               ex.printStackTrace();
            }
         }
      }
   }

   /**
    * left justify a string by padding it with spaces on the right side.
    * @return the string right padded with spaces so that the string length is equal to that specified in the length parameter.
    * @param numSpaces total number of spaces the string should occupy.
    */
   private static String pad(String string, int length)
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append(string);
      int numSpaces = length - string.length();

      for(int i=0; i<numSpaces; ++i)
         buffer.append(" ");

      return buffer.toString();
   }

   /**
    * format a time duration given in seconds to a human readable format given by mm:ss.
    */
   private static String formatTime(int time)
   {
      return "" + (time / 60) + ":" + (time % 60 < 10 ? "0" : "") + (time % 60);
   }
}
