package hw2.merging;

import hw1.main.ConfigurationManager;
import hw2.indexing.CatalogEntry;
import hw2.indexing.CatalogReader;
import hw2.indexing.Indexer;
import hw2.indexreading.IndexReader;
import util.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by Abhishek Mulay on 6/5/17.
 */
public class IndexMerger {

    final static String INVERTED_INDEX_FOLDER = ConfigurationManager.getConfigurationValue("inverted.index.files.directory");
    final static String DESTINATION_FOLDER = ConfigurationManager.getConfigurationValue("completed.files.directory");
    final static String INVERTED_INDEX_TERM_SEPARATOR = ConfigurationManager.getConfigurationValue("inverted.index.term.separator");
    final static String FINAL_INVERTED_INDEX_FILE_NAME = ConfigurationManager.getConfigurationValue("final.inverted" + ".index" + ".file.name");
    final static String INVERTED_INDEX_FILE_PATH = INVERTED_INDEX_FOLDER + FINAL_INVERTED_INDEX_FILE_NAME;
    final static String FINAL_CATALOG_FILE_NAME = ConfigurationManager.getConfigurationValue("final.catalog.name");
    final static String MERGED_CATALOG_FILE_PATH = INVERTED_INDEX_FOLDER + FINAL_CATALOG_FILE_NAME;

    private static final Set<String> vocabularySet = CatalogReader.getVocabulary();

    // Takes in two catalog files and merges the inverted index files corresponding to those catalogs into a new
    // inverted index and a catalog.
    // file path needs to be complete path
//    public static void merge(final String catalogFilePath1, final String catalogFilePath2) {
//        String invertedIndexFile1 = FileUtils.getInvertedIndexFileForCatalog(catalogFilePath1);
//        String invertedIndexFile2 = FileUtils.getInvertedIndexFileForCatalog(catalogFilePath2);
//
//        int indexNumber = Indexer.INDEX_NUMBER;
//        String mergedInvertedIndexFilePath = getMergedFilePath(invertedIndexFile1, invertedIndexFile2, indexNumber+1);
//        String mergedCatalogFilePath = getMergedFilePath(catalogFilePath1, catalogFilePath2, indexNumber+1);
//        Indexer.INDEX_NUMBER += 1;
//
//        System.out.println("Merging ["+ new File(catalogFilePath1).getName() +"] and [" + new File(catalogFilePath2).getName() + "] into " + "["+ new File(mergedCatalogFilePath).getName() +"]\n");
//        // need to getCatalogAsMap catalog files into a map in memory
//        Map<String, CatalogEntry> catalogMap1 = CatalogReader.getCatalogAsMap(catalogFilePath1);
//        Map<String, CatalogEntry> catalogMap2 = CatalogReader.getCatalogAsMap(catalogFilePath2);
//
//        int position = 0;
//        int offset = 0;
//        int bytesSoFar = 0;
//
//        // catalog for newly merged index files created.
//        Map<String, CatalogEntry> mergedCatalog = new HashMap<>();
//
//        // loop over for each term
//        for (Map.Entry<String, CatalogEntry> entry1 : catalogMap1.entrySet()) {
//            StringBuilder buffer = new StringBuilder();
//            String term = entry1.getKey();
//            String lineFromIndex1 = "";
//
//            // where is this entry going to be written, bytes
//            position = bytesSoFar;
//
//            CatalogEntry catalogEntry1 = entry1.getValue();
//            lineFromIndex1 = CatalogReader.read(invertedIndexFile1, catalogEntry1.getPosition(), catalogEntry1.getOffset());
//
//            // the term is present in both catalogs
//            if (catalogMap2.containsKey(term)) {
//                CatalogEntry catalogEntry2 = catalogMap2.get(term);
//                String lineFromIndex2 = CatalogReader.read(invertedIndexFile2, catalogEntry2.getPosition(), catalogEntry2.getOffset());
//
//                String mergedEntry = mergeEntries(lineFromIndex1, lineFromIndex2);
//                buffer.append(mergedEntry);
//            } else {
//                buffer.append(lineFromIndex1);
//            }
//            // line will end with line break
//            buffer.append("\n");
//
//            bytesSoFar += buffer.length();
//            offset = bytesSoFar - position;
//            mergedCatalog.put(term, new CatalogEntry(term, position, offset));
//
//            // write to index and catalog
//            String combinedEntry = buffer.toString();
//            byte[] bytes = combinedEntry.getBytes();
//
////            FileUtils.writeBytesToFile(bytes, mergedInvertedIndexFilePath);
//            FileUtils.writeLineToFile(combinedEntry, mergedInvertedIndexFilePath);
//        }
//
//        // after these above loop, map1 will get over, there might be entries left in map2.
//        for (Map.Entry<String, CatalogEntry> entry2 : catalogMap2.entrySet()) {
//            StringBuilder buffer = new StringBuilder();
//            String term = entry2.getKey();
//            position = bytesSoFar;
//
//            // if the term is not present in mergedCatalog then add it
//            if (!mergedCatalog.containsKey(term)) {
//                CatalogEntry catalogEntry2 = entry2.getValue();
//                String lineFromIndex2 = CatalogReader.read(invertedIndexFile2, catalogEntry2.getPosition(), catalogEntry2.getOffset());
//                buffer.append(lineFromIndex2);
//                buffer.append('\n');
//
//                bytesSoFar += buffer.length();
//                offset = bytesSoFar - position;
//                mergedCatalog.put(term, new CatalogEntry(term, position, offset));
//
//                // write to index and catalog
//                String combinedEntry = buffer.toString();
//                byte[] bytes = combinedEntry.getBytes();
//
////                FileUtils.writeBytesToFile(bytes, mergedInvertedIndexFilePath);
//                FileUtils.writeLineToFile(combinedEntry, mergedInvertedIndexFilePath);
//            }
//        }
//
//        // create catalog for newly merged index
//        Indexer.createCatalogFile(mergedCatalog, mergedCatalogFilePath);
//
//        // these files are merged, move them to other folder.
//        FileUtils.copyCatalogAndIndexFilesToFolder(catalogFilePath1, catalogFilePath2, DESTINATION_FOLDER);
//    }

