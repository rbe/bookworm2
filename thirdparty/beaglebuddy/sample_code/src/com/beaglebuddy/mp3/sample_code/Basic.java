package com.beaglebuddy.mp3.sample_code;

import java.io.IOException;
import java.util.List;

import com.beaglebuddy.mp3.MP3;
import com.beaglebuddy.id3.enums.PictureType;



/**
 * This code demonstrates how to open an .mp3 file from a local file system and how to display the information found in the .mp3 file's ID32.x tag.
 */
public class Basic
{
   /**
    * shows how to use the Beaglebuddy MP3 library to open an .mp3 file on the local file system.
    * @param args  command line arguments - none are needed for this example.
    */
   public static void main(String[] args)
   {
      try
      {
         MP3 mp3 = new MP3("c:/mp3/ac dc/hells bells.mp3");

         // if there was any invalid information (ie, ID3v2.x frames) in the .mp3 file,
         // then display the errors to the user
         if (mp3.hasErrors())
         {                                      // display the errors that were found
            List<String> errors = mp3.getErrors();
            for(String error : errors)
               System.out.println(error);
            mp3.save();                         // discard the invalid information (ID3v2.x frames) and
         }                                      // save only the valid frames back to the .mp3 file

         // see if the .mp3 file has valid MPEG audio frames
         List<String> errors = mp3.validateMPEGFrames();

         if (errors.size() != 0)
         {
            for(String error : errors)
               System.out.println(error);
         }

         // print out all the internal information about the .mp3 file
         System.out.println(mp3);

         // print out some information about the song
         System.out.println("codec..............: " + mp3.getCodec()                          + "\n"         +
                            "bit rate...........: " + mp3.getBitrate()                        + " kbits/s\n" +
                            "bit rate type......: " + mp3.getBitrateType()                    + "\n"         +
                            "frequency..........: " + mp3.getFrequency()                      + " hz\n"      +
                            "audio duration.....: " + mp3.getAudioDuration()                  + " s\n"       +
                            "audio size.........: " + mp3.getAudioSize()                      + " bytes\n"   +
                            "album..............: " + mp3.getAlbum()                          + "\n"         +
                            "artist.............: " + mp3.getBand()                           + "\n"         +
                            "contributing artist: " + mp3.getLeadPerformer()                  + "\n"         +
                            "lyrics by..........: " + mp3.getLyricsBy()                       + "\n"         +
                            "music by...........: " + mp3.getMusicBy()                        + "\n"         +
                            "picture............: " + mp3.getPicture(PictureType.FRONT_COVER) + "\n"         +
                            "publisher..........: " + mp3.getPublisher()                      + "\n"         +
                            "rating.............: " + mp3.getRating()                         + "\n"         +
                            "title..............: " + mp3.getTitle()                          + "\n"         +
                            "track #............: " + mp3.getTrack()                          + "\n"         +
                            "year recorded......: " + mp3.getYear()                           + "\n"         +
                            "lyrics.............: " + mp3.getLyrics()                         + "\n");

         // store some information about the song in the .mp3 file
         String lyrics = "I’m rolling thunder pouring rain"                             + "\n" +
                         "I’m coming on like a hurricane"                               + "\n" +
                         "my lightning's flashing across the sky"                       + "\n" +
                         "you're only young but you're gonna die"                       + "\n" +
                         "I won't take no prisoners won't spare no lives"               + "\n" +
                         "nobody's putting up a fight"                                  + "\n" +
                         "I got my bell I’m gonna take you to hell"                     + "\n" +
                         "I’m gonna get ya satan get ya"                                + "\n" +
                         ""                                                             + "\n" +
                         "hells bells, hells bells"                                     + "\n" +
                         "hells bells, you got me ringing"                              + "\n" +
                         "hells bells, my temperature's high"                           + "\n" +
                         "hells bells"                                                  + "\n" +
                         ""                                                             + "\n" +
                         "I’ll give you black sensations up and down your spine"        + "\n" +
                         "if you're into evil you're a friend of mine"                  + "\n" +
                         "see the white light flashing as I split the night"            + "\n" +
                         "cos if good's on the left then I’m sticking to the right"     + "\n" +
                         "I won't take no prisoners won't spare no lives"               + "\n" +
                         "nobody's puttin' up a fight"                                  + "\n" +
                         "I got my bell I’m gonna take you to hell"                     + "\n" +
                         "I’m gonna get ya satan get ya"                                + "\n" +
                         ""                                                             + "\n" +
                         "hells bells, hells bells"                                     + "\n" +
                         "hells bells, you got me ringing"                              + "\n" +
                         "hells bells, my temperature's high"                           + "\n" +
                         "hells bells"                                                  + "\n" +
                         ""                                                             + "\n" +
                         "hells bells, satan’ coming to you"                            + "\n" +
                         "hells bells, he's ringing them now"                           + "\n" +
                         ""                                                             + "\n" +
                         "those hells bells, my temperature's high"                     + "\n" +
                         "hells bells, across the sky"                                  + "\n" +
                         "hells bells, they're taking you down"                         + "\n" +
                         "hells bells, they’re dragging you down"                       + "\n" +
                         "hells bells, gonna split the night"                           + "\n" +
                         "hells bells, there's no way to fight"                         + "\n" +
                         ""                                                             + "\n" +
                         "hells bells";

         mp3.setBand("AC DC");
         mp3.setAlbum("Back In Black");
         mp3.setTitle("Hells Bells");
         mp3.setTrack(1);
         mp3.setYear(1980);
         mp3.setLyrics(lyrics);
//       mp3.setPicture(PictureType.FRONT_COVER, new File("C:/images/ac_dc.back_in_black.jpg"));
         mp3.setYear(1980);
         mp3.setAudioDuration(312);              // if we know how long the song is, we can set it explicitly:    312 seconds == 5 minutes and 12 seconds

         if (mp3.getAudioDuration() == 0)       // otherwise, if the length of the song hasn't been specified,
            mp3.setAudioDuration();             // then calculate it from the mpeg audio frames

         // save the new information to the .mp3 file
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
