package com.michael.geo;

import org.json.JSONObject;

/**
 * Created by Michael on 9/21/2015.
 * Represents geo location  
 */
public class GeoData extends GenericData {
    private static final String[] header = new String[]{"_id","name","type","geo_position.latitude","geo_position.longitude"};
    public GeoData(JSONObject jsonObject) {
        super(jsonObject, header);
    }
    
    public static String[] getHeader() {
        return getHeader(header);
    }
}
