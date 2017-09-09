package hw2.merging;

import hw1.main.ConfigurationManager;
import util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek Mulay on 6/6/17.
 */
public class BulkMerger {
    final static String INVERTED_INDEX_FOLDER = ConfigurationManager.getConfigurationValue("inverted.index.files.directory");

    public BulkMerger(String INVERTED_INDEX_FOLDER) {
        File dir = new File(INVERTED_INDEX_FOLDER);
        int numOfFiles = dir.listFiles().length;
        if (numOfFiles < 1) {
            throw new IllegalStateException("Can not use Bulk Merger. Inverted Index Files are not available in ["+ dir +"]");
        }
    }


    private List<File> getAllCatalogFiles() {
        File dir = new File(INVERTED_INDEX_FOLDER);
        File[] allFiles = dir.listFiles();
        List<File> catalogList = new ArrayList<>();
        for (File file : allFiles) {
            if (file.getName().contains("_catalog"))
                catalogList.add(file);
        }
        return catalogList;
    }

    public void bulkMerge() {

        List<File> allCatalogFiles = getAllCatalogFiles();
        if (allCatalogFiles.size() < 1) { // base case
            System.out.println(allCatalogFiles+ ">>>>>>>>");
            return;
        }
        if (allCatalogFiles.size() == 1) { // base case, there will be one catalog and one index file.
            File catalogFile = allCatalogFiles.get(0);
            String invertedIndexFileForCatalog = FileUtils.getInvertedIndexFileForCatalog(catalogFile.getPath());
            File indexFile = new File(invertedIndexFileForCatalog);
            try {
                Path catalogOld  = Paths.get(catalogFile.getPath());
                Files.move(catalogOld, catalogOld.resolveSibling("index_catalog.txt"));

                Path indexOld  = Paths.get(indexFile.getPath());
                Files.move(indexOld, indexOld.resolveSibling("index.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("\nMerging " + allCatalogFiles.size() + " index files.");
            for (int index=0; index < allCatalogFiles.size()-1; index+=2) {
                File firstCatalogFile = allCatalogFiles.get(index);
                File secondCatalogFile = allCatalogFiles.get(index+1);
                InvertedIndexFileMerger.merge(firstCatalogFile.getPath(), secondCatalogFile.getPath());
            }
            // recur
            bulkMerge();
        }

    }
}
