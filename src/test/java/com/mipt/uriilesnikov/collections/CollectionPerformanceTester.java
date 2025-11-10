package com.mipt.uriilesnikov.collections;

import org.junit.jupiter.api.Test;
import java.util.*;

import static java.lang.System.nanoTime;

public class CollectionPerformanceTester {

    private static final int CNT = 10_000;
    private static final String ROW = "%-25s | %12d | %12d%n";

    @Test
    public void ArrayListVSLinkedListPerformanceTest() {
        System.out.println("Performance comparison for " + CNT + " elements:");
        System.out.println("Operation                 | ArrayList(ms)| LinkedList(ms)");
        System.out.println("----------------------------------------------------------");

        testAddToEnd();
        testAddToBeginning();
        testInsertIntoMiddle();
        testAccessByIndex();
        testRemoveFromBeginning();
        testRemoveFromEnd();
    }

    private void testAddToEnd() {
        long timeArrayList = timeOperation(() -> {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < CNT; i++) {
                list.add(i);
            }
        });

        long timeLinkedList = timeOperation(() -> {
            List<Integer> list = new LinkedList<>();
            for (int i = 0; i < CNT; i++) {
                list.add(i);
            }
        });

        System.out.printf(ROW, "Add to end", timeArrayList, timeLinkedList);
    }

    private void testAddToBeginning() {
        long timeArrayList = timeOperation(() -> {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < CNT; i++) {
                list.addFirst(i);
            }
        });

        long timeLinkedList = timeOperation(() -> {
            List<Integer> list = new LinkedList<>();
            for (int i = 0; i < CNT; i++) {
                list.addFirst(i);
            }
        });

        System.out.printf(ROW, "Add to beginning", timeArrayList, timeLinkedList);
    }

    private void testInsertIntoMiddle() {
        long timeArrayList = timeOperation(() -> {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < CNT; i++) {
                list.add(list.size() / 2, i);
            }
        });

        long timeLinkedList = timeOperation(() -> {
            List<Integer> list = new LinkedList<>();
            for (int i = 0; i < CNT; i++) {
                list.add(list.size() / 2, i);
            }
        });

        System.out.printf(ROW, "Insert into middle", timeArrayList, timeLinkedList);
    }

    private void testAccessByIndex() {
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < CNT; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }

        long timeArrayList = timeOperation(() -> {
            for (int i = 0; i < CNT; i++) {
                int value = arrayList.get(i);
            }
        });

        long timeLinkedList = timeOperation(() -> {
            for (int i = 0; i < CNT; i++) {
                int value = linkedList.get(i);
            }
        });

        System.out.printf(ROW, "Access by index", timeArrayList, timeLinkedList);
    }

    private void testRemoveFromBeginning() {
        long timeArrayList = timeOperation(() -> {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < CNT; i++) {
                list.add(i);
            }
            while (!list.isEmpty()) {
                list.removeFirst();
            }
        });

        long timeLinkedList = timeOperation(() -> {
            List<Integer> list = new LinkedList<>();
            for (int i = 0; i < CNT; i++) {
                list.add(i);
            }
            while (!list.isEmpty()) {
                list.removeFirst();
            }
        });

        System.out.printf(ROW, "Remove from beginning", timeArrayList, timeLinkedList);
    }

    private void testRemoveFromEnd() {
        long timeArrayList = timeOperation(() -> {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < CNT; i++) {
                list.add(i);
            }
            while (!list.isEmpty()) {
                list.removeLast();
            }
        });

        long timeLinkedList = timeOperation(() -> {
            List<Integer> list = new LinkedList<>();
            for (int i = 0; i < CNT; i++) {
                list.add(i);
            }
            while (!list.isEmpty()) {
                list.removeLast();
            }
        });

        System.out.printf(ROW, "Remove from end", timeArrayList, timeLinkedList);
    }

    private long timeOperation(Runnable operation) {
        long start = nanoTime();
        operation.run();
        long end = nanoTime();
        return (end - start) / 1_000_000;
    }
}