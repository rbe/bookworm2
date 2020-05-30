package com.beaglebuddy.mp3.sample_code;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import com.beaglebuddy.mp3.MP3;



/**
 * This code allows user's to benchmark the performance of the Beaglebuddy MP3 library.
 * It displays the performance of the library when reading mp3 files from a local drive and also from a URL.
 * The code for both tests is run in a single thread.  In the future, we hope to add code for running the same tests using a thread pool.
 * But for now, all benchmark tests are single threaded.
 * The results you get will depend on the speed of your computer, your hard drive, and your internet connection.
 */
public class Performance
{
   private static final DecimalFormat decimalFormat = new DecimalFormat("###0.00");
   private static       URL[]         mp3URLs       = new URL[46];

   static
   {
      try
      {
         mp3URLs[ 0] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/1.Toys%20In%20The%20Attic.mp3"                     );
         mp3URLs[ 1] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/2.Uncle%20Salty.mp3"                               );
         mp3URLs[ 2] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/3.Adam's%20Apple.mp3"                              );
         mp3URLs[ 3] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/4.Walk%20This%20Way.mp3"                           );
         mp3URLs[ 4] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/5.Big%20Ten%20Inch%20Record.mp3"                   );
         mp3URLs[ 5] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/6.Sweet%20Emotion.mp3"                             );
         mp3URLs[ 6] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/7.No%20More%20No%20More.mp3"                       );
         mp3URLs[ 7] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/8.Round%20And%20Round.mp3"                         );
         mp3URLs[ 8] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/9.You%20See%20Me%20Crying.mp3"                     );
         mp3URLs[ 9] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/01.Hells%20Bells.mp3"                              );
         mp3URLs[10] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/01.prelude.mp3"                                    );
         mp3URLs[11] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/02.reach%20out%20for%20the%20light.mp3"            );
         mp3URLs[12] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/03.serpents%20in%20paradise.mp3"                   );
         mp3URLs[13] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/04.malleus%20maleficarum.mp3"                      );
         mp3URLs[14] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/05.breaking%20away.mp3"                            );
         mp3URLs[15] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/06.farewell.mp3"                                   );
         mp3URLs[16] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/07.the%20story%20of%20rome.mp3"                    );
         mp3URLs[17] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/08.in%20nomine%20patris.mp3"                       );
         mp3URLs[18] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/09.avantasia.mp3"                                  );
         mp3URLs[19] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/10.a%20new%20dimension.mp3"                        );
         mp3URLs[20] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/11.inside.mp3"                                     );
         mp3URLs[21] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/12.sign%20%20of%20the%20cross.mp3"                 );
         mp3URLs[22] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/13.the%20tower.mp3"                                );
         mp3URLs[23] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/14.the%20final%20sacrifice.mp3"                    );
         mp3URLs[24] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/01.No%20Fun%20After%20Midnight.mp3"                );
         mp3URLs[25] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/02.Lay%20Down.mp3"                                 );
         mp3URLs[26] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/03.Walking%20The%20Distance.mp3"                   );
         mp3URLs[27] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/04.Glastonbury%20Massacre.mp3"                     );
         mp3URLs[28] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/05.Danger%20U.X.B.mp3"                             );
         mp3URLs[29] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/06.The%20Witch%20Of%20Berkeley.mp3"                );
         mp3URLs[30] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/07.Last%20Stand.mp3"                               );
         mp3URLs[31] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/08.The%20Romp.mp3"                                 );
         mp3URLs[32] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/09.The%20King%20Is%20Dead.mp3"                     );
         mp3URLs[33] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/10.I'm%20The%20One%20Who%20Loves%20You.mp3"        );
         mp3URLs[34] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/11.Ringside%20Seat.mp3"                            );
         mp3URLs[35] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/01.Rock%20Brigade.mp3"                             );
         mp3URLs[36] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/02.Hello%20America.mp3"                            );
         mp3URLs[37] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/03.Sorrow%20Is%20A%20Woman.mp3"                    );
         mp3URLs[38] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/04.It%20could%20Be%20You.mp3"                      );
         mp3URLs[39] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/05.Saltellite.mp3"                                 );
         mp3URLs[40] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/06.When%20The%20Walls%20Came%20Tumbling%20Down.mp3");
         mp3URLs[41] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/07.Wasted.mp3"                                     );
         mp3URLs[42] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/08.Rocks%20Off.mp3"                                );
         mp3URLs[43] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/09.It%20Don't%20Matter.mp3"                        );
         mp3URLs[44] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/10.Answer%20To%20The%20Master.mp3"                 );
         mp3URLs[45] = new URL("http://www.beaglebuddy.com/content/downloads/mp3/11.Overture.mp3"                                   );
      }
      catch (MalformedURLException ex)
      {
         ex.printStackTrace();
         System.exit(0);
      }
   }

