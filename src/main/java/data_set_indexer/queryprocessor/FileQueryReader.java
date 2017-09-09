package hw1.queryprocessor;


import hw1.main.ConfigurationManager;
import hw1.pojos.Query;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek Mulay on 5/17/17.
 */
public class FileQueryReader {
    //    private final List<String> stopWords = QueryCleaner.getStopWords();
    public static final String QUERY_FILE_PATH = ConfigurationManager.getConfigurationValue("query.file.path");

    public List<Query> getAllQueries(String QUERY_FILE_PATH) {
        List<Query> queries = new ArrayList<Query>();
        try {
            FileInputStream stream = new FileInputStream(QUERY_FILE_PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));

            String line = "";
            while ((line = br.readLine()) != null) {

                if (!line.isEmpty()) {
                    int queryId = Integer.parseInt(line.substring(0, line.indexOf('.')));
                    String originalQuery = line.substring(6, line.length());
                    String cleanedQuery = cleanQuery(originalQuery);
                    queries.add(new Query(queryId, originalQuery, cleanedQuery));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queries;
    }

    private String cleanQuery(String query) {
        return QueryCleaner.analyzeString(query);
    }
}
