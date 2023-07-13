package com.sample.tests;

import com.sample.Utils.ExcelOperations;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.DataProvider;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class Test3 extends ExcelOperations {

    @DataProvider
    public static Iterator<Object[]> JsonData (String Data) {
        String finalJson = null;
        Object updatedJSON = null;
        try {
            JSONParser parser = new JSONParser();
            //Use JSONObject for simple JSON and JSONArray for array of JSON.
            JSONObject data = (JSONObject) parser.parse(
                    new FileReader("src/main/resources/payload.json"));//path to the JSON file.

            String jsonobj1 = (String) data.get("message");
            String json1 = data.toJSONString();
            finalJson = json1.replace(jsonobj1, Data);
            updatedJSON = finalJson;
            System.out.println("JSON message is " + finalJson);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return (Iterator<Object[]>) updatedJSON;
    }
}