package hw2.indexing;

import hw1.main.ConfigurationManager;
import util.ListUtils;


import java.util.List;

/**
 * Created by Abhishek Mulay on 6/2/17.
 */
public class IndexingUnit {
    final static String INVERTED_INDEX_RECORD_SEPARATOR = ConfigurationManager.getConfigurationValue("inverted.index.file.record.separator");
    final static String INVERTED_INDEX_TERM_SEPARATOR = ConfigurationManager.getConfigurationValue("inverted.index.term.separator");
    final static String INVERTED_INDEX_CATALOG_SEPARATOR = ConfigurationManager.getConfigurationValue("inverted.index.catalog.separator");

    // these properties should not change
    private final String type = "UNIGRAM";
    private final String term;
    private final String documentId;
    private final int docIdMappingNumber;
    private final int termFrequency;
    private final List<Integer> position;

    private int ttf;
    private int documentFrequency;

    public IndexingUnit(String term, String documentId, int docIdMappingNumber, int termFrequency, List<Integer> position, int ttf, int documentFrequency) {
        this.term = term;
        this.documentId = documentId;
        this.docIdMappingNumber = docIdMappingNumber;
        this.termFrequency = termFrequency;
        this.position = position;
        this.ttf = ttf;
        this.documentFrequency = documentFrequency;
    }

    public String getType() {
        return type;
    }

    public String getTerm() {
        return term;
    }

    public String getDocumentId() {
        return documentId;
    }

    public int getTermFrequency() {
        return termFrequency;
    }

    public List<Integer> getPosition() {
        return position;
    }

    public int getTtf() {
        return ttf;
    }

    public int getDocumentFrequency() {
        return documentFrequency;
    }

    public void setTtf(int ttf) {
        this.ttf = ttf;
    }

    public void setDocumentFrequency(int documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

    //    cancel=AP890103-0105:1:-1:-1:[239];AP890103-0176:1:-1:-1:[142];
    @Override
    public String toString() {
        return docIdMappingNumber + INVERTED_INDEX_CATALOG_SEPARATOR + termFrequency
                + INVERTED_INDEX_CATALOG_SEPARATOR + documentFrequency + INVERTED_INDEX_CATALOG_SEPARATOR + ttf +
                INVERTED_INDEX_CATALOG_SEPARATOR + position + INVERTED_INDEX_RECORD_SEPARATOR;
    }

    public static String toWritableString(String term, List<IndexingUnit> indexingUnitList) {
        StringBuilder builder = new StringBuilder();
        builder.append(term).append(INVERTED_INDEX_TERM_SEPARATOR);
        for (IndexingUnit unit : indexingUnitList)
            builder.append(unit.toString());
        return builder.toString();
    }

    public String toPrettyString() {
        return "IndexingUnit {term=" + term + ", docId=" + documentId + ", docId=" + docIdMappingNumber+ ", tf=" +  termFrequency + ", df=" + documentFrequency + ", " + "ttf=" + ttf + ", positions=" + ListUtils.toCompactString(position) + "}";
    }

}
