package com.sample.steps;



import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GCSfileUploadSteps extends CustomBaseStep {

    private static final Logger logger = LoggerFactory.getLogger(GCSfileUploadSteps.class);
    static String bucketName = gcs_bucket_name;

    //region Method to perform GCS File Upload
    public static void GCSfileUpload() throws IOException, ExecutionException, InterruptedException {
    try{
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String current_Date1 = today.format(formatter);

        String gcsfileuploadPath = "demofiles/samplefile.txt";
        // The ID of your GCS bucket

        // The path to your file to upload
        String directoryPath = System.getProperty("user.dir");
        String projectPath = "\\src\\main\\resources\\samplefile.txt";
        String filePath = directoryPath.concat(projectPath);

        Storage storage = StorageOptions.newBuilder().setProjectId(gcp_project_id).build().getService();
        // Upload a blob to the  created bucket
        BlobId blobId = BlobId.of(bucketName, gcsfileuploadPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();


        Path path = Paths.get(filePath);
        Stream<String> lines = Files.lines(path);

        List<String> dataList = lines.collect(Collectors.toList());
        List<String> replacedList = new ArrayList<>();
        String str = null;
        for (String data : dataList) {
            str = data.replaceAll("date1", current_Date1);
            replacedList.add(str);
        }

        Files.write(path, replacedList);
        lines.close();

        storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));
        System.out.println("File " + filePath + " uploaded to bucket " + bucketName + " as " + gcsfileuploadPath);

        Files.write(path, dataList);
        //   lines.close();


    }
    catch(IOException ex) {
        ex.printStackTrace();
        System.out.printf("File upload Failed");
    }
        System.out.printf("Uploaded successfully");
}
    //endregion

    //region Method to perform GCS File delete
    public static void deletefileFromGCS() throws IOException, ExecutionException, InterruptedException {

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String current_Date1 = today.format(formatter);

        String gcsfileuploadPath = "demofiles/samplefile.txt";


        // The path to your file to upload
        String directoryPath = System.getProperty("user.dir");
        String projectPath = "\\src\\main\\resources\\samplefile.txt";
        String filePath = directoryPath.concat(projectPath);

        Storage storage = StorageOptions.newBuilder().setProjectId(gcp_project_id).build().getService();
        // Upload a blob to the  created bucket
        BlobId blobId = BlobId.of(bucketName, gcsfileuploadPath);
        storage.delete(blobId);

        logger.info("File " + filePath + " deleted from bucket " + bucketName + " as " + gcsfileuploadPath);
        System.out.println("Deleted successfully");
    }
    //endregion
}
