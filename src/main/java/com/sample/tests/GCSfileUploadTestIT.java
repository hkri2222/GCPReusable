package com.sample.tests;

import com.sample.Utils.ConfigReader;
import com.sample.steps.GCSfileUploadSteps;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GCSfileUploadTestIT extends ConfigReader{


    //Test for upload file to GCS bucket
    @Test
    public static void fileUploadingToGCS() throws InterruptedException, ExecutionException, IOException {
        GCSfileUploadSteps.GCSfileUpload();
    }

    //Test for delete file from GCS bucket
    @Test
    public static void deletefileFromGCS() throws InterruptedException, ExecutionException, IOException {
        GCSfileUploadSteps.deletefileFromGCS();
    }

}
