package com.beaglebuddy.mp3.sample_code;

import java.io.IOException;
import java.net.URL;

import com.beaglebuddy.mp3.MP3;
import com.beaglebuddy.id3.enums.PictureType;


/**
 * This code demonstrates how to open an .mp3 file via a URL.
 * <p>
 * When you load an .mp3 file from a URL, the Beaglebuddy MP3 library treats the .mp3 file as being read only.  For that reason, you can not change any of the information
 * in the .mp3 file, nor can you save it.  You may only read the values that are currently stored in the .mp3 file.
 * </p>
 * <p>
 * Please note that the URL must be correctly encoded.  That is, it must conform to <a href="http://www.ietf.org/rfc/rfc1738.txt">RFC 1738</a>.  A more user friendly explanation can be found at
 * the <a href="http://www.w3schools.com/tags/ref_urlencode.asp">HTML URL Encoding Reference</a>.  Also note that the java class <i>java.net.URLEncoder</a> does not work properly when passed
 * an entire URL.
 * </p>
 * <p>
 * Thus, the following URL will not work in java: http://www.beaglebuddy.com/content/downloads/mp3/01.Hells Bells.mp3<br/>
 * Instead, you must specify the correctly encoded URL to the MP3 constructor as: http://www.beaglebuddy.com/content/downloads/mp3/01.Hells%20Bells.mp3
 * </p>
 * Another issue when accessing an .mp3 file through a URL is the use of proxy servers.
 * If you run into the problem of being able to access an .mp3 file in your web browser, such as the following: <br/>
 * <a href="http://www.beaglebuddy.com/content/downloads/mp3/01.Hells%20Bells.mp3">http://www.beaglebuddy.com/content/downloads/mp3/01.Hells%20Bells.mp3</a><br/>
 * but using the exact same URL in your java program does not work (you get a connection time out error message), then perhaps you're internet connection is
 * utilizing a proxy server.  If so, you can solve this problem by specifying the proxy server's host and port as system properties in one of two ways:
 * <ol>
 *    <li>Specifying them as Defines on the command line to the java JRE</li>
 *    <li>Specifying them with java code in your application</li>
 * </ol>
 * <br/>
 * To specify them on the command line, do something like the following: <br/>
 * <code>
 * <pre class="beaglebuddy">
 * %java_jre_home%\java -Dhttp.proxyHost=usproxy.mycompany.com -Dhttp.proxyPort=9000 -classpath beaglebuddy_mp3.jar;. com.beaglebuddy.mp3.sample_code.BasicURL
 * </pre>
 * </code>
 * To specify them in your code, add the following two lines to your code<br/>
 * <code>
 * <pre class="beaglebuddy">
 * System.setProperty("http.proxyHost", "usproxy.mycompany.com");
 * System.setProperty("http.proxyPort", "9000");
 * </pre>
 * </code>
 * </p>
 */
public class BasicURL
{
   /**
    * shows how to use the Beaglebuddy MP3 library to open an .mp3 file via a URL.
    * @param args  command line arguments - none are needed for this example.
    */
   public static void main(String[] args)
   {
      try
      {
         // if you use a proxy server, then specify the server name and port
         // System.setProperty("http.proxyHost", "myproxy.mycompany.com");
         // System.setProperty("http.proxyPort", "2020");

         MP3 mp3 = new MP3(new URL("http://www.beaglebuddy.com/content/downloads/mp3/01.Hells%20Bells.mp3"));

         // if there was any invalid information (ie, ID3v2.x frames) in the .mp3 file,
         // then display the errors to the user
         if (mp3.hasErrors())
            mp3.displayErrors(System.out);      // display the errors that were found

         // print out all the internal information about the .mp3 file
         System.out.println(mp3);

         // get some specific information about the song
         System.out.println("audio duration: " + mp3.getAudioDuration()                  + "ms\n"      +
                            "audio size....: " + mp3.getAudioSize()                      + " bytes\n"  +
                            "lyrics by.....: " + mp3.getLyricsBy()                       + "\n"        +
                            "music by......: " + mp3.getMusicBy()                        + "\n"        +
                            "picture.......: " + mp3.getPicture(PictureType.FRONT_COVER) + "\n"        +
                            "publisher.....: " + mp3.getPublisher()                      + "\n"        +
                            "rating........: " + mp3.getRating()                         + "\n"        +
                            "title.........: " + mp3.getTitle()                          + "\n"        +
                            "band..........: " + mp3.getBand()                           + "\n"        +
                            "lyrics........: " + mp3.getLyrics()                         + "\n");
      }
      catch (IOException ex)
      {
         // an error occurred reading the .mp3 file.
         // you may try to read it again to see if the error still occurs.
         ex.printStackTrace();
      }
      catch (IllegalStateException ex)
      {
         // this mp3 file was loaded from a URL.
         // the Beaglebuddy MP3 library treats mp3 files loaded from a URL as read-only.
         // if someone tries to modify it, then an IllegalStateException is thrown
         ex.printStackTrace();
      }
   }
}
