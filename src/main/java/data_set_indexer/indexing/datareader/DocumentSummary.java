package hw1.indexing.datareader;

/**
 * Created by Abhishek Mulay on 6/8/17.
 */
public class DocumentSummary {
    private final String documentId;
    private final int docIdMappingNumber;
    private final int documentLength;

    public DocumentSummary(String documentId, int docIdMappingNumber, int documentLength) {
        this.documentId = documentId;
        this.docIdMappingNumber = docIdMappingNumber;
        this.documentLength = documentLength;
    }

    public String getDocumentId() {
        return documentId;
    }

    public int getDocIdMappingNumber() {
        return docIdMappingNumber;
    }

    public int getDocumentLength() {
        return documentLength;
    }
}
