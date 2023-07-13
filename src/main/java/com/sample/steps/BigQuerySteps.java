package com.sample.steps;



import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;
import com.google.gson.*;

import org.json.simple.JSONObject;
import com.sample.Utils.BQutils;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import java.util.*;


public class BigQuerySteps extends CustomBaseStep {
     static Logger logger = LoggerFactory.getLogger(BigQuerySteps.class.getName());

    static Map<String, Map<String,String>> bigQueryResults;
    static String TableName = BigQueryTableName;


    public static void validateDataFromBQ(String expt_rslt) throws  ParseException {

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(expt_rslt);
        String word = jsonObject.get("word").toString();

        try {
            BQutils bq = new BQutils();
            String query = "SELECT * FROM `" + TableName + "`  where word = '" + word + "' limit 1";



            System.out.println("Big query to be executed: "+query);
            TableResult tr = bq.get_results_Bquery(query);

            System.out.println("Result set is "+ tr);

            Map<String, Map<String,String>> resultsMap = new HashMap<>();
            Map<String, String> resultsMap2 = new HashMap<>();

            List<String> fieldNames = new ArrayList<>();

            for(Field field : tr.getSchema().getFields()){
                fieldNames.add(field.getName());

            }
            System.out.println("Field Names are "+fieldNames);


            int count = 1;
            for (FieldValueList row : tr.iterateAll()) {
                Map<String,String> rowMap = new HashMap<>();

                for(String field : fieldNames){
                    try{
                        if(row.get(field).isNull()){
                            rowMap.put(field, null);
                            resultsMap2.put(field,null);
                        }else{
                            rowMap.put(field, row.get(field).getStringValue());
                            resultsMap2.put(field,row.get(field).getStringValue());
                        }

                    }catch(NullPointerException e1){
                        e1.printStackTrace();
                        rowMap.put(field, null);
                    }
                }
                resultsMap.put(Integer.toString(count), rowMap);
                count++;
            }
            boolean resultsetEmpty = false;
            bigQueryResults = resultsMap;
            if(bigQueryResults.size()>0){
                //  logger.info("BQ Results are : "+bigQueryResults.entrySet());
                Gson gson = new Gson();
                String json = gson.toJson(resultsMap2);
                System.out.println("Actual message is - "+json);
                System.out.println("Expected message is - "+ expt_rslt);
                JSONAssert.assertEquals("Comparing the expected and actual message", expt_rslt, json, JSONCompareMode.LENIENT);
            }
            else{
                resultsetEmpty = true;
            }
            Assert.assertFalse(resultsetEmpty, "Results empty : No data in BQ table");

        }catch (Exception e){
            e.printStackTrace();
        }



    }

}
