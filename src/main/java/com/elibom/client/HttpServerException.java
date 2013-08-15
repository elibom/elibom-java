package com.elibom.client;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 *
 * @author German Escobar
 */
public class HttpServerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int statusCode;

    private JSONObject body;

    public HttpServerException(int statusCode) {
        this(statusCode, null);
    }

    public HttpServerException(int statusCode, JSONObject body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    @Override
    public String getMessage() {
        return "The server returned with status " + statusCode + " " + getStatusDescription(statusCode);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public JSONObject getBody() {
        return body;
    }

    public String getStatusDescription(int statusCode) {
        Map<Integer,String> status = new HashMap<Integer,String>();
        status.put(400, "Bad Request");
        status.put(401, "Unauthorized");
        status.put(404, "Not Found");
        status.put(409, "Conflict");
        status.put(500, "Internal Server Error");

        String ret = status.get(statusCode);
        if (ret == null) {
            return "";
        }

        return ret;
    }

}
