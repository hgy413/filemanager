package com.jb.filemanager.os;

//CHECKSTYLE:OFF

import java.lang.reflect.Array;

/*
 * 为了在低版也能用到高版本的API, 复制官方源代码的{@code Arrays}, 并去除暂时没用到的API, 如有需要再补充回去.<br>
 * 官方源码版本: SDK 5.0.1<br>
 */
public class ZArrays {

    private ZArrays() {
		/* empty */
    }

    /**
     * Copies {@code newLength} elements from {@code original} into a new array.
     * If {@code newLength} is greater than {@code original.length}, the result
     * is padded with the value {@code null}.
     *
     * @param original
     *            the original array
     * @param newLength
     *            the length of the new array
     * @return the new array
     * @throws NegativeArraySizeException
     *             if {@code newLength < 0}
     * @throws NullPointerException
     *             if {@code original == null}
     * @since 1.6
     */
    public static <T> T[] copyOf(T[] original, int newLength) {
        if (original == null) {
            throw new NullPointerException("original == null");
        }
        if (newLength < 0) {
            throw new NegativeArraySizeException(Integer.toString(newLength));
        }
        return copyOfRange(original, 0, newLength);
    }

    /**
     * Copies elements from {@code original} into a new array, from indexes
     * start (inclusive) to end (exclusive). The original order of elements is
     * preserved. If {@code end} is greater than {@code original.length}, the
     * result is padded with the value {@code null}.
     *
     * @param original
     *            the original array
     * @param start
     *            the start index, inclusive
     * @param end
     *            the end index, exclusive
     * @return the new array
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code start < 0 || start > original.length}
     * @throws IllegalArgumentException
     *             if {@code start > end}
     * @throws NullPointerException
     *             if {@code original == null}
     * @since 1.6
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] copyOfRange(T[] original, int start, int end) {
        int originalLength = original.length; // For exception priority
        // compatibility.
        if (start > end) {
            throw new IllegalArgumentException();
        }
        if (start < 0 || start > originalLength) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int resultLength = end - start;
        int copyLength = Math.min(resultLength, originalLength - start);
        T[] result = (T[]) Array.newInstance(original.getClass()
                .getComponentType(), resultLength);
        System.arraycopy(original, start, result, 0, copyLength);
        return result;
    }

}