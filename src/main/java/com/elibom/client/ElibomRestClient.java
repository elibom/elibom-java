package com.elibom.client;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * The <a href="http://www.elibom.com/">elibom.com</a> REST API client.
 *
 * @author Germán Escobar
 */
public class ElibomRestClient {

    private static final String DEFAULT_HOST = "https://www.elibom.com";

    private final String LIB_VERSION = "java-0.2.6";

    private String host;

    private String username;

    private String apiPassword;

    /**
     * Initializes the client with the supplied <code>username</code> and <code>apiPassword</code>.
     *
     * @param username the email you use to access your account at <a href="http://www.elibom.com/">elibom.com</a>.
     * @param apiPassword your API password, which can be found in the settings section of your account.
     */
    public ElibomRestClient(String username, String apiPassword) {
        this(username, apiPassword, DEFAULT_HOST);
    }

    /**
     * Mostly used for testing. Initializes the client with the supplied <code>username</code>, <code>apiPassword</code> and
     * <code>host</code>. Mostly used for testing.
     *
     * @param username the email you use to access your account at <a href="http://www.elibom.com/">elibom.com</a>.
     * @param apiPassword your API password, which you can find in the settings of your account.
     * @param host the host to which the requests are going to be made.
     */
    public ElibomRestClient(String username, String apiPassword, String host) {
        Preconditions.notEmpty(username, "no username provided");
        Preconditions.notEmpty(apiPassword, "no apiPassword provided");
        Preconditions.notEmpty(host, "no host provided");
        Preconditions.isUrl(host, "host is not a valid URL");

        this.username = username;
        this.apiPassword = apiPassword;

        if (host.endsWith("/")) {
            host = host.substring(0, host.length() -1);
        }
        this.host = host;
    }

