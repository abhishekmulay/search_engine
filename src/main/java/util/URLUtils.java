package util;

import hw3.models.CrawlableURL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.net.URLCanonicalizer;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Abhishek Mulay on 6/16/17.
 */

public class URLUtils {

    static Logger LOG = LogManager.getLogger(URLUtils.class);
    private static final URLCanonicalizer canonicalizer = new URLCanonicalizer();

    public static String getCanonicalURLAbhi(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Can not canonicalize empty or null url.");
        }

        String validUrl = canonicalizer.canonicalize(url);
        URL urlObj = null;
        URI uriObj = null;
        URL finalUrl = null;
        try {
            validUrl = java.net.URLDecoder.decode(validUrl, "UTF-8");
            urlObj = new URL(validUrl);

            uriObj = new URI(urlObj.getProtocol(), urlObj.getHost(), urlObj.getPath(), null);

            String path = uriObj.getPath();

            // remove double forward slashes
            path = path.replaceAll("\\/\\/", "/");

            // remove fragment after #
            if (!path.isEmpty() && path.contains("#")) {
                path = path.substring(0, path.indexOf("#"));
            }

            if (!path.isEmpty() && path.contains("?")) {
                path = path.substring(0, path.indexOf("?"));
            }

            finalUrl = new URL(urlObj.getProtocol(), urlObj.getHost(), path);
            String encodedFinalUrl = URLEncoder.encode(finalUrl.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // if exception is thrwon, use url as canonicalized url.
        return (finalUrl == null) ? url : finalUrl.toString();
    }

    public static String getCanonicalURL(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Can not canonicalize empty or null url.");
        }

        if (url.contains("#")) {
            url = url.substring(0, url.indexOf("#"));
        }

        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }

        URL myurl = null;
        try {
            myurl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        int port = myurl.getPort();

        String path = myurl.getPath();
        if (path.contains("//")) {
            path = path.replaceAll("//", "/");
        }

        if ((myurl.getProtocol().equals("http") && port == 80) || (myurl.getProtocol().equals("https") && port == 443)) {
            port = -1;
        }

        String canUrl = "http" + "://" + myurl.getHost().toLowerCase() + (port > -1 ? ":" + myurl.getPort() : "")+ path;
        return canUrl;
    }

    public static Set<CrawlableURL> getCrawlableUrls(Elements anchors, int currentDepth, CrawlableURL curl) {
        Set<CrawlableURL> crawlableURLList = new HashSet<>();
//        Elements filteredAnchors = URLFilter.filterURLs(anchors);

        for (Element anchor : anchors) {
            String href = anchor.attr("abs:href");

            try {
                if (!href.isEmpty()) {
                    URI uri = URI.create(href);
                    String canonicalURL = URLUtils.getCanonicalURL(href);
                    String text = anchor.text().toLowerCase();
                    String titleText = anchor.attr("title").toLowerCase();
                    String keywords = titleText.contains(text) ? titleText : (titleText + text);
                    CrawlableURL crawlableURL = new CrawlableURL(uri, canonicalURL, keywords, currentDepth + 1);
                    crawlableURLList.add(crawlableURL);
                }
            } catch (Exception e) {
                LOG.error("Exception creating CrawlableURL for URL= ["+ href+"]\n", e);
                e.printStackTrace();
            }
        }
        return crawlableURLList;
    }

}
