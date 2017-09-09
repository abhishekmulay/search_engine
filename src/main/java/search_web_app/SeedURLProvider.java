package hw3;

import hw3.models.CrawlableURL;
import util.URLUtils;

import java.net.URI;
import java.util.*;

/**
 * Created by Abhishek Mulay on 6/21/17.
 */
public class SeedURLProvider {

    private static List<CrawlableURL> seedUrlList = new ArrayList();

    static {
        final String url1 = "http://en.wikipedia.org/wiki/Lists_of_nuclear_disasters_and_radioactive_incidents";
        final String url2 = "http://en.wikipedia.org/wiki/Nuclear_and_radiation_accidents_and_incidents";
        final String url3 = "http://www.world-nuclear.org/info/Safety-and-Security/Safety-of-Plants/Fukushima-Accident/";
        final String url4 = "http://en.wikipedia.org/wiki/Fukushima_Daiichi_nuclear_disaster";
        final String url5 = "http://fukushimaupdate.com/archiv/";

        List<String> urlKeywords1 = new ArrayList<>();
        String keywords1 = "nuclear,"+"radioactive,"+"disaster,"+"incident";

        List<String> urlKeywords2 = new ArrayList<>();
        String keywords2 = "nuclear,"+"radioactive,"+"accident," +"incident,";

        List<String> urlKeywords3 = new ArrayList<>();
        String keywords3 = "nuclear,"+"radioactive,"+"accident,"+"incident," + "saftey," + "security,";

        List<String> urlKeywords4 = new ArrayList<>();
        String keywords4 = "fukushima,"+"daiichi,"+"accident,"+"nuclear,";

        List<String> urlKeywords5 = new ArrayList<>();
        String keywords5 = "fukushima";

        seedUrlList.add(new CrawlableURL(URI.create(url1), URLUtils.getCanonicalURL(url1), keywords1, 0));
        seedUrlList.add(new CrawlableURL(URI.create(url2), URLUtils.getCanonicalURL(url2), keywords2, 0));
        seedUrlList.add(new CrawlableURL(URI.create(url3), URLUtils.getCanonicalURL(url3), keywords3, 0));
        seedUrlList.add(new CrawlableURL(URI.create(url4), URLUtils.getCanonicalURL(url4), keywords4, 0));
        seedUrlList.add(new CrawlableURL(URI.create(url5), URLUtils.getCanonicalURL(url5), keywords5, 0));
    }

    public static List<CrawlableURL> getSeedUrls() {
        return seedUrlList;
    }

}
