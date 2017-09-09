package hw3;

import junit.framework.TestCase;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Abhishek Mulay on 6/29/17.
 */
public class URLPriorityCalculatorTest extends TestCase {
    @Test
    public void testScore() {
        String text1 = "acrobatic airplane some random bullshit";
        String text2 = "symbolist poetry nuclear";
        String text3 = "Trans-Israel Pipeline 2014 Israeli oil spill";
        String text4 = "Thyroid cancer caused by neglect";
        String text5 = "nuclear nuclear cancer";

        System.out.println(URLPriorityCalculator.calculatePriority(text1));
        System.out.println(URLPriorityCalculator.calculatePriority(text2));
        System.out.println(URLPriorityCalculator.calculatePriority(text3));
        System.out.println(URLPriorityCalculator.calculatePriority(text4));
        System.out.println(URLPriorityCalculator.calculatePriority(text5));

    }

    @Test
    public void testFukushimaUpdateWebsite() throws IOException {
        String url = "http://fukushimaupdate.com";
        Connection.Response response = Jsoup.connect(url)
                .timeout(10 * 1000)
                .header("Accept-Language", "en")
                .header("Accept-Encoding", "gzip, deflate")
                .maxBodySize(0)
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .execute();
        int statusCode = response.statusCode();
        Document document = response.parse();
        System.out.println(document.select(LinkSelectorProvider.defaultLinkSelector));
        System.out.println(statusCode);
    }

}