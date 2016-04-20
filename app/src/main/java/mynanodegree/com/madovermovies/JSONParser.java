package mynanodegree.com.madovermovies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Arjun on 4/19/16 for MadOverMovies.
 */
public class JSONParser {

    public static String fetchJSON(String url) {
        try {
            InputStream is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;

            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            is.close();

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
