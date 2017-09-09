package hw3;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Abhishek Mulay on 6/24/17.
 */
public class URLPriorityCalculator {


//    {"nuclear", "saftey", "earthquake", "11 march 2011", "nuclear power", "tepco",
//            "nuclear event scale", "fukushima", "reactor", "zirconium", "radiation", "cancer", "zircaloy",
//            "bwr", "tsnami", "evacuation","core meltdown", "contamination", "death", "fission",
//            "fission reaction", "control rods", "scram", "decay heat", "radiation", "daiichi"};


    private static String[] mostRelevantWords = {"nuclear", "fukushima", "radiation", "fission"};

    private static String[] relevantWords = {"tepco", "zirconium", "cancer", "zircaloy", "bwr", "tsnami", "evacuation", "meltdown", "control rods", "decay heat"};

    private static List<String> mostRelevantWordList = Arrays.asList(mostRelevantWords);
    private static List<String> relevantWordList = Arrays.asList(relevantWords);

    public static int calculatePriority(String titleKeywords) {
        int score = 1;
        String[] keywords = titleKeywords.split("[\\s,_:\\-;]+");

        for (String word : keywords) {
            if (word != null && !word.isEmpty()) {
                String token = word.toLowerCase();
                if (mostRelevantWordList.contains(token)) {
                    score *= 100;
                }

                if (relevantWordList.contains(token)) {
                    score *= 10;
                }
            }
        }

        return score;
    }

}
