package hw3.crawler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

/**
 * Created by Abhishek Mulay on 6/20/17.
 */
public class URLFilter {

    static Pattern excludePattern = Pattern.compile("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|css|js|xml|pdf|ttf|txt))$)", Pattern.CASE_INSENSITIVE);

    public static Elements filterURLs(Elements urls) {
        Elements filteredElements = new Elements();

        for (Element link : urls) {
            String href = link.attr("href");
            if (isCrawlableURL(href)) {
                filteredElements.add(link);
            }
        }
        return filteredElements;
    }


    public static boolean isCrawlableURL(final String href) {
        if (href == null || href.isEmpty()) { return false; }

        // wikipedia specific non useful file url fragments.
        if (href.contains("/wiki/File:") || href.contains("Template_talk")) { return false; }

        // URLs matching this pattern should not be crawled.
        if (excludePattern.matcher(href).matches()) { return false; }

        return true;
    }

    public static void main(String[] args) {

        String [] urls = {
                "http://img12.deviantart.net/65e4/i/2013/003/6/6/png_floating_terrain_by_moonglowlilly-d5qb58m.png",
                "https://www.cleverfiles.com/howto/wp-content/uploads/2016/08/mini.jpg",
                "https://css-tricks.com/almanac/selectors/a/attribute/lol#lawda-lasun",
                "http://www.pdf995.com/samples/pdf.PDF",
                "https://upload.wikimedia.org/wikipedia/commons/2/2c/Rotating_earth_%28large%29.gif",
                "https://www.w3schools.com/html/",
                "https://www.google.com/sitemap.xml"
        };

        for (String href : urls) {
            System.out.println(href);
//            System.out.println(excludePattern.matcher(href).matches());
        }



        Logger LOG = LogManager.getLogger(URLFilter.class);
        LOG.trace("TRACE");
        LOG.info("INFO");
        LOG.warn("WARN");
        LOG.error("ERROR");
    }
}


