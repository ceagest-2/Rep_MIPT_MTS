package com.mipt.uriilesnikov.collections;

import java.util.Iterator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomArrayListTest {

    @Test
    void testAddAndGetMethods() {
        CustomArrayList<Integer> list = new CustomArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        assertEquals(4, list.get(3));
        assertEquals(5, list.get(4));
    }

    @Test
    void testRemoveMethod() {
        CustomArrayList<String> list = new CustomArrayList<>();
        list.add("Alex");
        list.add("Bob");
        list.add("Yuri");
        list.add("Diana");
        list.add("Lada");

        String deleteElement = list.remove(1);

        assertEquals("Alex", list.get(0));
        assertEquals("Yuri", list.get(1));
        assertEquals("Diana", list.get(2));
        assertEquals("Lada", list.get(3));
        assertEquals("Bob", deleteElement);
    }

    @Test
    void testSizeMethod() {
        CustomArrayList<Integer> list = new CustomArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        assertEquals(5, list.size());
    }

    @Test
    void testIsEmptyMethod() {
        CustomArrayList<Integer> listEmpty = new CustomArrayList<>();
        CustomArrayList<Integer> listIsNotEmpty = new CustomArrayList<>();

        listIsNotEmpty.add(1);
        listIsNotEmpty.add(2);
        listIsNotEmpty.add(3);

        assertTrue(listEmpty.isEmpty());
        assertFalse(listIsNotEmpty.isEmpty());
    }

    @Test
    void testIteratorHasNextMethod() {
        CustomArrayList<String> list1 = new CustomArrayList<>();
        CustomArrayList<String> list2 = new CustomArrayList<>();

        list1.add("Alex");

        Iterator<String> iterator1 = list1.iterator();
        Iterator<String> iterator2 = list2.iterator();

        assertTrue(iterator1.hasNext());
        assertFalse(iterator2.hasNext());
    }

    @Test
    void testIteratorNextMethod() {
        CustomArrayList<String> list = new CustomArrayList<>();

        list.add("Alex");
        list.add("Yuri");
        list.add("Diana");
        list.add("Lada");

        Iterator<String> iterator = list.iterator();

        assertEquals("Alex", iterator.next());
        assertEquals("Yuri", iterator.next());
        assertEquals("Diana", iterator.next());
        assertEquals("Lada", iterator.next());
    }
}