   /**
    * runs the benchmark tests and displays the results on the console.
    * @param args  command line argument specifying the root directory containing your .mp3 files.
    *              If a root directory is not specified on the command line, then c:\mp3 will be searched.
    */
   public static void main(String[] args)
   {
      // if you get a connection time out exception when running the URL benchmarks, you most likely need to specify the proxy server's host and port
      // System.setProperty("http.proxyHost", "myproxy.mycompany.com");
      // System.setProperty("http.proxyPort", "9000");

      System.out.println(pad("Test", 20) + pad("Threading", 20) + pad("Num .mp3\'s", 20) + pad("Time (s)", 20) + pad("Rate (mp3's/s)", 20));
      System.out.println(benchmarkLocalFiles(new File(args.length != 0 ? args[0] : "c:\\mp3")));
      System.out.println(benchmarkURLFiles());
   }

   /**
    * @return the results of reading in .mp3 files from a local file system.
    */
   private static String benchmarkLocalFiles(File rootDirectory)
   {
      String result = "";

      try
      {
         long start      = System.currentTimeMillis();
         int  numMp3s    = countMp3Files(rootDirectory);       // read in all the mp3 files found under this directory
         long end        = System.currentTimeMillis();
         double duration = (end - start) / 1000.0;             // duration in s
         double rate     = numMp3s / duration;
         result = pad("Local Files", 20) + pad("Single", 20) + pad(String.valueOf(numMp3s), 20) + pad(String.valueOf(duration), 20) + pad(decimalFormat.format(rate), 20);
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
         result = ex.getMessage();
      }
      return result;
   }

   /**
    * @return the results of reading in .mp3 files from a URL using a single thread.
    */
   private static String benchmarkURLFiles()
   {
      String result = "";

      try
      {
         long start      = System.currentTimeMillis();
         int  numMp3s    = mp3URLs.length;

         for(URL mp3URL : mp3URLs)                                            // read in .mp3's from a URL
            new MP3(mp3URL);

         long end        = System.currentTimeMillis();
         double duration = (end - start) / 1000.0;                            // duration in s
         double rate     = numMp3s / duration;
         result = pad("URL Files", 20) + pad("Single", 20) + pad(String.valueOf(numMp3s), 20) + pad(String.valueOf(duration), 20) + pad(decimalFormat.format(rate), 20);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         result = ex.getMessage();
      }
      return result;
   }
   /**
    * read in all the mp3 files found under the specified directory.
    * @return the number of mp3 files found under the specified directory.
    */
   private static int countMp3Files(File directory) throws IOException
   {
      int numMp3s = 0;

      for(File file : directory.listFiles())
      {
         if (file.isDirectory())
         {
            numMp3s += countMp3Files(file);
         }
         else if (file.getName().endsWith(".mp3"))
         {
            new MP3(file);
            numMp3s++;
         }
      }
      return numMp3s;
   }

   /**
    * @return text right padded to length with spaces.
    */
   private static String pad(String text, int length)
   {
      StringBuffer buffer = new StringBuffer(text);

      int numSpaces = length - text.length();

      for(int i=0; i<numSpaces; ++i)
         buffer.append(" ");

      return buffer.toString();
   }
}
