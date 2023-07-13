package com.sample.tests;

import com.sample.Utils.ExcelOperations1;
import com.sample.Utils.ExcelOperationsBQ;
import com.sample.steps.BigQuerySteps;
import com.sample.steps.GCSfileUploadSteps;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class BigQueryIT {
    //Validate data from BQtable
    @Test(dataProvider = "ReadData",dataProviderClass = ExcelOperationsBQ.class)
    public void validateBigQeury(String expt_rslt) throws InterruptedException, ExecutionException, IOException, ParseException {
        BigQuerySteps.validateDataFromBQ(expt_rslt);
    }
}
