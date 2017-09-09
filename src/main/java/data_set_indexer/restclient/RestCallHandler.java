package hw1.restclient;

import hw1.main.ConfigurationManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by Abhishek Mulay on 5/17/17.
 */
public class RestCallHandler {

    private final String INDEX_NAME = ConfigurationManager.getConfigurationValue("index.name");
    private final String TYPE_NAME = ConfigurationManager.getConfigurationValue("type.name");
    private final String BULK_API_ENDPOINT = '/' + INDEX_NAME + '/' + INDEX_NAME + "/_bulk";
    private final String DOCUMENT_API = '/' + INDEX_NAME + '/' + TYPE_NAME + '/';

    private HttpHost localHost = null;
    private RestClient restClient = null;

    public void openConnection() {
        this.localHost = new HttpHost("localhost", 9200, "http");
        this.restClient = RestClient.builder(this.localHost).build();
    }

    public void closeConnection() {
        try {
            this.restClient.close();
            this.restClient = null;
            this.localHost = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Response get(final String body, final String endPoint) {
        HttpEntity entity = new NStringEntity(body, ContentType.APPLICATION_JSON);
        Response response = null;
        try {
            response = restClient.performRequest("GET", endPoint, Collections.singletonMap("pretty", "true"), entity);
            System.out.println(endPoint + " | STATUS: " + response.getStatusLine().getStatusCode() + " " + response
                    .getStatusLine().getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response post(final String body, final String endPoint) {
        Response response = null;
        HttpEntity entity = new NStringEntity(body, ContentType.APPLICATION_JSON);
        try {
            response = restClient.performRequest("POST", endPoint, Collections.<String, String>emptyMap(), entity);
            System.out.println(endPoint + " | Status: " + response.getStatusLine().getStatusCode() + " " + response
                    .getStatusLine().getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response bulkPOST(String bulkRequestBody) {
        HttpEntity entity = new NStringEntity(bulkRequestBody, ContentType.APPLICATION_JSON);
        Response response = null;
        try {
            response = restClient.performRequest("POST", BULK_API_ENDPOINT, Collections.<String, String>emptyMap(), entity);
            System.out.println(BULK_API_ENDPOINT + " | STATUS: " + response.getStatusLine().getStatusCode() + " " +
                    response.getStatusLine().getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    public char[] getMetadata(String index, String type, String encodedUrlId) {
        return new char[0];
    }
}
