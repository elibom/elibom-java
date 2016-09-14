package com.elibom.client;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Germán Escobar
 */
public class Account {

    private JSONObject json;

    private String name;

    private BigDecimal credits;

    private long ownerId;

    public Account(JSONObject json) throws JSONException {
        this.json = json;
        this.name = json.getString("name");
        if (json.has("credits")) {
            this.credits = json.getBigDecimal("credits");
        }
        this.ownerId = json.getJSONObject("owner").getLong("id");
    }

    public String getName() {
        return name;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public long getOwnerId() {
        return ownerId;
    }

    @Override
    public String toString() {
        return json.toString();
    }

}
