package hw1.statistics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hw1.main.ConfigurationManager;
import hw1.pojos.Query;
import hw1.pojos.TermStatistics;
import hw1.ranking.vectorspacemodels.VectorSpaceModelsCalculator;
import hw1.pojos.VectorStatistics;
import hw1.restclient.RestCallHandler;
import hw2.indexing.IndexingUnit;
import hw2.search.DocumentSummaryProvider;
import hw2.search.TermSearcher;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;

import java.io.IOException;
import java.util.*;

/**
 * Created by Abhishek Mulay on 5/22/17.
 */
public class StatisticsProvider {
    private static RestCallHandler handler = new RestCallHandler();
    private static String INDEX_NAME = ConfigurationManager.getConfigurationValue("index.name");
    private static String TYPE_NAME = ConfigurationManager.getConfigurationValue("type.name");
    private final static String STATISTICS_API = "/" + INDEX_NAME + "/" + TYPE_NAME + "/_search?scroll=1m";
    private final static String scrollEndPoint = "_search/scroll";
    private final static String GET_TTF_ENDPOINT = "/" + INDEX_NAME + "/" + TYPE_NAME + "/_search?filter_path=hits.hits.fields.ttf";
    private final static double vocabularySize = Double.parseDouble(ConfigurationManager.getConfigurationValue("corpus.vocabulary.size"));
    private final static double lambda = 0.5;
    private static DocumentSummaryProvider summaryProvider = new DocumentSummaryProvider();

    // avoid instantiation
    private StatisticsProvider(){}

    // return map of <documentId, List<TermStatistics>>
    public static Map<String, List<TermStatistics>> getStatistics(String term) throws IOException {
        handler.openConnection();
        final String body = "{\n" +
                            "    \"size\" : 4000,\n" +
                            "    \"query\" : {\n" +
                            "        \"term\": {\"text\": \"" + term + "\"}\n" +
                            "    },\n" +
                            "    \"_source\": \"docLength\", \n" +
                            "    \"script_fields\" : {\n" +
                            "        \"doc_frequency\" : {\n" +
                            "            \"script\" : {\n" +
                            "              \"lang\": \"groovy\",   \n" +
                            "              \"inline\": \"_index['text']['" + term + "'].df()\"\n" +
                            "            }\n" +
                            "        },\n" +
                            "        \"term_frequency\" : {\n" +
                            "            \"script\" : {\n" +
                            "              \"lang\": \"groovy\",   \n" +
                            "              \"inline\": \"_index['text']['" + term + "'].tf()\"\n" +
                            "            }\n" +
                            "        },\n" +
                            "        \"ttf\" : {\n" +
                            "            \"script\" : {\n" +
                            "              \"lang\": \"groovy\",   \n" +
                            "              \"inline\": \"_index['text']['" + term + "'].ttf()\"\n" +
                            "            }\n" +
                            "        }\n" +
                            "    }\n" +
                            "}";

        Response response = handler.get(body, STATISTICS_API);
        String jsonString = EntityUtils.toString(response.getEntity());
        handler.closeConnection();
        return extractStatistics(term, jsonString);
    }

    public static Map<String, List<TermStatistics>> getStatisticsForAllDocuments(String term) throws IOException {
        handler.openConnection();
        final String body = "{\n" +
                            "    \"size\" : 10000,\n" +
                            "    \"query\" : {\n" +
                            "        \"match_all\": {}\n" +
                            "    },\n" +
                            "    \"_source\": \"docLength\", \n" +
                            "    \"script_fields\" : {\n" +
                            "        \"doc_frequency\" : {\n" +
                            "            \"script\" : {\n" +
                            "              \"lang\": \"groovy\",   \n" +
                            "              \"inline\": \"_index['text']['" + term + "'].df()\"\n" +
                            "            }\n" +
                            "        },\n" +
                            "        \"term_frequency\" : {\n" +
                            "            \"script\" : {\n" +
                            "              \"lang\": \"groovy\",   \n" +
                            "              \"inline\": \"_index['text']['" + term + "'].tf()\"\n" +
                            "            }\n" +
                            "        },\n" +
                            "        \"ttf\" : {\n" +
                            "            \"script\" : {\n" +
                            "              \"lang\": \"groovy\",   \n" +
                            "              \"inline\": \"_index['text']['" + term + "'].ttf()\"\n" +
                            "            }\n" +
                            "        }\n" +
                            "    }\n" +
                            "}";
        Response response = handler.get(body, STATISTICS_API);
        String jsonString = EntityUtils.toString(response.getEntity());
        handler.closeConnection();
        return extractStatistics(term, jsonString);
    }

