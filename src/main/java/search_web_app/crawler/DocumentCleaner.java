package hw3.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Abhishek Mulay on 6/20/17.
 */
public class DocumentCleaner {
    public static String getCleanContent(Document doc) {
        doc.select("script, style, link, hidden, form, img").remove();
        return doc.toString();
    }

    public static Document getCleanDocument(Document doc) {
        doc.select("script, style, link, hidden, form, img").remove();
        return doc;
    }
}
