package com.sample.steps;


import com.sample.Utils.FileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class CustomBaseStep {

    private static final Logger logger  = LoggerFactory.getLogger(CustomBaseStep.class);

    static String gcp_project_id = FileConfig.getInstance().getStringConfigValue("gcpresources.spanner.gcpprojectname");
    static String topic_Id = FileConfig.getInstance().getStringConfigValue("gcpresources.pubsub.PubSub_TOPIC");
    static String subscription_Id = FileConfig.getInstance().getStringConfigValue("gcpresources.pubsub.PubSub_Subscription");
    static List<String> purge_subscription_Id = FileConfig.getInstance().getStringConfigValues(("purgesubscriptions"));
    static String BigQueryTableName = FileConfig.getInstance().getStringConfigValue("gcpresources.bigquery.gcpbqtablename");
    static String gcs_bucket_name= FileConfig.getInstance().getStringConfigValue("gcpresources.cloudstorage.gcsbucketname");
}
