package hw3;

import hw1.main.ConfigurationManager;
import hw1.restclient.RestCallHandler;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;

import java.io.*;

/**
 * Created by Abhishek Mulay on 7/4/17.
 */
public class BulkDocImporter {

    private static String index = ConfigurationManager.getConfigurationValue("team.elastic.index");
    private static String type = ConfigurationManager.getConfigurationValue("team.elastic.type");
    private static String BULK_GET_ENDPOINT = "/" + index + "/" + type  + "/_search?pretty=false&scroll=2m";

    private static String ALL_DATA_DUMP_FILE = ConfigurationManager.getConfigurationValue("team.elastic.dump.file.path");

    public void extractAllDocuments() {
        RestCallHandler handler = new RestCallHandler();
        handler.openConnection();

        File file = new File(ALL_DATA_DUMP_FILE);
        BufferedWriter bufferedWriter = null;
        try {
            if (!file.exists())
                file.createNewFile();
            else {
                file.delete();
                file.createNewFile();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter(bufferedWriter);

        final int batchSize = 20;
        final String body = "{\n" +
                "  \"size\": " + batchSize + " \n" +
                "}";

        Response response = handler.get(body, BULK_GET_ENDPOINT);

        try {
            String jsonString = EntityUtils.toString(response.getEntity());
            System.out.println(jsonString);
            writer.write(jsonString);
            writer.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }

        writer.flush();
        writer.close();

        handler.closeConnection();
    }


    public static void main(String[] args) {
        BulkDocImporter importer = new BulkDocImporter();
        importer.extractAllDocuments();
    }

}
