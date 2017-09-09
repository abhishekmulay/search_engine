package hw2.search;

import hw1.main.ConfigurationManager;
import hw2.indexing.IndexingUnit;
import hw2.indexreading.IndexReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek Mulay on 6/8/17.
 */
public class TermSearcher {

    final static String FINAL_CATALOG_NAME = ConfigurationManager.getConfigurationValue("final.catalog.name");
    final static String INVERTED_INDEX_FOLDER = ConfigurationManager.getConfigurationValue("inverted.index.files.directory");

    final static String CATALOG_FILE = INVERTED_INDEX_FOLDER + FINAL_CATALOG_NAME;

    private static IndexReader reader = new IndexReader(CATALOG_FILE);

    static {
        if (!new File(CATALOG_FILE).exists())
            throw new IllegalStateException("[TermSearcher] Index file not found.");
    }

    // returns list of IndexUnits, can return empty list if term not present in corpus
    public static List<IndexingUnit> search(final String term) {
        List<IndexingUnit> indexingUnitList = reader.get(term);
        if (indexingUnitList== null || indexingUnitList.size() == 0)
            return new ArrayList<>();
        else
            return indexingUnitList;
    }

}
