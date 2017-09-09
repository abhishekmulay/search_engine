package hw1.pojos;

import java.util.List;

/**
 * Created by Abhishek Mulay on 5/22/17.
 */
public class TermStatistics {

    private String term;
    private String documentId;
    private int termFrequency;
    private int documentFrequency;
    private int documentLength;
    private int ttf;
    private List<Integer> positions;

    public TermStatistics(String term, String documentId, int documentLength, int termFrequency, int documentFrequency, int ttf) {
        this.term = term;
        this.documentId = documentId;
        this.documentLength = documentLength;
        this.termFrequency = termFrequency;
        this.documentFrequency = documentFrequency;
        this.ttf = ttf;
    }

    // for hw2 proximity search
    public TermStatistics(String term, String documentId, int documentLength, int termFrequency, int documentFrequency, int ttf, List<Integer> positions) {
        this.term = term;
        this.documentId = documentId;
        this.documentLength = documentLength;
        this.termFrequency = termFrequency;
        this.documentFrequency = documentFrequency;
        this.ttf = ttf;
        this.positions = positions;
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

    public int getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(int termFrequency) {
        this.termFrequency = termFrequency;
    }

    public int getDocumentFrequency() {
        return documentFrequency;
    }

    public void setDocumentFrequency(int documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

    public int getTtf() {
        return ttf;
    }

    public void setTtf(int ttf) {
        this.ttf = ttf;
    }

    public int getDocumentLength() {
        return documentLength;
    }

    public void setDocumentLength(int documentLength) {
        this.documentLength = documentLength;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public void setPositions(List<Integer> positions) {
        this.positions = positions;
    }

    @Override
    public String toString() {
        if (this.positions.size() > 0) {
            return "TermStatistics" +
                    "{" +
                    "term='" + term + '\'' +
                    ", documentId='" + documentId + '\'' +
                    ", termFrequency=" + termFrequency +
                    ", documentFrequency=" + documentFrequency +
                    ", ttf=" + ttf +
                    ", position=" + positions +
                    '}';
        }
        return "TermStatistics" +
                "{" +
                "term='" + term + '\'' +
                ", documentId='" + documentId + '\'' +
                ", termFrequency=" + termFrequency +
                ", documentFrequency=" + documentFrequency +
                ", ttf=" + ttf +
                '}';
    }
}