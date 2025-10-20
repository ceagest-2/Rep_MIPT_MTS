package com.mipt.uriilesnikov.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        HashMap<Integer, Student> hashMap = new HashMap<>();
        TreeMap<Integer, Student> treeMap = new TreeMap<>(Collections.reverseOrder());
        ArrayList<Student> students = new ArrayList<>();

        for (int i = 0; i < 11; i++) {
            students.add(new Student(i, String.format("student%d", i), i));
        }

        for (int i = 0; i < 11; i++) {
            hashMap.put(i, students.get(i));
            treeMap.put(i, students.get(i));
        }
    }
}