    private static Map<String, List<TermStatistics>> extractStatistics(String term, String jsonString) {
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
        Map<String, List<TermStatistics>> docIdTermStatisticsMap = parseTermStatistics(jsonNode, term);

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
                Map<String, List<TermStatistics>> docIdTermStatsMapFromNextPage = parseTermStatistics(nextPageJsonNode, term);
                docIdTermStatisticsMap.putAll(docIdTermStatsMapFromNextPage );
            }
        }
        System.out.println("Total term statistics for '" + term + "' = " + docIdTermStatisticsMap.size());
        handler.closeConnection();
        return docIdTermStatisticsMap;
    }

    ///////////////////// Get statistics for all words in query ////////////////
    public static Map<String, List<TermStatistics>> getTermStatisticsForQuery(Query query) throws IOException {
        String cleanedQuery = query.getCleanedQuery();
        String[] terms = cleanedQuery.split(" ");

        Map<String, List<TermStatistics>> docIdTermStatisticsMap = new HashMap<>();

        for (String term : terms) {
            // get map of <docId, List[stats for terms in that docId]>
            Map<String, List<TermStatistics>> statistics = StatisticsProvider.getStatistics(term);

            // update main map with values
            for (Map.Entry<String, List<TermStatistics>> entry : statistics.entrySet()) {
                String documentId = entry.getKey();
                List<TermStatistics> statisticsForDocumentId = entry.getValue();

                if (docIdTermStatisticsMap.containsKey(documentId)) {
                    List<TermStatistics> previousTermStatistics = docIdTermStatisticsMap.get(documentId);
                    previousTermStatistics.addAll(statisticsForDocumentId);
                    docIdTermStatisticsMap.put(documentId, previousTermStatistics);
                } else {
                    docIdTermStatisticsMap.put(documentId, statisticsForDocumentId);
                }
            }
        }

        return docIdTermStatisticsMap;
    }

    public static Map<String, List<VectorStatistics>> getVectorTermStatisticsForQuery(Query query) throws IOException {
        String cleanedQuery = query.getCleanedQuery();
        String[] terms = cleanedQuery.split(" ");

        Map<String, List<VectorStatistics>> docIdVectorStatisticsMap = new HashMap<>();

        for (String term : terms) {
            // term frequency of word in this query
            int tfwq = 0;
            for (String t : terms)
                if (t.equals(term))
                    tfwq+=1;

            // get map of <docId, List[stats for terms in that docId]>
            Map<String, List<VectorStatistics>> statistics = VectorSpaceModelsCalculator.getVectorStatistics(term, tfwq);

            // update main map with values
            for (Map.Entry<String, List<VectorStatistics>> entry : statistics.entrySet()) {
                String documentId = entry.getKey();
                List<VectorStatistics> vectorStatisticsForDocumentId = entry.getValue();

                if (docIdVectorStatisticsMap.containsKey(documentId)) {
                    List<VectorStatistics> previousVectorStatistics = docIdVectorStatisticsMap.get(documentId);
                    previousVectorStatistics.addAll(vectorStatisticsForDocumentId);
                    docIdVectorStatisticsMap.put(documentId, previousVectorStatistics);
                } else {
                    docIdVectorStatisticsMap.put(documentId, vectorStatisticsForDocumentId);
                }
            }
        }

        return docIdVectorStatisticsMap;
    }

    private static Map<String, List<TermStatistics>> parseTermStatistics(JsonNode jsonNode, String term) {
        // return map of <documentId, List<TermStatistics>>
        Map<String, List<TermStatistics>> termStatisticsMap = new HashMap<>();

        if (jsonNode.has("hits")) {
            boolean hitsPresent = jsonNode.get("hits").get("hits").size() > 0;
            if (hitsPresent) {
                JsonNode hitsArray = jsonNode.get("hits").get("hits");
                for (final JsonNode hit : hitsArray) {
                    String documentId = hit.has("_id") ? hit.get("_id").asText() : "";
                    int docLength = hit.has("_source") ? hit.get("_source").get("docLength").asInt() : -1;
                    if (hit.has("fields")) {
                        int tf = hit.get("fields").has("term_frequency") ? hit.get("fields").get("term_frequency").get(0).asInt() : -1;
                        int df = hit.get("fields").has("doc_frequency") ? hit.get("fields").get("doc_frequency").get(0).asInt() : -1;
                        int ttf = hit.get("fields").has("ttf") ? hit.get("fields").get("ttf").get(0).asInt() : -1;

                        TermStatistics termStatistics = new TermStatistics(term, documentId, docLength, tf, df, ttf);
                        if (termStatisticsMap.containsKey(documentId)) {
                            List<TermStatistics> stats = termStatisticsMap.get(documentId);
                            stats.add(termStatistics);
                            termStatisticsMap.put(documentId, stats);
                        } else {
                            List<TermStatistics> newList = new ArrayList<>();
                            newList.add(termStatistics);
                            termStatisticsMap.put(documentId, newList);
                        }
                    }
                }
            }
        }
        return termStatisticsMap;
    }

    /////////////////////////////////////////////////////////////////////////
    //                Reading from Inverted Index.
    ////////////////////////////////////////////////////////////////////////

    ////// Map<documentId, List<TermStatistics>> for a query
    public static Map<String,List<TermStatistics>> getStatisticsForQueryFromIndex(Query query) throws IOException {
        String cleanedQuery = query.getCleanedQuery();
        String[] terms = cleanedQuery.split(" ");

        Map<String, List<TermStatistics>> docIdTermStatisticsMap = new HashMap<>();

        for (String term : terms) {
            // get map of <docId, List[stats for terms in that docId]>
            Map<String, List<TermStatistics>> statistics = StatisticsProvider.getStatisticsFromIndex(term);

            // update main map with values
            for (Map.Entry<String, List<TermStatistics>> entry : statistics.entrySet()) {
                String documentId = entry.getKey();
                List<TermStatistics> statisticsForDocumentId = entry.getValue();

                if (docIdTermStatisticsMap.containsKey(documentId)) {
                    List<TermStatistics> previousTermStatistics = docIdTermStatisticsMap.get(documentId);
                    previousTermStatistics.addAll(statisticsForDocumentId);
                    docIdTermStatisticsMap.put(documentId, previousTermStatistics);
                } else {
                    docIdTermStatisticsMap.put(documentId, statisticsForDocumentId);
                }
            }
        }

        return docIdTermStatisticsMap;
    }

    // Map<documentId, List<TermStatistics>> for a single word/term
    public static Map<String, List<TermStatistics>> getStatisticsFromIndex(final String term) {
        List<IndexingUnit> indexingUnits = TermSearcher.search(term);
        Map<String, List<TermStatistics>> docIdTermStatisticsMap = new HashMap<>();
        for (IndexingUnit unit : indexingUnits) {
            String documentId = unit.getDocumentId();
            int tf = unit.getTermFrequency();
            int df = unit.getDocumentFrequency();
            int ttf = unit.getTtf();
            int documentLength = summaryProvider.getDocumentLength(documentId);

            TermStatistics termStatistics = new TermStatistics(term, documentId, documentLength, tf, df, ttf);
            if (docIdTermStatisticsMap.containsKey(documentId)) {
                List<TermStatistics> previousTermStatisticsList = docIdTermStatisticsMap.get(documentId);
                previousTermStatisticsList.add(termStatistics);
                docIdTermStatisticsMap.put(documentId, previousTermStatisticsList);
            } else {
                List<TermStatistics> newList = new ArrayList<>();
                newList.add(termStatistics);
                docIdTermStatisticsMap.put(documentId, newList);
            }
        }
        return docIdTermStatisticsMap;
    }
}

