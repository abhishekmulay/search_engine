package hw2.indexing;

import hw1.indexing.datareader.DataReader;

import hw1.indexing.datareader.DocumentSummary;
import hw1.indexing.datareader.TextSanitizer;
import hw1.main.ConfigurationManager;
import hw1.pojos.HW1Model;
import hw2.search.DocumentSummaryProvider;
import util.FileUtils;
import util.ListUtils;
import util.MapUtils;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by Abhishek Mulay on 6/2/17.
 */
public class Indexer {

    final static String INVERTED_INDEX_FOLDER = ConfigurationManager.getConfigurationValue("inverted.index.files.directory");
    final static String COMPLETED_FOLDER = ConfigurationManager.getConfigurationValue("completed.files.directory");
    final static boolean STEMMING_ENABLED = Boolean.parseBoolean(ConfigurationManager.getConfigurationValue("stemming.enabled"));
    private static DocumentSummaryProvider summaryProvider = new DocumentSummaryProvider();

    // get term frequency for given word in given text
    public static int getTermFrequencyinText(final String term, final String[] tokens) {
        int counter = 0;
        for (final String token : tokens) {
            if (token.equals(term))
                counter += 1;
        }
        return counter;
    }

    // get positions of term in given text
    private static List<Integer> getTermPositionInText(final String term, final String[] tokens) {
        int counter = 0;
        List<Integer> positions = new ArrayList<>();
        for (final String token : tokens) {
            if (token.equals(term))
                positions.add(counter);
            counter += 1;
        }
        return positions;
    }

    // for a HW1Model return a map of  Map<term, Map<docId, IndexingUnit>>
    public static Map<String, Map<String, IndexingUnit>> getTermDocIdIndexingUnitMapForModel(final HW1Model model, Map<String, Map<String, IndexingUnit>> termDocIdIndexingUnitMap) {
        String documentId = model.getDocno();
        int docIdMappingNumber = summaryProvider.getDocIdMappingNumber(documentId);
        String text = model.getText();
        // remove stop-words and tokenize
        String[] tokens = TextSanitizer.removeStopWords(text, STEMMING_ENABLED);

        for (String term : tokens) {
            if (!term.isEmpty()) {
                int tf = getTermFrequencyinText(term, tokens);
                List<Integer> termPositionInText = getTermPositionInText(term, tokens);
                int ttf = tf, df = 1;
                IndexingUnit indexingUnit = new IndexingUnit(term, documentId, docIdMappingNumber, tf, termPositionInText, ttf, df);

                if (termDocIdIndexingUnitMap.containsKey(term)) {
                    Map<String, IndexingUnit> previosuDocIdIndexingUnitMap = termDocIdIndexingUnitMap.get(term);
                    previosuDocIdIndexingUnitMap.put(documentId, indexingUnit);
                    termDocIdIndexingUnitMap.put(term, MapUtils.sortByTF(previosuDocIdIndexingUnitMap));

                } else {
                    Map<String, IndexingUnit> newDocIdIndexingUnitMap = new TreeMap<>();
                    newDocIdIndexingUnitMap.put(documentId, indexingUnit);
                    termDocIdIndexingUnitMap.put(term, newDocIdIndexingUnitMap);
                }
            }
        }
        // update df and ttf for records so far
        termDocIdIndexingUnitMap = updateDocIdIndexingUnitMapValues(termDocIdIndexingUnitMap);
        return termDocIdIndexingUnitMap;
    }

