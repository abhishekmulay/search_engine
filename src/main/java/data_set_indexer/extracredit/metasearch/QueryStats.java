package hw1.extracredit.metasearch;

/**
 * Created by Abhishek Mulay on 5/29/17.
 */
class QueryStats {
    String queryId;
    String documentId;
    double rank;
    double score;

    public QueryStats(String queryId, String documentId, double rank, double score) {
        this.queryId = queryId;
        this.documentId = documentId;
        this.rank = rank;
        this.score = score;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "QueryStats{" +
                "queryId='" + queryId + '\'' +
                ", documentId='" + documentId + '\'' +
                ", rank=" + rank +
                ", score=" + score +
                '}';
    }
}
