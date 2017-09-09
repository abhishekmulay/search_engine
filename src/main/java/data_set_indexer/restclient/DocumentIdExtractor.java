package hw1.restclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hw1.main.ConfigurationManager;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Abhishek Mulay on 5/19/17.
 */
public class DocumentIdExtractor {

    private RestCallHandler handler = new RestCallHandler();
    private String INDEX_NAME = ConfigurationManager.getConfigurationValue("index.name");
    private String TYPE_NAME = ConfigurationManager.getConfigurationValue("type.name");
    private final String keepAliveTime = "2m";
    private String endPoint = "/" + INDEX_NAME + "/" + TYPE_NAME + "/_search?scroll=" + keepAliveTime;
    private String scrollEndPoint = "_search/scroll";

    public Set<String> getAllDocumentIds() throws IOException {
        handler.openConnection();
        final String body = "{\n" +
                            "  \"_source\": \"false\", \n" +
                            "  \"size\": 10000\n" +
                            "}\n";
        Set<String> docids = new HashSet<>();
        String scrollId = "";

        Response response = this.handler.get(body, endPoint);
        HttpEntity entity = response.getEntity();
        String jsonString = EntityUtils.toString(entity);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);

        if (jsonNode.has("_scroll_id")) {
            scrollId = jsonNode.get("_scroll_id").asText();
            docids.addAll(extraceDocumentIds(jsonNode));
        }

        boolean hitsPresent = true;
        while (hitsPresent) {
            final String scrollBody = "{\n" +
                    "    \"scroll\" : \"1m\", \n" +
                    "    \"scroll_id\": \"" + scrollId + "\"\n" +
                    "}\n";

            Response nextPageResponse = handler.post(scrollBody, scrollEndPoint);
            String nextJson = EntityUtils.toString(nextPageResponse.getEntity());
            JsonNode jsonTree = mapper.readTree(nextJson);
            hitsPresent = jsonTree.get("hits").get("hits").size() > 0;
            if (jsonNode.has("_scroll_id")) {
                scrollId = jsonTree.get("_scroll_id").asText();
            }
            docids.addAll(extraceDocumentIds(jsonTree));
        }
        handler.closeConnection();
        return docids;
    }

    private Set<String> extraceDocumentIds(JsonNode jsonNode) {
        Set<String> docids = new HashSet<String>();
        JsonNode hitsArray = jsonNode.get("hits").get("hits");
        if (hitsArray.isArray()) {
            for (JsonNode hit : hitsArray) {
                if (hit.has("_id")) {
                    String docId = hit.get("_id").asText();
                    docids.add(docId);
                }
            }
        }
        return docids;
    }

}