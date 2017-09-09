package hw1.extracredit.metasearch;

import hw1.main.ConfigurationManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek Mulay on 5/29/17.
 */

//http://www.ccs.neu.edu/home/jaa/CSG339.06F/resources/condorcet.pdf

//    http://web.ics.purdue.edu/~brandeis/Condorcet_Fuse_for_Improved_Retrieval.pdf
public class CondorcetFuseRanker {

    public CondorcetFuseRanker() {
        List<QueryStats> tfidfQueryStatsList = this.getTfidfQueryStatsList();
    }

    private  List<QueryStats> readQueryResultFile(String filepath) {
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
    private  List<QueryStats> getOkapiQueryStatsList () {
        String okapiOutputFile = ConfigurationManager.getConfigurationValue("okapi.output.file");
        List<QueryStats> okapiQueryStatsList = readQueryResultFile(okapiOutputFile);
        return okapiQueryStatsList;
    }

    private  List<QueryStats> getTfidfQueryStatsList() {
        String tfIdfOutputFile = ConfigurationManager.getConfigurationValue("tfidf.output.file");
        List<QueryStats> tfidfQueryStatsList = readQueryResultFile(tfIdfOutputFile);
        return tfidfQueryStatsList;
    }

    private  List<QueryStats> getBM25QueryStatsList() {
        String bm25OutputFile = ConfigurationManager.getConfigurationValue("bm-25.output.file");
        List<QueryStats> bm25QueryStatsList = readQueryResultFile(bm25OutputFile);
        return bm25QueryStatsList;
    }

    private  List<QueryStats> getUnigramWithLaplace() {
        String unigramWithLaplaceSmoothingOutputFile = ConfigurationManager.getConfigurationValue("laplace.output.file");
        List<QueryStats> laplaceQueryStatsList = readQueryResultFile(unigramWithLaplaceSmoothingOutputFile);
        return laplaceQueryStatsList;
    }

    private  List<QueryStats> getUnigramWithJelinek() {
        String unigramWithJelinekSmoothingOutputFile = ConfigurationManager.getConfigurationValue("jelinek.output.file");
        List<QueryStats> jelinekQueryStatsList = readQueryResultFile(unigramWithJelinekSmoothingOutputFile);
        return jelinekQueryStatsList;
    }

}
