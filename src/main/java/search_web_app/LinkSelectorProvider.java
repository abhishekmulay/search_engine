package hw3;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 6/21/17.
 */
public class LinkSelectorProvider {

    // Map<domain-name, css-selector>
    static Map<String, String> domainNameCSSMap = new HashMap<>();

    public static final String defaultLinkSelector = "a[href]:not([href~=(?i).*(\\." +
            "(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
            "|rm|smil|wmv|swf|wma|zip|rar|gz|csv|xls|ppt|doc|docx|exe|dmg|midi|mid|qt|txt|ram|json))$)" +
            ":not([href~=(?i)^#])";

    public static final String wikipediaSelector = "#content :not(.mw-editsection) > a[href]:not([href~=(?i).*(\\." +
            "(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz|csv|xls|ppt|doc|docx|exe|dmg|midi|mid|qt|txt|ram|json))$):not([href~=(?i)^#]):not([rel~=(?i)^nofollow])";

    public static final String oldwikipediaSelector = "#content :not(.mw-editsection) > a[href]:not([href~=(?i)" +
            ".*(\\." +
            "(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz|csv|xls|ppt|doc|docx|exe|dmg|midi|mid|qt|txt|ram|json))$):not([href~=(?i)^#])";

    static {
        domainNameCSSMap.put("en.wikipedia.org", wikipediaSelector);
    }

    public static String getAnchorSelector(final String domainName) {
        if (domainNameCSSMap.containsKey(domainName)) {
            return domainNameCSSMap.get(domainName);
        } else {
            return defaultLinkSelector;
        }
    }

}
