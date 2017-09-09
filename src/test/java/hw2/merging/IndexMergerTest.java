package hw2.merging;

import hw1.main.ConfigurationManager;
import hw2.indexing.CatalogEntry;
import hw2.indexing.CatalogReader;
import hw2.indexreading.IndexReader;
import junit.framework.TestCase;
import org.junit.Assert;
import org.omg.CORBA.INV_FLAG;
import org.omg.PortableInterceptor.INACTIVE;
import util.MapUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 6/5/17.
 */
public class IndexMergerTest extends TestCase {
    final String INVERTED_INDEX_FOLDER = ConfigurationManager.getConfigurationValue("inverted.index.files.directory");

    public void testReadFromCatalog() {
//        salary:1161:708
        final String term = "salary";
        final int position = 1161;
        final int offset = 708;

        final String expected = "salary=AP890106-0025:2.0:-1.0:-1.0:[388.0, 433.0];AP890107-0128:2.0:-1.0:-1.0:[268.0, 302.0];AP890104-0268:2.0:-1.0:-1.0:[36.0, 73.0];AP890106-0202:1.0:-1.0:-1.0:[344.0];AP890107-0017:1.0:-1.0:-1.0:[146.0];AP890107-0002:1.0:-1.0:-1.0:[373.0];AP890107-0112:1.0:-1.0:-1.0:[62.0];AP890106-0096:5.0:-1.0:-1.0:[105.0, 453.0, 530.0, 747.0, 854.0];AP890106-0253:1.0:-1.0:-1.0:[362.0];AP890102-0062:1.0:-1.0:-1.0:[715.0];AP890102-0094:1.0:-1.0:-1.0:[128.0];AP890103-0214:1.0:-1.0:-1.0:[278.0];AP890101-0062:1.0:-1.0:-1.0:[66.0];AP890102-0017:2.0:-1.0:-1.0:[190.0, 207.0];AP890103-0125:1.0:-1.0:-1.0:[600.0];AP890103-0191:1.0:-1.0:-1.0:[263.0];AP890102-0145:1.0:-1.0:-1.0:[625.0];AP890103-0107:1.0:-1.0:-1.0:[562.0];";

        final String INVERTED_INDEX_FILE_PATH = INVERTED_INDEX_FOLDER + "/1_2.txt";
        String line = CatalogReader.read(INVERTED_INDEX_FILE_PATH, position, offset);
        Assert.assertEquals(expected, line);
    }

    // returns line2 + record1
    public void testMergeEntries1() {
        // both lines have separator at the end
        final String line1 = "rival=AP890103-0122:1:-1:-1:[303];AP890102-0006:3:-1:-1:[476, 123, 333];";
        final String line2 = "rival=AP890105-0084:2:-1:-1:[24, 50];AP890105-0214:5:-1:-1:[18, 10, 12, 12, 15];";
        final String expected = "rival=AP890105-0214:5:-1:-1:[18, 10, 12, 12, 15];" +
                                      "AP890102-0006:3:-1:-1:[476, 123, 333];" +
                                      "AP890105-0084:2:-1:-1:[24, 50];" +
                                      "AP890103-0122:1:-1:-1:[303]";

        String actual = IndexMerger.mergeEntries(line1, line2);
        Assert.assertEquals(expected, actual);

//        // when line1 has only one record
//        final String line1_1 = "rival=AP890103-0122:1.0:-1.0:-1.0:[303.0]";
//        final String line2_1 = "rival=AP890105-0084:1.0:-1.0:-1.0:[24.0];AP890105-0214:1.0:-1.0:-1.0:[18.0];";
//        final String expected_1 = "rival=AP890105-0084:1:-1:-1:[24];AP890105-0214:1:-1:-1:[18];AP890103-0122:1:-1:-1:[303];";
//
//        String actual_1 = IndexMerger.mergeEntries(line1_1, line2_1);
//        Assert.assertEquals(expected_1, actual_1);
//
//        // when line2 does not have a separator
//        final String line1_2 = "rival=AP890103-0122:1:-1:-1:[303];";
//        final String line2_2 = "rival=AP890105-0084:1:-1:-1:[24]";
//        final String expected_2 =  "rival=AP890105-0084:1:-1:-1:[24];AP890103-0122:1:-1:-1:[303];";
//
//        String actual_2 = IndexMerger.mergeEntries(line1_2, line2_2);
//        Assert.assertEquals(expected_2, actual_2);
    }

    public void testMergeEntries() {
        final String line1 = "rival=AP890102-0006:3:-1:-1:[476, 123, 333];AP890103-0122:1:-1:-1:[303];";
        final String line2 = "rival=AP890105-0214:5:-1:-1:[18, 10, 12, 12, 15];AP890105-0084:2:-1:-1:[24, 50];";

        final String expected =   "rival=AP890105-0214:5:-1:-1:[18, 10, 12, 12, 15];" +
                                        "AP890102-0006:3:-1:-1:[476, 123, 333];" +
                                        "AP890105-0084:2:-1:-1:[24, 50];" +
                                        "AP890103-0122:1:-1:-1:[303];";

        System.out.println(IndexReader.parseIndexEntry(line1));
        System.out.println(IndexReader.mergeEntries(line1, line2));

        Assert.assertEquals(expected, IndexReader.mergeEntries(line1, line2));
    }

    public void testDupesInIndex() {
        String finalIndexFile = INVERTED_INDEX_FOLDER + "index.txt";
        Map<String, Integer> wordLineNoMap = new HashMap<>();
        try {
            InputStream content = new FileInputStream(finalIndexFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(content));
            String line = "";
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                String term = line.substring(0, line.indexOf("="));
                if (wordLineNoMap.containsKey(term)) {
                    System.out.println("[" + term + "] is duplicate at line: " + lineNumber);
                }
                ++lineNumber;
                wordLineNoMap.put(term, lineNumber);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(MapUtils.getPrettyString(wordLineNoMap));
    }

}