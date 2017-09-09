package hw1.ranking.vectorspacemodels;

import hw1.main.ConfigurationManager;
import hw1.pojos.TermStatistics;
import hw1.pojos.VectorStatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 5/22/17.
 */
public class OkapiTFCalculator {

    private static double averageDocumentLength = Double.parseDouble(ConfigurationManager.getConfigurationValue("average.document.length"));

    public static double okapi_tf(TermStatistics termStats) {
        int termFrequency = termStats.getTermFrequency();
        int documentLength = termStats.getDocumentFrequency();
        double okapi = termFrequency / (termFrequency + 0.5 + (1.5 * (documentLength / averageDocumentLength)));
        return okapi;
    }

    // takes in a Map of documentId, List<TermTermStatistics>
    // calculates okapi_tf for each TermStatistics object and
    // returns Map<docId, final okapi_tf> value
    public static Map<String, Double> okapi_tf(Map<String, List<TermStatistics>> docIdTermStatisticsMap) {
        Map<String, Double> docIdOkapiValuesMap = new HashMap<>();
        for (Map.Entry<String, List<TermStatistics>> entry : docIdTermStatisticsMap.entrySet()) {
            String documentId = entry.getKey();
            List<TermStatistics> termStatisticsList = entry.getValue();

            double finalOkapiScore = 0.0;
            for (TermStatistics termStats : termStatisticsList) {
                finalOkapiScore += okapi_tf(termStats);
            }
            docIdOkapiValuesMap.put(documentId, finalOkapiScore);
        }
        return docIdOkapiValuesMap;
    }

    public static Map<String, Double> okapi_tf_from_es(Map<String, List<VectorStatistics>> docIdVectorStatisticsMap) {
        Map<String, Double> docIdOkapiValuesMap = new HashMap<>();
        for (Map.Entry<String, List<VectorStatistics>> entry : docIdVectorStatisticsMap.entrySet()) {
            String documentId = entry.getKey();
            List<VectorStatistics> vectorStatisticsList = entry.getValue();

            double finalOkapiScore = 0.0;
            for (VectorStatistics vectorStats : vectorStatisticsList) {
                finalOkapiScore += vectorStats.getOkapi();
            }
            docIdOkapiValuesMap.put(documentId, finalOkapiScore);
        }
        return docIdOkapiValuesMap;
    }


}
