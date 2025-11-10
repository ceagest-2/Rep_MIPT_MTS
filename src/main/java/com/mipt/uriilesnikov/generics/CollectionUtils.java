package com.mipt.uriilesnikov.generics;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {
    public static <T> List<T> mergeLists(List<? extends T> list1, List<? extends T> list2) {
        final List<T> list = new ArrayList<>(list1);
        list.addAll(list2);
        return list;
    }

    public static <T> void addAll(List<? super T> destination, List<? extends T> source) {
        destination.addAll(source);
    }
}
