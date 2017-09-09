package hw1.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import hw1.main.ConfigurationManager;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by Abhishek Mulay on 5/20/17.
 */
public class TermVectorExtractor {

//    POST /ap_dataset/hw1/_mtermvectors
//    {
//        "ids" : ["AP890101-0060", "AP890101-0062"],
//        "parameters": {
//            "fields": [
//            "text"
//            ],
//            "term_statistics": true
//        }
//    }

    final static int CHUNK_SIZE = Integer.parseInt(ConfigurationManager.getConfigurationValue("chunk.size"));
    private final static String INDEX_NAME = ConfigurationManager.getConfigurationValue("index.name");
    private final static String TYPE_NAME = ConfigurationManager.getConfigurationValue("type.name");
    private final static String API_ENDPOINT = "/"+INDEX_NAME+"/" + TYPE_NAME + "/_mtermvectors";
    private static  final RestCallHandler handler = new RestCallHandler();

    private static List<List<String>> createSubList(Set<String> documentIds) {
        List<String> documentIdList = new ArrayList<String>(documentIds);
        List<List<String>> ids = new ArrayList<List<String>>();
        int count=0;
        List<String> subList = new ArrayList<String>();
        for (String id : documentIdList) {
            String documentId = documentIdList.get(count);
            subList.add(documentId);

            if (count % CHUNK_SIZE == 0) {
                ids.add(subList);
                subList = new ArrayList<String>();
            }
            count = count + 1;
        }
        // elements less than chunk size remaining in last map
        ids.add(subList);
        return ids;
    }

//    POST /ap_dataset/hw1/_mtermvectors
//    {
//        "ids" : ["AP890101-0060", "AP890101-0062"],
//        "parameters": {
//            "fields": ["text"],
//            "term_statistics": true
//        }
//    }
    public static void getMultipleTermVectors(List<String> documentIds) {
        try {
        ObjectMapper mapper = new ObjectMapper();
        String docIdsString = mapper.writeValueAsString(documentIds);
        String body =   "    {\n" +
                        "        \"ids\" : "+ docIdsString +",\n" +
                        "        \"parameters\": {\n" +
                        "            \"fields\": [\n" +
                        "            \"text\"\n" +
                        "            ],\n" +
                        "            \"term_statistics\": true\n" +
                        "        }\n" +
                        "    }\n";

        handler.openConnection();
        Response response = handler.post(body, API_ENDPOINT);
        handler.closeConnection();

            String jsonString = EntityUtils.toString(response.getEntity());
//            System.out.println("Term Vectors: \n" + jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    GET /ap_dataset/hw1/AP890101-0060/_termvectors
//    {
//        "fields" : ["text"],
//        "offsets" : true,
//        "payloads" : true,
//        "positions" : true,
//        "term_statistics" : true,
//        "field_statistics" : true
//    }
    public static String getTermVector(String documentId) {
        final String endPoint = '/' + INDEX_NAME + '/' + TYPE_NAME + '/' + documentId + "/_termvectors";
        final String body = "{\n" +
                            "  \"fields\" : [\"text\"],\n" +
                            "  \"offsets\" : true,\n" +
                            "  \"payloads\" : true,\n" +
                            "  \"positions\" : true,\n" +
                            "  \"term_statistics\" : true,\n" +
                            "  \"field_statistics\" : true\n" +
                            "}";
        handler.openConnection();
        Response response = handler.get(body, endPoint);
        String jsonString = "";
        try {
            jsonString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.closeConnection();

        System.out.println(endPoint + "  JSON String \n\n" +  jsonString);
        return jsonString;
    }

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(new File("output.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        DocumentIdExtractor extractor = new DocumentIdExtractor();
//        try {

//            Set<String> documentIds = extractor.getAllDocumentIds();

            String documentId = "AP890101-0062";
            TermVectorExtractor.getTermVector(documentId);

//            List<List<String>> documentIdSublist = TermVectorExtractor.createSubList(documentIds);

//            TermVectorExtractor.getMultipleTermVectors(documentIdSublist.get(1));

//            for (List<String> listOfDocIds : documentIdSublist ) {
//                TermVectorExtractor.getMultipleTermVectors(listOfDocIds);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
