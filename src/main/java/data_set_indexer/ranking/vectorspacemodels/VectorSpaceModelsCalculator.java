package hw1.ranking.vectorspacemodels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hw1.main.ConfigurationManager;
import hw1.pojos.VectorStatistics;
import hw1.restclient.RestCallHandler;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 5/26/17.
 */
public class VectorSpaceModelsCalculator {
    private static RestCallHandler handler = new RestCallHandler();
    private static String INDEX_NAME = ConfigurationManager.getConfigurationValue("index.name");
    private static String TYPE_NAME = ConfigurationManager.getConfigurationValue("type.name");
    private final static String STATISTICS_API = "/" + INDEX_NAME + "/" + TYPE_NAME + "/_search?scroll=2m";
    private final static String scrollEndPoint = "_search/scroll";

    // for OKAPI and TF-IDF
    public static Map<String, List<VectorStatistics>> getVectorStatistics(String term) {
        return getVectorStatistics(term, 1);
    }

    // for BM25
    public static Map<String, List<VectorStatistics>> getVectorStatistics(String term, int tfwq) {
        double k1 = 1.2;
        double k2 = 100;
        double b = 0.75;
        double corpusSize = 84678.0;
        double avgDocLength = 441.0;

        final String body =
                "{\n" +
                "\"size\" : 10000,"+
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"text\": \"" + term + "\"" +
                "    }\n" +
                "  },\n" +
                "  \"_source\": \"docLength\",\n" +
                "  \"script_fields\": {\n" +
                "    \"okapi\": {\n" +
                "      \"script\": {\n" +
                "        \"lang\": \"groovy\",\n" +
                "        \"inline\": \"double tf = _index['text'][word].tf(); int dl =doc['docLength'].value; double df = _index['text'][word].df();  double okapiScore = tf / (tf + 0.5 + (1.5 * (dl/avgDocLength))); return okapiScore;\",\n" +
                "        \"params\": {\n" +
                "          \"word\": \"" + term + "\",\n" +
                "          \"avgDocLength\": "+avgDocLength+"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"tfidf\": {\n" +
                "      \"script\": {\n" +
                "        \"lang\": \"groovy\",\n" +
                "        \"inline\": \"double tf = _index['text'][word].tf(); int dl =doc['docLength'].value; double df = _index['text'][word].df();  double okapiScore = tf / (tf + 0.5 + (1.5 * (dl/avgDocLength))); double tfidf= okapiScore * Math.log10(corpusSize/df); return tfidf;\",\n" +
                "        \"params\": {\n" +
                "          \"word\": \"" + term + "\",\n" +
                "          \"corpusSize\": "+corpusSize+",\n" +
                "          \"avgDocLength\": "+avgDocLength+" \n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"bm25\": {\n" +
                "      \"script\": {\n" +
                "        \"lang\": \"groovy\",\n" +
                "        \"inline\": \"double tf = _index['text'][word].tf(); int dl = doc['docLength'].value; double df = _index['text'][word].df(); double first = Math.log10((corpusSize + 0.5)/(df + 0.5)); double second = ((tf+ k1 * tf) / (tf + k1 * ((1-b) + (b* (dl/avgDocLength))))); double last = (tfwq + k2 * tfwq)/ (tfwq+ k2);  double bm = first * second * last; return bm;\",\n" +
                "        \"params\": {\n" +
                "          \"word\": \"" + term + "\",\n" +
                "          \"corpusSize\": "+corpusSize+",\n" +
                "          \"avgDocLength\": "+avgDocLength+",\n" +
                "          \"k1\": "+k1+",\n" +
                "          \"k2\": "+k2+",\n" +
                "          \"b\":  "+b+",\n" +
                "          \"tfwq\": "+tfwq+"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "\n";

        handler.openConnection();
        Response response = handler.get(body, STATISTICS_API);
        String jsonString = "";
        try {
            jsonString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.closeConnection();
        return extractStatistics(term, jsonString);
    }

    private static Map<String, List<VectorStatistics>> extractStatistics(String term, String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        String scrollId = "";
        try {
            jsonNode = mapper.readTree(jsonString);
            if (jsonNode.has("_scroll_id")) {
                scrollId = jsonNode.get("_scroll_id").asText();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // parsing tf, df and ttf.
        Map<String, List<VectorStatistics>> docIdVectorTermStatisticsMap = parseTermStatistics(jsonNode, term);

        handler.openConnection();
        if (!scrollId.isEmpty()) {
            boolean hitsPresent = true;
            while (hitsPresent) {
                final String scrollBody = "{\n" +
                        "    \"scroll\" : \"1m\", \n" +
                        "    \"scroll_id\": \"" + scrollId + "\"\n" +
                        "}\n";

                Response nextPageResponse = handler.post(scrollBody, scrollEndPoint);
                String nextJsonString = null;
                JsonNode nextPageJsonNode = null;
                try {
                    nextJsonString = EntityUtils.toString(nextPageResponse.getEntity());
                    nextPageJsonNode = mapper.readTree(nextJsonString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hitsPresent = nextPageJsonNode.get("hits").get("hits").size() > 0;
                if (jsonNode.has("_scroll_id")) {
                    scrollId = nextPageJsonNode.get("_scroll_id").asText();
                }
                // parsing here again
                Map<String, List<VectorStatistics>> nextPageDocIdVectorTermStatisticsMap = parseTermStatistics(nextPageJsonNode, term);
                docIdVectorTermStatisticsMap.putAll(nextPageDocIdVectorTermStatisticsMap);
            }
        }
        System.out.println("Total term statistics for '" + term + "' = " + docIdVectorTermStatisticsMap.size());
        handler.closeConnection();
        return docIdVectorTermStatisticsMap;
    }

    private static Map<String, List<VectorStatistics>> parseTermStatistics(JsonNode jsonNode, String term) {
        // return map of <documentId, List<TermStatistics>>
        Map<String, List<VectorStatistics>> vectorTermStatisticsMap = new HashMap<>();

        if (jsonNode.has("hits")) {
            boolean hitsPresent = jsonNode.get("hits").get("hits").size() > 0;
            if (hitsPresent) {
                JsonNode hitsArray = jsonNode.get("hits").get("hits");
                for (final JsonNode hit : hitsArray) {
                    String documentId = hit.has("_id") ? hit.get("_id").asText() : "";
                    int docLength = hit.has("_source") ? hit.get("_source").get("docLength").asInt() : -1;
                    if (hit.has("fields")) {
                        double okapi = hit.get("fields").has("okapi") ? hit.get("fields").get("okapi").get(0).asDouble() : -1.0;
//                        double tfidf = hit.get("fields").has("tfidf") ? hit.get("fields").get("tfidf").get(0).asDouble() : -1.0;
                        double tfidf = 0.001;
                        double bm25 = hit.get("fields").has("bm25") ? hit.get("fields").get("bm25").get(0).asDouble() : -1.0;

                        VectorStatistics stats = new VectorStatistics(term, documentId, docLength, okapi, tfidf, bm25);
                        if (vectorTermStatisticsMap.containsKey(documentId)) {
                            List<VectorStatistics> vectorStatisticsList = vectorTermStatisticsMap.get(documentId);
                            vectorStatisticsList.add(stats);
                            vectorTermStatisticsMap.put(documentId, vectorStatisticsList);
                        } else {
                            List<VectorStatistics> newList = new ArrayList<>();
                            newList.add(stats);
                            vectorTermStatisticsMap.put(documentId, newList);
                        }
                    }
                }
            }
        }
        return vectorTermStatisticsMap;
    }


    public static void main(String[] args) {
        getVectorStatistics("corrupt");
    }

}
