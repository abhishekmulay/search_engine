package util;

import hw2.indexing.IndexingUnit;

import java.util.*;

/**
 * Created by Abhishek Mulay on 5/24/17.
 */
public class MapUtils {

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    public static <K, V extends Comparable<? super V>> String getPrettyString(Map<K, V> map) {
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            buffer.append(key).append("\t").append(value).append('\n');
        }
        return buffer.toString();
    }


    public static Map<String, IndexingUnit> sortByTF(Map<String, IndexingUnit> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, IndexingUnit>> list = new LinkedList<Map.Entry<String, IndexingUnit>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, IndexingUnit>>() {
            @Override
            public int compare(Map.Entry<String, IndexingUnit> o1, Map.Entry<String, IndexingUnit> o2) {
                return o1.getValue().getTermFrequency() > o2.getValue().getTermFrequency() ? -1 : 1;
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, IndexingUnit> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<String, IndexingUnit> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
