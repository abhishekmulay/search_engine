package hw1.main;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Abhishek Mulay on 5/11/17.
 */
public class ConfigurationManager {
    private static Properties properties = new Properties();
    private static InputStream  stream = null;
    final static String PROPERTIES_FILE_PATH = "src/main/resources/config.properties";

    static {
        try {
            stream = new FileInputStream(PROPERTIES_FILE_PATH);
            properties.load(stream);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static String getConfigurationValue(String propertyName) {
        return properties.getProperty(propertyName);
    }
}
