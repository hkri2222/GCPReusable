package com.sample.steps;

import com.google.api.core.ApiFuture;
import com.google.cloud.spanner.*;
import com.google.spanner.v1.TypeCode;

import java.util.HashMap;
import java.util.Map;

public class SpannerQueryToHashMap {

    public static void main(String[] args) {
        // Provide your Spanner project ID, instance ID, and database ID
        String projectId = "your-project-id";
        String instanceId = "your-instance-id";
        String databaseId = "your-database-id";

        // Provide the table name and the SQL query to execute
        String tableName = "your-table-name";
        String query = "SELECT * FROM " + tableName;

        // Query Spanner and convert the result to a HashMap
        Map<String, Object> resultMap = queryToHashMap(projectId, instanceId, databaseId, query);

        // Print the resulting HashMap
        System.out.println(resultMap);
    }

    private static Map<String, Object> queryToHashMap(String projectId, String instanceId, String databaseId, String query) {
        Map<String, Object> resultMap = new HashMap<>();

        try (Spanner spanner = SpannerOptions.newBuilder().setProjectId(projectId).build().getService()) {
            DatabaseClient databaseClient = spanner.getDatabaseClient(DatabaseId.of(projectId, instanceId, databaseId));
            ResultSet resultSet = databaseClient.singleUse().executeQuery(Statement.of(query));

//            if (resultSet.next()) {
//                for (int i = 0; i < resultSet.getColumnCount(); i++) {
//                    String columnName = resultSet.getMetadata().getColumn(i).getName();
//                    Object columnValue = resultSet.getColumnType(i).getCode().equals(TypeCode.ARRAY)
//                            ? resultSet.getArray(i).toList()
//                            : resultSet.getString(i);
//                    resultMap.put(columnName, columnValue);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        }
        return resultMap;
    }
}
