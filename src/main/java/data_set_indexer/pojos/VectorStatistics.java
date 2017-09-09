package hw1.pojos;

/**
 * Created by Abhishek Mulay on 5/26/17.
 */
public class VectorStatistics {
    private String term;
    private String documentId;
    private int docLength;
    private double okapi;
    private double tfidf;
    private double mb25;

    public VectorStatistics(String term, String documentId, int docLength, double okapi, double tfidf, double mb25) {
        this.term = term;
        this.documentId = documentId;
        this.docLength = docLength;
        this.okapi = okapi;
        this.tfidf = tfidf;
        this.mb25 = mb25;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getDocLength() {
        return docLength;
    }

    public void setDocLength(int docLength) {
        this.docLength = docLength;
    }

    public double getOkapi() {
        return okapi;
    }

    public void setOkapi(double okapi) {
        this.okapi = okapi;
    }

    public double getTfidf() {
        return tfidf;
    }

    public void setTfidf(double tfidf) {
        this.tfidf = tfidf;
    }

    public double getMb25() {
        return mb25;
    }

    public void setMb25(double mb25) {
        this.mb25 = mb25;
    }
}
