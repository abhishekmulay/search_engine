package hw1.ranking.vectorspacemodels;

import hw1.pojos.TermStatistics;
import hw1.pojos.VectorStatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 5/24/17.
 */
public class TfIdfCalculator {

    final static int corpusSize = 84678;

    private TfIdfCalculator() {}

    public static double tfidf(TermStatistics termStatistics) {
        int docFrequency = termStatistics.getDocumentFrequency();
        double logFactor = Math.log(corpusSize/docFrequency);
        double tfidf = OkapiTFCalculator.okapi_tf(termStatistics) * logFactor;
        return tfidf;
    }

    // takes in a Map of documentId, List<TermTermStatistics>
    // calculates tfidf for each TermStatistics object and
    // returns Map<docId, final tfidf> value
    public static Map<String, Double> tfidf(Map<String, List<TermStatistics>> docIdTermStatisticsMap) {
        Map<String, Double> docIdOkapiValuesMap = new HashMap<>();
        for (Map.Entry<String, List<TermStatistics>> entry : docIdTermStatisticsMap.entrySet()) {
            String documentId = entry.getKey();
            List<TermStatistics> termStatisticsList = entry.getValue();

            double finalTfIdfScore = 0.0;
            for (TermStatistics termStats : termStatisticsList) {
                finalTfIdfScore += tfidf(termStats);
            }
            docIdOkapiValuesMap.put(documentId, finalTfIdfScore);
        }
        return docIdOkapiValuesMap;
    }



    public static Map<String, Double> tfidf_from_es(Map<String, List<VectorStatistics>> docIdVectorStatisticsMap) {
        Map<String, Double> docIdOkapiValuesMap = new HashMap<>();
        for (Map.Entry<String, List<VectorStatistics>> entry : docIdVectorStatisticsMap.entrySet()) {
            String documentId = entry.getKey();
            List<VectorStatistics> vectorStatisticsList = entry.getValue();

            double finalTfidfScore = 0.0;
            for (VectorStatistics vectorStats : vectorStatisticsList) {
                finalTfidfScore += vectorStats.getTfidf();
            }
            docIdOkapiValuesMap.put(documentId, finalTfidfScore);
        }
        return docIdOkapiValuesMap;
    }


}
