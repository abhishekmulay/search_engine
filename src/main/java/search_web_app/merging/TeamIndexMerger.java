package hw3.merging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hw1.main.ConfigurationManager;
import hw1.restclient.RestCallHandler;
import hw3.models.DocumentModel;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Response;
import org.junit.Test;
import sun.rmi.runtime.Log;

import javax.json.Json;
import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by Abhishek Mulay on 6/29/17.
 */
public class TeamIndexMerger {

    static Logger LOG = LogManager.getLogger(TeamIndexMerger.class);
    private static final String OUTPUT_DIR = ConfigurationManager.getConfigurationValue("hw3.models.file.path");
//    private String serverAddress = ConfigurationManager.getConfigurationValue("team.elastic.server.address");
    private String index = ConfigurationManager.getConfigurationValue("team.elastic.index");
    private String type = ConfigurationManager.getConfigurationValue("team.elastic.type");
    private String TEAM_ELASTIC_ENDPOINT = "/" + index + "/" + type + "/";

    public void bulkMerge() throws IOException {
        JsonRecordReader recordReader = new JsonRecordReader();
        Map<String, Set<String>> inlinksMap = recordReader.getInlinksMap();
        RestCallHandler handler = new RestCallHandler();
        handler.openConnection();
        ObjectMapper mapper = new ObjectMapper();

        LOG.info("Bulk inserting documents at [" + TEAM_ELASTIC_ENDPOINT + "]");

        int count = 1;
        StringBuilder bulkRequestBuffer = new StringBuilder();

        File outputDir = new File(OUTPUT_DIR);
        for (File file : outputDir.listFiles()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                for (String line; (line = br.readLine()) != null; ) {
                    DocumentModel model = mapper.readValue(line, DocumentModel.class);

                    String encodedUrlId = URLEncoder.encode(model.getDocno());
                    bulkRequestBuffer.append(handler.getMetadata(index, type, encodedUrlId));
                    Set<String> inlinks = inlinksMap.get(model.getDocno());
                    model.attachedInlinks(inlinks);

                    bulkRequestBuffer.append(mapper.writeValueAsString(model)).append("\n");

                    if (count % 5 == 0) {
                        Response response = handler.bulkPOST(bulkRequestBuffer.toString());
                        LOG.info(response.getStatusLine());
                        // clear buffer
                        bulkRequestBuffer.setLength(0);
                        LOG.info("Inserting [" + count + "] documents complete.");
                    }
                    count++;
                }
            }
        }
        handler.closeConnection();
    }

    public void merge() throws IOException {
        RestCallHandler handler = new RestCallHandler();
        handler.openConnection();
        ObjectMapper mapper = new ObjectMapper();

        JsonRecordReader recordReader = new JsonRecordReader();
        Map<String, Set<String>> inlinksMap = recordReader.getInlinksMap();

        LOG.info("Bulk inserting documents at [" + TEAM_ELASTIC_ENDPOINT + "]");

        File outputDir = new File(OUTPUT_DIR);
        for (File file : outputDir.listFiles()) {
            LOG.info("\nInserting documents from ["+ file.getPath() + "]");
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                for (String line; (line = br.readLine()) != null; ) {
                    DocumentModel model = mapper.readValue(line, DocumentModel.class);
                    String docno = model.getDocno();
                    String encodedUrlId = URLEncoder.encode(docno);
                    Set<String> inlinks = inlinksMap.get(docno);
                    model.attachedInlinks(inlinks);

                    String jsonString = mapper.writeValueAsString(model);

                    if (inlinks == null) {
                        inlinks = Collections.emptySet();
                    }
                    // update doc if it already exisits.

                    final String body = "{\n" +
                            "    \"script\" : {\n" +
                            "        \"inline\": \"ctx._source.author += params.author; if(ctx._source.in_links!=null) ctx._source.in_links.addAll(params.inlinks)\",\n" +
                            "        \"lang\": \"painless\",\n" +
                            "        \"params\" : {\n" +
                            "            \"author\" : \" and Abhishek \",\n" +
                            "            \"inlinks\": "+ mapper.writeValueAsString(inlinks) +"\n" +
                            "        }\n" +
                            "    }\n" +
                            "}";

                    // if doc not present we will get error, insert fresh new doc.
                    Response updateResponse = handler.post(body, TEAM_ELASTIC_ENDPOINT + encodedUrlId + "/_update");
                    if (updateResponse==null || updateResponse.getStatusLine().getStatusCode() != 200) {
                        handler.post(jsonString, TEAM_ELASTIC_ENDPOINT + encodedUrlId);
                    }
                }
            }
            LOG.info("\nDone inserting documents from ["+ file.getPath() + "].");
        }
        handler.closeConnection();
    }

    public static void main(String[] args) throws IOException {
        TeamIndexMerger indexMerger = new TeamIndexMerger();
        indexMerger.bulkMerge();
    }

}
