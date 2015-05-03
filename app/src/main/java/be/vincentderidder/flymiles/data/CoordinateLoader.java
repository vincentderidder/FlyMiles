package be.vincentderidder.flymiles.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import be.vincentderidder.flymiles.location;

/**
 * Created by Vincent on 3/05/2015.
 */
public class CoordinateLoader extends AsyncTaskLoader<location> {
    private static final String LOG_TAG = "Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBwLWUVxPSo7zi7X0TUZ4B280tAGnbJGho";
    private static location l;
    public CoordinateLoader(Context context, location loc){
        super(context);
        this.l = loc;
    }

    @Override
    protected void onStartLoading() {
        if (l == null) {
            forceLoad();
        }
    }

    @Override
    public location loadInBackground() {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&reference=" + URLEncoder.encode(l.reference, "utf8"));
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        }catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return l;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return l;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return l;
    }
}
