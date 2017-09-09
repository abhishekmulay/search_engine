package hw1.ranking.proximitymodel;

import hw1.main.ConfigurationManager;
import hw1.pojos.TermStatistics;
import hw2.search.ProximitySearcher;
import hw2.search.TermSearcher;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 6/14/17.
 */
public class ProximityScoreCalculator {

    private static final double C = 1500.0;
    private static final double vocabularySize = Double.parseDouble(ConfigurationManager.getConfigurationValue("corpus" + ".vocabulary.size"));

    public static Map<String, Double> getProximityScoreMap(Map<String, List<TermStatistics>> docIdTermStatisticsMap) {
            Map<String, Double> docIdFinalValuesMap = new HashMap<>();
            docIdTermStatisticsMap.entrySet().forEach(stringListEntry -> {
                String documentId = stringListEntry.getKey();
                List<TermStatistics> termStatisticsList = stringListEntry.getValue();
                double score = getProximityScore(termStatisticsList);
                docIdFinalValuesMap.put(documentId, score);
            });
            return docIdFinalValuesMap;
    }

    private static double getProximityScore(List<TermStatistics> termStatisticsList) {
        List<List<Integer>> listOfPositionList = new LinkedList<>();

        for (TermStatistics statistics : termStatisticsList) {
            listOfPositionList.add(statistics.getPositions());
        }

        int minimumSpan = ProximitySearcher.getMinimumSpan(listOfPositionList);
        double score = (C - minimumSpan) * (termStatisticsList.size() / vocabularySize);
        return score;
    }

}
