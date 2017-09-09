package hw2.search;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.util.CoreMap;
import hw1.main.ConfigurationManager;
import hw2.indexing.IndexingUnit;
import util.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Abhishek Mulay on 6/8/17.
 */
public class TermListTester {
    final String TEST_TERMS_FILE = ConfigurationManager.getConfigurationValue("indexing.test.terms.list");
    final static String TERM_TEST_OUTPUT_FILE = ConfigurationManager.getConfigurationValue("indexing.test.output.file");

    private List<String> readTestTerms() {
        List<String> testTerms = new ArrayList<String>();
        try {
            InputStream content = new FileInputStream(TEST_TERMS_FILE);
            BufferedReader br = new BufferedReader(new InputStreamReader(content));
            String line = "";
            while ((line = br.readLine()) != null) {
                testTerms.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testTerms;
    }

    public void testTermsAgainstIndex(final String outputFilePath) {
        List<String> testTerms = readTestTerms();
        StringBuilder builder = new StringBuilder();
        for (String term : testTerms) {
            List<IndexingUnit> indexingUnitList = TermSearcher.search(term);
            if (indexingUnitList.size() > 1) {
                builder.append(term).append(" ");
                builder.append(indexingUnitList.get(0).getDocumentFrequency()).append(" ");
                builder.append(indexingUnitList.get(0).getTtf()).append("\n");
            }
        }
        String data = builder.toString();
        System.out.println(data);
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        FileUtils.writeBytesToFile(bytes, outputFilePath);
    }

//    public static void lemmatize(String text)

    //    }
//        }
//            }
//                System.out.println("lemmatized version :" + lemma);
//                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
//                String word = token.get(CoreAnnotations.TextAnnotation.class);
//            {
//            for(CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class))
//        {
//        for(CoreMap sentence: document.get(CoreAnnotations.SentencesAnnotation.class))
//
//        Annotation document = pipeline.process(text);
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);
//        props.put("annotators", "tokenize, ssplit, pos, lemma");
//        Properties props = new Properties();
//    {
    public static void main(String[] args) {
//        TermListTester termListTester = new TermListTester();
//        termListTester.testTermsAgainstIndex(TERM_TEST_OUTPUT_FILE);

//        partyman 5 5
//        imported 782 1017
//        waterway 209 284

        final String term = "partyman";
        System.out.println(TermSearcher.search(term));
    }

}
