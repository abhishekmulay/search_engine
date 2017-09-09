package hw2.search;

import hw2.indexing.IndexingUnit;
import org.omg.PortableServer.POA;

import java.util.*;

/**
 * Created by Abhishek Mulay on 6/10/17.
 */
public class ProximitySearcher {

    public static int getMinimumSpan(List<List<Integer>> listOfPositionList) {
        if (listOfPositionList == null || listOfPositionList.size() < 2)
            return -1;

        int totalNumbers = 0;
        for (List<Integer> list : listOfPositionList) {
            totalNumbers += list.size();
        }

        int[] indices = new int[listOfPositionList.size()];
        for (int i = 0; i < listOfPositionList.size(); i++)
            indices[i] = 1;

        PriorityQueue<Integer> maxheap = new PriorityQueue<Integer>(10, new Comparator<Integer>() {
            @Override
            public int compare(Integer int1, Integer int2) {
                return int2 - int1;
            }
        });
        PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>(10, new Comparator<Integer>() {
            @Override
            public int compare(Integer int1, Integer int2) {
                return int1 - int2;
            }
        });
        PriorityQueue<Integer> minSpanHeap = new PriorityQueue<Integer>(10, new Comparator<Integer>() {
            @Override
            public int compare(Integer int1, Integer int2) {
                return int1 - int2;
            }
        });

        for (List<Integer> list : listOfPositionList) {
            int first = list.get(0);
            minHeap.add(first);
            maxheap.add(first);
        }

        int index = 0;
        int max = 0;
        int min = 0;
        int minimumSpan = 0;

        while (index < totalNumbers) {
            int flagIndex;
            for (flagIndex=0; flagIndex < indices.length; flagIndex++) {
                int flag = indices[flagIndex];
                if (flag == 0) {
                    Integer changedElement = listOfPositionList.get(flagIndex).get(0);
                    minHeap.add(changedElement);
                    maxheap.add(changedElement);
                    break;
                }
            }

            max = maxheap.peek();
            min = minHeap.poll();
            minimumSpan = max - min;
            minSpanHeap.add(minimumSpan);

            int miniumElementArrayNumber = -1;
            int indexOfMin = -1;
            for (int arrIndex = 0; arrIndex < listOfPositionList.size(); arrIndex++) {
                List<Integer> list = listOfPositionList.get(arrIndex);
                if (list.contains(min)) {
                    miniumElementArrayNumber = arrIndex;
                    indexOfMin = list.indexOf(min);
                    break;
                }
            }

            List<Integer> list = listOfPositionList.get(miniumElementArrayNumber);
            Integer removedInt = list.remove(indexOfMin);
            list.add(removedInt);

            int i = 0;
            while (i < indices.length) {
                if (i == miniumElementArrayNumber) {
                    indices[i] = 0;
                } else {
                    indices[i] = 1;
                }
                i++;
            }

            index += 1;
        }

        return minSpanHeap.poll();
    }

    public static void main(String[] args) {
        List<List<Integer>> listOfPositionList = new LinkedList<>();

        Integer[] position1 = {2, 5, 10, 15};
        Integer[] position2 = {2, 3, 6, 9};
        Integer[] position3 = {4, 8, 16, 21};
        List<Integer> list1 = new LinkedList<Integer>(Arrays.asList(position1));
        List<Integer> list2 = new LinkedList<Integer>(Arrays.asList(position2));
        List<Integer> list3 = new LinkedList<Integer>(Arrays.asList(position3));


//        Integer[] position1 = {129, 32};
//        Integer[] position2 = {120};
//
//        List<Integer> list1 = new LinkedList<Integer>(Arrays.asList(position1));
//        List<Integer> list2 = new LinkedList<Integer>(Arrays.asList(position2));

        listOfPositionList.add(list1);
        listOfPositionList.add(list2);
        listOfPositionList.add(list3);

        System.out.println("Ans: " + getMinimumSpan(listOfPositionList));
    }
}
