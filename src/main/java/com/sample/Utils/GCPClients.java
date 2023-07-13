package com.sample.Utils;
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.redis.v1.CloudRedisClient;
import com.google.cloud.redis.v1.CloudRedisSettings;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.StorageOptions.Builder;
import com.google.common.collect.Lists;
import java.io.FileInputStream;
import java.io.IOException;
import org.threeten.bp.Duration;

public class GCPClients {
    private GoogleCredentials credentials = null;
    private BigtableDataClient bigtableDataClient;

    public GCPClients(String jsonPath) {
        if (!jsonPath.isEmpty()) {
            try {
                this.credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath)).createScoped(Lists.newArrayList(new String[]{"https://www.googleapis.com/auth/cloud-platform"}));
            } catch (IOException var4) {
                throw new RuntimeException("Could not read key file " + jsonPath, var4);
            }
        } else {
            System.out.println("Using  default application credentials.");

            try {
                this.credentials = GoogleCredentials.getApplicationDefault();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }

    }

    public Storage get_storage_client() {
        Storage storage = (Storage)((Builder)StorageOptions.newBuilder().setCredentials(this.credentials)).build().getService();
        return storage;
    }

    public BigtableDataClient get_bigtable_client(String projectId, String instanceId) throws IOException {
        BigtableDataSettings settings = BigtableDataSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(this.credentials)).setProjectId(projectId.trim()).setInstanceId(instanceId.trim()).build();

        try {
            if (this.bigtableDataClient == null) {
                this.bigtableDataClient = BigtableDataClient.create(settings);
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return this.bigtableDataClient;
    }

    public BigQuery get_bq_client() {
        BigQueryOptions bqoptions = ((com.google.cloud.bigquery.BigQueryOptions.Builder)BigQueryOptions.newBuilder().setCredentials(this.credentials)).build();
        BigQuery bq = (BigQuery)bqoptions.getService();
        return bq;
    }

    public Datastore get_datastore_client() {
        DatastoreOptions datastoreoptions = ((com.google.cloud.datastore.DatastoreOptions.Builder)DatastoreOptions.newBuilder().setCredentials(this.credentials)).build();
        Datastore datastore = (Datastore)datastoreoptions.getService();
        return datastore;
    }

    public SpannerOptions get_spanner_options() {
        SpannerOptions spannerOptions = ((com.google.cloud.spanner.SpannerOptions.Builder)SpannerOptions.newBuilder().setCredentials(this.credentials)).build();
        return spannerOptions;
    }

    public Spanner get_spanner_client() {
        Spanner spanner = (Spanner)this.get_spanner_options().getService();
        return spanner;
    }

    public CloudRedisClient get_redis_client() {
        CloudRedisClient cloudRedisClient = null;

        try {
            cloudRedisClient = CloudRedisClient.create(this.cloudRedisSettings());
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return cloudRedisClient;
    }

    public CloudRedisSettings cloudRedisSettings() {
        CloudRedisSettings cloudRedisSettings = null;
        com.google.cloud.redis.v1.CloudRedisSettings.Builder cloudRedisSettingsBuilder = CloudRedisSettings.newBuilder();
        ((com.google.cloud.redis.v1.CloudRedisSettings.Builder)cloudRedisSettingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(this.credentials))).getInstanceSettings().setRetrySettings(cloudRedisSettingsBuilder.getInstanceSettings().getRetrySettings().toBuilder().setTotalTimeout(Duration.ofSeconds(30L)).build());

        try {
            cloudRedisSettings = cloudRedisSettingsBuilder.build();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return cloudRedisSettings;
    }
}
