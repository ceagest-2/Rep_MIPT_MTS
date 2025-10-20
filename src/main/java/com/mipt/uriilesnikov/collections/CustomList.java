package com.mipt.uriilesnikov.collections;

/**
 * An ordered collection (also known as a <i>sequence</i>) that allows duplicate elements
 * and provides positional access to its elements via integer indices.
 * <p>
 * This interface is a simplified version of {@link java.util.List}, supporting only
 * the core operations: adding elements, accessing by index, removing by index,
 * and querying size or emptiness.
 * <p>
 * Implementations of this interface may or may not allow {@code null} elements;
 * however, the reference implementation {@link com.mipt.uriilesnikov.collections.CustomArrayList}
 * explicitly prohibits {@code null} values.
 *
 * @param <A> the type of elements in this list
 */
public interface CustomList<A> {

    /**
     * Appends the specified element to the end of this list.
     * <p>
     * The behavior of this operation may depend on the implementation:
     * some implementations may reject certain elements (e.g., {@code null}).
     *
     * @param elem the element to be appended to this list
     * @throws IllegalArgumentException if the specified element is not allowed
     *         (e.g., if the implementation does not permit {@code null} elements)
     */
    void add(A elem);

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()})
     */
    A get(int index);

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()})
     */
    A remove(int index);

    /**
     * Returns the number of elements in this list.
     * <p>
     * If this list contains more than {@code Integer.MAX_VALUE} elements,
     * returns {@code Integer.MAX_VALUE}.
     *
     * @return the number of elements in this list
     */
    int size();

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list has no elements, otherwise {@code false}
     */
    boolean isEmpty();
}