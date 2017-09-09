package hw1.extracredit;

import hw1.main.ConfigurationManager;
import hw1.pojos.Query;
import hw1.queryprocessor.FileQueryReader;
import hw1.queryprocessor.QueryProcessor;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek Mulay on 5/30/17.
 */
public class EC2Test extends TestCase{
     private final String TEST_OUTPUT_FILE = ConfigurationManager.getConfigurationValue("test.model.output.file");
    private final String EC_QUERIES_FILE  = ConfigurationManager.getConfigurationValue("ec.query.file.path");

    @Test
    public void testModelForSingleQuery() {
        FileQueryReader reader = new FileQueryReader();
        List<Query> allQueries = reader.getAllQueries(EC_QUERIES_FILE);
        Query query = allQueries.get(0);

        List<Query> singleQueryList = new ArrayList<>();
        singleQueryList.add(query);
        QueryProcessor processor = new QueryProcessor();
        processor.calculateOkapi_tf(singleQueryList, TEST_OUTPUT_FILE);

    }
}