    /**
     * Sends an SMS message to one or more destinations with the specified <code>text</code>.
     *
     * @param to the destinations (separated by comma) to which you want to send the SMS message.
     * @param text the text of the SMS message, max 160 characters.
     *
     * @return a String that you can use to query the delivery (using the {@link #getDelivery(String)} method).
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public String sendMessage(String to, String text) throws HttpServerException, RuntimeException {
        Preconditions.notEmpty(to, "no destinations provided");
        Preconditions.notEmpty(text, "no text provided");
        Preconditions.maxLength(text, 160, "text has more than 160 characters");

        try {
            JSONObject json = new JSONObject().put("to", to).put("text", text);
            HttpURLConnection connection = post("/messages", json);
            return getJsonObject(connection.getInputStream()).getString("deliveryToken");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    
    /**
     * Sends an SMS message to one or more destinations with the specified <code>text</code> and a campaign id.
     *
     * @param to the destinations (separated by comma) to which you want to send the SMS message.
     * @param text the text of the SMS message, max 160 characters.
     * @param campaign a tag used to identify a group of messages.
     *
     * @return a String that you can use to query the delivery (using the {@link #getDelivery(String)} method).
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public String sendMessage(String to, String text, String campaign) throws HttpServerException, RuntimeException {
        Preconditions.notEmpty(to, "no destinations provided");
        Preconditions.notEmpty(text, "no text provided");
        Preconditions.notEmpty(campaign, "no campaign provided");
        Preconditions.maxLength(text, 160, "text has more than 160 characters");

        try {
            JSONObject json = new JSONObject().put("to", to).put("text", text).put("campaign", campaign);
            HttpURLConnection connection = post("/messages", json);
            return getJsonObject(connection.getInputStream()).getString("deliveryToken");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Schedules an SMS message for the specified <code>scheduleDate</code> to one or more destinations and with the specified
     * <code>text</code>.
     *
     * @param to the destinations (separated by comma) to which we are going to send the scheduled SMS message.
     * @param text the text of the SMS message, max 160 characters.
     * @param scheduleDate the date in which the message is going to be sent.
     *
     * @return the id of the scheduled message which you can query using the {@link #getSchedule(long)} method.
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public long scheduleMessage(String to, String text, Date scheduleDate) throws HttpServerException, RuntimeException {
        Preconditions.notEmpty(to, "no destinations provided");
        Preconditions.notEmpty(text, "no text provided");
        Preconditions.maxLength(text, 160, "text has more than 160 characters");
        Preconditions.notNull(scheduleDate, "no scheduleDate provided");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            JSONObject json = new JSONObject().put("to", to).put("text", text).put("scheduleDate", sdf.format(scheduleDate));
            HttpURLConnection connection = post("/messages", json);
            return getJsonObject(connection.getInputStream()).getLong("scheduleId");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Schedules an SMS message for the specified <code>scheduleDate</code> to one or more destinations and with the specified
     * <code>text</code> and a campaign id.
     *
     * @param to the destinations (separated by comma) to which we are going to send the scheduled SMS message.
     * @param text the text of the SMS message, max 160 characters.
     * @param scheduleDate the date in which the message is going to be sent.
     * @param campaign an tag used to identify a group of messages.
     *
     * @return the id of the scheduled message which you can query using the {@link #getSchedule(long)} method.
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public long scheduleMessage(String to, String text, Date scheduleDate,String campaign) throws HttpServerException, RuntimeException {
        Preconditions.notEmpty(to, "no destinations provided");
        Preconditions.notEmpty(text, "no text provided");
        Preconditions.maxLength(text, 160, "text has more than 160 characters");
        Preconditions.notEmpty(campaign, "no campaign provided");
        Preconditions.notNull(scheduleDate, "no scheduleDate provided");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            JSONObject json = new JSONObject().put("to", to).put("text", text).put("scheduleDate", sdf.format(scheduleDate)).put("campaign", campaign);
            HttpURLConnection connection = post("/messages", json);
            return getJsonObject(connection.getInputStream()).getLong("scheduleId");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }    
    
    /**
     * Query the last <code>numMessages</code> messages sent from an user
     * 
     * @param numMessages number of messages that will be consulted
     * 
     * @return a List of Message objects or an empty List if no messages is found
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public List<Message> getLastMessages(int numMessages) throws HttpServerException, RuntimeException {
    	Preconditions.isInteger(numMessages, "numMessages must be greater than zero");
    	
    	try {
    		HttpURLConnection connection = get("/messages?perPage="+numMessages+"&user="+this.username);
    		JSONObject json = getJsonObject(connection.getInputStream());List<Message> messages = new ArrayList<Message>();
    		JSONArray jm = json.getJSONArray("messages");
    		for (int i=0; i < jm.length(); i++) {
    			messages.add(new Message(jm.getJSONObject(i)));
    		}
    		
    		return messages;
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	} catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /**
     * Query the last <code>numMessages</code> messages sent from an user between two dates
     * 
     * @param numMessages number of messages that will be consulted
     * @param startDate the initial date from the report 
     * @param endDate the end date from the report
     * @return a List of Message objects or an empty List if no messages is found
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public List<Message> getLastMessages(int numMessages, Date startDate, Date endDate) throws HttpServerException, RuntimeException {
        Preconditions.isInteger(numMessages, "numMessages must be greater than zero");
        Preconditions.notNull(startDate, "no startDate provided");
        Preconditions.notNull(endDate, "no endDate provided");
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            HttpURLConnection connection = get("/messages?perPage="+numMessages+"&user="+this.username+"&startDate="+sdf.format(startDate)+"&endDate="+sdf.format(endDate));
            JSONObject json = getJsonObject(connection.getInputStream());List<Message> messages = new ArrayList<Message>();
            JSONArray jm = json.getJSONArray("messages");
            for (int i=0; i < jm.length(); i++) {
                messages.add(new Message(jm.getJSONObject(i)));
            }
            
            return messages;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Query the delivery with the specified <code>deliveryId</code>.
     *
     * @param deliveryId
     *
     * @return a Delivery object with the info of the delivery and its messages.
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public Delivery getDelivery(String deliveryId) throws HttpServerException, RuntimeException {
        Preconditions.notEmpty(deliveryId, "no deliveryId provided");

        try {
            HttpURLConnection connection = get("/messages/" + deliveryId);
            JSONObject json = getJsonObject(connection.getInputStream());
            return new Delivery(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Query the scheduled messages.
     *
     * @return a List of Schedule objects or an empty List if no schedule is found
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public List<Schedule> getScheduledMessages() throws HttpServerException, RuntimeException {
        try {
            HttpURLConnection connection = get("/schedules/scheduled");

            List<Schedule> schedules = new ArrayList<Schedule>();
            JSONArray json = getJsonArray(connection.getInputStream());
            for (int i=0; i < json.length(); i++) {
                schedules.add(new Schedule(json.getJSONObject(i)));
            }

            return schedules;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Query the scheduled message with the specified id.
     *
     * @param id the id of the scheduled message to query.
     *
     * @return a Schedule object.
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public Schedule getScheduledMessage(long id) throws HttpServerException, RuntimeException {
        try {
            HttpURLConnection connection = get("/schedules/" + id);
            return new Schedule(getJsonObject(connection.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cancels the scheduled message with the specified <code>id</code>.
     *
     * @param id the id of the schedule to be canceled.
     *
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public void unschedule(long id) throws HttpServerException, RuntimeException {
        try {
            delete("/schedules/" + id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Query your account users (those who have access to the account).
     *
     * @return a List of User objects.
     *
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public List<User> getUsers() throws HttpServerException, RuntimeException {
        try {
            HttpURLConnection connection = get("/users");

            List<User> users = new ArrayList<User>();
            JSONArray json = getJsonArray(connection.getInputStream());
            for (int i=0; i < json.length(); i++) {
                users.add(new User(json.getJSONObject(i)));
            }

            return users;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Query a single account user.
     *
     * @param id the id of the account user to query.
     *
     * @return a User object.
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public User getUser(long id) throws HttpServerException, RuntimeException {
        try {
            HttpURLConnection connection = get("/users/" + id);
            return new User(getJsonObject(connection.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Query your account info.
     *
     * @return an Account object.
     *
     * @throws HttpServerException if the server responds with a HTTP status code other than <code>200 OK</code>.
     * @throws RuntimeException wraps any other unexpected exception.
     */
    public Account getAccount() throws HttpServerException, RuntimeException {
        try {
            HttpURLConnection connection = get("/account");
            return new Account(getJsonObject(connection.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpURLConnection post(String resource, JSONObject json) throws JSONException, IOException {
        URL url = buildUrl(resource);

        OutputStreamWriter out = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", buildAuthorizationHeader());
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-API-Source", LIB_VERSION);

            connection.setDoOutput(true);
            out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            json.write(out);
            out.flush();

            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                throw new HttpServerException(statusCode, getJsonObject(connection.getErrorStream()));
            }

            return connection;
        } finally {
            closeResource(out);
        }
    }

    private HttpURLConnection get(String resource) throws IOException, JSONException {
        URL url = buildUrl(resource);

        OutputStreamWriter out = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", buildAuthorizationHeader());
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-API-Source", LIB_VERSION);

            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                throw new HttpServerException(statusCode, getJsonObject(connection.getErrorStream()));
            }

            return connection;
        } finally {
            closeResource(out);
        }
    }

