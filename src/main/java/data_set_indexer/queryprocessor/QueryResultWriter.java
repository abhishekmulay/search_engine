package hw1.queryprocessor;

import hw1.pojos.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 5/23/17.
 */
public class QueryResultWriter {

    public static void writeQueryResultToFile(Query query, Map<String, Double> docIdScoreMap, String outputFile) {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(new File(outputFile), true));
            int queryNumber = query.getQueryId();
            int rank = 0;
            int maxNumberOfEntriesToWrite = Integer.MAX_VALUE;
            int count = 0;
            String documentId = "";
            Double score = 0.0;
            System.out.println("\n\nWritting " + docIdScoreMap.size() + " lines for query id = [" + query.getQueryId() + "]\n\n");
            for (Map.Entry<String, Double> entry : docIdScoreMap.entrySet()) {
                documentId = entry.getKey();
                score = entry.getValue();
                rank += 1;
//                if (count == maxNumberOfEntriesToWrite) {
//                    break;
//                }

                // <query-number> Q0 <docno> <rank> <score> Exp
                writer.println(queryNumber + " Q0 " + documentId + " " + rank + " " + score + " Exp");
                count += 1;
            }
            writer.print("\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
