package hw2.indexing;

import hw1.main.ConfigurationManager;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Abhishek Mulay on 6/4/17.
 */
public class CatalogReader {
//
    final static String INVERTED_INDEX_FOLDER = ConfigurationManager.getConfigurationValue("inverted.index.files.directory");

    private CatalogReader() {}

    /**
     * Reads an entry from Inverted Index File at given position (bytes) and given offset/length (bytes).
     * @param position
     * @param offset
     * @return
     */
    public static String read(final String invertedIndexFilePath ,final int position,final int offset) {
        String entry = "";
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(invertedIndexFilePath, "r");
            // move file pointer to position from where we want to getCatalogAsMap
            randomAccessFile.seek(position);
            byte [] entryForTerm = new byte[offset];
            randomAccessFile.read(entryForTerm, 0, entryForTerm.length);

            entry = new String(entryForTerm, StandardCharsets.UTF_8);
            // remove whitespace and line break
            entry = entry.replace("\n", "").replace("\r", "").trim();
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entry;
    }


    /**
     * Returns Map of term, CatalogEntry for given catalog file.
     * @param catalogFilePath
     * @return Map<term, CatalogEntry>
     */
    public static Map<String, CatalogEntry> getCatalogAsMap(final String catalogFilePath) {
        Map<String, CatalogEntry> catalogEntryMap = new HashMap<>();
        try {
            FileReader fileReader = new FileReader(catalogFilePath);
            BufferedReader br = new BufferedReader(fileReader);

            String line;
            while ((line=br.readLine()) != null) {
                //     term:position:offset
                //ex.  stove:7975663:151
                String[] catalogItems = line.split(":");
                String term = catalogItems[0];
                int position = Integer.parseInt(catalogItems[1]);
                int offset = Integer.parseInt(catalogItems[2]);
                CatalogEntry catalogEntry  = new CatalogEntry(term, position, offset);
                catalogEntryMap.put(term, catalogEntry);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return catalogEntryMap;
    }

    public static Set<String> getVocabulary() {
        return null;
    }
}
