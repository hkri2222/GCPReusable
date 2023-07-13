package com.sample.Utils;

import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.BigQuery.JobOption;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;


public class BQutils {
    private static Logger LOGGER = Logger.getLogger(BQutils.class.getName());
    protected static Properties prop = new Properties();
    private GCPClients gcpclients;

    public BQutils() throws IOException {

        String gcp_service_account_file_path = FileConfig.getInstance().getStringConfigValue("gcp_service_account_path");
        this.gcpclients = new GCPClients(gcp_service_account_file_path);
    }

    public TableResult get_results_Bquery(String query) {
        TableResult result = null;

        try {
            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            result = this.gcpclients.get_bq_client().query(queryConfig, new JobOption[0]);
            LOGGER.info("Query ran successfully");
        } catch (InterruptedException | BigQueryException var4) {
            LOGGER.info("Query did not run \n" + var4.toString());
        }

        return result;
    }
}
