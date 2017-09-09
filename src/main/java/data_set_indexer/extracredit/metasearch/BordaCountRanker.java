package hw1.extracredit.metasearch;

import hw1.main.ConfigurationManager;
import hw1.pojos.Query;
import hw1.queryprocessor.FileQueryReader;
import hw1.queryprocessor.QueryResultWriter;
import util.MapUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 5/29/17.
 */
public class BordaCountRanker {

    // getCatalogAsMap query from result file and create List<QueryStats> which contains info about that query.
    private static List<QueryStats> readQueryResultFile(String filepath) {
        List<QueryStats> queryStatsList = new ArrayList<>();
        try {
            InputStream content = new FileInputStream(filepath);
            BufferedReader br = new BufferedReader(new InputStreamReader(content));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] terms = line.split(" ");
                String queryId = terms[0];
                String documentId = terms[2];
                String rank = terms[3];
                String score = terms[4];
                QueryStats stats  = new QueryStats(queryId, documentId, Double.parseDouble(rank), Double.parseDouble(score));
                queryStatsList.add(stats);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queryStatsList;
    }

    // model specific methods.
    private static List<QueryStats> getOkapiQueryStatsList () {
        String okapiOutputFile = ConfigurationManager.getConfigurationValue("okapi.output.file");
        List<QueryStats> okapiQueryStatsList = readQueryResultFile(okapiOutputFile);
        return okapiQueryStatsList;
    }

    private static List<QueryStats> getTfidfQueryStatsList() {
        String tfIdfOutputFile = ConfigurationManager.getConfigurationValue("tfidf.output.file");
        List<QueryStats> tfidfQueryStatsList = readQueryResultFile(tfIdfOutputFile);
        return tfidfQueryStatsList;
    }

    private static List<QueryStats> getBM25QueryStatsList() {
        String bm25OutputFile = ConfigurationManager.getConfigurationValue("bm-25.output.file");
        List<QueryStats> bm25QueryStatsList = readQueryResultFile(bm25OutputFile);
        return bm25QueryStatsList;
    }

    private static List<QueryStats> getUnigramWithLaplace() {
        String unigramWithLaplaceSmoothingOutputFile = ConfigurationManager.getConfigurationValue("laplace.output.file");
        List<QueryStats> laplaceQueryStatsList = readQueryResultFile(unigramWithLaplaceSmoothingOutputFile);
        return laplaceQueryStatsList;
    }

    private static List<QueryStats> getUnigramWithJelinek() {
        String unigramWithJelinekSmoothingOutputFile = ConfigurationManager.getConfigurationValue("jelinek.output.file");
        List<QueryStats> jelinekQueryStatsList = readQueryResultFile(unigramWithJelinekSmoothingOutputFile);
        return jelinekQueryStatsList;
    }

    // get only the queryStats for given queryid
    private static List<QueryStats> getFilteredQueryStatsListForQuery(Query query, List<QueryStats> modelQueryStatList) {
        List<QueryStats> filteredList = new ArrayList<>();
        for (QueryStats stats :  modelQueryStatList) {
            if (Integer.parseInt(stats.getQueryId()) == query.getQueryId()) {
                filteredList.add(stats);
            }
        }
        return filteredList;
    }

    // calculate Borda score
    private static Map<String,Double> calculateFinalBordaScoreMap(List<QueryStats> modelQueryStatList, Map<String, Double> finalScores) {
        final int maxScore = 1001;
        double bordaScore = 0.0;

        for (QueryStats stat: modelQueryStatList) {
            bordaScore = (maxScore - stat.getRank());

            if (finalScores.containsKey(stat.getDocumentId())) {
                double previous = finalScores.get(stat.getDocumentId());
                finalScores.put(stat.getDocumentId(), previous + bordaScore);
            } else {
                finalScores.put(stat.getDocumentId(), bordaScore);
            }
        }
        return finalScores;
    }

    ///////////////////////
    //    main
    //////////////////////
    public static void main(String[] args) {
        String bordaOutputFile = ConfigurationManager.getConfigurationValue("borda.output.file");
        File file = new File(bordaOutputFile);
        file.delete(); // delete previous file

        FileQueryReader reader = new FileQueryReader();
        List<Query> allQueries = reader.getAllQueries(FileQueryReader.QUERY_FILE_PATH);

        for (Query query : allQueries) {
            Map<String, Double> finalScores = new HashMap<>();
            List<QueryStats> filteredQueryStatsListForOkapi = getFilteredQueryStatsListForQuery(query, getOkapiQueryStatsList());
            finalScores =  calculateFinalBordaScoreMap(filteredQueryStatsListForOkapi, finalScores);

            List<QueryStats> filteredQueryStatsListForTfidf = getFilteredQueryStatsListForQuery(query, getTfidfQueryStatsList());
            finalScores =  calculateFinalBordaScoreMap(filteredQueryStatsListForTfidf, finalScores);

            List<QueryStats> filteredQueryStatsListForBM25 = getFilteredQueryStatsListForQuery(query, getBM25QueryStatsList());
            finalScores =  calculateFinalBordaScoreMap(filteredQueryStatsListForBM25, finalScores);

            List<QueryStats> filteredQueryStatsListForLaplace = getFilteredQueryStatsListForQuery(query, getUnigramWithLaplace());
            finalScores =  calculateFinalBordaScoreMap(filteredQueryStatsListForLaplace, finalScores);

            List<QueryStats> filteredQueryStatsListForJelinek = getFilteredQueryStatsListForQuery(query, getUnigramWithJelinek());
            finalScores =  calculateFinalBordaScoreMap(filteredQueryStatsListForJelinek, finalScores);

            // sort
            Map<String, Double> sortedDocIdFinalBordaValuesMap = MapUtils.sortByValue(finalScores);

            // print
//            String prettyString = MapUtils.getPrettyString(sortedDocIdFinalBordaValuesMap);
//            System.out.println(prettyString);
            QueryResultWriter.writeQueryResultToFile(query, sortedDocIdFinalBordaValuesMap, bordaOutputFile);
        }

    }
}

