package util;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Created by Abhishek Mulay on 6/16/17.
 */
public class URLUtilsTest extends TestCase {

    public static void testCanonicalization() {
        // Remove default port
        String url1 = "http://example.com:80/asd";
        String expectedUrl1 = "http://example.com/asd";

        // Decoding octets for unreserved characters
        String url2 = "http://example.com/%7Ehome";
        String expectedUrl2 = "http://example.com/~home";

        // this case should not happen, join relative path and base path while creating URL
//        https://stackoverflow.com/questions/1389184/building-an-absolute-url-from-a-relative-url-in-java
//        // Remove . and ..
//        String url3 = "http://example.com/a/b/../c";
//        String expectedUrl3 = "http://example.com/a/b/c/";

        // Force trailing slash for directories
        String url4 = "http://example.com/a/b/";
        String expectedUrl4 = "http://example.com/a/b/";

//        // Remove default index pages
//        String url5 = "http://example.com/index.html";
//        String expectedUrl5 = "http://example.com";

        // Removing the fragment
        String url6 = "http://example.com/a#b/c";
        String expectedUrl6 = "http://example.com/a";


        String url7 = "http://www.ies.unsw.edu.au/sites/all/files/MD BookReview_EnergyPolicy2013.pdf";
        String expected7="http://www.ies.unsw.edu.au/sites/all/files/MD%20BookReview_EnergyPolicy2013.pdf";


        URLUtils.getCanonicalURL(url1);
        URLUtils.getCanonicalURL(url2);
        URLUtils.getCanonicalURL(url4);
        URLUtils.getCanonicalURL(url6);
        URLUtils.getCanonicalURL(url7);

//        Assert.assertEquals("should remove default port", expectedUrl1, URLUtils.getCanonicalURL(url1));
//        Assert.assertEquals("should decode octets for unreserved characters", expectedUrl2, URLUtils.getCanonicalURL(url2));
////        Assert.assertEquals("should remove . and ..", expectedUrl3, URLUtils.getCanonicalURL(url3));
//        Assert.assertEquals("should force trailing slash for directories", expectedUrl4, URLUtils.getCanonicalURL(url4));
////        Assert.assertEquals("should remove default index pages", expectedUrl5, URLUtils.getCanonicalURL(url5));
//        Assert.assertEquals("should remove the fragment", expectedUrl6, URLUtils.getCanonicalURL(url6));
//        Assert.assertEquals("should encode spaces", expected7, URLUtils.getCanonicalURL(url7));
    }


    public static void main(String[] args) {

    }
}


