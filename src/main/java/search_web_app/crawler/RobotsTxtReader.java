package hw3.crawler;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhishek Mulay on 6/19/17.
 */
public class RobotsTxtReader {

    static Logger LOG = LogManager.getLogger(RobotsTxtReader.class);
    private static Map<String, BaseRobotRules> robotsTxtRules = new HashMap<String, BaseRobotRules>();
    private static List<String> ignoreList = new ArrayList<>();

    static {
        ignoreList.add("http://www.webcitation.org");
        ignoreList.add("http://jrr.oxfordjournals.org:80:");
        ignoreList.add("http://proceedings.asmedigitalcollection.asme.org:80");
        ignoreList.add("naiic.go.jp");
    }

    public static boolean isUrlAllowed(final String url) {
        boolean urlAllowed = false;
        try {
            String USER_AGENT = "Googlebot/2.1 (+http://www.google.com/bot.html)";
            URL urlObj = new URL(url);
            String hostId = urlObj.getProtocol() + "://" + urlObj.getHost() + (urlObj.getPort() > -1 ? ":" + urlObj.getPort() : "");
            if (ignoreList.contains(hostId)) {
                return false;
            }
            BaseRobotRules rules = robotsTxtRules.get(hostId);
            if (rules == null) {
                HttpGet httpget = new HttpGet(hostId + "/robots.txt");
                HttpContext context = new BasicHttpContext();
//                HttpClient httpclient1 = HttpClientBuilder.create()
//                        .disableRedirectHandling()
//                        .build();
                HttpClient httpclient = HttpClientBuilder.create().disableAutomaticRetries().build();

//                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpResponse response = null;
                    response = httpclient.execute(httpget, context);
                if (response.getStatusLine() != null && response.getStatusLine().getStatusCode() == 404) {
                    rules = new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_ALL);
                    // consume entity to deallocate connection
                    EntityUtils.consumeQuietly(response.getEntity());
                } else {
                    BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
                    SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
                    rules = robotParser.parseContent(hostId, IOUtils.toByteArray(entity.getContent()), "text/plain", USER_AGENT);
                }
                robotsTxtRules.put(hostId, rules);
            }
            urlAllowed = rules.isAllowed(url);

        } catch (Exception e) {
            LOG.error("Exception reading rules from Robots.txt for [" + url + "] ", e);
            e.printStackTrace();
            return false;
        }

        return urlAllowed;
    }

    public static boolean isUrlAllowed1(final String url) {
        String USER_AGENT = "LightbringerBot";
        URL urlObj = null;
        try {
            urlObj = new URL(url);
            String hostId = urlObj.getProtocol() + "://" + urlObj.getHost() + (urlObj.getPort() > -1 ? ":" + urlObj.getPort() : "");
            if (ignoreList.contains(hostId)) {
                return false;
            }
//            RobotExclusion robotExclusion = new RobotExclusion();
//            return robotExclusion.allows(urlObj, USER_AGENT);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
