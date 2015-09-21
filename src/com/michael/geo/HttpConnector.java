package com.michael.geo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * Created by Michael on 9/21/2015.
 * This class used to connect to web service
 */
public class HttpConnector {
    private static final String CONNECTION_POINT = "http://api.goeuro.com/api/v2/position/suggest/en/";
    private static final String ERROR_WRONG_OBJECT = "Wrong JSON format";
    private static final String ERROR_ENTITY_IS_NULL = "Web service returned null";
    private static final String ERROR_CONNECTION_FAILED  = "Connection failed";
    private static final String ERROR_UNKNOWN = "Unknown error";

    /**
     * This function is used to retrieve data from webservice 
     * @param city - city to find
     * @param requestClass - desirable class for return. 
     * @return array of GeoData objects
     */
    public static GenericData[] requestWebService(String city, Class<?> requestClass) {
        JSONArray data = doRequest(city);
        GenericData[] result = new GenericData[data.length()];
        
        Constructor constructor = null;
        if (GenericData.class.isAssignableFrom(requestClass))
            try {
                Class<?>[] types = new Class[] { JSONObject.class };
                constructor = requestClass.getConstructor(types);
            } catch (NoSuchMethodException e) {
                constructor = null;
            }
        for (int i = 0; i<data.length(); i++) {
            try {
                if (constructor == null) 
                    result[i] = new GenericData(data.getJSONObject(i), new String[]{});
                else
                    try {
                        result[i] = (GenericData) constructor.newInstance(data.getJSONObject(i));
                    } catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
                        result[i] = new GenericData(data.getJSONObject(i), new String[]{});
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * This function returns raw JSONArray from server
     * In case of error the 1st element of array contains JSONObject which has error description in "error" key.
     * @param city - city to find
     * @return - JSONArray with found data
     */
    private static JSONArray doRequest(String city) {
        //user part of request should be escaped
        String uri = CONNECTION_POINT+encodeURLtoUTF8(city);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public Object handleResponse(HttpResponse httpResponse) throws IOException {
                int status = httpResponse.getStatusLine().getStatusCode();
                //check HTTP response code. Should in 200..300
                if (status>=200 && status < 300) {
                    HttpEntity entity = httpResponse.getEntity();
                    return (entity == null) ? createError(ERROR_ENTITY_IS_NULL) : createArray(EntityUtils.toString(entity));
                }
                else return createError("Web service returned: " + String.valueOf(status));
            }
        };
        Object response = null;
        try {
            HttpGet request = new HttpGet();
            request.setURI(URI.create(uri));
            response = httpClient.execute(request, responseHandler);
            
        } catch (IOException e) {
            return createError(ERROR_CONNECTION_FAILED);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                response = createError(ERROR_CONNECTION_FAILED);
            }
        }
        return response instanceof JSONArray ? (JSONArray)response : createError(ERROR_UNKNOWN);
    }

    /**
     * Creates array from given string
     * Returns default error object in case of failure
     * @param data - string
     * @return JSONArray
     */
    private static JSONArray createArray(String data) {
        try {
            return new JSONArray(data);
        } catch (JSONException e) {
            return createError(ERROR_WRONG_OBJECT);
        }
    }

    /**
     * Creates default error object from given message
     * Error object is a JSONArray with one element which looks like {'error': description}
     * @param message - error message
     * @return JSONArray 
     */
    private static JSONArray createError(String message) {
        JSONArray error = new JSONArray();
        JSONObject errorObj = new JSONObject();
        try {
            errorObj.put("error", message);
            error.put(errorObj);
        } catch (JSONException e) {
        }
        return error;
    }
    /**
     * Encode URL to UTF-8 and handle the exception if happened
     * @param url - URL
     * @return encoded URL
     */
    private static String encodeURLtoUTF8(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8").replaceAll("\\+","%20");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

}
