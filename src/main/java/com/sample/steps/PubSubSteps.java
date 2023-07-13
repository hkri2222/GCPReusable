package com.sample.steps;



import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.sample.Utils.TestCase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.*;

public class PubSubSteps extends CustomBaseStep {

    private static final int MYTHREADS = 10;
    private static final Logger logger = LoggerFactory.getLogger(PubSubSteps.class);

    //region Method to publish messages to a given topic from a dataprovider class
    public static void publishMessage(TestCase testCase) throws IOException, ExecutionException, InterruptedException {

         TopicName topicName = TopicName.of(gcp_project_id, topic_Id);
         Publisher publisher = null;

        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();

            String message1 = testCase.getMessage();
            ByteString data = ByteString.copyFromUtf8(String.valueOf(message1));
            System.out.println("Message to be published is - " + message1);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            // Once published, returns a server-assigned message id (unique within the topic)
            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            String messageId = messageIdFuture.get();
            System.out.println("Published message ID: " + messageId);

        } finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }

    }
    //endregion

    //region Method to publish messages to a given topic from a json file
    public static void publishMessage1(String Id,String message,String msg) throws IOException, ExecutionException, InterruptedException {

//        readProperties("src/main/resources/application.properties");
//        String projectId = getProperty("ProjectID");
//        String topicId =   getProperty("PubsubTopicID");
        TopicName topicName = TopicName.of(gcp_project_id, topic_Id);
        Publisher publisher = null;

        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();
            try {
                JSONParser parser = new JSONParser();
                JSONObject data1 = (JSONObject) parser.parse(new FileReader("src/main/resources/payload.json"));
                data1.put("message", message);
                data1.put("id",Integer.parseInt(Id));
                JSONArray array1 = (JSONArray) data1.get("msg1");
                String description=null;
                for (int i=0;i<array1.size();i++){
                    JSONObject msg1 = (JSONObject) array1.get(i);
                    msg1.put("description",msg);
                    data1.put("msg1",msg1);
                }

                String json2 = data1.toJSONString();
                ByteString data = ByteString.copyFromUtf8(String.valueOf(json2));
                System.out.println("Message to be published is - " + json2);
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

                // Once published, returns a server-assigned message id (unique within the topic)
                ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
                String messageId = messageIdFuture.get();
                System.out.println("Published message ID: " + messageId);

            }catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }

    }
    //endregion

    //region Method to subscribe messages from a given subscription
    public static void subscribeMessage() {

        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(gcp_project_id, subscription_Id);
        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    // Handle incoming message, then ack the received message.
                    System.out.println("Id: " + message.getMessageId());
                    System.out.println("Data: " + message.getData().toStringUtf8());
                    consumer.ack();
                };

        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
            // Start the subscriber.
            subscriber.startAsync().awaitRunning();
            System.out.printf("Listening for messages on %s:\n", subscriptionName.toString());
            // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
            subscriber.awaitTerminated(30, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            // Shut down the subscriber after 30s. Stop receiving messages.
            subscriber.stopAsync();
        }
    }
    //endregion

    public static void purgeSubscriptions() {

        ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);

        for (int i = 0; i < purge_subscription_Id.size(); i++) {
            Runnable worker = new PurgeSubscription(purge_subscription_Id.get(i));
            executor.execute(worker);
        }
        executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {

        }

        System.out.printf("Finished all threads");
    }

    public static class PurgeSubscription implements Runnable {
        private final String subscription;

        PurgeSubscription(String subscription) {
            this.subscription = subscription;
        }

        @Override
        public void run() {
            subscribeAsync(subscription);
        }
    }

    public static void subscribeAsync(String subscriptionId) {

        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(gcp_project_id, subscriptionId);

        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    // Handle incoming message, then ack the received message.
                    System.out.printf("Id: " + message.getMessageId());
                    System.out.printf("Data: " + message.getData().toStringUtf8());
                    consumer.ack();
                };
        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
            // Start the subscriber.
            subscriber.startAsync().awaitRunning();
            System.out.printf("Listening for messages on {}", subscriptionName.toString());
            // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
            subscriber.awaitTerminated(30, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            // Shut down the subscriber after 30s. Stop receiving messages.
            subscriber.stopAsync();
        }
    }


}
