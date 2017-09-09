package hw1.indexing.datawriter;

import hw1.indexing.datareader.DataReader;
import hw1.main.ConfigurationManager;
import hw1.restclient.RestCallHandler;

import java.util.*;

/**
 * Created by Abhishek Mulay on 5/11/17.
 */
public class DataWriter {

    private String INDEX_NAME = ConfigurationManager.getConfigurationValue("index.name");
    private String TYPE_NAME = ConfigurationManager.getConfigurationValue("type.name");
    final static int CHUNK_SIZE = Integer.parseInt(ConfigurationManager.getConfigurationValue("chunk.size"));

    private String getMetadata(String indexName, String typeName, String documentId) {
        String actionMetaData = String.format
                ("{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_id\" : \"%s\" } }%n",
                        indexName, typeName, documentId);
        return actionMetaData;
    }

    //    { "index" : { "_index" : "ap_dataset", "_type" : "hw1", "_id" : "ABC123" } }
    //    { "field1" : "value1" }
    public void insertChunks(RestCallHandler restCallHandler, Map<String, String> jsonMap) {
        DataReader reader = new DataReader();
        String actionMetaData = "";
        String json = "";
        String documentId = "";
        StringBuffer bulkRequestBody = new StringBuffer();

        for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
            documentId = entry.getKey();
            json = entry.getValue();
            actionMetaData = getMetadata(INDEX_NAME, TYPE_NAME, documentId);
            bulkRequestBody.append(actionMetaData);
            bulkRequestBody.append(json);
            bulkRequestBody.append("\n");
        }
        System.out.println("Bulk inserting " + jsonMap.size() + " documents in " + INDEX_NAME);
        restCallHandler.bulkPOST(bulkRequestBody.toString());
    }

    public List<Map<String, String>> createSubMaps(Map<String, String> jsonMap) {
        List<Map<String,String>> mapList = new ArrayList<Map<String, String>>();
        int count=0;
        Map<String, String> subMap = new HashMap<String, String>();
        for (Map.Entry<String, String> entry :jsonMap.entrySet()) {
            String documentId = entry.getKey();
            String json = entry.getValue();
            subMap.put(documentId, json);
            if (count % CHUNK_SIZE == 0) {
                mapList.add(subMap);
                subMap = new HashMap<String, String>();
            }
            count = count + 1;
        }
        // elements less than chunk size remaining in last map
        mapList.add(subMap);
        return mapList;
    }

    public void bulkInsertDocuments(Map<String, String> allJsonMap) {
        // Divide the big map of JSON strings into small maps each containing 1000 json.
        RestCallHandler restCallHandler = new RestCallHandler();
        restCallHandler.openConnection();
        if (allJsonMap.size() > CHUNK_SIZE) {
            List<Map<String, String>> jsonMaps = createSubMaps(allJsonMap);
            for (Map<String, String> jsonMap : jsonMaps) {
                insertChunks(restCallHandler, jsonMap);  // insert 1000 json documents at once.
            }
        } else {
            insertChunks(restCallHandler, allJsonMap);
        }
        restCallHandler.closeConnection();
    }
}
