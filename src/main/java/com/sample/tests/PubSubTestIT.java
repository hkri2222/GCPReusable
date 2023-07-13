package com.sample.tests;


import com.sample.Utils.ConfigReader;
import com.sample.Utils.ExcelOperations;
import com.sample.Utils.ExcelOperations1;
import com.sample.Utils.TestCase;
import com.sample.steps.PubSubSteps;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class PubSubTestIT extends ConfigReader {

    //Test for purge message from subscriptions
    @Test
    public void purgeSubscriptions() throws IOException, ExecutionException, InterruptedException {
        PubSubSteps.purgeSubscriptions();
    }

    /*Test to publish and subscribe message
    Since dataprovider class is mentioned in the test,csv file path is also specified in the dataprovider class*/

    @Test(dataProvider = "ReadData",dataProviderClass = ExcelOperations1.class)
    public void publishMessageAndSubscribeMessage(TestCase testCaseData) throws IOException, ExecutionException, InterruptedException {
        PubSubSteps.publishMessage(testCaseData);
    }

    //Test to publish message
    @Test(dataProvider = "ReadData",dataProviderClass = ExcelOperations.class)
    public void publishMessage(String Id,String message,String msg) throws IOException, ExecutionException, InterruptedException {
        PubSubSteps.publishMessage1( Id,message,msg);
}

    //Test to subscribe message
    @Test
    public void subscribeMessage() throws IOException, ExecutionException, InterruptedException {
        PubSubSteps.subscribeMessage();
    }

}
