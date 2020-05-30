package com.beaglebuddy.mp3.sample_code;

import java.io.IOException;

import com.beaglebuddy.mp3.MP3;
import com.beaglebuddy.id3.enums.ID3TagVersion;
import com.beaglebuddy.id3.enums.v23.FrameType;
import com.beaglebuddy.id3.pojo.v23.Level;
import com.beaglebuddy.id3.v23.ID3v23Frame;
import com.beaglebuddy.id3.v23.ID3v23Tag;
import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyEqualization;


/**
 * Since the MP3 class only provides access methods for the most commonly used .mp3 information, it does not provide any methods for setting the eq.
 * Therefore, if you want to set the eq for a song, you will need to do so using the ID3v2.x tag directly, as shown below.
 */
public class Equalization
{
   /**
    * shows how to use the Beaglebuddy MP3 library to specify the equalizer settings for an .mp3 file.
    * This assumes that the mp3 file contains an ID3v2.3 tag.
    * @param args  command line arguments.
    */
   public static void main(String[] args)
   {
      try
      {
         // set the eq curve on a song
         MP3 mp3 = new MP3("c:\\music\\bon jovi\\livin on a prayer.mp3");

         // if there was any invalid information (ie, frames) in the .mp3 file, then display the errors to the user
         if (mp3.hasErrors())
         {
            mp3.displayErrors(System.out);      // display the errors that were found
            mp3.save();                         // discard the invalid information (frames) and
         }                                      // save only the valid frames back to the .mp3 file
                                                //
         // since equalization is not one of the more popular frames, it is not supported by the MP3 class.
         // therefore, we need to work directly with the ID3v2.3 tag
         if (mp3.getVersion() == ID3TagVersion.ID3V2_3)
         {
            // create a 32 band eq
            Level[] levels = {new Level(Level.Direction.INCREMENT,    10, 5),
                              new Level(Level.Direction.INCREMENT,    12, 5),
                              new Level(Level.Direction.INCREMENT,    16, 4),
                              new Level(Level.Direction.INCREMENT,    20, 4),
                              new Level(Level.Direction.INCREMENT,    32, 3),
                              new Level(Level.Direction.INCREMENT,    40, 2),
                              new Level(Level.Direction.INCREMENT,    50, 2),
                              new Level(Level.Direction.INCREMENT,    63, 2),
                              new Level(Level.Direction.INCREMENT,    80, 1),
                              new Level(Level.Direction.INCREMENT,   100, 0),
                              new Level(Level.Direction.INCREMENT,   125, 0),
                              new Level(Level.Direction.INCREMENT,   160, 1),
                              new Level(Level.Direction.DECREMENT,   200, 2),
                              new Level(Level.Direction.DECREMENT,   250, 3),
                              new Level(Level.Direction.DECREMENT,   315, 4),
                              new Level(Level.Direction.DECREMENT,   400, 5),
                              new Level(Level.Direction.DECREMENT,   500, 5),
                              new Level(Level.Direction.DECREMENT,   630, 5),
                              new Level(Level.Direction.DECREMENT,   800, 3),
                              new Level(Level.Direction.DECREMENT,  1000, 2),
                              new Level(Level.Direction.DECREMENT,  1250, 1),
                              new Level(Level.Direction.DECREMENT,  1600, 0),
                              new Level(Level.Direction.DECREMENT,  2000, 0),
                              new Level(Level.Direction.INCREMENT,  2500, 2),
                              new Level(Level.Direction.INCREMENT,  3150, 2),
                              new Level(Level.Direction.INCREMENT,  4000, 3),
                              new Level(Level.Direction.INCREMENT,  5000, 3),
                              new Level(Level.Direction.INCREMENT,  6300, 4),
                              new Level(Level.Direction.INCREMENT,  8000, 4),
                              new Level(Level.Direction.INCREMENT, 10000, 5),
                              new Level(Level.Direction.INCREMENT, 12500, 5),
                              new Level(Level.Direction.INCREMENT, 16000, 5)};

            // create the ID3v2.3 equalization frame
            ID3v23Frame frame = new ID3v23Frame(FrameType.EQUALIZATION, new ID3v23FrameBodyEqualization(5, levels));

            // get the ID3v2.3 tag so we can work directly with it
            ID3v23Tag iD3v23Tag = mp3.getID3v23Tag();

            // remove any existing equalization frame
            iD3v23Tag.removeFrame(FrameType.EQUALIZATION);
            // add the new equalization frame
            iD3v23Tag.addFrame(frame);
         }
         // save the ID3v2.3 tag to the .mp3 file
         mp3.save();
         System.out.println(mp3);
      }
      catch (IOException ex)
      {
         // an error occurred reading/saving the .mp3 file.
         // you may try to read it again to see if the error still occurs.
         ex.printStackTrace();
      }
   }
}
