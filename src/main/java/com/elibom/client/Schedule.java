package com.elibom.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author German Escobar
 */
public class Schedule {

    private JSONObject json;

    private long id;

    private long userId;

    private Date scheduledAt;

    private Date createdAt;

    private String status = "scheduled";

    private boolean isFile;

    private String fileName;

    private boolean fileHasText;

    private String text;

    private String destinations;

    public Schedule(JSONObject json) throws JSONException, ParseException {
        this.json = json;
        this.id = json.getLong("id");
        if (json.has("user")) {
            this.userId = json.getJSONObject("user").getLong("id");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.scheduledAt = sdf.parse(json.getString("scheduledTime"));
        this.createdAt = sdf.parse(json.getString("creationTime"));

        this.isFile = json.getBoolean("isFile");
        if (isFile) {
            this.fileName = json.getString("fileName");
            this.fileHasText = json.getBoolean("fileHasText");
            if (!fileHasText) {
                this.text = json.getString("text");
            }
        } else {
            this.destinations = json.getString("destinations");
            this.text = json.getString("text");
        }
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public Date getScheduledAt() {
        return scheduledAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public boolean isFile() {
        return isFile;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isFileHasText() {
        return fileHasText;
    }

    public String getText() {
        return text;
    }

    public String getDestinations() {
        return destinations;
    }

    @Override
    public String toString() {
        return json.toString();
    }

}
