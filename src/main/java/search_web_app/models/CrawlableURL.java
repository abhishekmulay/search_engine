package hw3.models;

import hw3.URLPriorityCalculator;

import java.net.URI;

/**
 * Created by Abhishek Mulay on 6/21/17.
 */
public class CrawlableURL {
    private final URI originalUrl;
    private final String canonicalizedUrl;
    private String titleKeywords;
    private final int depth;
    private int score;

    public CrawlableURL(URI originalUrl, String canonicalizedUrl, String titleKeywords, int depth) {
        this.originalUrl = originalUrl;
        this.canonicalizedUrl = canonicalizedUrl;
        this.titleKeywords = titleKeywords;
        this.depth = depth;
        this.score = calculatePriorityScore(originalUrl, canonicalizedUrl, titleKeywords);
    }


    public URI getOriginalUrl() {
        return originalUrl;
    }

    public String getCanonicalizedUrl() {
        return canonicalizedUrl;
    }

    public String getTitleKeywords() {
        return titleKeywords;
    }

    public int getDepth() {
        return depth;
    }

    public int getScore() {
        return score;
    }

    private int calculatePriorityScore(URI originalUrl, String canonicalizedUrl, String titleKeywords) {
        return URLPriorityCalculator.calculatePriority(titleKeywords);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrawlableURL)) return false;

        CrawlableURL that = (CrawlableURL) o;

        return getCanonicalizedUrl().equals(that.getCanonicalizedUrl());
    }

    @Override
    public int hashCode() {
        return getCanonicalizedUrl().hashCode();
    }

    @Override
    public String toString() {
        return "CrawlableURL{" +
                "originalUrl=" + originalUrl +
                ", canonicalizedUrl='" + canonicalizedUrl + '\'' +
                ", titleKeywords=" + titleKeywords +
                ", depth=" + depth +
                ", score=" + score +
                '}';
    }
}
