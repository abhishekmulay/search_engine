package hw2;

import hw1.main.ConfigurationManager;
import hw2.indexing.Indexer;

import hw2.merging.BulkMerger;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Abhishek Mulay on 6/7/17.
 */
public class App {


    final static boolean STEMMING_ENABLED = Boolean.parseBoolean(ConfigurationManager.getConfigurationValue("stemming.enabled"));
    final static String INVERTED_INDEX_FOLDER = ConfigurationManager.getConfigurationValue("inverted.index.files.directory");
    final static int FILE_CHUNK_SIZE = Integer.parseInt(ConfigurationManager.getConfigurationValue("indexing.data.file.chunk.size"));
    final static String DATA_PATH = ConfigurationManager.getConfigurationValue("data.set.path");
    final static String TEST_DATA_PATH = ConfigurationManager.getConfigurationValue("test.data.set.path");

    private static void index() {
        try {
            Indexer.runIndex(TEST_DATA_PATH , FILE_CHUNK_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void merge() {
        BulkMerger merger = new BulkMerger(INVERTED_INDEX_FOLDER);
        merger.bulkMerge();
    }

    public static void main(String[] args) {
        // Writes output to output.txt file instead of stdout
        try {
            System.setOut(new PrintStream(new File("output.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        long timeAtStart = System.nanoTime();
        ///////////////////////////////////////////////////////////////////
        System.out.println(STEMMING_ENABLED ? "Stemming is [ON]" : "Stemming is [OFF]");
        index();
        merge();
        hw1.main.App.runOkapi();
        ///////////////////////////////////////////////////////////////////
        long timeAtEnd = System.nanoTime();
        long elapsedTime = timeAtEnd - timeAtStart;
        double seconds = (double) elapsedTime / 1000000000.0;
        System.out.println("\nTotal time taken: " + seconds / 60.0 + " minutes");
    }
}
