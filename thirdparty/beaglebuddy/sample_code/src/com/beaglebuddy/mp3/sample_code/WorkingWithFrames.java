package com.beaglebuddy.mp3.sample_code;

import java.io.IOException;

import com.beaglebuddy.mp3.MP3;
import com.beaglebuddy.id3.enums.ID3TagVersion;
import com.beaglebuddy.id3.enums.v23.Encoding;
import com.beaglebuddy.id3.enums.v23.FrameType;
import com.beaglebuddy.id3.v23.ID3v23Frame;
import com.beaglebuddy.id3.v24.ID3v24Frame;
import com.beaglebuddy.id3.v23.ID3v23Tag;
import com.beaglebuddy.id3.v24.ID3v24Tag;
import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyTextInformation;
import com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodyTextInformation;
import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyRelativeVolumeAdjustment;

/**
 * Since the MP3 class only provides access methods for the most commonly used .mp3 information, if you wish to access some of the less common information stored in .mp3 files,
 * you will need to do so using the ID3 v2.x tag directly, as shown below.
 */
public class WorkingWithFrames
{
   /**
    * shows how to directly access the underlying frames found in an ID3v2.x tag inside an .mp3 file.
    * @param args  command line arguments - none are needed for this example.
    */
   public static void main(String[] args)
   {
      try
      {
         MP3 mp3 = new MP3("c:/music/ac dc/hells bells.mp3");

         // if there was any invalid information (ie, frames) in the .mp3 file,
         // then display the errors to the user
         if (mp3.hasErrors())
         {
            mp3.displayErrors(System.out);      // display the errors that were found
            mp3.save();                         // discard the invalid information (frames) and
         }                                      // save only the valid frames back to the .mp3 file

         // see what version of the ID3 tag the mp3 file has
         if (mp3.getVersion() == ID3TagVersion.ID3V2_3)
         {
            // get the ID3 v2.3 tag so we can work directly with it
            ID3v23Tag id3v23Tag = mp3.getID3v23Tag();

            // remove any existing date frame
            id3v23Tag.removeFrame(FrameType.DATE);
            // set the date the song was recorded as July 25th
            ID3v23FrameBodyTextInformation frameBody1 = new ID3v23FrameBodyTextInformation(FrameType.DATE, Encoding.ISO_8859_1, "0725");
            ID3v23Frame                    textFrame  = new ID3v23Frame(FrameType.DATE, frameBody1);
            id3v23Tag.addFrame(textFrame);

            // remove any existing relative volume frame
            id3v23Tag.removeFrame(FrameType.RELATIVE_VOLUME_ADJUSTMENT);
            // set the relative volume adjustments and peak volumes for all 6 channels
            ID3v23FrameBodyRelativeVolumeAdjustment frameBody2 = new ID3v23FrameBodyRelativeVolumeAdjustment(50, 50, 50, 50, 50, 50, 500, 500, 500, 500, 500, 500);
            ID3v23Frame                             volFrame   = new ID3v23Frame(FrameType.RELATIVE_VOLUME_ADJUSTMENT, frameBody2);
            id3v23Tag.addFrame(volFrame);
         }
         else if (mp3.getVersion() == ID3TagVersion.ID3V2_4)
         {
            // get the ID3 v2.4 tag so we can work directly with it
            ID3v24Tag id3v24Tag = mp3.getID3v24Tag();

            com.beaglebuddy.id3.enums.v24.FrameType recordingDate = com.beaglebuddy.id3.enums.v24.FrameType.RECORDING_TIME;
            // remove any existing date frame
            id3v24Tag.removeFrame(recordingDate);
            // set the date the song was recorded as July 25th
            ID3v24FrameBodyTextInformation frameBody1 = new ID3v24FrameBodyTextInformation(recordingDate, com.beaglebuddy.id3.enums.v24.Encoding.ISO_8859_1, "0725");
            ID3v24Frame                    textFrame  = new ID3v24Frame(recordingDate, frameBody1);
            id3v24Tag.addFrame(textFrame);
         }

         mp3.save();
         System.out.println(mp3);
      }
      catch (IOException ex)
      {
         System.out.println("An error occurred while reading/saving the mp3 file.");
         ex.printStackTrace();
      }
   }
}
