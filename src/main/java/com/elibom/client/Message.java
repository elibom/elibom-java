package com.elibom.client;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author German Escobar
 */
public class Message {

    private JSONObject json;

    private long id;

    private long userId;

    private String to;

    private String operator;

    private String text;

    private String status;

    private String statusDetail;

    private BigDecimal credits;

    private String from;

    private Date createdAt;

    private Date sentAt;

    public Message(JSONObject json) throws JSONException, ParseException {
        this.json = json;

        this.id = json.getLong("id");
        if (json.has("user")) {
            this.userId = json.getJSONObject("user").getLong("id");
        }
        this.to = json.getString("to");
        this.operator = json.getString("operator");
        this.from = json.getString("from");
        this.text = json.getString("text");
        this.status = json.getString("status");
        this.statusDetail = json.getString("statusDetail");
        this.credits = json.getBigDecimal("credits");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createdAt = sdf.parse(json.getString("createdAt"));
        if (json.has("sentAt") && json.getString("sentAt") != null) {
            this.sentAt = sdf.parse(json.getString("sentAt"));
        }

    }

    public Message(long id, long userId, String to, String operator, String from, String text, String status, String statusDetail, BigDecimal credits, Date createdAt, Date sentAt) {
        this.id = id;
        this.userId = userId;
        this.to = to;
        this.operator = operator;
        this.from = from;
        this.text = text;
        this.status = status;
        this.statusDetail = statusDetail;
        this.credits = credits;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getTo() {
        return to;
    }

    public String getOperator() {
        return operator;
    }

    public String getText() {
        return text;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public String getFrom() {
        return from;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getSentAt() {
        return sentAt;
    }

    @Override
    public String toString() {
        return json.toString();
    }

}
