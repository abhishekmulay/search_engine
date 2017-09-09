package hw1.extracredit.pseudorelevancefeedback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hw1.main.ConfigurationManager;
import hw1.pojos.Query;
import hw1.queryprocessor.FileQueryReader;
import hw1.restclient.RestCallHandler;
import util.MapUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 5/30/17.
 */
public class EC2 {

    private static RestCallHandler handler = new RestCallHandler();
    private static final String INDEX_NAME = ConfigurationManager.getConfigurationValue("index.name");
    private static final String TYPE_NAME = ConfigurationManager.getConfigurationValue("type.name");
    private static final String SIGNIFICANT_TERMS_API = "/" + INDEX_NAME + "/" + TYPE_NAME + "/_search?";

    public static Map<String, List<String>> getSignificantTerms(final String term) {
        final String body =
                "{\n" +
                "    \"query\" : {\n" +
                "        \"terms\" : {\"text\" : [ \"" + term + "\" ]}\n" +
                "    },\n" +
                "    \"aggregations\" : {\n" +
                "        \"significantTerms\" : {\n" +
                "            \"significant_terms\" : {\n" +
                "              \"field\" : \"text\"   \n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"script_fields\" : {\n" +
                "        \"idf\" : {\n" +
                "            \"script\" : {\n" +
                "              \"lang\": \"groovy\",   \n" +
                "              \"inline\": \"double df = _index['text'][word].df(); double idf = Math.log(84678/df); return idf;\",\n" +
                "              \"params\": {\n" +
                "                \"word\" : \""+term+"\"  \n" +
                "              }\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"size\": 1,\n" +
                "    \"_source\": false\n" +
                "}\n" +
                "\n";

        handler.openConnection();
        Response response = handler.get(body, SIGNIFICANT_TERMS_API);
        handler.closeConnection();
        Map<String, List<String>> significantTermsMap = null;
        try {
            String jsonString = EntityUtils.toString(response.getEntity());
            significantTermsMap  = extractSignificantTerms(term, jsonString);
//            System.out.println("\t\t\t" + term + "\tsignificantTerms = " + significantTermsMap.get(term));
//            System.out.println(significantTermsMap);
//            double idf = extractIDF(term, jsonString);
//            System.out.println("IDF " + idf);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return significantTermsMap;
    }

    private static double extractIDF(String term, String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        double idf = -1.0;
        try {
            jsonNode = mapper.readTree(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean hitsPresent = jsonNode.has("hits") && jsonNode.get("hits").has("hits") && jsonNode.get("hits").get("hits").isArray();
        if (hitsPresent) {
            JsonNode hitsArray = jsonNode.get("hits").get("hits");
            if (hitsArray.size() > 0) {
                idf = hitsArray.get(0).get("fields").get("idf").get(0).asDouble();
            }
        }

        return idf;
    }

    private static Map<String, List<String>> extractSignificantTerms(String term, String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        List<String> significantTerms = new ArrayList<>();
        try {
            jsonNode = mapper.readTree(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean bucketsPresent = jsonNode.has("aggregations") &&
                                jsonNode.get("aggregations").has("significantTerms") &&
                                jsonNode.get("aggregations").get("significantTerms").has("buckets");

        if (bucketsPresent) {
            JsonNode buckets = jsonNode.get("aggregations").get("significantTerms").get("buckets");
            for (JsonNode bucket : buckets) {
                String significantTerm = bucket.get("key").asText();
                significantTerms.add(significantTerm);
            }
        }
        Map<String, List<String>> significantTermsMap = new HashMap<>();
        significantTermsMap.put(term, significantTerms);
        return significantTermsMap;
    }

    public static void getSignificantTermsForQuery(Query query) {
        String[] terms = query.getCleanedQuery().split(" ");
        System.out.println( "\n\n"+ query.getQueryId() + "\t" + query.getCleanedQuery());

        List<String> significantTermsForQuery = new ArrayList<>();
        for (String term : terms) {
            Map<String, List<String>> significantTermsForTermMap = getSignificantTerms(term);
            significantTermsForQuery.addAll(significantTermsForTermMap.get(term));
        }

        // create map of <significantTerm, its count in significant terms for each word in query>
        Map<String, Integer> significantTermsCounterMap = new HashMap<>();
        for (String word : significantTermsForQuery) {
            if (significantTermsCounterMap.containsKey(word)) {
                Integer previousCount = significantTermsCounterMap.get(word);
                significantTermsCounterMap.put(word, previousCount + 1);
            } else {
                significantTermsCounterMap.put(word, 1);
            }
        }

        // get top 5 words
        String topRelevantWords = "";
        int counter = 0;
        for (Map.Entry<String, Integer> entry : MapUtils.sortByValue(significantTermsCounterMap).entrySet()) {
            if (counter >= 5) { break;}
            String word = entry.getKey();
            topRelevantWords +=  word + ",";
            counter++;
        }

        System.out.println("\n" + topRelevantWords);
    }

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(new File("output.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileQueryReader reader = new FileQueryReader();
        List<Query> allQueries = reader.getAllQueries(FileQueryReader.QUERY_FILE_PATH);
//        for (Query query : allQueries) {
//            getSignificantTermsForQuery(query);
//        }

        getSignificantTermsForQuery(allQueries.get(0));
    }

}
