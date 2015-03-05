package cashpa.bettermtd;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by Hyunbin on 3/2/15.
 */

public class ServiceHandler {

    static String response = null;

    public ServiceHandler() {
        // This is a constructor
    }

    // Make a GET request with only URL and no parameters
    public String makeServiceCall(String url) {
        return this.makeServiceCall(url, null);
    }

    // Make a GET request with URL and parameters
    public String makeServiceCall(String url, List<NameValuePair> params) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpEntity httpEntity = null;
        HttpResponse httpResponse = null;

        // Parse the url to include parameters passed to service call
        if(params!=null) {
            String paramString = URLEncodedUtils.format(params, "utf-8");
            url += "?" + paramString;
        }

        // Makes the GET request
        HttpGet httpGet = new HttpGet(url);
        try {
            httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Returns JSON object as a string to be decoded in main app
        return response;
    }
}