    private HttpURLConnection delete(String resource) throws IOException, JSONException {
        URL url = buildUrl(resource);

        OutputStreamWriter out = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            connection.setRequestProperty("Authorization", buildAuthorizationHeader());
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-API-Source", LIB_VERSION);
            connection.connect();

            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                throw new HttpServerException(statusCode, getJsonObject(connection.getErrorStream()));
            }

            return connection;
        } finally {
            closeResource(out);
        }
    }

    private URL buildUrl(String resource) {
        Preconditions.notEmpty(resource, "no resource provided");
        if (!resource.startsWith("/")) {
            resource = "/" + resource;
        }

        try {
            return new URL(host + resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildAuthorizationHeader() {
        String credentials = username + ":" + apiPassword;
        return "Basic " + DatatypeConverter.printBase64Binary(credentials.getBytes());
    }

    private JSONObject getJsonObject(InputStream stream) throws JSONException, IOException {
        if (stream == null) {
            return null;
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String body = getBody(in);

            return new JSONObject(body);
        } finally {
            closeResource(stream);
        }
    }

    private JSONArray getJsonArray(InputStream stream) throws JSONException, IOException {
        if (stream == null) {
            return null;
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String body = getBody(in);

            return new JSONArray(body);
        } finally {
            closeResource(stream);
        }
    }

    private String getBody(BufferedReader in) throws IOException {
        String body = "";
        String line;
        while ((line = in.readLine()) != null) {
            body += line;
        }

        return body;
    }

    private void closeResource(Closeable closeable) {
        if (closeable != null) {
            try { closeable.close(); } catch (Exception e) {}
        }
    }

    
}
