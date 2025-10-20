package com.mipt.uriilesnikov.collections;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A resizable-array implementation of the {@link CustomList} interface.
 * Implements all optional list operations and permits all elements except {@code null}.
 * <p>
 * The list is backed by an internal array that grows automatically as elements are added.
 * The capacity grows by 50% when the current capacity is exceeded.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong>
 *
 * @param <A> the type of elements in this list
 */
public class CustomArrayList<A> implements CustomList<A> {

    /**
     * The array buffer into which elements are stored.
     * The capacity of the list is the length of this array buffer.
     * Any unused positions are filled with {@code null}.
     */
    private Object[] array;

    /**
     * The number of elements currently stored in the list.
     * Always satisfies {@code 0 <= size <= capacity}.
     */
    private int size;

    /**
     * The current capacity of the internal array (i.e., its length).
     * Must be equal to {@code array.length}.
     */
    private int capacity;

    /**
     * Constructs an empty list with an initial capacity of 4.
     */
    public CustomArrayList() {
        array = new Object[4];
        size = 0;
        capacity = 4;
    }

    /**
     * Appends the specified element to the end of this list.
     * <p>
     * If the internal array is full, it is expanded to 1.5 times its current capacity.
     *
     * @param elem the element to be appended to this list
     * @throws IllegalArgumentException if the specified element is {@code null}
     */
    @Override
    public void add(A elem) {
        if (elem == null) {
            throw new IllegalArgumentException();
        }

        if (size >= capacity) {
            capacity = (int) (capacity * 1.5);
            Object[] newArray = new Object[capacity];
            System.arraycopy(array, 0, newArray, 0, size);
            array = newArray;
        }

        array[size] = elem;
        size++;
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()})
     */
    @Override
    public A get(int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }

        //noinspection unchecked
        return (A) array[index];
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * The last occupied position is explicitly set to {@code null} to aid garbage collection.
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()})
     */
    @Override
    public A remove(int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }

        int newSize = size - 1;
        @SuppressWarnings("unchecked") A oldElement = (A) array[index];

        System.arraycopy(array, index + 1, array, index, newSize - index);
        size = newSize;
        array[size] = null; // eliminate obsolete reference

        return oldElement;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list has no elements, otherwise {@code false}
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * <p>
     * The returned iterator fails fast: if the list is structurally modified
     * at any time after the iterator is created, except through the iterator's
     * own {@code remove} method (which is not implemented here), the iterator
     * will throw a {@link ConcurrentModificationException}.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    public Iterator<A> iterator() {
        return new Itr();
    }

    /**
     * An optimized iterator implementation for {@link CustomArrayList}.
     * This class provides fail-fast behavior by checking for unexpected
     * structural modifications during iteration.
     */
    private class Itr implements Iterator<A> {

        /**
         * Index of the next element to be returned by {@link #next()}.
         */
        int cursor;

        /**
         * Index of the last element returned by {@link #next()},
         * or -1 if no such element exists.
         */
        int lastRet = -1;

        /**
         * Constructs an iterator starting at the beginning of the list.
         */
        Itr() {}

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iterator has more elements
         */
        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        /**
         * Returns the next element in the iteration.
         * <p>
         * This implementation checks for concurrent modification by verifying
         * that the cursor does not exceed the current length of the backing array.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         * @throws ConcurrentModificationException if the backing array has been
         *         resized unexpectedly (indicating a structural modification)
         */
        @Override
        public A next() {
            int i = cursor;

            if (i >= size) {
                throw new NoSuchElementException();
            }

            Object[] data = CustomArrayList.this.array;

            if (i >= data.length) {
                throw new ConcurrentModificationException();
            }

            cursor = i + 1;
            lastRet = i;
            //noinspection unchecked
            return (A) data[lastRet];
        }
    }
}