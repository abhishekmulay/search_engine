package hw1.ranking.languagemodels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hw1.main.ConfigurationManager;
import hw1.pojos.Query;
import hw1.pojos.TermStatistics;
import hw1.restclient.RestCallHandler;
import hw1.statistics.StatisticsProvider;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Abhishek Mulay on 5/27/17.
 */
public class UnigramWithJelinekSmoothingCalculator {
    private static double vocabularySize = Double.parseDouble(ConfigurationManager.getConfigurationValue("corpus.vocabulary.size"));
    private static RestCallHandler handler = new RestCallHandler();
    private static String INDEX_NAME = ConfigurationManager.getConfigurationValue("index.name");
    private static String TYPE_NAME = ConfigurationManager.getConfigurationValue("type.name");
    private final static double lambda = 0.6;
    private final static String GET_TTF_ENDPOINT = "/" + INDEX_NAME + "/" + TYPE_NAME + "/_search?filter_path=hits.hits.fields.ttf";

    public static Map<String, Double> applyJMModelAndGetValuesMap(Query query, Set<String> allDocIds) throws IOException {
        String cleanedQuery = query.getCleanedQuery();
        String[] terms = cleanedQuery.split(" ");

        Map<String, Double> finalJMValuesForQuery = new HashMap<>();

        for (String term : terms) {
            double ttf = getTTFforTerm(term);
            double defaultValue = Math.log((1 - lambda) * (ttf / vocabularySize));
            Map<String, Double> jmValuesForTerm = new HashMap<>();
            for (String id : allDocIds) {
                // initialize with docId and defaultValue for this term.
                jmValuesForTerm.put(id, defaultValue);
            }

            // get map of <docId, List[stats for terms in that docId]>
            Map<String, List<TermStatistics>> statistics = StatisticsProvider.getStatistics(term);

            // there will be only one match, calculate jmScore for this term.
            // add this result into term score map
            for (Map.Entry<String, List<TermStatistics>> entry : statistics.entrySet()) {
                String documentId = entry.getKey();
                List<TermStatistics> statisticsForDocumentId = entry.getValue();
                TermStatistics termStatisticsForMatchingTerm = statisticsForDocumentId.get(0);
                double jmScore = p_jm(termStatisticsForMatchingTerm);
                jmValuesForTerm.put(documentId, jmScore);
            }

            // now update the query level map with jm values for single term
            for (Map.Entry<String, Double> entry : jmValuesForTerm.entrySet()) {
                String documentId = entry.getKey();
                Double jmValue = entry.getValue();

                if (finalJMValuesForQuery.containsKey(documentId)) {
                    double previousValue = finalJMValuesForQuery.get(documentId);
                    double newValue = (previousValue + jmValue);
                    finalJMValuesForQuery.put(documentId, newValue);
                } else {
                    finalJMValuesForQuery.put(documentId, jmValue);
                }
            }

        }// term loop ends

        return finalJMValuesForQuery;
    }

    private static double getTTFforTerm(String term) {
        handler.openConnection();
        final String body = "{\n" +
                "    \"size\": 1, \n" +
                "    \"_source\": false, \n" +
                "    \"query\" : {\n" +
                "        \"term\": {\n" +
                "          \"text\": \"" + term + "\"  \n" +
                "        }\n" +
                "    },\n" +
                "    \"script_fields\" : {\n" +
                "        \"ttf\" : {\n" +
                "            \"script\" : {\n" +
                "              \"lang\": \"groovy\",   \n" +
                "              \"inline\": \"_index['text']['" + term + "'].ttf()\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}\n";

        Response response = handler.get(body, GET_TTF_ENDPOINT);
        handler.closeConnection();
        String jsonString = "";
        double ttf = 1.0;
        try {
            jsonString = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonString);
            boolean hitsPresent = jsonNode.has("hits") && jsonNode.get("hits").get("hits").size() > 0;
            if (hitsPresent) {
                JsonNode hit = jsonNode.get("hits").get("hits").get(0);
                if (hit.has("fields") && hit.get("fields").has("ttf")) {
                    ttf = hit.get("fields").get("ttf").get(0).asDouble();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ttf;
    }

    public static double p_jm(TermStatistics termStatistics) {
        double termFrequency = termStatistics.getTermFrequency() * 1.0;
        double documentLength = termStatistics.getDocumentLength() * 1.0;
        double ttf = termStatistics.getTtf() * 1.0;
//        double score = Math.log(lambda * (termFrequency / documentLength) + (1 - lambda) * (ttf / vocabularySize));
        double score = 0.001;
        return score;
    }

}
