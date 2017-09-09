package util;

/**
 * Created by Abhishek Mulay on 6/20/17.
 */
public class StringUtils {

    public static String removeWhiteSpace(String str) {
        return str.replaceAll("\\s","").trim();
    }

    public static String removeLineBreak(String str) {
        return str.replaceAll("\\r\\n|\\r|\\n", " ");
    }
}
