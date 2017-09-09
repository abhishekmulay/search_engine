package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Abhishek Mulay on 6/9/17.
 */
public class ListUtils {

    public static <T> String toCompactString(List<T> list) {
        int size = list.size();
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        int counter = 0;
        while (true) {
            if (counter == size - 1) {
                builder.append(list.get(counter)).append(']');
                return builder.toString();
            }
            builder.append(list.get(counter)).append(',');
            counter +=1;
        }
    }

    public static void main(String[] args) {
        List<Integer> numList = new ArrayList<>();
        numList.add(1); numList.add(2); numList.add(3); numList.add(4);
        String str = toCompactString(numList);
        System.out.println(str);
        int[] ints = fromString(str);
        System.out.println(Arrays.toString(ints));
    }

    public static int[] fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(",");
        int result[] = new int[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(strings[i].trim());
        }
        return result;
    }

}