    public static String getMergedFilePath(String file1, String file2, int mergedFileNumber) {
        if (file1.isEmpty() || file2.isEmpty() || file1 == null || file2 == null)
            throw new IllegalArgumentException("Illegal file parameter");

        // replace extension.
        String indexFileName1 = file1.replace(INVERTED_INDEX_FOLDER, "").replace(".txt", "");
        String indexFileName2 = file2.replace(INVERTED_INDEX_FOLDER, "").replace(".txt", "");
        String mergedFilePath = null;

        // for catalogFiles
        if (indexFileName1.contains("_catalog") && indexFileName2.contains("_catalog")) {
            mergedFilePath = INVERTED_INDEX_FOLDER + mergedFileNumber + "_catalog.txt";

        // for index files
        } else {
            mergedFilePath = INVERTED_INDEX_FOLDER + mergedFileNumber + ".txt";
        }

        return mergedFilePath;
    }

    // 1) remove line breakes from both lines
    // 2) remove term from first line and get remaining record as substring
    // 3) append the record from line1 to end of line2
    public static String mergeEntries(String line1, String line2) {
        return  IndexReader.mergeEntries(line1, line2);
    }


    public static void merge() {
        // Map<CatalogFile, Map<term, CatalogEntry>
        Map<File, Map<String, CatalogEntry>> allCatalogTermEntryMap = new HashMap<>();
//        Set<String> vocabularySet = CatalogReader.getVocabulary();

        List<File> allCatalogFiles = FileUtils.getAllCatalogFiles();
        System.out.println("Merging [" + allCatalogFiles.size() + "] catalog files.");
        for (File catalogFile : allCatalogFiles) {
            Map<String, CatalogEntry> catalogAsMap = CatalogReader.getCatalogAsMap(catalogFile.getPath());
            allCatalogTermEntryMap.put(catalogFile, catalogAsMap);
        }

        Map<String, CatalogEntry> mergedCatalog = new HashMap<>();
        int position = 0, offset=0;
        // merge
        int nextPosition=0;
        for (String term : vocabularySet) {
            if (term.isEmpty() || term == null)
                continue;
            StringBuilder buffer = new StringBuilder();
            position = nextPosition;
            //   Map<CatalogFile, Map<term, CatalogEntry>
            List<String> linesForTerm = new ArrayList<>();
            for (Map.Entry<File, Map<String, CatalogEntry>> fileMapEntry : allCatalogTermEntryMap.entrySet()) {
                File catalogFile = fileMapEntry.getKey();
                String invertedIndexFilePath = FileUtils.getInvertedIndexFileForCatalog(catalogFile.getPath());
                Map<String, CatalogEntry> termCatalogEntryMap = fileMapEntry.getValue();

                if (termCatalogEntryMap.containsKey(term)) {
                    CatalogEntry catalogEntry = termCatalogEntryMap.get(term);
                    String lineFromIndex = CatalogReader.read(invertedIndexFilePath, catalogEntry.getPosition(), catalogEntry.getOffset());
                    linesForTerm.add(lineFromIndex);
                }
            }
            String mergedLine = IndexReader.getMergedLineForTerm(term, linesForTerm);
            buffer.append(mergedLine);
            buffer.append("\n");
            FileUtils.writeLineToFile(buffer.toString(), INVERTED_INDEX_FILE_PATH);

            nextPosition+=buffer.length();
            offset = nextPosition - position;
            mergedCatalog.put(term, new CatalogEntry(term, position, offset));
        }
        Indexer.createCatalogFile(mergedCatalog, MERGED_CATALOG_FILE_PATH);
    }

}
