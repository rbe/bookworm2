package com.beaglebuddy.mp3.sample_code;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.beaglebuddy.mp3.MP3;




/**
 * <p>
 * This class removes invalid information (frames) from your .mp3 files.
 * When the beaglebuddy MP3 library reads in an .mp3 file, it parses and validates all the information stored in the frames in the ID3v2.x tag.
 * If any invalid frames are found, they are removed, and your are left with only the valid ID3v2.x frames.
 * You can then save the mp3 file, and the invalid ID3v2.x frames will be discarded.
 * </p>
 * <p>
 * Examples of invalid ID3v2.x frames include frames with invalid ID3v2.x frame id, comment frames with an empty comment, Year released frames with no year specified,
 * album picture frames without a picture, etc.
 * </p>
 * <p>
 * The end result of running this class on your .mp3 files is that all of the invalid frames are removed and you have valid ID3v2.x tags with valid information in your .mp3 files.
 * </p>
 */
public class CleanMP3Files
{
   /**
    * shows how to use the Beaglebuddy MP3 library to make sure all of your .mp3 files only contain valid ID3v2.x information.
    * @param args  command line argument specifying the root directory containing your .mp3 files.
    *              If a root directory is not specified on the command line, then c:\mp3 will be searched.
    */
   public static void main(String[] args)
   {
      // start looking for mp3 files in the c:\mp3\ directory, unless the user specified a different one
      cleanMp3Songs(new File(args.length != 0 ? args[0] : "c:\\mp3"));
   }

   /**
    * perform the following clean up activities on all .mp3 files in the specified directory: <br/>
    * 1. remove invalid information (frames)
    * 2. remove the obsolete ID3v1 tag if present
    * 3. set the "Artist" and "Contributing Artist" fields
    * 4. set the length of the song if it has not already been set
    * @param directory   root directory containing .mp3 files.
    */
   public static void cleanMp3Songs(File directory)
   {
      for(File file : directory.listFiles())
      {
         // if this is a sub-directory, then go look for .mp3 files in it
         if (file.isDirectory())
         {
            cleanMp3Songs(file);
         }
         else if (file.getName().endsWith(".mp3"))
         {
            MP3 mp3 = null;
            try
            {
               mp3 = new MP3(file);
               System.out.print(pad(mp3.getPath(), 110) + " - ");

               // display any errors that were found in the ID3v2.x tag
               if (mp3.hasErrors())
               {
                  List<String> errors = mp3.getErrors();
                  for(String error : errors)
                     System.out.print(error);
               }

               if (mp3.getAudioDuration() == 0)       // if the length of the song hasn't been specified,
                  mp3.setAudioDuration();             // then calculate it from the mpeg audio data

               // some .mp3 files store the band name in the Lead Performer = TPE1 frame, which windows displays as the "Contributing Artist".
               // others store the band name in the Band = TPE2 frame, which windows displays as the "Artist".
               // if either of these fields are empty, then they will be set to the other's value.
               // for example, if the band field is empty, and the lead performer field is not, then the band field will be set to the value of the lead performer field
               if (mp3.getBand() == null && mp3.getLeadPerformer() != null)
                  mp3.setBand(mp3.getLeadPerformer());
               if (mp3.getBand() != null && mp3.getLeadPerformer() == null)
                  mp3.setLeadPerformer(mp3.getBand());

               // discard any invalid information (ID3v2.x frames) and save only the valid frames back to the ID3v2.x tag in the .mp3 file
               // along with any of the changes we just made (setting audio duration, band, and/or lead performer
               mp3.save();

               // the ID3v2.x tag has extra space reserved to make it easier for adding new information
               // make sure its not too big and just wasting space.  feel free to change this size (in bytes) to whatever you like.
               if (mp3.getID3v2xPadding() > 128)
               {
                  mp3.setID3v2xPadding(128);
                  mp3.save();
               }

               // remove the obsolete ID3v1 tag (if present) from the end of the .mp3 file
               if (mp3.hasID3v1Tag())
                  mp3.removeID3v1Tag();

               // remove the obsolete Lyrics3v2 tag (if present) from the end of the .mp3 file
               if (mp3.hasLyrics3v2Tag())
                  mp3.removeLyrics3v2Tag();

               // remove the obsolete APE tag (if present) from the end of the .mp3 file
               // if an APE tag is present at the beginning of the file, then just leave it
               if (mp3.hasAPETag() && mp3.getAPETag().getFilePosition() != 0)
                  mp3.removeAPETag();

               // remove the ID3v2.4 tag (if present) from the end of the .mp3 file
               if (mp3.hasID3v24TagAtEnd())
                  mp3.removeID3v24TagAtEnd();

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
               // an error occurred reading/saving the .mp3 file.
               // you may try to read it again to see if the error still occurs.
               System.err.println("Error occurred while reading/saving " + file.getPath() + ".");
               if (mp3 != null)
                   System.out.println(mp3);
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
}
