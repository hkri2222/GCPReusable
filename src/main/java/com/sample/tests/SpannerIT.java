package com.sample.tests;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spanner.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SpannerIT {
    public static void main(String[] args) throws IOException {
        // Set up Spanner client

        String credentialsFilePath = "src/main/resources/cred.json";
        InputStream credentialsStream = new FileInputStream(credentialsFilePath);
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);

        SpannerOptions options = SpannerOptions.newBuilder().setCredentials(credentials).build();
        Spanner spanner = options.getService();
        String instanceId = "spanner-test1-instance";
        String databaseId = "testing-db";
        DatabaseClient dbClient = spanner.getDatabaseClient(
                DatabaseId.of(options.getProjectId(), instanceId, databaseId));

        // Execute SQL query and get result set
        Statement statement = Statement.of("SELECT * FROM Singers");
        ResultSet resultSet = dbClient.singleUse().executeQuery(statement);

        // Print result set
        while (resultSet.next()) {
            long id = resultSet.getLong("Id");
            String name = resultSet.getString("FirstName");
            System.out.printf("Id=%d, Name=%s\n", id, name);
        }

        // Close Spanner client
        spanner.close();
    }
}
