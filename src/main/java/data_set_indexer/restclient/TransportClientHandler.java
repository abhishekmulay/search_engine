package hw1.restclient;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Created by Abhishek Mulay on 7/3/17.
 */
public class TransportClientHandler {


    TransportClient client = null;


    public TransportClientHandler() {
        Settings settings = Settings.builder()
                .put("cluster.name", "elastic4").build();
        client = new PreBuiltTransportClient(settings);
    }

    public void post() {

    }


}
