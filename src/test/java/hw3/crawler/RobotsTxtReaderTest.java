package hw3.crawler;

import hw3.LinkSelectorProvider;
import hw3.models.CrawlableURL;
import hw3.models.HW3Model;
import hw3.SeedURLProvider;
import junit.framework.TestCase;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import weka.core.pmml.jaxbbindings.REALSparseArray;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Created by Abhishek Mulay on 6/22/17.
 */
public class RobotsTxtReaderTest extends TestCase {


    @Test
    public void testGoogleUrl() {
        final String url1 = "https://www.google.com/search?client=safari&rls=en&q=IMMIGRATION+TO+UNITED+STATES&ie=UTF-8&oe=UTF-8#q=IMMIGRATION+TO+UNITED+STATES+donald+trump&safe=off";
        final String url2 = "https://en.wikipedia.org/wiki/SL-1";
        final String url3 = "https://en.wikipedia.org/wiki/Fukushima_Daiichi_nuclear_disaster";
        final String url4 = "https://en.wikipedia.org/wiki/Tokyo_Electric_Power_Company";
        final String url5 = "https://jsonformatter.org/";

        Assert.assertEquals("Should be crawlable", true, RobotsTxtReader.isUrlAllowed(url2));
        Assert.assertEquals("Should NOT be crawlable", false, RobotsTxtReader.isUrlAllowed(url1));
        Assert.assertEquals("Should be crawlable", true, RobotsTxtReader.isUrlAllowed(url3));
        Assert.assertEquals("Should be crawlable", true, RobotsTxtReader.isUrlAllowed(url4));
        Assert.assertEquals("Should be crawlable", true, RobotsTxtReader.isUrlAllowed(url5));



    }


    @Test
    public void testCrawling() {
        final String selector = "#content :not(.mw-editsection) > a[href]:not([href~=(?i).*(\\." +
                "(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz|csv|xls|ppt|doc|docx|exe|dmg|midi|mid|qt|txt|ram|json))$):not([href~=(?i)^#])";

        final String defaultLinkSelector = "a[href]:not([href~=(?i).*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
                "|rm|smil|wmv|swf|wma|zip|rar|gz|csv|xls|ppt|doc|docx|exe|dmg|midi|mid|qt|txt|ram|json))$):not([href~=(?i)^#])";

        String url  = "https://en.wikipedia.org/wiki/Fukushima_Daiichi_nuclear_disaster";
        try {
            Document document = Jsoup.connect(url).get();
            Elements links = document.select(selector);
//            for (Element link : links)
//                System.out.println(link.attr("abs:href"));
//            System.out.println("\nTotal links on page =" + document.select("a[href]").size());
//            System.out.println("Found " + links.size() + " urls on page after filtering.");

        } catch (IOException e) {
            e.printStackTrace();
        }


        List<CrawlableURL> seedUrls = SeedURLProvider.getSeedUrls();
        for (CrawlableURL curl : seedUrls) {
            String url1 = curl.getOriginalUrl().toString();
            try {
                Document document = Jsoup.connect(url1).get();
                Elements links = document.select(defaultLinkSelector);
//                for (Element link : links)
//                    System.out.println(link.attr("abs:href"));
//                System.out.println("\nTotal links on page =" + document.select("a[href]").size());
                System.out.println("Found " + links.size() + " urls on page after filtering." + url1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @Test
    public void testNonWikipediaURLs() {
        final String url1 = "http://www.world-nuclear.org/info/Safety-and-Security/Safety-of-Plants/Fukushima-Accident/";
        final String url = "https://en.wikipedia.org/wiki/Special:BookSources/981-210-210-8";

        final String url2 = "http://www.fukushimaupdate.com"; // 126

        Document document = null;
        try {
            URI uri = URI.create(url2);

            Connection.Response response = Jsoup.connect(uri.toString())
                    .timeout(10 * 1000)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .userAgent("Googlebot/2.1 (+http://www.google.com/bot.html)")
                    .execute();

            document = response.parse();
            System.out.println("Links found = " + document.select(LinkSelectorProvider.defaultLinkSelector).size());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}