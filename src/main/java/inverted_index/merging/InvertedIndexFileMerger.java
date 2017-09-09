package hw2.merging;

import hw1.main.ConfigurationManager;
import hw2.indexing.CatalogEntry;
import hw2.indexing.CatalogReader;
import hw2.indexing.Indexer;
import hw2.indexreading.IndexReader;
import util.FileUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 6/5/17.
 */
public class InvertedIndexFileMerger {

    final static String INVERTED_INDEX_FOLDER = ConfigurationManager.getConfigurationValue("inverted.index.files.directory");
    final static String DESTINATION_FOLDER = ConfigurationManager.getConfigurationValue("completed.files.directory");

    // Takes in two catalog files and merges the inverted index files corresponding to those catalogs into a new
    // inverted index and a catalog.
    private static int treeLevel = 1;
    public static void merge(final String catalogFile1, final String catalogFile2) {
        System.out.println("Merging [" + catalogFile1 + "] and [" + catalogFile2 + "]");
        String invertedIndexFile1 = FileUtils.getInvertedIndexFileForCatalog(catalogFile1);
        String invertedIndexFile2 = FileUtils.getInvertedIndexFileForCatalog(catalogFile2);

        long seed = (long) Math.round(Math.random() * 100000);
        String mergedInvertedIndexFilePath = getMergedFilePath(invertedIndexFile1, invertedIndexFile2, seed, treeLevel);
        String mergedCatalogFilePath = getMergedFilePath(catalogFile1, catalogFile2, seed, treeLevel);
        treeLevel +=1;

        // need to getCatalogAsMap catalog files into a map in memory
        Map<String, CatalogEntry> catalogMap1 = CatalogReader.getCatalogAsMap(catalogFile1);
        Map<String, CatalogEntry> catalogMap2 = CatalogReader.getCatalogAsMap(catalogFile2);

        int position = 0;
        int offset = 0;
        int bytesSoFar = 0;

        // catalog for newly merged index files created.
        Map<String, CatalogEntry> mergedCatalog = new HashMap<>();

        // loop over for each term
        for (Map.Entry<String, CatalogEntry> entry1 : catalogMap1.entrySet()) {
            StringBuilder buffer = new StringBuilder();
            String term = entry1.getKey();
            String lineFromIndex1 = "";

            // where is this entry going to be written, bytes
            position = bytesSoFar;

            CatalogEntry catalogEntry1 = entry1.getValue();
            lineFromIndex1 = CatalogReader.read(invertedIndexFile1, catalogEntry1.getPosition(), catalogEntry1.getOffset());

            // the term is present in both catalogs
            if (catalogMap2.containsKey(term)) {
                CatalogEntry catalogEntry2 = catalogMap2.get(term);
                String lineFromIndex2 = CatalogReader.read(invertedIndexFile2, catalogEntry2.getPosition(), catalogEntry2.getOffset());

                String mergedEntry = mergeEntries(lineFromIndex1, lineFromIndex2);
                buffer.append(mergedEntry);
            } else {
                buffer.append(lineFromIndex1);
            }
            buffer.append("\n");

            bytesSoFar += buffer.length();
            offset = bytesSoFar - position;
            mergedCatalog.put(term, new CatalogEntry(term, position, offset));

            // write to index and catalog
            String combinedEntry = buffer.toString();
            byte[] bytes = combinedEntry.getBytes(StandardCharsets.UTF_8);

            FileUtils.writeBytesToFile(bytes, mergedInvertedIndexFilePath);
        }

        // after these two loops, map1 will get over, there might be entries left in map2.
        for (Map.Entry<String, CatalogEntry> entry2 : catalogMap2.entrySet()) {
            StringBuilder buffer = new StringBuilder();
            String term = entry2.getKey();
            position = bytesSoFar;

            // if the term is not present in mergedCatalog then add it
            if (!mergedCatalog.containsKey(term)) {
                CatalogEntry catalogEntry2 = entry2.getValue();
                String lineFromIndex2 = CatalogReader.read(invertedIndexFile2, catalogEntry2.getPosition(), catalogEntry2.getOffset());
                buffer.append(lineFromIndex2);
                buffer.append('\n');

                bytesSoFar += buffer.length();
                offset = bytesSoFar - position;
                mergedCatalog.put(term, new CatalogEntry(term, position, offset));

                // write to index and catalog
                String combinedEntry = buffer.toString();
                byte[] bytes = combinedEntry.getBytes(StandardCharsets.UTF_8);

                FileUtils.writeBytesToFile(bytes, mergedInvertedIndexFilePath);
            }
        }

        // create catalog for newly merged index
        Indexer.createCatalogFile(mergedCatalog, mergedCatalogFilePath);

        // these files are merged, move them to other folder.
        FileUtils.copyCatalogAndIndexFilesToFolder(catalogFile1, catalogFile2, DESTINATION_FOLDER);
    }

    public static String getMergedFilePath(String file1, String file2, long seed, int treeLevel) {
        if (file1.isEmpty() || file2.isEmpty() || file1 == null || file2 == null)
            throw new IllegalArgumentException("Illegal file parameter");

        String indexFileName = file1.replace(INVERTED_INDEX_FOLDER, "").replace(".txt", "");
        String indexFileName2 = file2.replace(INVERTED_INDEX_FOLDER, "").replace(".txt", "");

        String mergedFilePath = INVERTED_INDEX_FOLDER + "v_"+ (treeLevel) + "_" + seed;
        if (indexFileName.contains("_catalog")) {
            mergedFilePath += "_catalog";
        }

        mergedFilePath += ".txt";
        return mergedFilePath;
    }

    // 1) remove line breakes from both lines
    // 2) remove term from first line and get remaining record as substring
    // 3) append the record from line1 to end of line2
    public static String mergeEntries(String line1, String line2) {
        return  IndexReader.mergeEntries(line1, line2);
    }
}
