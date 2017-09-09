package hw1.pojos;

/**
 * Created by Abhishek Mulay on 5/17/17.
 */
public class Query {
    private int queryId;
    private String originalQuery;
    private String cleanedQuery;

    public Query( int queryId, String originalQuery, String cleanedQuery) {
        this.queryId = queryId;
        this.originalQuery = originalQuery;
        this.cleanedQuery = cleanedQuery;
    }

    public int getQueryId() {
        return queryId;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public String getCleanedQuery() {
        return cleanedQuery;
    }

    @Override
    public String toString() {
        return "Query{" +
                "queryId=" + queryId +
                ", originalQuery='" + originalQuery + '\'' +
                ", cleanedQuery='" + cleanedQuery + '\'' +
                '}';
    }
}