    // this method will increment the tf, ttf and df counts for records
    private static Map<String, Map<String, IndexingUnit>> updateDocIdIndexingUnitMapValues(Map<String, Map<String, IndexingUnit>> termDocIdIndexingUnitMap) {
        int ttf = 0;
        int df = 0;
        for (Map.Entry<String, Map<String, IndexingUnit>> termMapEntry : termDocIdIndexingUnitMap.entrySet()) {
            String term = termMapEntry.getKey();

            ttf = 0;
            df = 0;
            for (Map.Entry<String, IndexingUnit> docIdIndexingUnitEntry : termDocIdIndexingUnitMap.get(term).entrySet()) {
                IndexingUnit unit = docIdIndexingUnitEntry.getValue();
                ttf += unit.getTermFrequency();
            }
            df = termDocIdIndexingUnitMap.get(term).size();

            for (Map.Entry<String, IndexingUnit> docIdIndexingUnitEntry : termDocIdIndexingUnitMap.get(term).entrySet()) {
                IndexingUnit unit = docIdIndexingUnitEntry.getValue();
                unit.setTtf(ttf);
                unit.setDocumentFrequency(df);
            }
        }
        return termDocIdIndexingUnitMap;
    }
    // chops a list into non-view sublists of length chunkSize
    static <T> List<List<T>> splitIntoChunks(List<T> list, final int chunkSize) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += chunkSize) {
            parts.add(new ArrayList<T>(list.subList(i, Math.min(N, i + chunkSize))));
        }
        return parts;
    }

    /////////////////////////////////////////////////////////////////////////////

    public static void writeTermDocIdIndexingUnitMapToFile(Map<String, Map<String, IndexingUnit>> docIdTermIndexingUnitMap, String invertedIndexFilePath, String catalogFilePath) {
        StringBuffer buffer = new StringBuffer();
        Map<String, CatalogEntry> catalogEntryMap = new HashMap<>();
        int position = 0;
        int offset = 0;

        // iterate over all terms
        for (Map.Entry<String, Map<String, IndexingUnit>> entry : docIdTermIndexingUnitMap.entrySet()) {
            String term = entry.getKey();
            Map<String, IndexingUnit> docIdIndexingUnitMap = entry.getValue();
            // how many bytes in file
            position = buffer.length();

            buffer.append(term).append('=');

            // iterate over map <docId, IndexingUnit> for the term
            for (Map.Entry<String, IndexingUnit> docIdIndexingUnitEntry : docIdIndexingUnitMap.entrySet()) {
                String documentId = docIdIndexingUnitEntry.getKey();
                IndexingUnit indexingUnit = docIdIndexingUnitEntry.getValue();

                int tf = indexingUnit.getTermFrequency();
                List<Integer> positionList = indexingUnit.getPosition();
                String positions = ListUtils.toCompactString(positionList);
                int df = 1;
                int ttf = tf;

                int docIdMappingNumber = summaryProvider.getDocIdMappingNumber(documentId);
                // term=docIdMappingNumber:tf1:df1:ttf1:[pos1, pos2, pos3];docId2:tf2:df2:ttf2:[pos1, pos2, pos3]
                buffer.append(docIdMappingNumber).append(':').append(tf).append(':').append(df).append(':').append(ttf).append(':').append(positions).append(';');
            }
            //add new line after every term entry
            buffer.append('\n');

            offset = buffer.length() - position;

            CatalogEntry catalogEntry = new CatalogEntry(term, position, offset);
            catalogEntryMap.put(term, catalogEntry);
        }
        String allTermsFromThisFileSet = buffer.toString();
        byte[] bytes = allTermsFromThisFileSet.getBytes(StandardCharsets.UTF_8);


        //save bytes[] into file
        FileUtils.writeBytesToFile(bytes, invertedIndexFilePath);
        // add entry for all terms in catalog
        createCatalogFile(catalogEntryMap, catalogFilePath);
    }

    public static void createCatalogFile(Map<String, CatalogEntry> catalogEntryMap, String catalogFilePath) {
        try {
            File file = new File(catalogFilePath);
            // create if file does not exist.
            if (!file.exists())
                file.createNewFile();

            StringBuffer buffer = new StringBuffer();
            for (Map.Entry<String, CatalogEntry> termCatalogEntryEntry : catalogEntryMap.entrySet()) {
                CatalogEntry catalogEntry = termCatalogEntryEntry.getValue();
                buffer.append(catalogEntry.getTerm()).append(':').append(catalogEntry.getPosition()).append(':').append(catalogEntry.getOffset()).append('\n');
            }
            String catalogEntries = buffer.toString();
            // if file is already present, append to file.
            FileOutputStream stream = new FileOutputStream(catalogFilePath, true);

            byte[] bytes = catalogEntries.getBytes(StandardCharsets.UTF_8);
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllInvertedIndexAndCatalogFiles() {
        File dir = new File(INVERTED_INDEX_FOLDER);
        File[] files = dir.listFiles();
        System.out.println("Deleting [" + files.length + "] inverted index files in " + INVERTED_INDEX_FOLDER);
        for (File file : files)
            if (!file.isDirectory())
                file.delete();


        File folder = new File(COMPLETED_FOLDER);
        File[] completedfiles = folder.listFiles();
        System.out.println("Deleting [" + completedfiles.length + "] completed files in " + COMPLETED_FOLDER);
        for (File file : completedfiles)
            if (!file.isDirectory())
                file.delete();
    }

    ///////////////////////////////////////////////////////////////////////////
    //     Writing Map<term, Map<docId, IndexingUnit>> to InvertedIndexFile   //

    //main method
    public static void runIndex(String dataPath, int fileChunkSize) throws IOException {
        DataReader reader = new DataReader();
        // delete previous inverted index files before creating new index.
        deleteAllInvertedIndexAndCatalogFiles();
        ArrayList<File> dataFiles = reader.getAllDataFiles(dataPath);
        // break the entire dataset files into group of chunkSize, each file has around 300 documents.
        List<List<File>> partsOfDataFiles = splitIntoChunks(dataFiles, fileChunkSize);

        int fileNameCounter = 0;
        for (List<File> listOfFiles : partsOfDataFiles) {
            // for file name: ex. INVERTED_INDEX_FOLDER/1.txt
            fileNameCounter += 1;

            // <term, <docId, IndexingUnit>
            Map<String, Map<String, IndexingUnit>> termDocIdIndexingUnitMap = new HashMap<>();
            int documentsProcessedInChunk = 0;
            // getCatalogAsMap chunkSize files at once.
            for (File dataFile : listOfFiles) {
                List<HW1Model> models = reader.readFileIntoModel(dataFile);

                documentsProcessedInChunk += models.size();
                System.out.println("Reading [" + models.size() + "] documents from file [" + dataFile.getName() + "]");

                // getCatalogAsMap all document models from one single file.
                for (HW1Model model : models) {
                    Map<String, Map<String, IndexingUnit>> termDocIdIndexingUnitMapForModel = getTermDocIdIndexingUnitMapForModel(model, termDocIdIndexingUnitMap);
                    termDocIdIndexingUnitMap.putAll(termDocIdIndexingUnitMapForModel);
                }
            }

            // write result of chunkSize files Map
            String invertedIndexFilePath = INVERTED_INDEX_FOLDER + fileNameCounter + ".txt";
            String catalogFilePath = INVERTED_INDEX_FOLDER + fileNameCounter + "_catalog.txt";
            writeTermDocIdIndexingUnitMapToFile(termDocIdIndexingUnitMap, invertedIndexFilePath, catalogFilePath);

            System.out.println("[Map<term, Map<docId, IndexingUnit>>] total entries = [" + termDocIdIndexingUnitMap.size() + "] documents processed [" + documentsProcessedInChunk + "]\n");
        }
    }

}
