package com.michael.geo;

import java.util.regex.Pattern;

/**
 * Created by Michael on 9/21/2015.
 * Represents complex object path divided by '.'
 */
public class ItemKey {
    private final String[] keys;
    
    public ItemKey(String key) {
        keys = key.split(Pattern.quote("."));
    }

    /**
     * get first part of path
     * in case of empty path returns empty string
     * @return string
     */
    public String first() {
        if (keys.length == 0)
            return "";
        else
            return keys[0];
    }

    /**
     * get last part of path
     * in case of empty path returns empty string 
     * @return string
     */
    public String last() {
        if (keys.length == 0)
            return "";
        else
            return keys[keys.length-1];
    }

    /**
     * get path tail excluded the first part of the path
     * @return string
     */
    public String tail() {
        String result = "";
        for (int i=1; i<keys.length; i++) 
            result = ("".equals(result) ? "" : ".")+keys[i];
        return result;
    }
}
