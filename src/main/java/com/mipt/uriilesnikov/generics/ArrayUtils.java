package com.mipt.uriilesnikov.generics;

public class ArrayUtils {
    public static <T> int findFirst(T[] array, T element) {
        if (array == null) {
            return -1;
        }
        int k = 0;
        for (T elem : array) {
            if (elem.equals(element)) {
                return k;
            }
            k++;
        }
        return -1;
    }
}
