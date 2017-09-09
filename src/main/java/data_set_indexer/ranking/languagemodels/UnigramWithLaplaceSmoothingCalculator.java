package hw1.ranking.languagemodels;

import hw1.main.ConfigurationManager;
import hw1.pojos.TermStatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 5/24/17.
 */
public class UnigramWithLaplaceSmoothingCalculator {

    private static double vocabularySize = Double.parseDouble(ConfigurationManager.getConfigurationValue("corpus.vocabulary.size"));

    public static double p_laplace(TermStatistics termStatistics) {
        int termFrequency = termStatistics.getTermFrequency();
        int documentLength = termStatistics.getDocumentLength();

        double score = Math.log((termFrequency + 1.0) / (documentLength + vocabularySize)) - (Math.log(1.0 / vocabularySize));
        return score;
    }

    public static Map<String, Double> lm_laplace(Map<String, List<TermStatistics>> docIdTermStatisticsMap, Map<String, Double> docIdFinalLaplaceValue) {
        Map<String, Double> docIdOkapiValuesMap = new HashMap<>();

        // update the main map with values for docs that contain term
        for (Map.Entry<String, List<TermStatistics>> entry : docIdTermStatisticsMap.entrySet()) {
            String documentId = entry.getKey();
            List<TermStatistics> termStatisticsList = entry.getValue();

            double finalUnigramWithLaplace = 0.0;
            for (TermStatistics termStats : termStatisticsList) {
                finalUnigramWithLaplace += p_laplace(termStats);
            }
            if (docIdFinalLaplaceValue.containsKey(documentId)) {
                double previousValue = docIdFinalLaplaceValue.get(documentId);
                docIdFinalLaplaceValue.put(documentId, previousValue + finalUnigramWithLaplace);
            } else {
                docIdFinalLaplaceValue.put(documentId, finalUnigramWithLaplace);
            }
        }
        return docIdFinalLaplaceValue;
    }

}
