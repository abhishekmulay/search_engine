package hw3.merging;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hw1.main.ConfigurationManager;
import hw1.restclient.RestCallHandler;
import hw3.models.DocumentModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Response;
import util.MapUtils;

import java.io.*;
import java.util.*;

/**
 * Created by Abhishek Mulay on 6/29/17.
 */
public class JsonRecordReader {

    static Logger LOG = LogManager.getLogger(JsonRecordReader.class);
    private static final String inlinksMapOutputFilePath = ConfigurationManager.getConfigurationValue("in.linkmap.output.file");
    private static final String outlinksOutputFilePath = ConfigurationManager.getConfigurationValue("out.linkmap.output.file");
    private static final String OUTPUT_DIR = ConfigurationManager.getConfigurationValue("hw3.models.file.path");
    private String serverAddress = ConfigurationManager.getConfigurationValue("team.elastic.server.address");
    private String index = ConfigurationManager.getConfigurationValue("team.elastic.index");
    private String type = ConfigurationManager.getConfigurationValue("team.elastic.type");

    private String TEAM_ELASTIC_ENDPOINT =  "/" + index + "/" + type + "/";

    JsonFactory jsonfactory = null;
    JsonParser jsonParser = null;
    ObjectMapper mapper = null;
    RestCallHandler handler = null;

    public JsonRecordReader() {
        JsonFactory jsonfactory = new JsonFactory();
        mapper = new ObjectMapper();
        handler = new RestCallHandler();
    }


    public void createLinkMap() throws IOException {
        // crate oulinks map file
        File outlinksFile = new File(outlinksOutputFilePath);
        if (outlinksFile.exists()) {
            outlinksFile.delete();
        } else {
            outlinksFile.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(outlinksFile);
        BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(fos));
        LOG.info("Creating link map in = [" + outlinksFile.getPath() + "]");

        File outputDir = new File(OUTPUT_DIR);
        for (File file : outputDir.listFiles()) {
            LOG.info("Reading file = [" + file + "]");
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                for (String line; (line = br.readLine()) != null; ) {
                    // process the line.
                    JsonNode jsonNode = mapper.readTree(line);
                    DocumentModel model = mapper.readValue(line, DocumentModel.class);
                    List<String> out_links = model.getOut_links();

                    // create outlink line
                    StringBuilder builder = new StringBuilder();
                    builder.append(model.getDocno());
                    out_links.stream().forEach(link -> builder.append(" ").append(link));

                    // write to outlink map
                    bwriter.write(builder.toString());
                    bwriter.newLine();
                }
            }
            LOG.info("Done reading file = [" + file + "]");
        }
        bwriter.close();
    }


    public Map<String, Set<String>> getOutLinksMap() throws IOException {
        File linkMapFile = new File(outlinksOutputFilePath);
        Set<String> outlinksSet = new HashSet<>();

        Map<String, Set<String>> outlinksMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(linkMapFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                String[] items = line.split(" ");
                String url = items[0];
                for (int index = 1; index < items.length; index++) {
                    outlinksSet.add(items[index]);
                }

                if (outlinksMap.containsKey(url)) {
                    Set<String> previousLinks = outlinksMap.get(url);
                    previousLinks.addAll(outlinksSet);
                    outlinksMap.put(url, previousLinks);
                } else {
                    outlinksMap.put(url, outlinksSet);
                }
            }
        }
        return outlinksMap;
    }


//    public Map<String, Set<String>> getInlinksMap(Map<String, Set<String>> outLinksMap) {
//        Map<String, Set<String>> inlinksMap = new HashMap<>(outLinksMap.size());
//        outLinksMap.entrySet().forEach(outlinkEntry -> {
//            // key, main url
//            String url = outlinkEntry.getKey();
//            // value, out links for this url
//            Set<String> outlinks = outlinkEntry.getValue();
//
//            Set<String> previousLinks = null;
//            Set<String> links = null;
//
//            for (final String link : outlinks) {
//                if (inlinksMap.containsKey(link)) {
//                    previousLinks = inlinksMap.get(link);
//                    previousLinks.add(url);
//                    inlinksMap.put(link, previousLinks);
//                } else {
//                    links = new HashSet<>();
//                    links.add(url);
//                    inlinksMap.put(link, links);
//                }
//                previousLinks = null;
//                links = null;
//            }
//        });
//
//        try {
//            FileOutputStream fos = new FileOutputStream(inlinksMapOutputFilePath);
//            BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(fos));
//            LOG.info("Creating in link map in = [" + inlinksMapOutputFilePath + "]");
//
//            inlinksMap.entrySet().forEach(entry -> {
//                StringBuilder builder = new StringBuilder();
//                String url = entry.getKey();
//                Set<String> inlinksSet = entry.getValue();
//                builder.append(url);
//                for (String link : inlinksSet)
//                    builder.append(" ").append(link);
//
//                try {
//                    bwriter.write(builder.toString());
//                    bwriter.newLine();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//            bwriter.close();
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//        return inlinksMap;
//    }

    public static void main(String[] args) {

        JsonRecordReader jsonRecordReader = new JsonRecordReader();

        Map<String, Set<String>> outLinksMap = null;
        Map<String, Set<String>> inlinksMap = null;
        try {
            outLinksMap = jsonRecordReader.getOutLinksMap();
            inlinksMap = jsonRecordReader.getInlinksMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Created outlinks map = " + outLinksMap.size());
        System.out.println("Created inlinks map = " + inlinksMap.size());

        //        try {
//            jsonRecordReader.createLinkMap();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public Map<String,Set<String>> getInlinksMap() throws IOException {
        File linkMapFile = new File(inlinksMapOutputFilePath);
        Set<String> inlinksSet = new HashSet<>();

        Map<String, Set<String>> inlinksMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(linkMapFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                String[] items = line.split(" ");
                String url = items[0];
                for (int index = 1; index < items.length; index++) {
                    inlinksSet.add(items[index]);
                }

                if (inlinksMap.containsKey(url)) {
                    Set<String> previousLinks = inlinksMap.get(url);
                    previousLinks.addAll(inlinksSet);
                    inlinksMap.put(url, previousLinks);
                } else {
                    inlinksMap.put(url, inlinksSet);
                }
            }
        }
        return inlinksMap;
    }

}
