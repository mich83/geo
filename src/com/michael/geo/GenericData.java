package com.michael.geo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

/**
 * Created by Michael on 9/21/2015.
 * Represents generic data. Could by extended with different column values. 
 */
public class GenericData {
    private String error;
    private static final String ERROR_UNEXPECTED = "Unexpected error";
    private LinkedHashMap<String, String> data;

    /**
     * Default constructor: creates object based on json object
     * @param json json object
     * @param header list of columns 
     */
    public GenericData(JSONObject json, String[] header) {
        if (json.has("error")) {
            try {
                error  = json.getString("error");
            } catch (JSONException e) {
                error = ERROR_UNEXPECTED;
            }
        } else {
            data = new LinkedHashMap<>();
            for (String key: header) {
                String[] keys = key.split(Pattern.quote("."));
                data.put(keys[keys.length-1],getValue(json, key).toString());
            }
        }
    }

    /**
     * get json object value with support of long paths with '.' as a delimiter
     * @param jsonObject - object
     * @param key - path to value
     * @return object value or empty string in case of exception
     */
    private Object getValue(JSONObject jsonObject, String key) {
        try {
            if (key.contains(".")) {
                ItemKey itemKey = new ItemKey(key);
                return getValue(jsonObject.getJSONObject(itemKey.first()), itemKey.tail());
            } else
                return jsonObject.get(key);
        } catch (JSONException e) {
            return "";
        }
    }

    /**
     * Is current object contains error?
     * @return error
     */
    public boolean hasError() {
        return error != null;
    }

    /**
     * get error message
     * @return error message
     */
    public String getError() {
        return error;
    }

    /**
     * returns line data for printing
     * @return data which could be passed directly to CSVPrinter
     */
    public Iterable<String> getLine() {
        return data.values();
    }

    /**
     * returns header data for printing
     * @param header - column list
     * @return data which could be passed directly to CSVPrinter
     */
    public static String[] getHeader(String[] header) {
        String[] result = header.clone();
        for (int i= 0; i<result.length;i++)
            result[i] = (new ItemKey(result[i])).last();
        return result;
    }

}
