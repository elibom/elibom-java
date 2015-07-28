package com.elibom.client;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author German Escobar
 */
public class Delivery {

    private JSONObject json;

    private String id;

    private String status;

    private int numSent;

    private int numFailed;

    private List<Message> messages;

    public Delivery(JSONObject json) throws JSONException, ParseException {
        this.json = json;

        this.id = json.getString("deliveryId");
        this.status = json.getString("status");
        this.numSent = json.getInt("numSent");
        this.numFailed = json.getInt("numFailed");

        this.messages = new ArrayList<Message>();
        JSONArray jm = json.getJSONArray("messages");
        for (int i=0; i < jm.length(); i++) {
            this.messages.add(new Message(jm.getJSONObject(i)));
        }
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public int getNumSent() {
        return numSent;
    }

    public int getNumFailed() {
        return numFailed;
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return json.toString();
    }

}
