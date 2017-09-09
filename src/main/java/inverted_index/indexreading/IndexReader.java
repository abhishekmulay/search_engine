package hw2.indexreading;

import hw1.main.ConfigurationManager;
import hw2.indexing.CatalogEntry;
import hw2.indexing.CatalogReader;
import hw2.indexing.IndexingUnit;
import hw2.search.DocumentSummaryProvider;
import util.FileUtils;
import util.ListUtils;

import java.io.File;
import java.util.*;

/**
 * Created by Abhishek Mulay on 6/7/17.
 */
public class IndexReader {

    private File invertedIndexFile;
    private File catalogFile;
    final static String INVERTED_INDEX_FOLDER = ConfigurationManager.getConfigurationValue("inverted.index.files.directory");
    final static String INVERTED_INDEX_SEPARATOR = ConfigurationManager.getConfigurationValue("inverted.index.file.record.separator");
    private static DocumentSummaryProvider summaryProvider = new DocumentSummaryProvider();

    public IndexReader(String catalogFilePath) {
        this.invertedIndexFile = new File(FileUtils.getInvertedIndexFileForCatalog(catalogFilePath));
        this.catalogFile = new File(catalogFilePath);
    }

    public List<IndexingUnit> get(String term) {
        Map<String, CatalogEntry> catalog = CatalogReader.getCatalogAsMap(this.catalogFile.getPath());
        if (!catalog.containsKey(term)) {
            System.out.println("Term [" + term + "] is not present in data set.");
            return null;
        }
        CatalogEntry catalogEntry = catalog.get(term);
        int position = catalogEntry.getPosition();
        int offset = catalogEntry.getOffset();

        String record = CatalogReader.read(this.invertedIndexFile.getPath(), position, offset);
        Map<String, List<IndexingUnit>> stringListMap = parseIndexEntry(record);
        return stringListMap.get(term);
    }


//    naturalized=AP890103-0105:1:-1:-1:[239];AP890103-0176:1:-1:-1:[142];
    public static Map<String, List<IndexingUnit>> parseIndexEntry(String entry) {
//        System.out.println(entry);
        String term = entry.substring(0, entry.indexOf("="));
        String docBlocksString = entry.substring(entry.indexOf("=")+1, entry.length());
        String[] docBlocks = docBlocksString.split(INVERTED_INDEX_SEPARATOR);
        Map<String, List<IndexingUnit>> termIndexingUnitListMap = new HashMap<>();

        List<IndexingUnit> indexingUnitList = new ArrayList<>();
        for (String block : docBlocks) {
            String[] indexingUnitParts = block.split(":");

            int docIdMappingNumber = Integer.parseInt(indexingUnitParts[0]);
            String documentId = summaryProvider.getOriginalDocumentId(docIdMappingNumber);

            int termFrequency = Integer.parseInt(indexingUnitParts[1]);
            int documentFrequency = Integer.parseInt(indexingUnitParts[2]);
            int ttf = Integer.parseInt(indexingUnitParts[3]);

            List<Integer> asList = new ArrayList<>();
            int[] positionArray = ListUtils.fromString(indexingUnitParts[4]);
            for (int i : positionArray) {
                asList.add(i);
            }

            indexingUnitList.add(new IndexingUnit(term, documentId, docIdMappingNumber, termFrequency, asList, ttf, documentFrequency));
        }
        termIndexingUnitListMap.put(term, indexingUnitList);
        return termIndexingUnitListMap;
    }

    // line should be of like => cancel=AP890103-0105:1:-1:-1:[239];AP890103-0176:1:-1:-1:[142];
    public static String mergeEntries(String line1, String line2) {
        line1 = line1.replace("\n", "").replace("\r", "").trim();
        line2 = line2.replace("\n", "").replace("\r", "").trim();

        String term = line1.substring(0, line1.indexOf("="));
        Map<String, List<IndexingUnit>> parsedTermIndexingUnitMap1 = parseIndexEntry(line1);
        Map<String, List<IndexingUnit>> parsedTermIndexingUnitMap2 = parseIndexEntry(line2);

        List<IndexingUnit> mergedRecords = mergeIndexingUnitsList(parsedTermIndexingUnitMap1.get(term), parsedTermIndexingUnitMap2.get(term));
        // update df and ttf of records.
        mergedRecords = updateRecordValues(mergedRecords);
        // convert into string representation
        String mergedLine = IndexingUnit.toWritableString(term, mergedRecords);
        return mergedLine;
    }

    private static List<IndexingUnit> updateRecordValues(List<IndexingUnit> mergedRecords) {
        final int df = mergedRecords.size();
        int ttf = 0;
        for (IndexingUnit unit : mergedRecords) {
            ttf += unit.getTermFrequency();
        }
        for (IndexingUnit unit : mergedRecords) {
            unit.setTtf(ttf);
            unit.setDocumentFrequency(df);
        }
        return mergedRecords;
    }

    private static List<IndexingUnit> mergeIndexingUnitsList(List<IndexingUnit> list1, List<IndexingUnit> list2) {
        List<IndexingUnit> mergedIndexingUnitsList = new ArrayList<>();

        int i =0, j =0;
        int list1Length = list1.size();
        int list2Length = list2.size();

        while (i < list1Length && j < list2Length) {
            IndexingUnit item1 = list1.get(i);
            IndexingUnit item2 = list2.get(j);
            if (item1.getTermFrequency() > item2.getTermFrequency()) {
                mergedIndexingUnitsList.add(item1);
                i++;
            } else {
                mergedIndexingUnitsList.add(item2);
                j++;
            }
        }

        if (i < list1Length) {
            while (i < list1Length) {
                mergedIndexingUnitsList.add(list1.get(i));
                i++;
            }
        }

        if (j < list2Length) {
            while (j < list2Length) {
                mergedIndexingUnitsList.add(list2.get(j));
                j++;
            }
        }

        return mergedIndexingUnitsList;
    }

    public static String getMergedLineForTerm(String term, List<String> linesForTerm) {
        return null;
    }
}
