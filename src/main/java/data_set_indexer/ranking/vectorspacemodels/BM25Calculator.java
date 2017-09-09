package hw1.ranking.vectorspacemodels;

import hw1.main.ConfigurationManager;
import hw1.pojos.Query;
import hw1.pojos.TermStatistics;
import hw1.pojos.VectorStatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 5/24/17.
 */
public class BM25Calculator {

    private final static int corpusSize = 84678;
    private final static double averageDocumentLength = Double.parseDouble(ConfigurationManager.getConfigurationValue("average.document.length"));


    public static double bm25(TermStatistics termStats, Query query) {
        double k1 = 1.2;
        double k2 = 100;
        double b = 0.75;

        int docFrequency = termStats.getDocumentFrequency();
        int termFrequency = termStats.getTermFrequency();
        int documentLength = termStats.getDocumentLength();

        // finding term frequency in query
        String term = termStats.getTerm();
        String[] queryTerms = query.getCleanedQuery().split(" ");
        int termFrequencyInQuery = 0;
        for (String t : queryTerms) {
            if (t.equals(term))
                termFrequencyInQuery +=1;
        }

        // apply BM25 formula
        final double firstLogTerm = Math.log((corpusSize + 0.5) / (docFrequency + 0.5));
        final double middleTerm = (termFrequency + ( k1 * termFrequency)) / (termFrequency + (k1 * ( (1-b) + b * (documentLength/averageDocumentLength))) );
        final double lastTerm =  (termFrequencyInQuery + (k2 * termFrequencyInQuery)) / (termFrequencyInQuery + k2);

        return firstLogTerm * middleTerm * lastTerm;
    }

    public static Map<String, Double> bm25(Map<String, List<TermStatistics>> docIdTermStatisticsMap, Query query) {
        Map<String, Double> docIdBm25ValuesMap = new HashMap<>();

        for (Map.Entry<String, List<TermStatistics>> entry : docIdTermStatisticsMap.entrySet()) {
            String documentId = entry.getKey();
            List<TermStatistics> termStatisticsList = entry.getValue();

            double finalBm25Value = 0.0;
            for (TermStatistics termStats : termStatisticsList) {
                finalBm25Value += bm25(termStats, query);
            }
            docIdBm25ValuesMap.put(documentId, finalBm25Value);
        }

        return docIdBm25ValuesMap;
    }

    public static Map<String, Double> bm25_from_es(Map<String, List<VectorStatistics>> docIdVectorStatisticsMap) {
        Map<String, Double> docIdBM25ValuesMap = new HashMap<>();
        for (Map.Entry<String, List<VectorStatistics>> entry : docIdVectorStatisticsMap.entrySet()) {
            String documentId = entry.getKey();
            List<VectorStatistics> vectorStatisticsList = entry.getValue();

            double finalBm25Score = 0.0;
            for (VectorStatistics vectorStats : vectorStatisticsList) {
                finalBm25Score += vectorStats.getMb25();
            }
            docIdBM25ValuesMap.put(documentId, finalBm25Score);
        }
        return docIdBM25ValuesMap;
    }


}
