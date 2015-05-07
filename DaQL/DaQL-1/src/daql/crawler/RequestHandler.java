/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql.crawler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * The following class has the role of handling the request / response action
 * from the crawlers.
 * 
 * @author arrascue
 */
public class RequestHandler {
    /**
     * This variable represents the parameter USER AGENT of the request.
     */
    private final String userAgent = "Mozilla/5.0";

    /**
     * This variable stores a JsonElement object representing the JSON object of
     * the last query.
     */
    private JsonElement jElement;

    /**
     * This variable stores a JsonObject object representing an object type in
     * JSON from the last query.
     */
    private JsonObject jObject;

    /**
     * This variable represents a JsonParser.
     */
    private JsonParser jParser;

    /**
     * This variables stores the query.
     */
    private String query;

    /**
     * This variables stores the result of the response to the query.
     */
    private StringBuffer result;

    /**
     * This variables stores the current HTTP client.
     */
    private HttpClient client;

    /**
     * This variables stores the current HTTP request.
     */
    private HttpGet request;

    /**
     * This variables stores the current HTTP response.
     */
    private HttpResponse response;

    /**
     * Get status code of last response.
     */
    private int lastStatusCode;

    /**
     * Default Constructor.
     */
    public RequestHandler() {
        jParser = new JsonParser();
    }

    /**
     * Constructor.
     */
    public RequestHandler(String query) {
        jParser = new JsonParser();
        this.query = query;
    }

    /**
     * Parses a JSON given as string.
     * 
     * @param jsonLine
     * @return
     */
    public void parse(String jsonLine) {
        jElement = jParser.parse(jsonLine);
        jObject = jElement.getAsJsonObject();
    }

    /**
     * Method returns a JSON object, the response of the sent request.
     */
    public JsonObject sendRequest() {
        result = new StringBuffer();
        client = new DefaultHttpClient();
        request = new HttpGet(query);
        // add request header
        request.addHeader("User-Agent", userAgent);
        request.addHeader("Accept-Charset", "utf-8");
        request.addHeader("Content-Type", "application/json; charset=utf-8");

        try {
            response = client.execute(request);
            lastStatusCode = response.getStatusLine().getStatusCode();

            System.out.println("Sending 'GET' request to URL : " + query);
            System.out.println("Response Code : " + lastStatusCode + "\n");

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                            response.getEntity().getContent(), "UTF-8"));

            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Now we have the JSON as a string, we can parse this.
        parse(result.toString());

        return jObject;
    }

    /**
     * @return the status code of the last response.
     */
    public int getLastStatusCode() {
        return lastStatusCode;
    }

    /**
     * Getter of query.
     * 
     * @return
     */
    public String getQuery() {
        return query;
    }

    /**
     * Setter of query.
     * 
     * @param query
     */
    public void setQuery(String query) {
        this.query = query;
    }
}
