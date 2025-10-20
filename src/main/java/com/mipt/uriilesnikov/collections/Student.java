package com.mipt.uriilesnikov.collections;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Student {
    int id;
    String name;
    double grade;

    public Student(int id, String name, double grade) {
        this.id = id;

        if (name == null) {
            throw new IllegalArgumentException();
        }
        this.name = name;

        if (grade < 0) {
            throw new IllegalArgumentException();
        }
        this.grade = grade;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getGrade() {
        return grade;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Student student) {
            return id == student.id && Objects.equals(name, student.name) && grade == student.grade;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, grade);
    }

    public static ArrayList<Student> findStudentsByGradeRange(Map<Integer, Student> map, double minGrade, double maxGrade) {
        ArrayList<Student> studentsWithSuitableGrade = new ArrayList<>();

        for (Student student : map.values()) {
            if (student.getGrade() >= minGrade && student.getGrade() <= maxGrade) {
                studentsWithSuitableGrade.add(student);
            }
        }

        return studentsWithSuitableGrade;
    }

    public static ArrayList<Student> getTopNStudents(TreeMap<Integer, Student> map, int n) {
        ArrayList<Student> topNStudents = new ArrayList<>();
        int currentIndStudent = 0;

        for (Student student : map.values()) {
            if (currentIndStudent < n) {
                topNStudents.add(student);
                currentIndStudent++;
            } else {
                break;
            }
        }

        return topNStudents;
    }
}
