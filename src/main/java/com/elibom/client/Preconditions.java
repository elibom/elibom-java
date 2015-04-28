package com.elibom.client;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class provides helper methods to check preconditions throwing an exception when not met.
 *
 * @author German Escobar
 */
public class Preconditions {

    /**
     * Checks that an object is not null.
     *
     * @param object the object to be tested
     * @param message the message for the exception in case the object is null.
     *
     * @throws IllegalArgumentException if the object is null.
     */
    public static void notNull(Object object, String message) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException("A precondition failed: " + message);
        }
    }

    /**
     * Checks that a string is not null or empty.
     *
     * @param value the string to be tested.
     * @param message the message for the exception in case the string is empty.
     *
     * @throws IllegalArgumentException if the string is empty.
     */
    public static void notEmpty(String value, String message) throws IllegalArgumentException {
        if (value == null || "".equals(value.trim())) {
            throw new IllegalArgumentException("A precondition failed: " + message);
        }
    }

    /**
     * Checks that the length of a string is not greater than <code>maxLength</code>.
     *
     * @param value the string to be tested
     * @param maxLength the max lenght of the string.
     * @param message the message for the exception in case the length of the string is greater than <code>maxLength</code>.
     *
     * @throws IllegalArgumentException if the length of the string is greater that <code>maxLength</code>.
     */
    public static void maxLength(String value, int maxLength, String message) throws IllegalArgumentException {
        if (value == null) {
            return;
        }

        if (value.length() > maxLength) {
            throw new IllegalArgumentException("A precondition failed: " + message);
        }
    }

    /**
     * Checks that a string is a valid URL.
     *
     * @param value the string to be tested.
     * @param message the message for the exception in case the string is not a valid URL.
     *
     * @throws IllegalArgumentException if the string is not a valid URL.
     */
    public static void isUrl(String value, String message) throws IllegalArgumentException {
        try {
            new URL(value);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("A precondition failed: " + message);
        }
    }
    
    public static void isInteger(int value, String message) throws IllegalArgumentException {
    	if(value < 1) {
    		throw new IllegalArgumentException("A precondition failed: " + message);
    	}
    }
}
