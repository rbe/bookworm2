package com.beaglebuddy.mp3.sample_code;

import java.io.IOException;

import com.beaglebuddy.mp3.MP3;
import com.beaglebuddy.id3.pojo.SynchronizedLyric;


/**
 * This code demonstrates how to add synchronized lyrics, i.e. lyrics in time with the music, to create Karaoke songs.
 * Although you can add the synchronized lyrics to your .mp3 files, it is the responsibility of the .mp3 player
 * to display the lyrics while it plays the .mp3 file.  Thus, it depends on which .mp3 player software you are
 * using to play the .mp3 file, as to whether or not the synchronized lyrics are displayed.
 */
public class Karaoke
{
   /**
     * shows how to use the Beaglebuddy MP3 library to add synchronized (i.e. Karaoke) lyrics to an .mp3 file.
     * @param args  command line arguments - none are needed for this example.
     */
   public static void main(String[] args)
   {
      try
      {
         // add Bon Jovi's "Livin on a Prayer" lyrics to the .mp3 so that they are displayed like a karaoke song by the mp3 player software
         MP3 mp3 = new MP3("c:\\music\\bon jovi\\livin on a prayer.mp3");

         // if there was any invalid information (ie, frames) in the .mp3 file, then display the errors to the user
         if (mp3.hasErrors())
         {
            mp3.displayErrors(System.out);      // display the errors that were found
            mp3.save();                         // discard the invalid information (ID3v2.x frames) and
         }                                      // save only the valid frames back to the .mp3 file

         // break the lyrics into syllables and specify when (in ms) in the song each syllable occurs
         SynchronizedLyric[] synchronizedLyrics = {new SynchronizedLyric("Tom-"   , 20350),
                                                   new SynchronizedLyric("my"     , 20400),
                                                   new SynchronizedLyric("used"   , 20625),
                                                   new SynchronizedLyric("to"     , 20700),
                                                   new SynchronizedLyric("work"   , 20800),
                                                   new SynchronizedLyric("on"     , 20900),
                                                   new SynchronizedLyric("the"    , 21000),
                                                   new SynchronizedLyric("docks"  , 21100),
                                                   new SynchronizedLyric("Un-"    , 22800),
                                                   new SynchronizedLyric("ions"   , 22900),
                                                   new SynchronizedLyric("been"   , 23000),
                                                   new SynchronizedLyric("on"     , 23100),
                                                   new SynchronizedLyric("strike" , 23200),
                                                   new SynchronizedLyric("He's"   , 23800),
                                                   new SynchronizedLyric("down"   , 23900),
                                                   new SynchronizedLyric("on"     , 24000),
                                                   new SynchronizedLyric("his"    , 24100),
                                                   new SynchronizedLyric("luck"   , 24200),
                                                   new SynchronizedLyric("it's"   , 25000),
                                                   new SynchronizedLyric("tough"  , 25300),
                                                   new SynchronizedLyric("so"     , 26900),
                                                   new SynchronizedLyric("tough"  , 27200)};

         mp3.setSynchronizedLyrics(synchronizedLyrics);

         // save the ID3v2.x tag to the .mp3 file
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
