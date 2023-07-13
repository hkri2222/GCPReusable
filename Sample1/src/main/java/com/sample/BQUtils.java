package com.sample;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.*;
import com.google.cloud.spanner.ResultSet;
import com.google.common.collect.Lists;

import java.io.FileInputStream;
import java.util.*;

public class BQUtils {
    private static BigQuery bigquery = null;
    private ThreadLocal<ResultSet> resultSet = new ThreadLocal<>();
    static Map<String, Map<String,String>> bigQueryResults;

    public void runBigQueryJob(String query) throws InterruptedException {
        try{
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/test/resources/cred.json"))
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            bigquery = BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();

            //bigquery = BigQueryOptions.getDefaultInstance().getService();
        } catch (Exception e){
            e.printStackTrace();
        }


        QueryJobConfiguration queryConfig =
                QueryJobConfiguration.newBuilder(query)
                        // Use standard SQL syntax for queries.
                        // See: https://cloud.google.com/bigquery/sql-reference/
                        .setUseLegacySql(false)
                        .build();

        // Create a job ID so that we can safely retry.
        JobId jobId = JobId.of(UUID.randomUUID().toString());
        Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());


        if (queryJob == null) {
            throw new RuntimeException("Job no longer exists");
        } else if (queryJob.getStatus().getError() != null) {
            // You can also look at queryJob.getStatus().getExecutionErrors() for all
            // errors, not just the latest one.
            throw new RuntimeException(queryJob.getStatus().getError().toString());
        }

        // Get the results.
        TableResult result = queryJob.getQueryResults();
        Map<String, Map<String,String>> resultsMap = new HashMap<>();

        List<String> fieldNames = new ArrayList<>();

        for(Field field : result.getSchema().getFields()){
            fieldNames.add(field.getName());
        }


        int count = 1;
        for (FieldValueList row : result.iterateAll()) {
            Map<String,String> rowMap = new HashMap<>();

            for(String field : fieldNames){
                try{
                    if(row.get(field).isNull()){
                        rowMap.put(field, null);
                    }else{
                        rowMap.put(field, row.get(field).getStringValue());
                    }

                }catch(NullPointerException e1){
                    e1.printStackTrace();
                    rowMap.put(field, null);
                }
            }


            resultsMap.put(Integer.toString(count), rowMap);
            count++;
        }

        bigQueryResults = resultsMap;
    }

    private static void getBigQueryObject()
    {
        if(bigquery==null)
            bigquery = BigQueryOptions.getDefaultInstance().getService();
    }

    public static void main(String args[]) throws InterruptedException {
        try{
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/cred.json"))
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            bigquery = BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();

            //bigquery = BigQueryOptions.getDefaultInstance().getService();
        } catch (Exception e){
            e.printStackTrace();
        }
        String query = "SELECT word, word_count FROM `bigquery-public-data.samples.shakespeare`WHERE corpus = 'juliuscaesar' ORDER BY word_count DESC limit 10;";
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();

        Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).build());
        queryJob =queryJob.waitFor();

        if(queryJob == null){
          throw new RuntimeException("Job no longer exists");

        }
        if(queryJob.getStatus().getError()!=null){
            throw new RuntimeException(queryJob.getStatus().getError().toString());
        }
        TableResult result = queryJob.getQueryResults();
        for (FieldValueList row: result.iterateAll()){
            String word = row.get("word").getStringValue();
            int wordCount = row.get("word_count").getNumericValue().intValue();
            System.out.println(word+" , "+ wordCount);
        }
//        Map<String, Map<String, String>> resultsMap = new HashMap<>();
//        Map<String, String> resultsMap2 = new HashMap<>();

//            List<String> fieldNames = new ArrayList<>();
//
//            for(Field field : tr.getSchema().getFields()){
//                fieldNames.add(field.getName());
//            }
//
//
//            int count = 1;
//            for (FieldValueList row : tr.iterateAll()) {
//                Map<String,String> rowMap = new HashMap<>();
//
//                for(String field : fieldNames){
//                    try{
//                        if(row.get(field).isNull()){
//                            rowMap.put(field, null);
//                            resultsMap2.put(field,null);
//                        }else{
//                            rowMap.put(field, row.get(field).getStringValue());
//                            resultsMap2.put(field,row.get(field).getStringValue());
//                        }
//
//                    }catch(NullPointerException e1){
//                        e1.printStackTrace();
//                        rowMap.put(field, null);
//                    }
//                }
//
//
//                resultsMap.put(Integer.toString(count), rowMap);
//
//                count++;
//            }
//
//            bigQueryResults = resultsMap;
//
//
//            //tr.iterateAll().forEach(row -> row.forEach(val -> System.out.printf("%s,", val.toString())));
//        }catch (Exception e){
//            e.printStackTrace();
//        }


        //String query = "SELECT last_push_ts,sku_upc_nbr,availability.fiol as availability_fiol,availability.cats\t as availability_cats, availability.fiis as availability_fiis,availability.bops\tas availability_bops,availability.sdd\tas availability_sdd,fulfillment.cats_quantity\tas fulfillment_cats_quantity, fulfillment.bopsqty\tas fulfillment_bopsqty,\tfulfillment.sddqty\tas fulfillment_sddqty FROM `mtech-daas-product-pdata-qa.raw_invntry.pull_m_ivs_rfd_push_enhancer_messages_fcc2_0_rtm` WHERE sku_upc_nbr = \"192081109021\";";


//    public Map<String, Map<String,String>> getBigQueryResults(){
//        return this.bigQueryResults;
//    }

    }}
