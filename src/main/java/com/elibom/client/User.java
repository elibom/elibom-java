package com.elibom.client;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author German Escobar
 */
public class User {

    private JSONObject json;

    private long id;

    private String name;

    private String email;

    private String status;

    public User(JSONObject json) throws JSONException {
        this.json = json;
        this.id = json.getLong("id");
        this.name = json.getString("name");
        this.email = json.getString("email");
        this.status = json.getString("status");
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return json.toString();
    }

}
