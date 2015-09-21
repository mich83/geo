package com.michael.geo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Michael on 9/21/2015.
 * Entry point
 */
public class GoEuroTest {
    public static void main(String[] args) {
        String key = "";
        //City name can contain spaces
        for (String arg: args) {
            key = key+" "+arg;
        }
        key = key.trim();
        if (key.length() == 0) {
            System.out.println("Usage: java -jar GoEuroTest.jar \"CITY_NAME\"");
            return;
        }
        //retrieve data from web server
        GenericData[] data = HttpConnector.requestWebService(key, GeoData.class);
        FileWriter writer;
        try {
            writer = new FileWriter(key+".csv");
        } catch (IOException e) {
            System.err.println("Cannot create file!");
            return;
        }
        try {
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            //print header 
            printer.printRecord(GeoData.getHeader());
            for (GenericData item : data) {
                if (item.hasError()) 
                    System.err.println(item.getError()); //print error to stderr
                else 
                    printer.printRecord(item.getLine()); //print data to file 
            }
            printer.close();
        } catch (IOException e) {
            System.err.println("IO Exception");
        }
    } 
}
